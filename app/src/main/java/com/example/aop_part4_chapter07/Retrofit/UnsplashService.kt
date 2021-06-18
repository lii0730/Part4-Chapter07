package com.example.aop_part4_chapter07.Retrofit

import com.example.aop_part4_chapter07.SearchResult.SearchResponseItem
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashService {

	@GET("photos/random/?count=30")
	suspend fun getImageList(
		@Query("client_id") client_id : String,
		@Query("query") searchKeyword : String?
	) : List<SearchResponseItem>
}