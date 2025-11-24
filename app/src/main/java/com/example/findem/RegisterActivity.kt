package com.example.pratica_jp

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.findem.FindEmTheme
import com.example.findem.MainActivity
//import com.example.pratica_jp.ui.theme.Pratica_jpTheme
//import com.google.firebase.Firebase
//import com.google.firebase.auth.auth

//private val ERROR.isSuccessful: Boolean


class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FindEmTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterPage(
                        modifier = Modifier.padding(innerPadding)
                    )
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
    val activity = LocalActivity.current as Activity
    Column(
        modifier = modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bem-vindo/a!",
            fontSize = 24.sp
        )

        Spacer (modifier = modifier.size(24.dp))

        OutlinedTextField(
            value = nameUser,
            label = { Text(text = "Digite seu nome de usuário") },
            modifier = modifier.fillMaxWidth(fraction=0.9F),
            onValueChange = { nameUser = it }
        )

        OutlinedTextField(
            value = email,
            label = { Text(text = "Digite seu e-mail") },
            modifier = modifier.fillMaxWidth(fraction=0.9F),
            onValueChange = { email = it }
        )

        //Spacer (modifier = modifier.size(12.dp))

        OutlinedTextField(
            value = password,
            label = { Text(text = "Digite sua senha") },
            modifier = modifier.fillMaxWidth(fraction=0.9F),
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = passwordVerify,
            label = { Text(text = "Confirme sua senha") },
            modifier = modifier.fillMaxWidth(fraction=0.9F),
            onValueChange = { passwordVerify = it },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer (modifier = modifier.size(24.dp))

        Row(modifier = modifier) {
            Button( onClick = {
                activity.startActivity(
                Intent(activity, MainActivity::class.java).setFlags(
                    FLAG_ACTIVITY_SINGLE_TOP) )

                Toast.makeText(activity, "Registro Concluido!", Toast.LENGTH_LONG).show()

//                activity.finish()
            },
                enabled = (email.isNotEmpty() && nameUser.isNotEmpty() && password.isNotEmpty()) && password == passwordVerify
            ) {
                Text("Registrar")
            }

            Spacer (modifier = modifier.size(24.dp))

            Button(
                onClick = { email = ""; password = ""; nameUser = ""; passwordVerify = "" },
                enabled = email.isNotEmpty() || password.isNotEmpty() || nameUser.isNotEmpty()  || passwordVerify.isNotEmpty()
            ) {
                Text("Limpar")
            }
        }
    }
}