package com.ahuja.sons.apiservice


import android.content.ContentValues
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.ahuja.sons.globals.Global
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


class ApiClient {


    var okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Set the log level to BODY
        })
      //  .addNetworkInterceptor(provideCacheInterceptor()!!)
        //.cache(provideCache())
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()




  private val retrofit = Retrofit.Builder()
                    .baseUrl(Global.BASE_URL)
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()

    val service = retrofit.create(Apis::class.java)

  /*  companion object {
       const val BASE_URL = "http://localhost:8000/"
        fun create(): Apis {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(Apis::class.java)

        }
    }*/

    private val mContext: Context? = null
    private fun provideCache(): Cache? {
        var cache: Cache? = null
        try {
            cache = Cache(
                File(mContext!!.cacheDir, "http-cache"),
                10 * 1024 * 1024
            ) // 10 MB
        } catch (e: java.lang.Exception) {
            Log.e(ContentValues.TAG, "Could not create Cache!")
        }
        return cache
    }

    /************* Offline Work Manager  */
    val HEADER_CACHE_CONTROL = "Cache-Control"
    val HEADER_PRAGMA = "Cinntra"

    private fun provideCacheInterceptor(): Interceptor? {
        return Interceptor { chain: Interceptor.Chain ->
            val response = chain.proceed(chain.request())
            val cacheControl: CacheControl = if (isConnected()) {
                CacheControl.Builder()
                    .maxAge(0, TimeUnit.SECONDS)
                    .build()
            } else {
                CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()
            }
            response.newBuilder()
                .removeHeader(HEADER_PRAGMA)
                .removeHeader(HEADER_CACHE_CONTROL)
                .header(
                    HEADER_CACHE_CONTROL,
                    cacheControl.toString()
                )
                .build()
        }
    }

    private fun provideOfflineCacheInterceptor(): Interceptor? {
        return Interceptor { chain: Interceptor.Chain ->
            var request = chain.request()
            if (!isConnected()) {
                val cacheControl = CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()
                request = request.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .cacheControl(cacheControl)
                    .build()
            }
            chain.proceed(request)
        }
    }

    fun isConnected(): Boolean {
        try {
            val e = mContext!!.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            val activeNetwork = e.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        } catch (e: Exception) {
            Log.w(ContentValues.TAG, e.toString())
        }
        return false
    }


}
