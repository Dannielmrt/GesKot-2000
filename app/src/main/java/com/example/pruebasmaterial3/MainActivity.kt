package com.example.pruebasmaterial3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.example.compose.backgroundDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

                    ListaDataScreen(innerPadding)
                }
            }
        }
    }
}

data class MyData(
    val Direccion: String,
    val Numero: String,
    val Activo: String,
    val Bicis_disponibles: String,
    val Espacios_libres: String,
    val Espacios_totales: String,
    val lastUpdate: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiTopAppBar() {
    TopAppBar(
        title = { Text("Datos del CSV") },

        modifier = Modifier.windowInsetsPadding(
            WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
        ),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataCard(data: MyData, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = data.Direccion,
                    style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "Bicis disponibles: ${data.Bicis_disponibles}",
                    style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Anclajes libres: ${data.Espacios_libres}",
                    style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Última actualización: ${data.lastUpdate}",
                    style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun ListaDataScreen(innerPadding: PaddingValues) {
    var datos by remember { mutableStateOf<List<MyData>>(emptyList()) } // <- importante: tipado explícito
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val url = "https://valencia.opendatasoft.com/explore/dataset/valenbisi-disponibilitat-valenbisi-dsiponibilidad/download/?format=csv"
        datos = withContext(Dispatchers.IO) { leerCsvComoData(url) } // leerCsvComoData devuelve List<MyData>
        cargando = false
    }

    if (cargando) {
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Cargando datos...")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // items(items = datos) { item: MyData -> ... } <- pongo el tipo explícito para evitar ambigüedad
            items(items = datos, key = { it.Direccion }) { item: MyData ->
                DataCard(data = item)
            }
        }
    }
}

fun leerCsvComoData(url: String): List<MyData> {
    val data = mutableListOf<MyData>()
    val conexion = URL(url).openConnection()
    BufferedReader(InputStreamReader(conexion.getInputStream())).use { reader ->
        val lineas = reader.readLines()
        for (i in 1 until lineas.size) { // salto cabecera
            val columnas = lineas[i].split(";") // OJO: si no funciona, prueba con ","
            if (columnas.size >= 8) { // el CSV de opendatasoft tiene 8 columnas en el ejemplo
                data.add(
                    MyData(
                        Direccion = columnas[0],
                        Numero = columnas[1],
                        Activo = columnas[2],
                        Bicis_disponibles = columnas[3],
                        Espacios_libres = columnas[4],
                        Espacios_totales = columnas[5],
                        lastUpdate = columnas[7]
                    )
                )
            }
        }
    }
    return data
}