package com.nothinglondon.sdkdemo

import android.os.Bundle
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Button
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nothinglondon.sdkdemo.ui.theme.NothingAndroidSDKDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NothingAndroidSDKDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GlyphSettingsButton(padding = innerPadding)
                }
            }
        }
    }
}

@Composable
fun GlyphSettingsButton(padding : PaddingValues) {
    val context = LocalContext.current

    Button(
        onClick = {
            gotoglyphsettings(context)
        }, contentPadding = padding
    ) {
        Text("Open Glyph Settings")
    }
}

fun gotoglyphsettings(context: Context){
    val intent = Intent().apply {
        component = ComponentName(
            "com.nothing.thirdparty",
            "com.nothing.thirdparty.matrix.toys.manager.ToysManagerActivity"
        )
    }
    context.startActivity(intent)

}