package lex.utils.net.retrofit

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.concurrent.TimeUnit

class NetClient private constructor() {
    private val retrofit: Retrofit
    val apiService: ApiService

    init {
        val okHttp = createOkHttp()
        retrofit = createRetrofit(okHttp)
        apiService = retrofit.create(ApiService::class.java)
    }

    private fun createOkHttp(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor { message ->
            try {
                val text = URLDecoder.decode(message, "utf-8")
                Log.i("HTTP", "OKHttp-----$text")
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                Log.i("HTTP", "OKHttp-----$message")
            }
        }
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            //                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
            .connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .addInterceptor(interceptor)
//            .authenticator(JavaNetAuthenticator())
            .retryOnConnectionFailure(true)
            .build()
    }

    private fun createRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
            .build()
    }

    /**
     * create you ApiService
     * Create an implementation of the API endpoints defined by the `service` interface.
     */
    fun <T> create(service: Class<T>?): T {
        if (service == null) {
            throw RuntimeException("Api service is null!")
        }
        return retrofit.create(service)
    }

    companion object {
        private const val DEFAULT_TIMEOUT = 10

        private const val BASE_URL = "https://api.openai.com/"

        val instance by lazy {
            NetClient()
        }
    }


}