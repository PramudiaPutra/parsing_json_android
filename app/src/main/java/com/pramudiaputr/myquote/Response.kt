package com.pramudiaputr.myquote

import com.squareup.moshi.Json

data class Response(
    val id: String,
    @Json(name = "en")
    val quote: String,
    val author: String,
)