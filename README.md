# AndroidUtils
Android Development Helper Classes for improving code structure.

[<h4> Logger </h4>](https://github.com/sandeep9094/AndroidUtils/blob/master/Logger.kt)

Logger is Kotlin class for Android development which show logs only in debug mode.
In release mode app logs will not shown in usb debugging.

[<h4> EncryptedPreferences </h4>](https://github.com/sandeep9094/AndroidUtils/blob/master/EncryptedPreferences.kt)

Encrypted Preferences is Kotlin class for Android development which save sensitive data in key-value pair.
EncryptedSharedPreferences is an alternative for using SharedPreferences for sensitive data

[<h4> Webview Error Page </h4>](https://github.com/sandeep9094/AndroidUtils/blob/master/webview_error_page.html)

Android webview error page always shows error page with its url. We should always try to hide domain with some error page.
This is just a sample to load default html error page on webview

[<h4> ApiResult </h4>](https://github.com/sandeep9094/AndroidUtils/blob/master/ApiResult.kt)

ApiResult is common structure for all api responses in app. This is sealed class which helps to change success response
according to your data class
```sh
class MainViewModel : ViewModel() {
    private var response:MutableLiveData<ApiResult<User>> = MutableLiveData()

    private fun fetchData() {
        response.value = ApiResult.Loading
        val apiCall = ApiCall(object: ApiCall.Callback {
        		fun onSuccess() {
        			val user = User("Sandeep Kumar", 24)
					response.value = ApiResult.Success(user)
        		}
        		fun onFailed() {
        			response.value = ApiResult.Error("Internet is not working!")
        		}
        })
    }
}
```

[<h4> LoadingState </h4>](https://github.com/sandeep9094/AndroidUtils/blob/master/LoadingState.kt)

LoadingState is sealed class which is used to update data loading status on ui, user should be aware of data loading state.
LoadingState have three stages : Loading, Success and Error

<h4> Automate Build Name </h4>

Application build always have app-release.apk as default name, how much time we can save by write gradle script to auto rename every build file name.

```sh
android {
    buildTypes {
        debug {
            applicationIdSuffix '.debug'
        }

        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    android.applicationVariants.all { variant ->
        variant.outputs.each { output ->
            if (variant.buildType.name == "debug") {
                output.outputFileName = output.outputFileName
                        .replace(project.name, "MyApp")
                        .replace("-", "_")
                        .replace(".apk", "_v${variant.versionName}.apk")
            } else {
                output.outputFileName = output.outputFileName
                        .replace(project.name, "MyApp")
                        .replace("-" + variant.buildType.name, "")
                        .replace(".apk", "_v${variant.versionName}.apk")
            }

        }
    }

}
```
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
[<h4> Shared Preferences </h4>](https://github.com/sandeep9094/AndroidUtils/blob/master/SharedPrefs.kt)
 Shared Preferences allow you to save and retrieve data in the form of key,value pair.
 
```sh
class SharedPrefs(context: Context) {

    private val preferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    private val USER_NAME = "user_name"

    var userName: String
        get() = preferences.getString(USER_NAME, "") ?: ""
        set(value) = preferences.edit().putString(USER_NAME, value).apply()

}
```

Initialize (lazy) SharedPrefs instance in Application class, and later on we will access instance of SharedPrefs directly from application class.
```sh
val Prefs: SharedPrefs by lazy {
    MyApplicationClass.sharedPrefs
}

class MyApplicationClass : Application() {
    companion object {
        lateinit var sharedPrefs: SharedPrefs
    }
    override fun onCreate() {
        super.onCreate()
        sharedPrefs = SharedPrefs(this)
    }
}
```
How to Update/Fetch data from shared preferences class.
```sh
// Update value in sharedPrefs
Prefs.userName = "Sandeep Kumar"

// Fetch value from sharedPrefs
val userName = Prefs.userName
```
