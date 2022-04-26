package com.example.listit.model

import android.os.Parcelable
import com.example.listit.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    var id: String = "",
    var title: String = "",
    var status: Int = 0
) : Parcelable {
    init {
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }
}
