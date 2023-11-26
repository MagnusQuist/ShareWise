package com.sdu.sharewise.data.model

data class Group(
    val groupUid: String,
    val name: String,
    val desc: String,
    val color: String,
    val ownerUid: String,
    val members: MutableList<String?> = mutableListOf(),
) {
    // Secondary constructor with no-argument default values
    constructor() : this("", "", "", "", "", mutableListOf())
}