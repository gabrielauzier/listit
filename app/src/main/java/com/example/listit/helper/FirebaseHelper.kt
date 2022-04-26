package com.example.listit.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FirebaseHelper {

    companion object {

        fun getDatabase() = FirebaseDatabase.getInstance().reference

        private fun getAuth() = FirebaseAuth.getInstance()

        fun getIdUser() = getAuth().uid

        fun isAuthenticated() = getAuth().currentUser != null

        fun validError(error: String): Int {
            return when {
                error.contains("") -> {
                    1
                }
                else -> {
                    0
                }
            }
        }
    }
}