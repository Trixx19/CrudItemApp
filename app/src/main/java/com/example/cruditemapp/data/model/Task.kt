package com.example.cruditemapp.data.model

import com.google.firebase.firestore.DocumentId

data class Task(
    var id: String? = null,
    val title: String = "",
    val description: String = ""
)