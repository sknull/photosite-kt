package de.visualdigits.photosite.service

import de.visualdigits.photosite.Application
import de.visualdigits.photosite.model.photosite.Photosite
import org.apache.commons.io.IOUtils
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.shredzone.acme4j.Account
import org.shredzone.acme4j.AccountBuilder
import org.shredzone.acme4j.Authorization
import org.shredzone.acme4j.Session
import org.shredzone.acme4j.Status
import org.shredzone.acme4j.challenge.Challenge
import org.shredzone.acme4j.challenge.Http01Challenge
import org.shredzone.acme4j.util.CSRBuilder
import org.shredzone.acme4j.util.KeyPairUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.security.KeyPair
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Helper class to obtain PEM key pairs for the given domains and convert them into a java compatible PKCS12 keystore.
 */
@Service
class DomainCertificatesService(
    private val photosite: Photosite
) {

    private val log = LoggerFactory.getLogger(DomainCertificatesService::class.java)

    companion object {
        private const val FILE_KEY_STORE = "keystore.p12"
        private const val FILE_DOMAIN_KEY = "domain.key"
        private const val FILE_DOMAIN_CHAIN_CRT = "domain-chain.crt"
        private const val FILE_DOMAIN_CSR = "domain.csr"
        private const val FILE_USER_KEY = "user.key"
        private const val RETRY_ATTEMPTS = 3
        private const val KEY_SIZE = 2048
    }

    fun refreshCertIfNeeded(
        certbotUri: String,
        certbotAlias: String,
        certbotPassword: String,
        expiryDate: LocalDateTime
    ) {
        if (photosite.isProfileActive("prod")) {
            val newExpiryDate = maintainServerCertificate(
                certbotUri = certbotUri,
                certbotAlias = certbotAlias,
                certbotPassword = certbotPassword,
                expiryDate = expiryDate
            )
            if (newExpiryDate.isAfter(expiryDate)) {
                Application.restart("ssl")
            }
        }
    }

    /**
     * Maintain server certificates if valid less than given [gracePeriod] days or [forceUpdate] is true.
     */
    fun maintainServerCertificate(
        certbotUri: String,
        certbotAlias: String,
        certbotPassword: String,
        forceUpdate: Boolean = false,
        expiryDate: LocalDateTime,
        gracePeriod: Long = 7
    ): LocalDateTime {
        log.info("Server certificate will expire at '$expiryDate' - updating now...")
        return if (forceUpdate || LocalDateTime.now().isAfter(expiryDate.minus(gracePeriod, ChronoUnit.DAYS))) {
            log.info("Updating certificates...")
            createCertificates(
                certbotUri = certbotUri,
                domains = listOf(photosite.domain),
                keystoreAlias = certbotAlias,
                keystorePassword = certbotPassword
            )
            val newExpiryDate = determineExpiryDate(certbotAlias, certbotPassword)
            log.info("Successfully updated server certificate, new certificate will be valid until '$newExpiryDate'")
            newExpiryDate
        } else {
            log.info("Skipping certificate update")
            expiryDate
        }
    }

    fun determineExpiryDate(
        alias: String?,
        password: String
    ): LocalDateTime {
        val keystore = Paths.get(Photosite.rootDirectory.canonicalPath, "secrets", FILE_KEY_STORE).toFile()
        val expiryDate = if (keystore.exists()) {
            runCatching {
                Files.newInputStream(keystore.toPath()).use { ins ->
                    val keystore = KeyStore.getInstance("PKCS12")
                    keystore.load(ins, password.toCharArray())
                    val cert =
                        keystore.getCertificate(alias) as X509Certificate
                    cert.notAfter
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
                }
            }.onFailure { e ->
                throw IllegalStateException("Could not determine expiry date from keystore: $keystore", e)
            }.getOrThrow()
        } else {
            // force update when file not found
            LocalDateTime.now().minus(31, ChronoUnit.DAYS)
        }
        return expiryDate
    }

    private fun createCertificates(
        certbotUri: String?,
        domains: Collection<String?>,
        keystoreAlias: String?,
        keystorePassword: String
    ) {
        fetchCertificate(certbotUri, domains)
        val keyFile = Paths.get(Photosite.rootDirectory.canonicalPath, "secrets", FILE_DOMAIN_KEY).toFile()
        val cerFile = Paths.get(Photosite.rootDirectory.canonicalPath, "secrets", FILE_DOMAIN_CHAIN_CRT).toFile()
        val keystoreBytes = convertPEMToPKCS12(keyFile, cerFile, keystorePassword, keystoreAlias)
        val keystore = Paths.get(Photosite.rootDirectory.canonicalPath, "secrets", FILE_KEY_STORE).toFile()
        log.info("Creating target keystore file '$keystore'")
        keystore.writeBytes(keystoreBytes)
    }

    /**
     * Generates a certificate for the given domains. Also takes care for the registration
     * process.
     *
     * @param domains Domains to get a common certificate for
     */
    private fun fetchCertificate(
        serverUri: String?,
        domains: Collection<String?>?
    ) {
        // Load the user key file. If there is no key file, create a new one.
        val userKeyPair = loadOrCreateUserKeyPair()

        // Create a session for Let's Encrypt.
        // Use "acme://letsencrypt.org" for production server
        log.info("Create new session")
        val session = Session(serverUri)

        // Get the Account.
        // If there is no account yet, create a new one.
        log.info("findOrRegisterAccount")
        val account = findOrRegisterAccount(session, userKeyPair)

        // Load or create a key pair for the domains. This should not be the userKeyPair!
        log.info("loadOrCreateDomainKeyPair")
        val domainKeyPair = loadOrCreateDomainKeyPair()
        runCatching {
            log.info("Create new order")
            // Order the certificate
            val order = account.newOrder().domains(domains).create()

            // Perform all required authorizations
            log.info("Authorize orders")
            for (auth in order.authorizations) {
                authorize(auth)
            }

            // Generate a CSR for all of the domains, and sign it with the domain key pair.
            val csrb = CSRBuilder()
            csrb.addDomains(domains)
            csrb.sign(domainKeyPair)

            // Write the CSR to a file, for later use.
            val domainCsrFile = Paths.get(Photosite.rootDirectory.canonicalPath, "secrets", FILE_DOMAIN_CSR).toFile()
            log.info("Creating CSR file '$domainCsrFile'")
            runCatching {
                FileWriter(domainCsrFile).use { out -> csrb.write(out) }
            }.onFailure { e ->
                throw IllegalStateException("Could not write csr file", e)
            }

            // Order the certificate
            order.execute(csrb.encoded)

            // Wait for the order to complete
            runCatching {
                var attempts = RETRY_ATTEMPTS
                while (order.status != Status.VALID && attempts-- > 0) {
                    log.info("Try " + (RETRY_ATTEMPTS - attempts + 1) + "/" + RETRY_ATTEMPTS + " to update order...")
                    // Did the order fail?
                    check(order.status != Status.INVALID) { "Order failed... Giving up." }

                    // Wait for a few seconds
                    Thread.sleep(3000L)

                    // Then update the status
                    order.update()
                }
            }.onFailure { ex ->
                log.error("interrupted", ex)
                Thread.currentThread().interrupt()
            }

            // Get the certificate
            val certificate = order.certificate
            log.info("Success! The certificate for domains {} has been generated!", domains)
            log.info("Certificate URL: {}", certificate!!.location)

            // Write a combined file containing the certificate and chain.
            val domainChainFile = Paths.get(Photosite.rootDirectory.canonicalPath, "secrets", FILE_DOMAIN_CHAIN_CRT).toFile()
            log.info("Creating domain chin file '$domainChainFile'")
            FileWriter(domainChainFile).use { fw -> certificate.writeCertificate(fw) }
        }.onFailure { e ->
            log.error("Could not fetch certificates", e)
            throw IllegalStateException("Could not fetch certificates", e)
        }
    }

    /**
     * Loads a user key pair from userKeyFile. If the file does not exist, a
     * new key pair is generated and saved.
     *
     * Keep this key pair in a safe place! In a production environment, you will not be
     * able to access your account again if you should lose the key pair.
     *
     * @return User's [java.security.KeyPair].
     */
    private fun loadOrCreateUserKeyPair(): KeyPair {
        val userKeyFile = Paths.get(Photosite.rootDirectory.canonicalPath, "secrets", FILE_USER_KEY).toFile()
        if (userKeyFile.exists()) {
            // If there is a key file, read it
            try {
                FileReader(userKeyFile).use { fr -> return KeyPairUtils.readKeyPair(fr) }
            } catch (e: Exception) {
                throw IllegalStateException("Could not read user key pair", e)
            }
        } else {
            // If there is none, create a new key pair and save it
            val userKeyPair = KeyPairUtils.createKeyPair(KEY_SIZE)
            log.info("Creating user key file '$userKeyFile'")
            try {
                FileWriter(userKeyFile).use { fw -> KeyPairUtils.writeKeyPair(userKeyPair, fw) }
            } catch (e: Exception) {
                throw IllegalStateException("Could not write user key pair", e)
            }
            return userKeyPair
        }
    }

    /**
     * Loads a domain key pair from #domainKeyFile. If the file does not exist,
     * a new key pair is generated and saved.
     *
     * @return Domain [KeyPair].
     */
    private fun loadOrCreateDomainKeyPair(): KeyPair {
        val domainKeyFile = Paths.get(Photosite.rootDirectory.canonicalPath, "secrets", FILE_DOMAIN_KEY).toFile()
        if (domainKeyFile.exists()) {
            try {
                FileReader(domainKeyFile).use { fr -> return KeyPairUtils.readKeyPair(fr) }
            } catch (e: Exception) {
                throw IllegalStateException("Could not domain read key pair", e)
            }
        } else {
            log.info("Creating domain key file '$domainKeyFile'")
            val domainKeyPair = KeyPairUtils.createKeyPair(KEY_SIZE)
            try {
                FileWriter(domainKeyFile).use { fw -> KeyPairUtils.writeKeyPair(domainKeyPair, fw) }
            } catch (e: Exception) {
                throw IllegalStateException("Could not domain write key pair", e)
            }
            return domainKeyPair
        }
    }

    /**
     * Finds your [org.shredzone.acme4j.Account] at the ACME server. It will be found by your user's
     * public key. If your key is not known to the server yet, a new account will be
     * created.
     *
     * This is a simple way of finding your [org.shredzone.acme4j.Account]. A better way is to get the
     * URL of your new account with [org.shredzone.acme4j.AcmeResource.getLocation] and store it somewhere.
     * If you need to get access to your account later, reconnect to it via [ ][Session.login] by using the stored location.
     *
     * @param session [Session] to bind with
     * @return [org.shredzone.acme4j.Account]
     */
    private fun findOrRegisterAccount(
        session: Session,
        accountKey: KeyPair
    ): Account {
        val account = try {
            AccountBuilder()
                .agreeToTermsOfService()
                .useKeyPair(accountKey)
                .create(session)
        } catch (e: Exception) {
            throw IllegalStateException("Could not create session", e)
        }
        log.info("Registered a new user, URL: {}", account.location)
        return account
    }

    /**
     * Authorize a domain. It will be associated with your account, so you will be able to
     * retrieve a signed certificate for the domain later.
     *
     * @param auth [org.shredzone.acme4j.Authorization] to perform
     */
    private fun authorize(
        auth: Authorization
    ) {
        log.info("Authorization for domain {}", auth.identifier.domain)

        // The authorization is already valid. No need to process a challenge.
        if (auth.status == Status.VALID) {
            return
        }

        // Find the desired challenge and prepare it.
        val challenge = httpChallenge(auth)

        // If the challenge is already verified, there's no need to execute it again.
        if (challenge.status == Status.VALID) {
            return
        }

        try {
            // Now trigger the challenge.
            challenge.trigger()

            // Poll for the challenge to complete.
            var attempts = RETRY_ATTEMPTS
            while (challenge.status != Status.VALID && attempts-- > 0) {
                // Did the authorization fail?
                check(challenge.status != Status.INVALID) { "Challenge failed... Giving up." }

                // Wait for a few seconds
                Thread.sleep(3000L)

                // Then update the status
                challenge.update()
            }
        } catch (ex: Exception) {
            log.error("interrupted", ex)
            Thread.currentThread().interrupt()
        }

        // All reattempts are used up and there is still no valid authorization?
        check(challenge.status == Status.VALID) {
            ("Failed to pass the challenge for domain "
                    + auth.identifier.domain + ", ... Giving up.")
        }
        log.info("Challenge has been completed. Remember to remove the validation resource.")
    }

    /**
     * Prepares a HTTP challenge.
     *
     * The verification of this challenge expects a file with a certain content to be
     * reachable at a given path under the domain to be tested.
     *
     * @param auth [Authorization] to find the challenge in
     * @return [org.shredzone.acme4j.challenge.Challenge] to verify
     */
    private fun httpChallenge(
        auth: Authorization
    ): Challenge {
        // Find a single http-01 challenge
        val challenge = auth.findChallenge(Http01Challenge::class.java)?: error("Found no " + Http01Challenge.TYPE + " challenge, don't know what to do...")
        val challengeDirectory = Paths.get(Photosite.rootDirectory.canonicalPath, ".well-known", "acme-challenge").toFile()
        check(!(!challengeDirectory.exists() && !challengeDirectory.mkdirs())) { "Could not create challenge directory: $challengeDirectory" }
        val challengeFile = File(challengeDirectory, challenge.token)
        log.info("Creating challenge file: $challengeFile")
        try {
            Files.newOutputStream(challengeFile.toPath()).use { outs ->
                IOUtils.write(
                    challenge.authorization.toByteArray(StandardCharsets.UTF_8),
                    outs
                )
            }
        } catch (e: Exception) {
            throw IllegalStateException("Could not create challenge file: $challengeFile", e)
        }
        return challenge
    }

    private fun convertPEMToPKCS12(
        keyFile: File,
        cerFile: File,
        password: String,
        alias: String?
    ): ByteArray {
        log.info("Converting keyfile into pkcs12 keystore")
        // Get the private key
        val certHolder = readCertFile(cerFile)
        return createPkcs12(keyFile, password, alias, certHolder)
    }

    private fun createPkcs12(
        keyFile: File,
        password: String,
        alias: String?,
        certHolder: X509CertificateHolder
    ): ByteArray {
        return runCatching {
            // Put them into a PKCS12 keystore and write it to a byte[]
            ByteArrayOutputStream().use { bos ->
                val ks = KeyStore.getInstance("PKCS12")
                ks.load(null)
                val key = readKeyFile(keyFile)
                ks.setKeyEntry(alias, key, password.toCharArray(), arrayOf(
                    JcaX509CertificateConverter()
                        .setProvider(BouncyCastleProvider())
                        .getCertificate(certHolder)
                ))
                ks.store(bos, password.toCharArray())
                bos.toByteArray()
            }
        }.onFailure { e ->
            throw IllegalStateException("Could not convert PEM to PKCS12", e)
        }.getOrThrow()
    }

    private fun readCertFile(
        cerFile: File
    ): X509CertificateHolder {
        val certHolder: X509CertificateHolder
        try {
            FileReader(cerFile).use { reader ->
                PEMParser(reader).use { pem ->
                    certHolder = pem.readObject() as X509CertificateHolder
                }
            }
        } catch (e: Exception) {
            throw IllegalStateException("Could not read cert file", e)
        }
        return certHolder
    }

    private fun readKeyFile(
        keyFile: File
    ): PrivateKey {
        val key: PrivateKey
        try {
            FileReader(keyFile).use { reader ->
                PEMParser(reader).use { pem ->
                    val pemKeyPair =
                        pem.readObject() as PEMKeyPair
                    val jcaPEMKeyConverter =
                        JcaPEMKeyConverter()
                            .setProvider(BouncyCastleProvider())
                    val keyPair = jcaPEMKeyConverter.getKeyPair(pemKeyPair)
                    key = keyPair.private
                }
            }
        } catch (e: Exception) {
            throw IllegalStateException("Could not read key file", e)
        }
        return key
    }
}
