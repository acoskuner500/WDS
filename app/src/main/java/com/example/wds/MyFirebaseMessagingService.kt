package com.example.wds

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(rm: RemoteMessage) {
        super.onMessageReceived(rm)
//        val title = rm.notification!!.title
//        val body = rm.notification!!.body
//        val imgSrc = rm.notification!!.imageUrl
//        println("DEBUG: Message payload: ${rm.data}")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}