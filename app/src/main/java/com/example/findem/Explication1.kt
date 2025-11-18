package com.example.findem

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findem.ui.theme.FindEmTheme

class Explication1 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FindEmTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Explication1Page(
                        modifier = Modifier.padding(innerPadding),
                        onContinueClick = {
                            startActivity(Intent(this, TelaMain::class.java))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Explication1Page(
    modifier: Modifier = Modifier,
    onContinueClick: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "O que é o FindEm?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = """
            Este aplicativo foi pensado para você que perdeu seu animal ou que está pensando em adotar um!
            Nas próximas telas, você entenderá como tudo funciona!
            """.trimIndent(),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(top = 16.dp)
        )

        Button(
            modifier = Modifier.padding(top = 30.dp),
            onClick = onContinueClick
        ) {
            Text("Início")
        }
    }
}
