package com.example.myapplication.activity

import com.example.lib.RetrofitService
import com.example.lib.Service
import okhttp3.Call
import retrofit2.http.GET

@RetrofitService
interface DotaService : Service {

    @GET("https://api.opendota.com/api/heroes")
    fun getDotaHeroes(): Call

    @GET("https://api.opendota.com/api/status")
    fun getDotaMatches(): Call
}