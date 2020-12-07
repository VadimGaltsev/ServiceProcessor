package com.example.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.lib.ServiceHolder
import okhttp3.Call
import okhttp3.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceHolder.getInstance().getService<DotaService>()!!.getDotaHeroes().enqueue(Callback)
        ServiceHolder.getInstance().getService<DotaService>()!!.getDotaMatches().enqueue(Callback)
        ServiceHolder.getInstance().getService<AnotherService>()!!.getDotaHeroes().enqueue(Callback)
        ServiceHolder.getInstance().getService<AnotherService>()!!.getDotaMatches().enqueue(Callback)
    }

    object Callback : okhttp3.Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.d("OTUS LESSON", "$e")
        }

        override fun onResponse(call: Call, response: Response) {
            Log.d("OTUS LESSON", "${response.body?.string()}")
        }
    }
}