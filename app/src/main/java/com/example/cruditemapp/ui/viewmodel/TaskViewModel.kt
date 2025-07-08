package com.example.cruditemapp.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cruditemapp.data.model.Task
import com.example.cruditemapp.data.repository.TaskRepository
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {

    private val repository = TaskRepository()
    private var listenerRegistration: ListenerRegistration? = null

    var tasks = mutableStateOf<List<Task>>(emptyList())
        private set

    init {
        startListeningForTasks()
    }

    private fun startListeningForTasks() {
        listenerRegistration = repository.getTasks().addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.w("TaskViewModel", "Erro ao ouvir as tarefas.", exception)
                return@addSnapshotListener
            }

            if (snapshot != null) {

                val fetchedTasks = snapshot.documents.map { document ->
                    val task = document.toObject(Task::class.java)
                    task?.id = document.id
                    task
                }.filterNotNull()

                tasks.value = fetchedTasks
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                repository.addTask(task)
                Log.d("TaskViewModel", "Tarefa adicionada com sucesso!")
            } catch (e: Exception) {
                Log.w("TaskViewModel", "Erro ao adicionar tarefa", e)
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                repository.deleteTask(taskId)
                Log.d("TaskViewModel", "Tarefa apagada com sucesso!")
            } catch (e: Exception) {
                Log.w("TaskViewModel", "Erro ao apagar tarefa", e)
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                repository.updateTask(task)
                Log.d("TaskViewModel", "Tarefa atualizada com sucesso!")
            } catch (e: Exception) {
                Log.w("TaskViewModel", "Erro ao atualizar tarefa", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
        Log.d("TaskViewModel", "Listener removido.")
    }
}