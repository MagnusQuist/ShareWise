package com.sdu.sharewise.data.model

data class UserModel(
    val uuid: String,
    val name: String,
    val email: String,
    val phone: String? = null,
)