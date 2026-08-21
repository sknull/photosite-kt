package de.visualdigits.photosite.data.photosite.mapper

import de.visualdigits.photosite.data.filesystem.mapper.toTranslation
import de.visualdigits.photosite.data.photosite.model.NavigationEntryDescriptor
import de.visualdigits.photosite.data.photosite.model.PhotositeDescriptor
import de.visualdigits.photosite.data.photosite.model.SslDescriptor
import de.visualdigits.photosite.data.photosite.model.plugins.EnliteDescriptor
import de.visualdigits.photosite.data.photosite.model.plugins.HtmlContentDescriptor
import de.visualdigits.photosite.data.photosite.model.plugins.LightBoxDescriptor
import de.visualdigits.photosite.data.photosite.model.plugins.LightGalleryDescriptor
import de.visualdigits.photosite.data.photosite.model.plugins.MarkdownContentDescriptor
import de.visualdigits.photosite.data.photosite.model.plugins.PhotoStoryDescriptor
import de.visualdigits.photosite.data.photosite.model.plugins.PluginsDescriptor
import de.visualdigits.photosite.data.photosite.model.plugins.RotatorDescriptor
import de.visualdigits.photosite.domain.data.model.photosite.NavigationEntry
import de.visualdigits.photosite.domain.data.model.photosite.Photosite
import de.visualdigits.photosite.domain.data.model.photosite.Plugins
import de.visualdigits.photosite.domain.data.model.photosite.Ssl
import de.visualdigits.photosite.domain.data.model.plugin.Enlite
import de.visualdigits.photosite.domain.data.model.plugin.HtmlContent
import de.visualdigits.photosite.domain.data.model.plugin.LightBox
import de.visualdigits.photosite.domain.data.model.plugin.LightGallery
import de.visualdigits.photosite.domain.data.model.plugin.MarkdownContent
import de.visualdigits.photosite.domain.data.model.plugin.PhotoStory
import de.visualdigits.photosite.domain.data.model.plugin.Rotator

fun PhotositeDescriptor.toPhotosite(): Photosite {
    return Photosite(
        serverPort = serverPort,
        internalPort = internalPort,
        internalEndpoints = internalEndpoints,
        ssl = ssl?.toSsl(),
        theme = theme,
        siteTitle = siteTitle,
        siteSubTitle = siteSubTitle,
        protocol = protocol,
        domain = domain,
        languages = languages,
        languageDefault = languageDefault,
        naviMain = naviMain?.toNavigationEntry(),
        naviSub = naviSub.map { it.toNavigationEntry() },
        naviStatic = naviStatic?.toNavigationEntry(),
        plugins = plugins?.toPlugins()
    )
}

fun SslDescriptor.toSsl(): Ssl {
    return Ssl(
        certbotUri = certbotUri,
        keyStore = keyStore,
        keyStoreType = keyStoreType,
        keyAlias = keyAlias,
        keyStorePassword = keyStorePassword
    )
}

fun NavigationEntryDescriptor.toNavigationEntry(): NavigationEntry {
    return NavigationEntry(
        rootFolder = rootFolder,
        numberOfEntries = numberOfEntries,
        translations = translations.map { it.toTranslation() }
    )
}

fun PluginsDescriptor.toPlugins(): Plugins {
    return Plugins(
        enlite = enlite.toEnlite(),
        html = html.toEHtmlContent(),
        lightbox = lightbox.toLightBox(),
        lightgallery = lightgallery.toLightGallery(),
        markdown = markdown.toMarkdownContent(),
        photostory = photostory.toPhotoStory(),
        rotator = rotator.toRotator()
    )
}

fun EnliteDescriptor.toEnlite(): Enlite {
    return Enlite()
}

fun HtmlContentDescriptor.toEHtmlContent(): HtmlContent {
    return HtmlContent(
        contentType = contentType
    )
}

fun LightBoxDescriptor.toLightBox(): LightBox {
    return LightBox(
        resizeDuration = resizeDuration,
        fadeDuration = fadeDuration,
        imageFadeDuration = imageFadeDuration,
        wrapAround = wrapAround
    )
}

fun LightGalleryDescriptor.toLightGallery(): LightGallery {
    return LightGallery(
        mode = mode,
        speed = speed,
        pause = pause,
        showThumbByDefault = showThumbByDefault,
        animateThumb = animateThumb,
        progressBar = progressBar,
        download = download
    )
}

fun MarkdownContentDescriptor.toMarkdownContent(): MarkdownContent {
    return MarkdownContent()
}

fun PhotoStoryDescriptor.toPhotoStory(): PhotoStory {
    return PhotoStory(
        mode = mode,
        speed = speed,
        pause = pause,
        showThumbByDefault = showThumbByDefault,
        animateThumb = animateThumb,
        progressBar = progressBar,
        download = download
    )
}


fun RotatorDescriptor.toRotator(): Rotator {
    return Rotator()
}

