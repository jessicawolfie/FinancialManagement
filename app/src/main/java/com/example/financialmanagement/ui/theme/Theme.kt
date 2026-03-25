package com.example.financialmanagement.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ColorScheme = lightColorScheme(
    primary = Verde,
    onPrimary = Branco,
    secondary = VerdeClaro,
    onSecondary = Branco,
    background = CinzaFundo,
    onBackground = Preto,
    surface = Branco,
    onSurface = Preto,
    error = Vermelho,
    onError = Branco
)

@Composable
fun ControleFinanceiroTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}