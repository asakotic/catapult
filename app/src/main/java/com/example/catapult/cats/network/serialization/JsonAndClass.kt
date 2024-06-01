package com.example.catapult.cats.network.serialization;

import kotlinx.serialization.json.Json

val JsonAndClass = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}

