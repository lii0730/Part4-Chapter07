package com.example.aop_part4_chapter07.SearchResult


import com.google.gson.annotations.SerializedName

data class Position(
    @SerializedName("latitude")
    val latitude: Any?,
    @SerializedName("longitude")
    val longitude: Any?
)