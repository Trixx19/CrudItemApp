package com.example.cruditemapp.data.repository

import com.example.cruditemapp.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.util.Log

class TaskRepository {
    private val db = FirebaseFirestore.getInstance()
    private val tasksCollection = db.collection("tasks")

    fun getTasks() = tasksCollection

    suspend fun addTask(task: Task) {
        try {
            val documentReference = tasksCollection.add(task).await()

            // pega o ID que o Firebase gerou e atualiza o documento,
            // preenchendo o campo 'id' com este valor.
            val taskId = documentReference.id
            tasksCollection.document(taskId).update("id", taskId).await()

        } catch (e: Exception) {
            Log.e("TaskRepository", "Erro ao adicionar tarefa e atualizar ID", e)
        }
    }

    suspend fun deleteTask(taskId: String) {
        tasksCollection.document(taskId).delete().await()
    }

    suspend fun updateTask(task: Task) {
        val taskId = task.id
        if (taskId != null) {
            tasksCollection.document(taskId).set(task).await()
        }
    }
}