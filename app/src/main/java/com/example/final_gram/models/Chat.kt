package com.example.final_gram.models

class Chat {
    var sms:String?=null

    var fromUid:String?=null
    var toUid:String?=null

    constructor(sms: String?, fromUid: String?, toUid: String?) {
        this.sms = sms
        this.fromUid = fromUid
        this.toUid = toUid
    }

    constructor()


}