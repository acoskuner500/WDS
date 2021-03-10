package com.example.wds.utilities

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(rm: RemoteMessage) {
        super.onMessageReceived(rm)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}