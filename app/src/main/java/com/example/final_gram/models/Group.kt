package com.example.final_gram.models

import java.io.Serializable

class Group :Serializable{

    var gr_name:String?=null
    var users: ArrayList<User>? = null



    constructor(gr_name: String?, users: ArrayList<User>?) {
        this.gr_name = gr_name
        this.users = users
    }

    constructor()
}