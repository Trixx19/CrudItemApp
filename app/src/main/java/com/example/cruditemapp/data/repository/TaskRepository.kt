package com.example.cruditemapp.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class TaskRepository {
    private val db = Firebase.firestore

    suspend fun addTask(task: Task) {
        db.collection("tasks").add(task).await()
    }
}