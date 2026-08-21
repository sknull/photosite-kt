package de.visualdigits.photosite.presentation.components.form

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.visualdigits.common.domain.model.platform.PlatformType
import de.visualdigits.common.presentation.components.form.ConfigurationEditForm
import de.visualdigits.compose.resources.Res
import de.visualdigits.compose.resources.title_settings
import de.visualdigits.photosite.presentation.model.PhotositeAction
import de.visualdigits.photosite.presentation.model.settings.SettingsForm
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsForm(
    platformType: PlatformType,
    settings: SettingsForm?,
    onAction: (PhotositeAction) -> Unit
) {
    settings?.let { s ->
        ConfigurationEditForm(
            platformType = platformType,
            headerContent = {
                Text(
                    text = stringResource(Res.string.title_settings),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(20.dp))
            },
            configuration = s,
            scrollbarModifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .width(10.dp)
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
            onValueChange = { keyValue ->
                onAction(
                    PhotositeAction.OnSettingsValueChanged(
                        keyValue = keyValue
                    )
                )
            },
            onCancelClick = {
                onAction(
                    PhotositeAction.OnEditSettingsCancelClick()
                )
            },
            onOkClick = {
                onAction(
                    PhotositeAction.OnSaveSettingsClick()
                )
            },
        )
    }
}
