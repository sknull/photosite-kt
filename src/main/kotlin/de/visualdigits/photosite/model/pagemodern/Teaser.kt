package de.visualdigits.photosite.model.pagemodern

import com.fasterxml.jackson.annotation.JsonAlias


class Teaser(
    @JsonAlias("googlemaps", "googleMaps") val googleMaps: GoogleMaps? = null,
    val texts: List<Text> = listOf()
)
