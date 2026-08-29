package com.surendra.suryanotes.ui.editor.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextColorPickerSheet(
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
){
    val controller = rememberColorPickerController()

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {

        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),

            controller = controller,

            onColorChanged = { envelope ->
                onColorSelected(envelope.color)
                onDismiss
            }
        )

        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .padding(horizontal = 24.dp),

            controller = controller
        )
    }
}