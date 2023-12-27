package com.example.modernplayground.data

import androidx.annotation.DrawableRes

/// Single User can have multiple accounts.
data class Account(
    val id: Long,
    val uid: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val altEmail: String,
    @DrawableRes val avatar: Int,
    val isCurrentAccount: Boolean = false
) {
    val fullName: String = "$firstName $lastName"
}