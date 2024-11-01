package org.wordpress.android.ui.reader.views.compose.dropdown

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.wordpress.android.ui.compose.theme.AppColor

object JetpackDropdownMenuColors {
    @Composable
    fun itemContentColor(isDarkTheme: Boolean = isSystemInDarkTheme()): Color = if (isDarkTheme) {
        AppColor.White
    } else {
        AppColor.Black
    }

    @Composable
    fun itemBackgroundColor(isDarkTheme: Boolean = isSystemInDarkTheme()): Color = if (isDarkTheme) {
        AppColor.DarkGray90
    } else {
        AppColor.White
    }

    @Composable
    fun itemDividerColor(isDarkTheme: Boolean = isSystemInDarkTheme()): Color = if (isDarkTheme) {
        AppColor.Gray50
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    }
}
