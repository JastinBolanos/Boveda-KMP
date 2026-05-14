package com.jastin.boveda

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform