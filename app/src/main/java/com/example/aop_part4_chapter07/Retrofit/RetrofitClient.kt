package com.example.aop_part4_chapter07.Retrofit

import com.example.aop_part4_chapter07.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

	val getUnsplashApiService by lazy {
		getRetrofitClient().create(UnsplashService::class.java)
	}

	fun getRetrofitClient(): Retrofit =
		Retrofit.Builder()
			.baseUrl(URL.UNSPLASH_BASE_URL)
			.client(buildOkHttpClient())
			.addConverterFactory(GsonConverterFactory.create())
			.build()

	fun buildOkHttpClient() : OkHttpClient =
		OkHttpClient.Builder()
			.addInterceptor(HttpLoggingInterceptor().apply {
				level = if(BuildConfig.DEBUG) {
					HttpLoggingInterceptor.Level.BODY
				} else {
					HttpLoggingInterceptor.Level.NONE
				}
			}).build()
}