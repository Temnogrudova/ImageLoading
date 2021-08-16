package com.example.imageloading

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val IMAGE_URL = "https://rkpandey.com/images/rkpDavidson.jpg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(TAG, "Loading image from URL into the image view")
        val imageView = findViewById<ImageView>(R.id.imageView)
        // Glide.with(this).load(IMAGE_URL).into(imageView)
        // TODO: Download/display a remote image in your Android app without a library
        // Approach 1: work happens on main thread because without it will noy work network thread in main thread
//        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitNetwork().build())
//        val bitmap = downloadBitmap(IMAGE_URL)
//        imageView.setImageBitmap(bitmap)

        // Approach 2: create a thread to do the image download, then send bitmap to the main thread
//        val uiHandler = Handler(Looper.getMainLooper()) // to send messages between threads
//                                                          because a handler is associated with a message queue of main thread
//        thread(start = true) { // start=true is mean start work right away in NEW thread because UI thread is only 1
//            Log.i(TAG, "Current thread ${Thread.currentThread().name}")
//            val bitmap = downloadBitmap(IMAGE_URL)
//            uiHandler.post {
//                Log.i(TAG, "Current thread in the UI handler: ${Thread.currentThread().name}")
//                imageView.setImageBitmap(bitmap)
//            }
//        }

        // Approach 3: coroutines
        CoroutineScope(Dispatchers.IO).launch {
            Log.i(TAG, "Current thread ${Thread.currentThread().name}")
            val bitmap = downloadBitmap(IMAGE_URL)
            withContext(Dispatchers.Main) {
                Log.i(TAG, "Current thread in the main dispatcher: ${Thread.currentThread().name}")
                imageView.setImageBitmap(bitmap)
            }
        }
        // Extra library functionality:
        // 1. image caching
        // 2. downsampling
        // placeholder, transformations, etc
    }

    private fun downloadBitmap(imageUrl: String): Bitmap? {
        return try {
            val conn = URL(imageUrl).openConnection() // open url connection
            conn.connect()
            val inputStream = conn.getInputStream() //reading the bytes  at this remote resource
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close() //prevent leak of memory
            bitmap
        } catch (e: Exception) { //in case malformed url it leads to io-exception or unknown service exception
            Log.e(TAG, "Exception $e")
            null
        }
    }
}