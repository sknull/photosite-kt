package de.visualdigits.photosite.domain.data.repository

import de.visualdigits.photosite.domain.data.model.page.Page

interface FilesystemPageRepository {

    fun getPageTree(): Page
}
