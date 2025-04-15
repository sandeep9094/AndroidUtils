[<h4> Retrofit </h4>](https://github.com/sandeep9094/AndroidUtils/tree/master/retrofit)

A type-safe HTTP client for Android and Java.
```sh
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:okhttp:4.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.4.1'
implementation 'com.squareup.okhttp3:okhttp-urlconnection:4.4.1'
```

Below are the classes required for Working with Retrofit
1. RetrofitInstance
2. ApiService
3. Network Connection Interceptor
4. No Internet Connection Exception Handling

RetrofitInstance

```kotlin
object RetrofitManager {

    const val BASE_URL = ""

    fun getApiService(context: Context): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getOkHttpClient(context))
            .build()
        return retrofit.create(ApiService::class.java)
    }

    private fun getOkHttpClient(context: Context): OkHttpClient {
        val logging = HttpLoggingInterceptor() // set your desired log level
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BASIC
        } else {
            logging.level = HttpLoggingInterceptor.Level.NONE
        }
        val okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(NetworkConnectionInterceptor(context))
            .addInterceptor(logging)
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
        return okHttpClientBuilder.build()
    }

}
```
ApiService

```kotlin
interface ApiService {
    @GET("/path")
    fun getRandomActivity(): Call<ModelClass>
}
```
Network Connection Interceptor

```kotlin
class NetworkConnectionInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected()) {
            throw NoConnectivityException()
        }
        val builder: Request.Builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

}
```
No Internet Connectivity Exception

```kotlin
class NoConnectivityException : IOException() {
    override val message: String
        get() = "No Internet Connection"
}
```

<h4> Retrofit Implementation </h4>

```kotlin

val apiService = RetrofitManager.getApiService(this)
        val call = apiService.getRandomActivity()
        call.enqueue(object : Callback<ModelClass> {
            override fun onResponse(call: Call<ModelClass>, response: Response<ModelClass>) {
                Log.d("MainActivity", "Response : ${response.body()}")
            }

            override fun onFailure(call: Call<ModelClass>, t: Throwable) {
                if (t is NoConnectivityException) {
                    Log.d("MainActivity", "onFailure : ${t.localizedMessage}")
                }
            }

        })
	
```


# ✅ Safe API Call with Retrofit in Kotlin

This guide demonstrates how to safely call APIs using `Retrofit` with a reusable `safeApiCall` function that wraps the response in a sealed result class and gracefully handles exceptions.

`safeApiCall` is a suspend function that wraps API calls in a safe block to handle:

- Null response bodies
- HTTP error codes like `401`, `500`, etc.
- Network exceptions like `SocketTimeoutException` and `IOException`



##  Usage

### 1. Create the `safeApiCall` Function

```kotlin
suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<T>
): ApiResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResult.Success(body)
            } else {
                ApiResult.Error("Response body is null")
            }
        } else {
            when (response.code()) {
                401 -> ApiResult.Error("Unauthorized: Access is denied due to invalid credentials")
                500 -> ApiResult.Error("Internal Server Error: The server encountered an error and could not complete your request.")
                else -> ApiResult.Error("Error ${response.code()}: ${response.message()}")
            }
        }
    } catch (e: Exception) {
        ApiResult.Error(
            when (e) {
                is HttpException -> {
                    val errorBody = e.response()?.errorBody()?.string()
                    "HTTP Error: ${e.code()} - ${e.message()} - $errorBody"
                }
                is SocketTimeoutException -> "Timeout Error: Request timed out"
                is IOException -> "Network Error: ${e.localizedMessage ?: "Unknown network error"}"
                else -> "Unknown Error: ${e.localizedMessage ?: "An unknown error occurred"}"
            }
        )
    }
}
```



### 2. Define Your Retrofit `ApiService`
Return type should be Response instead of Call

```kotlin
interface ApiService {
    @GET("users")
    suspend fun getUsers(): Response<List<User>>
}
```


## 3. Create Flow Result in ViewModel or Repository Class

```kotlin
class UserViewModel(private val apiService: ApiService) : ViewModel() {

    private val _userResult = MutableStateFlow<ApiResult<List<User>>?>(null)
    val userResult: StateFlow<ApiResult<List<User>>?> = _userResult

    fun fetchUsers() {
        _userResult.value = ApiResult.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val result = safeApiCall { apiService.getUsers() }
            _userResult.value = result
        }
    }
}
```


## 3. Activity or Fragment Usage

```kotlin
override fun onStart() {
    super.onStart()

    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.userResult.filterNotNull().collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        Toast.makeText(this@MainActivity, "Loading users...", Toast.LENGTH_SHORT).show()
                    }
                    is ApiResult.Success -> {
                        val users = result.data
                        Toast.makeText(this@MainActivity, "Fetched ${users.size} users", Toast.LENGTH_SHORT).show()
                    }
                    is ApiResult.Error -> {
                        Toast.makeText(this@MainActivity, result.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
```

