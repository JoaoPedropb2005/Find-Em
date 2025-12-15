package com.example.findem

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findem.ui.theme.FindEmTheme
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FindEmThemeCustom {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun RegisterPage(modifier: Modifier = Modifier) {
    var nameUser by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVerify by rememberSaveable { mutableStateOf("") }

    //val context = LocalContext.current
    val activity = LocalActivity.current as Activity

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
            modifier = //modifier
                //.padding(bottom = 10.dp)
                modifier.size(150.dp)
        )

        Text(
            text = "Bem-vindo/a!",
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.size(24.dp))

        OutlinedTextField(
            value = nameUser,
            label = { Text(text = "Digite seu nome de usuário") },
            modifier = Modifier.fillMaxWidth(fraction = 0.9F),
            onValueChange = { nameUser = it }
        )

        Spacer(modifier = Modifier.size(12.dp))

        OutlinedTextField(
            value = email,
            label = { Text(text = "Digite seu e-mail") },
            modifier = Modifier.fillMaxWidth(fraction = 0.9F),
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.size(12.dp))

        OutlinedTextField(
            value = password,
            label = { Text(text = "Digite sua senha") },
            modifier = Modifier.fillMaxWidth(fraction = 0.9F),
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.size(12.dp))

        OutlinedTextField(
            value = passwordVerify,
            label = { Text(text = "Confirme sua senha") },
            modifier = Modifier.fillMaxWidth(fraction = 0.9F),
            onValueChange = { passwordVerify = it },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.size(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = {
                    Firebase.auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(activity) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    activity,
                                    "Registro OK!", Toast.LENGTH_LONG
                                ).show()
                            } else {
                                Toast.makeText(
                                    activity,
                                    "Registro FALHOU!", Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                },
                enabled = email.isNotEmpty() && nameUser.isNotEmpty() && password.isNotEmpty() && passwordVerify.isNotEmpty()
            ) {
                Text("Registrar")
            }

            Button(
                onClick = {
                    email = ""
                    password = ""
                    nameUser = ""
                    passwordVerify = ""
                },
                enabled = email.isNotEmpty() || password.isNotEmpty() || nameUser.isNotEmpty() || passwordVerify.isNotEmpty()
            ) {
                Text("Limpar")
            }
        }
    }
}