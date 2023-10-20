package de.visualdigits.kotlin.photosite.util

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class ProfileHelper {

    @Autowired
    lateinit var envvironment: Environment

    fun isProfileActive(profile: String): Boolean {
        return envvironment.activeProfiles.contains(profile)
    }
}
