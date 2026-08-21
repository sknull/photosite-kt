package de.visualdigits.photosite.data.filesystem.mapper

import de.visualdigits.photosite.data.filesystem.model.CaptionDescriptor
import de.visualdigits.photosite.data.filesystem.model.ContentDescriptor
import de.visualdigits.photosite.data.filesystem.model.GoogleMapsDescriptor
import de.visualdigits.photosite.data.filesystem.model.ImageDescriptor
import de.visualdigits.photosite.data.filesystem.model.PageDescriptor
import de.visualdigits.photosite.data.filesystem.model.ParagraphDescriptor
import de.visualdigits.photosite.data.filesystem.model.SortDescriptor
import de.visualdigits.photosite.data.filesystem.model.TeaserDescriptor
import de.visualdigits.photosite.data.filesystem.model.TextDescriptor
import de.visualdigits.photosite.data.filesystem.model.TranslationDescriptor
import de.visualdigits.photosite.domain.data.model.common.Translation
import de.visualdigits.photosite.domain.data.model.page.Page
import de.visualdigits.photosite.domain.data.model.page.content.Caption
import de.visualdigits.photosite.domain.data.model.page.content.Content
import de.visualdigits.photosite.domain.data.model.page.content.GoogleMaps
import de.visualdigits.photosite.domain.data.model.page.content.Image
import de.visualdigits.photosite.domain.data.model.page.content.Paragraph
import de.visualdigits.photosite.domain.data.model.page.content.Sort
import de.visualdigits.photosite.domain.data.model.page.content.Teaser
import de.visualdigits.photosite.domain.data.model.page.content.Text


fun PageDescriptor.toPage(): Page {
    return Page(
        icon = icon,
        tocName = tocName,
        content = content.toContent(),
        translations = translations.map { it.toTranslation() }
    )
}

fun ContentDescriptor.toContent(): Content {
    return Content(
        contentType = contentType,
        mode = mode,
        speed = speed,
        pause = pause,
        download = download,
        sort = sort?.toSort(),
        teaser = teaser?.toTeaser(),
        captions = captions.map { it.toCaption() },
        keywords = keywords,
        paragraphs = paragraphs.map { it.toParagraph() },
        mdContent = mdContent,
        htmlContent = htmlContent
    )
}

fun SortDescriptor.toSort(): Sort {
    return Sort(
        by = by,
        dir = dir,
        order = order
    )
}

fun TeaserDescriptor.toTeaser(): Teaser {
    return Teaser(
        googleMaps = googleMaps?.toGoogleMaps(),
        texts = texts.map { it.toText() }
    )
}

fun GoogleMapsDescriptor.toGoogleMaps(): GoogleMaps {
    return GoogleMaps(
        name = name,
        width = width,
        height = height,
        align = align,
        lat = lat,
        lng = lng,
        zoom = zoom
    )
}

fun CaptionDescriptor.toCaption(): Caption {
    return Caption(
        name = name,
        alt = alt,
        caption = caption,
        translations = translations.map { it.toTranslation() }
    )
}

fun ParagraphDescriptor.toParagraph(): Paragraph {
    return Paragraph(
        image = image?.toImage(),
        googleMaps = googleMaps?.toGoogleMaps(),
        texts = texts.map { it.toText() }
    )
}

fun TextDescriptor.toText(): Text {
    return Text(
        lang = lang,
        value = value
    )
}

fun TranslationDescriptor.toTranslation(): Translation {
    return Translation(
        lang = lang,
        name = name,
        alt = alt,
        title = title
    )
}

fun ImageDescriptor.toImage(): Image {
    return Image(
        name = name,
        align = align,
        alt = alt
    )
}
