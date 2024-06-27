package com.example.modernplayground.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

// Most web servers today run web services using a common stateless web architecture known as REST,
// which stands for REpresentational State Transfer. Web services that offer this architecture are
// known as RESTful services.
//
// Requests are made to RESTful web services in a standardized way, via Uniform Resource Identifiers
// (URIs). A URI identifies a resource in the server by name, without implying its location or
// how to access it.

// A URL (Uniform Resource Locator) is a subset of a URI that specifies where a resource exists
// and the mechanism for retrieving it.
//
// For example:
//
// The following URL gets a list of available real estate properties on Mars:
//
// https://android-kotlin-fun-mars-server.appspot.com/realestate
//
// The following URL gets a list of Mars photos:
//
// https://android-kotlin-fun-mars-server.appspot.com/photos
//
// These URLs refer to an identified resource, such as /realestate or /photos, that is obtainable
// via the Hypertext Transfer Protocol (http:) from the network. You are using the /photos
// endpoint in this codelab. An endpoint is a URL that allows you to access a web service running
// on a server.

// Each web service request contains a URI and is transferred to the server using the same HTTP
// protocol that's used by web browsers, like Chrome. HTTP requests contain an operation to tell
// the server what to do.
//
// Common HTTP operations include:
//
// GET for retrieving server data.
// POST for creating new data on the server.
// PUT for updating existing data on the server.
// DELETE for deleting data from the server.

// Retrofit creates a network API for the app based on the content from the web service. It
// fetches data from the web service and routes it through a separate converter library that
// knows how to decode the data and return it in the form of objects, like String. Retrofit
// includes built-in support for popular data formats, such as XML and JSON. Retrofit
// ultimately creates the code to call and consume this service for you, including critical
// details, such as running the requests on background threads.

private const val BASE_URL = "https://android-kotlin-fun-mars-server.appspot.com"

// Retrofit needs the base URI for the web service and a converter factory to build a web
// services API. The converter tells Retrofit what to do with the data it gets back from the
// web service. In this case, you want Retrofit to fetch a JSON response from the web service
// and return it as a String. Retrofit has a ScalarsConverter that supports strings and other
// primitive types.
private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface MarsApiService {
    @GET("photos")
    suspend fun getPhotos(): List<MarsPhoto>
}

// In Kotlin, object declarations are used to declare singleton objects. Singleton pattern
// ensures that one, and only one, instance of an object is created and has one global point
// of access to that object. Object initialization is thread-safe and done at first access.

// Warning: Singleton pattern is not a recommended practice. Singletons represent global states
// that are hard to predict, particularly in tests. Objects should define which dependencies
// they need, instead of describing how to create them.
//
// Use Dependency injection over singleton pattern

// The call to create() function on a Retrofit object is expensive in terms of memory, speed,
// and performance. The app needs only one instance of the Retrofit API service, so you expose
// the service to the rest of the app using object declaration.
object MarsApi {
    // Lazy initialization
    val retrofitService : MarsApiService by lazy {
        retrofit.create(MarsApiService::class.java)
    }
}