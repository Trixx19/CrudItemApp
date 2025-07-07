package com.example.cruditemapp.ui.viewmodel


import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cruditemapp.data.model.Task // <- Altera para o teu pacote
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class TaskViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private var listenerRegistration: ListenerRegistration? = null

    var tasks = mutableStateOf<List<Task>>(emptyList())
        private set // O 'set' é privado para que só o ViewModel possa alterar a lista.

    init {
        startListeningForTasks()
    }


    private fun startListeningForTasks() {
        val tasksCollection = db.collection("tasks")


        listenerRegistration = tasksCollection.addSnapshotListener { snapshot, exception ->

            if (exception != null) {
                Log.w("TaskViewModel", "Erro ao ouvir as tarefas.", exception)
                return@addSnapshotListener
            }


            if (snapshot != null) {

                val fetchedTasks = snapshot.documents.map { document ->
                    document.toObject(Task::class.java)?.copy(id = document.id)
                }.filterNotNull() // Filtra qualquer resultado nulo que possa ocorrer.

                // Atualiza o estado com a nova lista de tarefas.
                tasks.value = fetchedTasks
            }
        }
    }

    fun addTask(task: Task) {
        db.collection("tasks").add(task)
            .addOnSuccessListener { Log.d("TaskViewModel", "Tarefa adicionada com sucesso!") }
            .addOnFailureListener { e -> Log.w("TaskViewModel", "Erro ao adicionar tarefa", e) }
    }


    fun deleteTask(taskId: String) {
        db.collection("tasks").document(taskId).delete()
            .addOnSuccessListener { Log.d("TaskViewModel", "Tarefa apagada com sucesso!") }
            .addOnFailureListener { e -> Log.w("TaskViewModel", "Erro ao apagar tarefa", e) }
    }

    fun updateTask(task: Task) {
        // Garantimos que a tarefa tem um ID antes de tentar atualizar.
        if (task.id != null) {
            db.collection("tasks").document(task.id)
                .set(task) // 'set' substitui o documento inteiro.
                .addOnSuccessListener { Log.d("TaskViewModel", "Tarefa atualizada com sucesso!") }
                .addOnFailureListener { e -> Log.w("TaskViewModel", "Erro ao atualizar tarefa", e) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove() // Remove o ouvinte.
        Log.d("TaskViewModel", "Listener removido.")
    }
}