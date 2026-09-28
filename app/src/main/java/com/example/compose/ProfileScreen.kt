package com.example.compose

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────
// 1) Composable con ESTADO (stateful)
//    Guarda los datos y maneja la lógica.
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    // rememberSaveable sobrevive a rotaciones de pantalla y cambios de configuración
    var savedName by rememberSaveable { mutableStateOf("Nicole García") }
    var draftName by rememberSaveable { mutableStateOf(savedName) } // borrador mientras se edita
    var isEditing by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isNameValid = draftName.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil de Usuario") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        ProfileContent(
            modifier = Modifier.padding(paddingValues),
            displayName = savedName,
            draftName = draftName,
            isEditing = isEditing,
            isNameValid = isNameValid,
            onDraftChange = { draftName = it },
            onEdit = {
                draftName = savedName // el borrador parte del valor guardado
                isEditing = true
            },
            onSave = {
                if (isNameValid) {
                    savedName = draftName.trim()
                    isEditing = false
                    scope.launch { snackbarHostState.showSnackbar("Perfil actualizado") }
                }
            },
            onCancel = {
                draftName = savedName // descarta los cambios
                isEditing = false
            }
        )
    }
}

// ─────────────────────────────────────────────
// 2) Composable SIN estado (stateless)
//    Solo dibuja lo que recibe → fácil de probar y previsualizar.
// ─────────────────────────────────────────────
@Composable
fun ProfileContent(
    displayName: String,
    draftName: String,
    isEditing: Boolean,
    isNameValid: Boolean,
    onDraftChange: (String) -> Unit,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // evita cortes en pantallas pequeñas o landscape
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Avatar circular
        Image(
            painter = painterResource(id = R.drawable.avatar_placeholder),
            contentDescription = "Foto de perfil de $displayName",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        // Nombre: transición suave entre modo lectura y edición
        Crossfade(targetState = isEditing, label = "nameEditing") { editing ->
            if (editing) {
                OutlinedTextField(
                    value = draftName,
                    onValueChange = onDraftChange,
                    label = { Text("Nombre") },
                    singleLine = true,
                    isError = !isNameValid,
                    supportingText = {
                        if (!isNameValid) Text("El nombre no puede estar vacío")
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onSave() })
                )
            } else {
                Text(
                    text = displayName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Información adicional dentro de una tarjeta
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileInfoRow(label = "Ubicación", value = "Mejicanos, San Salvador")
                HorizontalDivider() // si usas Material3 antiguo (<1.2), cámbialo por Divider()
                ProfileInfoRow(label = "Profesión", value = "Estudiante de Computación")
            }
        }

        // Botones de acción
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = if (isEditing) onSave else onEdit,
                enabled = !isEditing || isNameValid // no deja guardar un nombre vacío
            ) {
                Text(if (isEditing) "Guardar" else "Editar")
            }
            OutlinedButton(
                onClick = onCancel,
                enabled = isEditing // solo tiene sentido cancelar mientras se edita
            ) {
                Text("Cancelar")
            }
        }
    }
}

// ─────────────────────────────────────────────
// 3) Composable reutilizable: fila de información
// ─────────────────────────────────────────────
@Composable
fun ProfileInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            // weight + padding: si el texto es largo, salta de línea en vez de romper el diseño
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        )
    }
}

// ─────────────────────────────────────────────
// 4) Previews para ver los dos modos sin ejecutar la app
// ─────────────────────────────────────────────
@Preview(showBackground = true, name = "Lectura")
@Composable
private fun ProfilePreviewReading() {
    MaterialTheme {
        ProfileContent(
            displayName = "Nicole García",
            draftName = "Nicole García",
            isEditing = false,
            isNameValid = true,
            onDraftChange = {}, onEdit = {}, onSave = {}, onCancel = {}
        )
    }
}

@Preview(showBackground = true, name = "Edición")
@Composable
private fun ProfilePreviewEditing() {
    MaterialTheme {
        ProfileContent(
            displayName = "Nicole García",
            draftName = "",
            isEditing = true,
            isNameValid = false,
            onDraftChange = {}, onEdit = {}, onSave = {}, onCancel = {}
        )
    }
}