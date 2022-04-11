package com.example.final_gram.retrofit

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("Content-type:application/json",
    "Authorization:key=AAAATilbn6s:APA91bEFjpsLmGe3O4memqCrUCZjwZQPnoo6TTumVDkqABhWTzKX-OCJeH_pLqd82kVYAZmnOzWUBqsH-OP7A0c9Bzo7gg-Y0vlk1yyDaUzWMc4gx1RTOn_oJ2SBUve-AJAmG1VDPaSg")
    @POST("fcm/send")
    fun sendNotification(@Body sender: Sender):Call<MyResponce>
}