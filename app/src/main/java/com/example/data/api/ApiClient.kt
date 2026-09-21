package com.example.data.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton providing Retrofit API clients with resilience and proper User-Agents.
 */
object ApiClient {

  private val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

  private val headerInterceptor = Interceptor { chain ->
    val original = chain.request()
    val request = original.newBuilder()
      .header("User-Agent", "BaseShieldScanner/1.0 (Android; ChainId=8453)")
      .header("Accept", "application/json")
      .build()
    chain.proceed(request)
  }

  private val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BASIC
  }

  private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(headerInterceptor)
    .addInterceptor(loggingInterceptor)
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .build()

  val goPlusApi: GoPlusApiService by lazy {
    Retrofit.Builder()
      .baseUrl("https://api.gopluslabs.io/")
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
      .create(GoPlusApiService::class.java)
  }

  val geckoTerminalApi: GeckoTerminalApiService by lazy {
    Retrofit.Builder()
      .baseUrl("https://api.geckoterminal.com/")
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
      .create(GeckoTerminalApiService::class.java)
  }

  val dexScreenerApi: DexScreenerApiService by lazy {
    Retrofit.Builder()
      .baseUrl("https://api.dexscreener.com/")
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
      .create(DexScreenerApiService::class.java)
  }
}
