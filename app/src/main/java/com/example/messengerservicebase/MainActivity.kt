package com.example.messengerservicebase

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import android.view.Menu
import android.view.MenuItem

import android.content.*
import android.os.*
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var imagePathReceiver: BroadcastReceiver
    private var boundServiceMessenger: Messenger? = null
    private var isBound = false
    private val messenger = Messenger(ClientHandler(this))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindService(Intent(this, ImageService::class.java), serviceConnection, BIND_AUTO_CREATE)
        imagePathReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, intent: Intent?) {
                findViewById<TextView>(R.id.textLoad).text =
                    intent?.getStringExtra("message") ?: "No path =("
            }
        }
        registerReceiver(imagePathReceiver, IntentFilter("broadcastImagePath"))
    }

    fun onBindClick(view: View) {
        val message = Message.obtain(null, 1).apply {
            replyTo = messenger
            data = Bundle().apply {
                putString(
                    "link",
                    "https://img.icons8.com/ios/452/service.png"
                )
            }
        }
        boundServiceMessenger?.send(message)
    }

    fun onStartClick(view: View) {
        startService(
            Intent(this, ImageService::class.java).putExtra(
                "link",
                "https://img.icons8.com/ios/452/service.png"
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        unregisterReceiver(imagePathReceiver)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            boundServiceMessenger = Messenger(service)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            boundServiceMessenger = null
            isBound = false
        }
    }
}

private class ClientHandler(val context: MainActivity) : Handler() {
    override fun handleMessage(message: Message) {
        when (message.what) {
            2 -> {
                context.findViewById<TextView>(R.id.textLoad).text =
                    message.data.getString(
                        "response",
                        "https://static.thenounproject.com/png/409652-200.png"
                    )
            }
        }
    }
}