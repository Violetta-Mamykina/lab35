package com.example.lab7_3

import android.annotation.SuppressLint
import android.app.IntentService
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.util.Log
import java.io.FileOutputStream
import java.net.URL

class DownloadService : IntentService("DownloadService") {
    val MESSAGE_RESPONSE = "response"
    val URL_EXTRA = "url"
    val BROADCAST_TAG = "BROADCAST"
    val MESSAGE_EXTRA = "message"
    val MSG_TO_CLIENT_OK = 1
    val MSG_TO_SERVICE = 2
    private lateinit var messenger: Messenger

    override fun onBind(intent: Intent): IBinder? {
        messenger = Messenger(@SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_TO_SERVICE -> {
                        DownloadAsyncTask(msg.replyTo).execute(
                            msg.data.getString(URL_EXTRA)
                        )
                    }
                }
            }
        })
        return messenger.binder
    }


    override fun onHandleIntent(intent: Intent?) {
        val url = intent?.getStringExtra(URL_EXTRA)
        if (url == null)
            sendBroadcast("null url")
        else {
            val path = load(url)
            sendBroadcast(path)
        }
    }

    private fun load(url: String): String {
        return try {
            val bitmap = URL(url).openStream().use {
                return@use BitmapFactory.decodeStream(it)
            }
            saveImage(bitmap, (0..100).random().toString() + "_image")
        } catch (e: Exception) {
            print(e.message)
            ""
        }
    }

    private fun saveImage(b: Bitmap, imageName: String): String {
        val foStream: FileOutputStream
        try {
            foStream = this.applicationContext.openFileOutput(imageName, Context.MODE_PRIVATE)
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream)
            foStream.close()
        } catch (e: Exception) {
            Log.d("saveImage", " Something went wrong!")
            e.printStackTrace()
        }
        return applicationContext.getFileStreamPath(imageName).absolutePath
    }


    private fun sendBroadcast(msg: String) {
        sendBroadcast(Intent(BROADCAST_TAG).putExtra(MESSAGE_EXTRA, msg))
    }

    inner class DownloadAsyncTask(private val receiver: Messenger? = null) :
        AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg urls: String): String? {
            val url = urls[0]
            var str: String? = null
            try {
                str = load(url)
            } catch (e: Exception) {
                Log.e("Error", e.message)
            }
            return str
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val message = Message.obtain(null, MSG_TO_CLIENT_OK, 0, 0).apply {
                data = Bundle().apply { putString(MESSAGE_RESPONSE, result) }
            }
            receiver?.send(message)
        }
    }
}
