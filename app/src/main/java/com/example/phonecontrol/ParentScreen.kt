package com.example.phonecontrol

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

@Composable
fun ParentScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val childToken =
        "fGMrhTB1BUfkgsX8PwN5LS0afg"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row {
            var text by remember { mutableStateOf("") }
            val focusManager = LocalFocusManager.current
            OutlinedTextField(modifier = Modifier.weight(1f), value = text, onValueChange = {text = it})
            Spacer(modifier = Modifier.padding(start = 5.dp))
            Button(onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    sendMessageToChild(
                        context,
                        childToken,
                        text
                    )

                    withContext(Dispatchers.Main) {
                        focusManager.clearFocus()
                        text = ""
                    }
                }

            }) {
                Text("전송")
            }
        }
    }
}

/**
 * GoogleCredentials에서 Access Token 얻기
 */
suspend fun getAccessToken(context: Context): String = withContext(Dispatchers.IO) {
    //  Firebase 서비스 계정 파일
    val inputStream =
        context.assets.open("service_account.json")
    //  Google 서버에 인증 요청을 보내고
    val credentials = GoogleCredentials.fromStream(inputStream)
        .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
    //  Google 인증 서버에 연결해서 새로운 Access Token을 발급
    credentials.refreshIfExpired()
    //  인증 토큰 객체에서 문자열 형태의 토큰 값을 꺼냄
    credentials.accessToken.tokenValue
}

const val TAG: String = "ParentScreen"

val httpClient: OkHttpClient by lazy {
    OkHttpClient.Builder().build()
}

fun sendMessageToChild(context: Context, childToken: String, message: String) {
    CoroutineScope(Dispatchers.IO).launch {
        val accessToken = getAccessToken(context)
        val projectId = "phonecontrol-5fa7e"

        val json = """
        {
          "message": {
            "token": "$childToken",
            "data": {
              "message": "$message"
            }
          }
        }
    """.trimIndent()

        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("https://fcm.googleapis.com/v1/projects/$projectId/messages:send")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "FCM 전송 실패", e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "FCM 전송 성공: ${response.body?.string()}")
            }
        })
    }
}