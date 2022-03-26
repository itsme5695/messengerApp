package com.example.final_gram.models

import java.io.Serializable

class User: Serializable {
    var email: String? = null
    var displayName: String? = null
    var phoneNumber: String? = null
    var photoUrl: String? = null
    var uid: String? = null
    var online: Boolean? = null




    constructor()
    constructor(
        email: String?,
        displayName: String?,
        phoneNumber: String?,
        photoUrl: String?,
        uid: String?,
        online: Boolean?
    ) {
        this.email = email
        this.displayName = displayName
        this.phoneNumber = phoneNumber
        this.photoUrl = photoUrl
        this.uid = uid
        this.online = online
    }


}