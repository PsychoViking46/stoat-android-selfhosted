package chat.stoat.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import chat.stoat.persistence.KVStorage
import chat.stoat.persistence.connectToCustomInstance
import chat.stoat.persistence.currentCustomInstanceDomain
import chat.stoat.persistence.resetToOfficialInstance
import kotlinx.coroutines.launch

@Composable
fun CustomServerScreen(navController: NavController) {
    val context = LocalContext.current
    val kvStorage = remember { KVStorage(context) }
    val scope = rememberCoroutineScope()

    var domain by remember { mutableStateOf("") }
    var currentDomain by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        currentDomain = currentCustomInstanceDomain(kvStorage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .imePadding()
            .safeDrawingPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Connect to a self-hosted server",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxWidth()
            )

            Text(
                text = if (currentDomain != null) {
                    "Currently connected to: $currentDomain"
                } else {
                    "Currently connected to the official Stoat server"
                },
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .fillMaxWidth()
            )

            Column(
                modifier = Modifier.width(280.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = domain,
                    onValueChange = { domain = it },
                    label = { Text("Server address") },
                    placeholder = { Text("stoat.example.com") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                )

                if (error != null) {
                    Text(
                        text = error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(bottom = 10.dp))
                }

                Button(
                    onClick = {
                        error = null
                        isLoading = true
                        scope.launch {
                            try {
                                connectToCustomInstance(kvStorage, domain)
                                currentDomain = currentCustomInstanceDomain(kvStorage)
                                navController.popBackStack()
                            } catch (e: Exception) {
                                error = "Couldn't connect: ${e.message ?: "unknown error"}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading && domain.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Connect")
                }

                if (currentDomain != null) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                resetToOfficialInstance(kvStorage)
                                currentDomain = null
                                domain = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reset to official server")
                    }
                }
            }
        }

        Row {
            TextButton(onClick = { navController.popBackStack() }) {
                Text(text = "Back")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}
