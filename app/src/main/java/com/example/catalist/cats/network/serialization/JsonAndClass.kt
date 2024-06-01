package com.example.catalist.cats.network.serialization;

import com.example.catalist.cats.domen.CatInfo
import kotlinx.serialization.json.Json

val JsonAndClass = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}

