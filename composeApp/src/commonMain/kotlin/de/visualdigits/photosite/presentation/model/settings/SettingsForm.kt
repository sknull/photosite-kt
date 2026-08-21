package de.visualdigits.photosite.presentation.model.settings

import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration
import de.visualdigits.common.domain.model.configuration.EnumFieldDescriptor
import de.visualdigits.common.domain.model.configuration.StringFieldDescriptor
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.label_hostUrl
import de.visualdigits.compose.resources.label_language
import de.visualdigits.compose.resources.tooltip_hostUrl
import de.visualdigits.compose.resources.tooltip_language
import de.visualdigits.photosite.domain.model.type.Language

class SettingsForm(
    values: Map<SK, Any?> = mapOf(),
): AbstractConfiguration<SettingsForm, SK>(values, DESCRIPTORS) {

    companion object {
        val DESCRIPTORS = listOf(

            /** The UI language. */
            EnumFieldDescriptor(
                fieldClass = Language::class,
                key = SK.language,
                label = UiText.StringResourceId(Res.string.label_language),
                toolTip =  UiText.StringResourceId(Res.string.tooltip_language),
                options = { _, _ -> Language.options },
                keyFactory = Language,
                default = Language.EN
            ),

            StringFieldDescriptor(
                key = SK.hostUrl,
                label = UiText.StringResourceId(Res.string.label_hostUrl),
                toolTip = UiText.StringResourceId(Res.string.tooltip_hostUrl),
                valid = { _, value ->
                    if ((value as? String)?.isNotBlank() == true) Severity.Info else Severity.Error
                }
            ),
        )
    }

    override fun createInstance(newValues: Map<SK, Any?>): SettingsForm {
        return SettingsForm(newValues)
    }
}
