package de.visualdigits.photosite.domain.data.repository

import de.visualdigits.photosite.domain.data.model.photosite.Photosite

interface PhotositeRepository {

    fun getPhotosite(): Photosite
}
