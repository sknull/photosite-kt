package de.visualdigits.photosite.controller

import de.visualdigits.photosite.model.siteconfig.Photosite
import org.slf4j.LoggerFactory


abstract class AbstractBaseController(
    val photosite: Photosite
) {

    private val log = LoggerFactory.getLogger(AbstractBaseController::class.java)

    companion object
}

