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

```sh
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

```sh
interface ApiService {
    @GET("/path")
    fun getRandomActivity(): Call<ModelClass>
}
```
Network Connection Interceptor

```sh
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

```sh
class NoConnectivityException : IOException() {
    override val message: String
        get() = "No Internet Connection"
}
```

<h4> Retrofit Implementation </h4>

```sh

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
