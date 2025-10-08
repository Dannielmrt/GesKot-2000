package com.example.pruebasmaterial3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.compose.backgroundDark
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold(
                    topBar = { MiTopAppBar() },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { /* acción */ }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Refrescar")
                        }
                    },
                    modifier = Modifier.fillMaxSize()

                ) { innerPadding ->
                    val ejemplo = MyData(
                        nombre = "Ana",
                        edad = "25",
                        ciudad = "Madrid"
                    )

                    // 👉 Se lo pasamos al composable
                    DataCard(
                        modifier = Modifier.padding(innerPadding),
                        data = ejemplo
                    )
                }
            }
        }
    }
}

data class MyData(
    val nombre: String,
    val edad: String,
    val ciudad: String
)

// Este Composable debe estar definido fuera de MainActivity
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiTopAppBar() {
    TopAppBar(
        title = { Text("Datos del CSV") },
        // 👇 ESTO GESTIONA LA BARRA DE ESTADO (SIN NECESIDAD DE OTRO ARGUMENTO MODIFIER)
        modifier = Modifier.windowInsetsPadding(
            WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
        ),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
    )
}

fun leerCsvComoData(url: String): List<MyData> {

    val Data = mutableListOf<MyData>()
    val conexion = URL(url).openConnection()
    BufferedReader(InputStreamReader(conexion.getInputStream())).use { reader ->
        val lineas = reader.readLines()
        for (i in 1 until lineas.size) {
            val columnas = lineas[i].split(",")
            if (columnas.size >= 3) {
                Data.add(
                    MyData(
                        nombre = columnas[0],
                        edad = columnas[1],
                        ciudad = columnas[2]
                    )
                )
            }
        }
    }
    return Data
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataCard(data: MyData, modifier: Modifier) {
    Column(
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = data.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Edad: ${data.edad}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Ciudad: ${data.ciudad}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}