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
  ...
  defaultConfig {
    ...
    archivesBaseName = "${applicationId}_v${versionName}"
  }
}
```
Kotlin DSL Version
```sh
android {
  ...
  defaultConfig {
    ...
    setProperty("archivesBaseName", "${applicationId}_v${versionName}")
  }
}
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

# Contribute

Would you like to help with this project? Great! You don't have to be a developer, either. If you've found a bug or have an idea for an improvement, please open an issue and tell us about it.

If you are a developer wanting contribute an enhancement, bugfix or other patch to this project, please fork this repository and submit a pull request detailing your changes. We review all PRs!

This open source project is released under the MIT license which means if you would like to use this project's code in your own project you are free to do so. Speaking of, if you have used our code in a cool new project we would like to hear about it! Please send us an email.

