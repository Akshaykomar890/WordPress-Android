package org.wordpress.android.ui.compose.components.menu

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.wordpress.android.ui.reader.views.compose.dropdown.JetpackMenuElementData
import org.wordpress.android.ui.reader.views.compose.dropdown.NO_ICON
import org.wordpress.android.ui.utils.UiString.UiStringText

class JetpackMenuElementDataTest {
    @Test
    fun `Single should have the correct leadingIcon default value`() {
        val actual = JetpackMenuElementData.Item.Single("id", UiStringText("")).leadingIcon
        val expected = NO_ICON
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `SubMenu should have the correct leadingIcon value`() {
        val actual = JetpackMenuElementData.Item.SubMenu("id", UiStringText(""), emptyList()).leadingIcon
        val expected = NO_ICON
        assertThat(actual).isEqualTo(expected)
    }
}
