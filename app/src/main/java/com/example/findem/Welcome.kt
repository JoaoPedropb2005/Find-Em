package com.example.findem

/*import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge*/
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
/*
import androidx.compose.material3.Scaffold
*/
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
/*
import androidx.compose.ui.text.font.FontFamily
*/
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
/*
import com.example.findem.ui.theme.FindEmTheme
*/

/*class Welcome : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FindEmTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Welcome(
                        modifier = Modifier.padding(innerPadding),
                        onContinueClick = {
                            startActivity(Intent(this, Explication1Page::class.java))
                        }
                    )
                }
            }
        }
    }
}*/

@Composable
fun Welcome(
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

        Image(
            painter = painterResource(id = R.drawable.findem_logo),
            contentDescription = "Logo Findem",
            modifier = modifier
                .padding(bottom = 24.dp)
                .size(150.dp)
        )

        Text(
            text = "Bem Vindo(a) ao FindEm!",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Button(
            modifier = Modifier.padding(top = 24.dp),
            onClick = onContinueClick
        ) {
            Text("Continuar")
        }
    }
}
