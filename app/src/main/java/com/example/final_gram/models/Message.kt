package com.example.final_gram.models

class Message {
    var message:String?=null
    var date: String?=null

    var fromUid:String?=null
    var toUid:String?=null
    var rasm:String?=null


    constructor()
    constructor(message: String?, date: String?, fromUid: String?, toUid: String?, rasm: String?) {
        this.message = message
        this.date = date
        this.fromUid = fromUid
        this.toUid = toUid
        this.rasm = rasm
    }


}