package com.elegoo.robotcoder.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.elegoo.robotcoder.R
import com.elegoo.robotcoder.ui.components.AppTopBar
import com.elegoo.robotcoder.ui.components.PrimaryButton
import com.elegoo.robotcoder.ui.components.SecondaryButton
import com.elegoo.robotcoder.viewmodel.HomeViewModel

/**
 * Landing screen that introduces the app and routes users to the main areas.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToWorkspace: () -> Unit,
    onNavigateToConnect: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = { AppTopBar(title = "Home") },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_logo),
                contentDescription = "App logo placeholder",
                modifier = Modifier.size(96.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = uiState.appName,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = uiState.tagline,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Start Programming",
                onClick = onNavigateToWorkspace,
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = "Connect Rover",
                onClick = onNavigateToConnect,
            )

            Spacer(modifier = Modifier.height(12.dp))

            SecondaryButton(
                text = "Settings",
                onClick = onNavigateToSettings,
            )
        }
    }
}
