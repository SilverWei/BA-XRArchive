package net.aosaka.xrarchive

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import net.aosaka.xrarchive.ui.theme.XRArchiveTheme

class MainActivity : ComponentActivity() {

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XRArchiveTheme {
                XRArchiveApp()
            }
        }
    }
}