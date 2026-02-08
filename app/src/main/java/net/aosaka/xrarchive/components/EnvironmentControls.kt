package net.aosaka.xrarchive.components

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.platform.LocalSpatialCapabilities
import net.aosaka.xrarchive.R
import net.aosaka.xrarchive.ui.theme.XRArchiveTheme
import net.aosaka.xrarchive.environment.EnvironmentController

/**
 * Controls for changing the user's Environment, and toggling between Home Space and Full Space
 */
@Composable
fun EnvironmentControls(
    environmentController: EnvironmentController,
    modifier: Modifier = Modifier
) {
    // If we aren't able to access the session, these buttons wouldn't work and shouldn't be shown
    val activity = LocalActivity.current
    val session = LocalSession.current
    if (session != null && activity is ComponentActivity) {
        val uiIsSpatialized = LocalSpatialCapabilities.current.isSpatialUiEnabled
        //load the model early so it's in memory for when we need it
        val environmentModelName = "netzach.glb"
        val environment360ModelName = "netzach360.glb"
        environmentController.loadModelAsset(environmentModelName)
        environmentController.loadModelAsset(environment360ModelName)

        Surface(modifier.clip(CircleShape)) {
            Row(Modifier.width(IntrinsicSize.Min)) {
                if (uiIsSpatialized) {
                    SetVirtualEnvironmentButton(
                        is360 = false
                    ) {
                        environmentController.requestCustomEnvironment(
                            environmentModelName
                        )
                    }
                    SetVirtualEnvironmentButton(
                        is360 = true
                    ) {
                        environmentController.requestCustomEnvironment(
                            environment360ModelName
                        )
                    }
                    SetPassthroughButton { environmentController.requestPassthrough() }
                    VerticalDivider(
                        modifier = Modifier
                            .height(32.dp)
                            .align(Alignment.CenterVertically),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    RequestHomeSpaceButton { environmentController.requestHomeSpaceMode() }
                } else {
                    RequestFullSpaceButton { environmentController.requestFullSpaceMode() }
                }
            }
        }
    }
}

@Composable
private fun SetVirtualEnvironmentButton(
    is360: Boolean,
    modifier: Modifier = Modifier,
    onclick: () -> Unit
) {
    IconButton(
        onClick = onclick,
        modifier = modifier
            .padding(start = if (is360) 0.dp else 16.dp ,top = 16.dp, bottom = 16.dp, end = 16.dp)
            .background(MaterialTheme.colorScheme.onSecondary, CircleShape)
            .size(56.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.environment_24px),
            contentDescription = "set virtual environment",
            tint = if (is360) Color(0xFF00C853) else Color(0xFF5892F1)
        )
    }
}

@Composable
private fun SetPassthroughButton(
    modifier: Modifier = Modifier, onclick: () -> Unit
) {
    IconButton(
        onClick = onclick,
        modifier = modifier
            .padding(top = 16.dp, bottom = 16.dp, end = 16.dp)
            .background(MaterialTheme.colorScheme.onSecondary, CircleShape)
            .size(56.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.passthrough_24px),
            contentDescription = "set passthrough",
        )
    }
}

@Composable
private fun RequestHomeSpaceButton(onclick: () -> Unit) {
    IconButton(
        onClick = onclick,
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.onSecondary, CircleShape)
            .size(56.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_request_home_space),
            contentDescription = "enter home space mode"
        )
    }
}

@Composable
private fun RequestFullSpaceButton(onclick: () -> Unit) {
    IconButton(
        onClick = onclick, modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_request_full_space),
            contentDescription = "enter full space mode"
        )
    }
}

@Preview
@Composable
private fun PreviewSetVirtualEnvironmentButton() {
    XRArchiveTheme {
        SetVirtualEnvironmentButton(is360 = true) {}
    }
}

@Preview
@Composable
private fun PreviewRequestHomeSpaceButton() {
    XRArchiveTheme {
        RequestHomeSpaceButton {}
    }
}

@Preview
@Composable
private fun PreviewRequestFullSpaceButton() {
    XRArchiveTheme {
        RequestFullSpaceButton {}
    }
}

@Preview
@Composable
private fun PreviewSetPassthroughButton() {
    XRArchiveTheme {
        SetPassthroughButton {}
    }
}
