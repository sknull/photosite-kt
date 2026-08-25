package de.visualdigits.photosite.presentation.components.photo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import de.visualdigits.common.presentation.components.button.TextButton
import de.visualdigits.photosite.domain.model.photosite.ImageFileUi
import de.visualdigits.photosite.presentation.components.Image
import de.visualdigits.photosite.presentation.model.PhotositeAction

@Composable
fun PhotoCard(
    image: ImageFileUi?,
    hostUrl: String?,
    onAction: (PhotositeAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TextButton(
            modifier = Modifier
                .width(220.dp)
                .height(80.dp),
            text = "Return",
            textColor = Color.White,
            buttonColor = Color.Gray,
            shape = RoundedCornerShape(4.dp),
        ) {
            onAction(PhotositeAction.OnReturnClicked())
        }

        if (image != null) {
            Image(
                modifier = Modifier,
                url = "$hostUrl/${image.imagePath}",
                contentDescription = image.name,
                contentScale = ContentScale.Inside,
                maxImageSize = 2400
            )
        }
    }
}
