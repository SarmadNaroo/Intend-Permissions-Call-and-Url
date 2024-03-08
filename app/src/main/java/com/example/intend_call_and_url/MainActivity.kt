package com.example.intend_call_and_url

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.intend_call_and_url.ui.theme.Intend_Call_and_UrlTheme

class MainActivity : ComponentActivity() {
    companion object {
        private const val REQUEST_PHONE_CALL = 1
    }

    private var phoneNumber: String by mutableStateOf("")
    private var webUrl: String by mutableStateOf("")


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Intend_Call_and_UrlTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                colors = topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.primary,
                                ),
                                title = {
                                    Text("Connect Duo")
                                }
                            )
                        }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding), // Use the padding provided by the Scaffold. This ensures your content doesn't overlap with the top bar.
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CallUI(
                                phoneNumber = phoneNumber,
                                onPhoneNumberChange = { phoneNumber = it },
                                onCallButtonClick = {
                                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
                                    } else {
                                        makePhoneCall(phoneNumber)
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OpenBrowserUI(
                                urlText = webUrl,
                                onUrlChange = { webUrl = it },
                                onOpenBrowserClick = { openBrowserWithUrl(webUrl) }
                            )
                        }
                    }
                }
            }
        }
    }


    private fun makePhoneCall(number: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$number")
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PHONE_CALL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted. Make the call.
                makePhoneCall(phoneNumber)
            } else {
                // Permission was denied. Show a message to the user.
                Toast.makeText(this, "Permission denied to make a call", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openBrowserWithUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
        }
        startActivity(intent)
    }


}

@Composable
fun CallUI(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    onCallButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp)) // Adding some space at the top

        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = phoneNumber,
            onValueChange = onPhoneNumberChange,
            label = { Text("Enter Phone Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true // Ensuring the text field takes only a single line
        )

        Spacer(modifier = Modifier.height(16.dp)) // Adding space between TextField and Button

        Button(
            modifier = Modifier
                .fillMaxWidth(),
            onClick = onCallButtonClick
        ) {
            Text("Call")
        }

        Spacer(modifier = Modifier.height(16.dp)) // Adding some space at the bottom
    }
}

@Composable
fun OpenBrowserUI(
    urlText: String,
    onUrlChange: (String) -> Unit,
    onOpenBrowserClick: () -> Unit
) {
    Column(modifier = Modifier
        .padding(horizontal = 16.dp)
        .fillMaxWidth()
        .wrapContentHeight()
        .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp)) // Adding some space at the top

        TextField(
            value = urlText,
            onValueChange = onUrlChange,
            label = { Text("Enter URL") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onOpenBrowserClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Browser")
        }
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    Intend_Call_and_UrlTheme {
        Greeting("Android")
    }
}