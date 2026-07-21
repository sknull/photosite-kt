package de.visualdigits.photosite.model.photosite

import de.visualdigits.photosite.service.DomainCertificatesService
import org.hibernate.validator.internal.util.Contracts.assertNotNull
import org.hibernate.validator.internal.util.Contracts.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.DisabledIf
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.io.ByteArrayInputStream
import java.io.File
import java.security.KeyStore
import java.security.cert.X509Certificate

@SpringBootTest(properties = [
    "spring.config.import=optional:file:C:/Users/sknull/.photosite/secrets/secrets.yml"
])
@ActiveProfiles("ssl")
@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
class FixPemFilesTest @Autowired constructor(
    private val photosite: Photosite,
    private val domainCertificatesService: DomainCertificatesService,
) {

    @Test
    fun testPEMwithFullChain() {
        // Pfade zu deinen existierenden Geheimnissen
        val secretsDir = File("C:\\Users\\sknull\\.photosite\\secrets")
        val keyFile = File(secretsDir, "domain.key")
        val cerFile = File(secretsDir, "domain-chain.crt")
        val password = photosite.ssl!!.keyStorePassword!!
        val alias = photosite.ssl!!.keyAlias!!
        // Die neue Konvertierungslogik aufrufen
        val keystoreBytes = domainCertificatesService.convertPEMToPKCS12(keyFile, cerFile, password, alias)

        // Den Keystore zur Prüfung laden
        val ks = KeyStore.getInstance("PKCS12")
        ks.load(ByteArrayInputStream(keystoreBytes), password.toCharArray())

        // Die Zertifikatskette abrufen
        val chain = ks.getCertificateChain(alias)

        println("Anzahl der Zertifikate in der Kette: ${chain?.size}")

        // Validierung
        assertNotNull(chain, "Kette darf nicht null sein")
        assertTrue(chain.size >= 2, "Die Kette sollte mindestens 2 Zertifikate enthalten (Leaf + Intermediate)")

        chain.forEachIndexed { index, cert ->
            val x509 = cert as X509Certificate
            println("Zertifikat #$index: ${x509.subjectX500Principal}")
            println("Aussteller #$index: ${x509.issuerX500Principal}")
        }
    }
}
