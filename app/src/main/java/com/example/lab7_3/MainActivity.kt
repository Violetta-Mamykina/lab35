package com.example.lab7_3

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val MESSAGE_RESPONSE = "response"
    val URL_EXTRA = "url"
    val MSG_TO_CLIENT_OK = 1
    val MSG_TO_SERVICE = 2
    val URL = "https://usak.edu.tr/Images/uygulamalar/android.png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            val message = Message.obtain(null, MSG_TO_SERVICE, 0, 0).apply {
                replyTo = messenger
                data = Bundle().apply { putString(URL_EXTRA, URL) }
            }
            boundServiceMessenger?.send(message)
        }
    }


    private var boundServiceMessenger: Messenger? = null
    private var serviceConnected = false
    private val messenger = Messenger(ClientHandler())

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, DownloadService::class.java)
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (serviceConnected) {
            unbindService(serviceConnection)
            serviceConnected = false
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            boundServiceMessenger = null
            serviceConnected = false
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            boundServiceMessenger = Messenger(service)
            serviceConnected = true
        }
    }

    @SuppressLint("HandlerLeak")
    inner class ClientHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_TO_CLIENT_OK -> {
                    text.text = msg.data.getString(MESSAGE_RESPONSE)
                }
            }
        }
    }
}
