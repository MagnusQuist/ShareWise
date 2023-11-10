package com.sdu.sharewise.model

interface IUser {
    fun getEmail(): String?
    fun getUsername(): String?
    fun getGroups(): String?
}