package com.example.holatoastacomododecomponentes

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UIPrincipal()
        }
    }
}

@Composable
fun UIPrincipal() {
    val context = LocalContext.current
    var textoIngresado by remember { mutableStateOf("") }

    Text("¿Como te llamas?")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Agrega margen alrededor
        verticalArrangement = Arrangement.Center // Centra verticalmente
    ) {
        TextField(
            value = textoIngresado,
            onValueChange = { textoIngresado = it },
            label = { Text("Escribe tu nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = { Toast.makeText(context, "Hola $textoIngresado", Toast.LENGTH_SHORT).show()}) {
            Text("Saludo!")
        }
    }
}