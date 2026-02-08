package net.aosaka.xrarchive.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.aosaka.xrarchive.ui.theme.XRArchiveTheme

@Composable
fun TextPane(text: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.fillMaxSize()) {
        Text(text = text, modifier = Modifier.padding(16.dp))
    }
}

@Composable
@Preview
private fun MyLayOutPreview() {
    XRArchiveTheme {
        TextPane(modifier = Modifier, text = "Primary")
    }
}