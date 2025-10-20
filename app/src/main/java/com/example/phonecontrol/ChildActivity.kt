package com.example.phonecontrol

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.messaging.FirebaseMessaging

class ChildActivity : ComponentActivity() {
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            requestFcmToken()
        } else {
            Toast.makeText(this, "알림 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }else{
            requestFcmToken()
        }
    }

    private fun requestFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }

            val token = task.result
            println("TOKEN:$token")
        }
    }
}