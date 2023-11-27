package com.sdu.sharewise.data.model

data class User(
    val uuid: String,
    val name: String,
    val email: String,
    val phone: String? = null,
)  {
    // Secondary constructor with no-argument default values
    constructor() : this("", "", "", "")
}