package com.example.taskpagination.util

import android.util.Log
import com.google.firebase.inappmessaging.FirebaseInAppMessagingClickListener
import com.google.firebase.inappmessaging.model.Action
import com.google.firebase.inappmessaging.model.InAppMessage

class InAppMessageClickListener: FirebaseInAppMessagingClickListener {
    override fun messageClicked(inAppMessage: InAppMessage, action: Action) {
        // Determine which URL the user clicked
        val url = action.actionUrl
        // Get general information about the campaign
        val metadata = inAppMessage.campaignMetadata
        Log.e("AppTest", "URL is: " + url)
        Log.e("AppTest", "Meta Data: " + metadata)
    }
}