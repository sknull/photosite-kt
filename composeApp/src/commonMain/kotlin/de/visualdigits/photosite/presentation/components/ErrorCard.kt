package de.visualdigits.photosite.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Severity
import de.visualdigits.common.domain.model.ui.UiText
import de.visualdigits.common.domain.util.color


@Composable
fun ErrorCard(
    modifier: Modifier = Modifier,
    errorMessage: UiText?,
    severity: Severity? = Severity.Error,
    shapeContainer: Shape,
    space: Dp = 8.dp
) {
    if (errorMessage != null) {
        val color = severity?.color()?:Severity.Error.color()
        Box(
            modifier = modifier
                .clip(shapeContainer)
                .fillMaxWidth()
                .border(width = 1.dp, color = color, shape = shapeContainer)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
                .padding(top = space)
        ) {
            Text(
                text = errorMessage.asString(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                color = color,
                modifier = Modifier
                    .padding(16.dp)
            )
        }

        Spacer(Modifier.height(space).fillMaxWidth())
    }
}
