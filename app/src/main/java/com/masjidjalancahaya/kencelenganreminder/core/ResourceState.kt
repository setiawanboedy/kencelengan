package com.masjidjalancahaya.kencelenganreminder.core

sealed class ResourceState<T>(val data: T?, val message: String? = null) {
    class Success<T>(data: T) : ResourceState<T>(data)
    class Loading<T>(data: T? = null) : ResourceState<T>(data)
    class Error<T>(message: String, data: T? = null) : ResourceState<T>(data, message)
}
