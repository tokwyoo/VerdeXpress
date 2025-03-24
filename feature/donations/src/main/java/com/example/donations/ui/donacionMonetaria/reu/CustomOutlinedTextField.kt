package com.example.donations.ui.donacionMonetaria.reu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.design.R

@Composable
fun ErrorText(errorMessage: String) {
    Text(
        text = errorMessage,
        color = Color.Red,
        fontSize = 12.sp,
        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
    )
}
val verdeBoton = Color(0xFF78B153)
val roundedShape = RoundedCornerShape(12.dp)

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    errorMessage: String?,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    readOnly: Boolean = false,
    isNumberField: Boolean = false,
    showNumberControls: Boolean = false
) {
    Column {
        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = verdeBoton,
            focusedLabelColor = verdeBoton,
            cursorColor = verdeBoton,
            errorBorderColor = Color.Red,
            errorLabelColor = Color.Red,
            errorCursorColor = Color.Red
        )
        if (isNumberField && showNumberControls) {
            // Usar un Box para posicionar la etiqueta flotante
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = modifier) {
                val interactionSource = remember { MutableInteractionSource() }
                val isFocused by interactionSource.collectIsFocusedAsState()
                val isEmpty = value.isEmpty()
                var numeroValue by remember { mutableStateOf("") }
                var statusErrorN by remember { mutableStateOf<String?>(null) }
                // Row con borde para el campo y controles
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(
                            width = 1.dp,
                            color = if (isError) Color.Red else Color.Gray,
                            shape = roundedShape
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Campo de texto (parte izquierda)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = value,
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                                    onValueChange(newValue)
                                }
                            },
                            textStyle = LocalTextStyle.current.copy(
                                color = Color.Black,
                                fontSize = 16.sp
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 16.dp,
                                    top = if (isEmpty && !isFocused) 0.dp else 1.dp
                                ),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            readOnly = readOnly,
                            interactionSource = interactionSource
                        )
                        // Mostrar etiqueta dentro del campo solo si está vacío y no tiene focus
                        if (isEmpty && !isFocused) {
                            Text(
                                text = label,
                                color = Color.Gray,
                                fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                    // Primera línea vertical divisoria
                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(56.dp),
                        color = Color.Gray
                    )
                    // Botón de decremento
                    Box(
                        modifier = Modifier
                            .size(width = 56.dp, height = 56.dp)
                            .clickable {
                                val currentValue = value.toIntOrNull() ?: 0
                                if (currentValue > 1) { // No permitir decrementar por debajo de 1
                                    onValueChange((currentValue - 1).toString())
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "−",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                    // Segunda línea vertical divisoria
                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(56.dp),
                        color = Color.Gray
                    )
                    // Botón de incremento
                    Box(
                        modifier = Modifier
                            .size(width = 56.dp, height = 56.dp)
                            .clickable {
                                val currentValue = value.toIntOrNull() ?: 0
                                if (currentValue < 50000) { // No permitir incrementar más allá de 10
                                    onValueChange((currentValue + 1).toString())
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }
                // Etiqueta flotante (solo visible cuando está enfocado o tiene valor)
                if (isFocused || !isEmpty) {
                    val labelColor = Color.Gray
                    val labelTextSize = 13.sp
                    // Fondo para la etiqueta
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .offset(y = (-8).dp)
                            .background(
                                color = Color.White,
                                shape = RoundedCornerShape(4.dp)
                            )
                    ) {
                        Text(
                            text = label,
                            fontSize = labelTextSize,
                            color = labelColor,
                            fontFamily = FontFamily(Font(R.font.sf_pro_display_bold)),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        } else {
            // Versión original para otros campos
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = {
                    Text(
                        text = label,
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.sf_pro_display_bold))
                    )
                },
                isError = isError,
                trailingIcon = trailingIcon,
                shape = roundedShape,
                colors = textFieldColors,
                modifier = modifier.fillMaxWidth(),
                readOnly = readOnly,
                keyboardOptions = if (isNumberField) {
                    KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                } else {
                    KeyboardOptions.Default
                }
            )
        }
        errorMessage?.let { ErrorText(it) }
    }
}
