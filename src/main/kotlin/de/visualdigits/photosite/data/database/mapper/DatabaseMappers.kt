package de.visualdigits.photosite.data.database.mapper

import de.visualdigits.photosite.data.database.model.CaptionEntity
import de.visualdigits.photosite.data.database.model.ImageFileEntity
import de.visualdigits.photosite.data.database.model.PageEntity
import de.visualdigits.photosite.data.database.model.ParagraphEntity
import de.visualdigits.photosite.data.database.model.TextEntity
import de.visualdigits.photosite.data.database.model.TranslationEntity
import de.visualdigits.photosite.domain.data.model.common.KmpOffsetDateTime
import de.visualdigits.photosite.domain.data.model.common.Language
import de.visualdigits.photosite.domain.data.model.common.Translation
import de.visualdigits.photosite.domain.data.model.page.Page
import de.visualdigits.photosite.domain.data.model.page.content.Caption
import de.visualdigits.photosite.domain.data.model.page.content.Content
import de.visualdigits.photosite.domain.data.model.page.content.ContentType
import de.visualdigits.photosite.domain.data.model.page.content.GoogleMaps
import de.visualdigits.photosite.domain.data.model.page.content.Image
import de.visualdigits.photosite.domain.data.model.page.content.ImageFile
import de.visualdigits.photosite.domain.data.model.page.content.Paragraph
import de.visualdigits.photosite.domain.data.model.page.content.Sort
import de.visualdigits.photosite.domain.data.model.page.content.SortDir
import de.visualdigits.photosite.domain.data.model.page.content.Teaser
import de.visualdigits.photosite.domain.data.model.page.content.Text
import de.visualdigits.photosite.domain.util.getRelativeResourcePath
import de.visualdigits.photosite.domain.util.getThumbnailPath
import de.visualdigits.photosite.presentation.model.CaptionDto
import de.visualdigits.photosite.presentation.model.ContentDto
import de.visualdigits.photosite.presentation.model.GoogleMapsDto
import de.visualdigits.photosite.presentation.model.ImageDto
import de.visualdigits.photosite.presentation.model.ImageFileDto
import de.visualdigits.photosite.presentation.model.PageDto
import de.visualdigits.photosite.presentation.model.ParagraphDto
import de.visualdigits.photosite.presentation.model.SortDto
import de.visualdigits.photosite.presentation.model.TeaserDto
import de.visualdigits.photosite.presentation.model.TextDto
import de.visualdigits.photosite.presentation.model.TranslationDto
import kotlinx.datetime.toJavaZoneOffset
import kotlinx.datetime.toKotlinUtcOffset
import java.io.File
import java.time.OffsetDateTime
import kotlin.time.toJavaInstant

fun Page.toPageEntity(): PageEntity {
    return PageEntity(
        id = id,
        icon = icon,
        tocName = tocName,
        directory = directory?.canonicalPath,
        path = path(),
        ariaName = ariaName,
        contentType = content.contentType?.name,
        mode = content.mode,
        speed = content.speed,
        pause = content.pause,
        download = content.download,
        sortBy = content.sort?.by,
        sortDir = content.sort?.dir?.name,
        sortOrder = content.sort?.order,
        teaserGoogleMapsName = content.teaser?.googleMaps?.name,
        teaserGoogleMapsWidth = content.teaser?.googleMaps?.width,
        teaserGoogleMapsHeight = content.teaser?.googleMaps?.height,
        teaserGoogleMapsAlign = content.teaser?.googleMaps?.align,
        teaserGoogleMapsLat = content.teaser?.googleMaps?.lat,
        teaserGoogleMapsLng = content.teaser?.googleMaps?.lng,
        teaserGoogleMapsZoom = content.teaser?.googleMaps?.zoom,
        images = content.images.map { it.toImageFileEntity() }.toMutableList(),
        teaserTexts = content.teaser?.texts?.map { it.toTextEntity() }?.toMutableList(),
        captions = content.captions.map { it.toCaptionEntity() }.toMutableList(),
        keywords = content.keywords.toMutableList(),
        paragraphs = content.paragraphs.map { it.toParagraphEntity() }.toMutableList(),
        mdContent = content.mdContent,
        htmlContent = content.htmlContent,
        translations = translations.map { it.toTranslationEntity() }.toMutableList()
    )
}

fun Page.toPageDto(): PageDto {
    return PageDto(
        icon = icon,
        tocName = tocName,
        path = path(),
        ariaName = ariaName,
        content = ContentDto(
            contentType = content.contentType,
            mode = content.mode,
            speed = content.speed,
            pause = content.pause,
            download = content.download,
            sort = SortDto(
                by = content.sort?.by,
                dir = content.sort?.dir,
                order = content.sort?.order,
            ),
            teaser = content.teaser?.toTeaserDto(),
            captions = content.captions.map { it.toCaptionDto() },
            keywords = content.keywords.toMutableList(),
            paragraphs = content.paragraphs.map { it.toParagraphDto() },
            mdContent = content.mdContent,
            htmlContent = content.htmlContent,
            images = content.images.map { it.toImageFileDto() },
        ),
        translations = translations.map { it.toTranslationDto() },
        children  = children.map { child -> child.toPageDto() },
        lastModified = OffsetDateTime.ofInstant(
            lastModified.toInstant().toJavaInstant(),
            lastModified.offset.toJavaZoneOffset()
        )
    )
}

fun Teaser.toTeaserDto(): TeaserDto {
    return TeaserDto(
        googleMaps = googleMaps?.toGoogleMapsDto(),
        texts = texts.map { it.toTextDto() }
    )
}

fun GoogleMaps.toGoogleMapsDto(): GoogleMapsDto {
    return GoogleMapsDto(
        name = name,
        width = width,
        height = height,
        align = align,
        lat = lat,
        lng = lng,
        zoom = zoom
    )
}

fun Text.toTextDto(): TextDto {
    return TextDto(
        lang = lang,
        value = value
    )
}

fun PageEntity.toPage(): Page {
    val page = Page(
        id = id,
        icon = icon,
        tocName = tocName,
        content = Content(
            contentType = contentType?.let { ct -> ContentType.valueOf(ct) },
            mode = mode,
            speed = speed,
            pause = pause,
            download = download,
            sort = Sort(
                by = sortBy,
                dir = sortDir?.let { d -> SortDir.valueOf(d) },
                order = sortOrder
            ),
            teaser = if (teaserGoogleMapsLat != null && teaserGoogleMapsLng != null) {
                Teaser(
                    googleMaps = GoogleMaps(
                        name = teaserGoogleMapsName,
                        width = teaserGoogleMapsWidth,
                        height = teaserGoogleMapsHeight,
                        align = teaserGoogleMapsAlign,
                        lat = teaserGoogleMapsLat,
                        lng = teaserGoogleMapsLng,
                        zoom = teaserGoogleMapsZoom,
                    ),
                    texts = teaserTexts?.map { it.toText() } ?: listOf()
                )
            } else null,
            captions = captions.map { it.toCaption() },
            keywords = keywords,
            paragraphs = paragraphs.map { it.toParagraph() },
            mdContent = mdContent,
            htmlContent = htmlContent,
            images = images.map { it.toImageFile() }.toMutableList()
        ),
        translations = translations.map { it.toTranslation() },
        directory = directory?.let { d -> File(d) },
        path = path,
        ariaName = ariaName
    )

    return page
}

fun Caption.toCaptionEntity(): CaptionEntity {
    return CaptionEntity(
        id = id,
        name = name,
        alt = alt,
        caption = caption,
        translations = translations.map { it.toTranslationEntity() }.toMutableList()
    )
}

fun Caption.toCaptionDto(): CaptionDto {
    return CaptionDto(
        name = name,
        alt = alt,
        caption = caption,
        translations = translations.map { it.toTranslationDto() }.toMutableList()
    )
}

fun CaptionEntity.toCaption(): Caption {
    return Caption(
        id = id,
        name = name,
        alt = alt,
        caption = caption,
        translations = translations.map { it.toTranslation() }
    )
}

fun Translation.toTranslationEntity(): TranslationEntity {
    return TranslationEntity(
        id = id,
        lang = lang?.language,
        alt = alt,
        name = name,
        title = title
    )
}

fun Translation.toTranslationDto(): TranslationDto {
    return TranslationDto(
        lang = lang,
        alt = alt,
        name = name,
        title = title
    )
}

fun TranslationEntity.toTranslation(): Translation {
    return Translation(
        id = id,
        lang = lang?.let { Language(it) } ?: Language.DE,
        alt = alt,
        name = name,
        title = title
    )
}

fun Paragraph.toParagraphEntity(): ParagraphEntity {
    return ParagraphEntity(
        id = id,
        imageName = image?.name,
        imageAlign = image?.align,
        imageAlt = image?.alt,
        googleMapsName = googleMaps?.name,
        googleMapsWidth = googleMaps?.width,
        googleMapsHeight = googleMaps?.height,
        googleMapsAlign = googleMaps?.align,
        googleMapsLat = googleMaps?.lat,
        googleMapsLng = googleMaps?.lng,
        googleMapsZoom = googleMaps?.zoom,
        texts = texts?.map { it.toTextEntity() }?.toMutableList()
    )
}

fun Paragraph.toParagraphDto(): ParagraphDto {
    return ParagraphDto(
        image = image?.toImageDto(),
        googleMaps = googleMaps?.toGoogleMapsDto(),
        texts = texts?.map { it.toTextDto() }?.toMutableList()
    )
}

fun Image.toImageDto(): ImageDto {
    return ImageDto(
        name = name,
        align = align,
        alt = alt
    )
}

fun ParagraphEntity.toParagraph(): Paragraph {
    return Paragraph(
        id = id,
        image = Image(
            name = imageName,
            align = imageAlign,
            alt = imageAlt,
        ),
        googleMaps = GoogleMaps(
            name = googleMapsName,
            width = googleMapsWidth,
            height = googleMapsHeight,
            align = googleMapsAlign,
            lat = googleMapsLat,
            lng = googleMapsLng,
            zoom = googleMapsZoom,
        ),
        texts = texts?.map { it.toText() }
    )
}

fun Text.toTextEntity(): TextEntity {
    return TextEntity(
        id = id,
        lang = lang?.language,
        value = value
    )
}

fun TextEntity.toText(): Text {
    return Text(
        id = id,
        lang = lang?.let { Language(it) },
        value = value
    )
}

fun ImageFile.toImageFileEntity(): ImageFileEntity {
    return ImageFileEntity(
        id = id,
        file = file.canonicalPath,
        name = name,
        apertureValue = apertureValue,
        exposureTime = exposureTime,
        exposureBias = exposureBias,
        isoEquivalent = isoEquivalent,
        focalLength = focalLength,
        make = make,
        model = model,
        lensModel = lensModel,
        lastModified = OffsetDateTime.ofInstant(
            lastModified.toInstant().toJavaInstant(),
            lastModified.offset.toJavaZoneOffset()
        )
    )
}

fun ImageFile.toImageFileDto(): ImageFileDto {
    return ImageFileDto(
        name = name,
        imagePath = getRelativeResourcePath(file),
        thumbnailPath = getThumbnailPath(file),
        apertureValue = apertureValue,
        exposureTime = exposureTime,
        exposureBias = exposureBias,
        isoEquivalent = isoEquivalent,
        focalLength = focalLength,
        make = make,
        model = model,
        lensModel = lensModel,
        lastModified = OffsetDateTime.ofInstant(
            lastModified.toInstant().toJavaInstant(),
            lastModified.offset.toJavaZoneOffset()
        )
    )
}

fun ImageFileEntity.toImageFile(): ImageFile {
    return ImageFile(
        id = id,
        file = File(file),
        name = name,
        apertureValue = apertureValue,
        exposureTime = exposureTime,
        exposureBias = exposureBias,
        isoEquivalent = isoEquivalent,
        focalLength = focalLength,
        make = make,
        model = model,
        lensModel = lensModel,
        lastModified = KmpOffsetDateTime(lastModified.toInstant().toEpochMilli(), lastModified.offset.toKotlinUtcOffset())
    )
}
