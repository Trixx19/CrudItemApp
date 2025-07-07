package com.example.cruditemapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cruditemapp.data.model.Task
import com.example.cruditemapp.ui.viewmodel.TaskViewModel // <- Altera para o teu pacote

@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = viewModel()
) {
    val tasks by viewModel.tasks
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        // Campo de entrada para o título.
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título da Tarefa") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Campo de entrada para a descrição.
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Botão para adicionar uma nova tarefa.
        Button(
            onClick = {
                if (title.isNotEmpty() && description.isNotEmpty()) {
                    // Chama a função do ViewModel para adicionar a tarefa.
                    viewModel.addTask(Task(title = title, description = description))
                    // Limpa os campos de texto após adicionar.
                    title = ""
                    description = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar Tarefa")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de tarefas que ocupa o resto do ecrã.
        LazyColumn {
            // 'items' é uma função otimizada para listas no LazyColumn.
            items(tasks) { task ->
                TaskCard(
                    task = task,
                    onDelete = { viewModel.deleteTask(task.id!!) },
                    onUpdate = {
                        selectedTask = task
                        showDialog = true
                    }
                )
            }
        }
    }

    // Se 'showDialog' for verdadeiro, mostra o diálogo de edição.
    if (showDialog) {
        UpdateTaskDialog(
            task = selectedTask,
            onDismiss = { showDialog = false },
            onUpdate = { updatedTask ->
                viewModel.updateTask(updatedTask)
                showDialog = false
            }
        )
    }
}

/**
 * Um cartão para exibir uma única tarefa.
 */
@Composable
fun TaskCard(
    task: Task,
    onDelete: () -> Unit,
    onUpdate: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = task.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onDelete) {
                    Text("Apagar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onUpdate) {
                    Text("Editar")
                }
            }
        }
    }
}


/**
 * O diálogo que aparece para editar uma tarefa.
 */
@Composable
fun UpdateTaskDialog(
    task: Task?,
    onDismiss: () -> Unit,
    onUpdate: (Task) -> Unit
) {
    if (task == null) return

    // Estados para os campos de texto do diálogo.
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Tarefa") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição") },
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                // Cria uma cópia da tarefa com os novos dados e chama a função de update.
                onUpdate(task.copy(title = title, description = description))
            }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}