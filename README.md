# AndroidUtils
Android Development Helper Classes for improving code structure.

[<h4> Logger </h4>](https://github.com/sandeep9094/AndroidUtils/blob/master/Logger.kt)

Logger is Kotlin class for Android development which show logs only in debug mode.
In release mode app logs will not shown in usb debugging.


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

<h4>PDF File Picker </h4>
Pdf file selector and viewer
 
Add permission in manifest.xml and enable legeacy external storage
```sh
<manifest>
	...
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        tools:ignore="ScopedStorage" />

    <application
    	...
        android:requestLegacyExternalStorage="true">
    </application>

</manifest>
```

Below class have required funcations
1. Pick pdf file 
2. Open pdf file
3. Get pdf file display name
4. Thumbnail image of file

```sh
object FilePickerUtil {

    fun openFilePicker(activity: Activity, responseCode: Int) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        activity.startActivityForResult(intent, responseCode)
    }

    fun viewFile(activity: Activity, fileUri: Uri?) {
        val viewIntent = Intent(Intent.ACTION_VIEW)
        viewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        viewIntent.data = fileUri
        val intent = Intent.createChooser(viewIntent, "Choose an application to open with:")
        activity.startActivity(intent)
    }

    fun getFileName(activity: Activity, uri: Uri): String {
        val fileName = "Unknown"
        val cursor: Cursor? = activity.contentResolver.query(
            uri, null, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex <= 0) {
                    return fileName
                }
                return it.getString(columnIndex)
            }
        }
        return fileName
    }

    fun getBitmapFromUri(activity: Activity, uri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor: ParcelFileDescriptor? = activity.contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (exception: Exception) {
            return null
        }
    }
}

```

<b>Implementation</b>
```sh
FilePickerUtil.openFilePicker(this, PICK_PDF_FILE) // To open file picker

FilePickerUtil.viewFile(this, fileUri) //To open selected file uri
```
Activity Result for getting result of selected pdf file
```sh
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.also { uri ->
                //Use file uri
            }
        }

    }
```

## [Encrypted Shared Preferences](https://github.com/sandeep9094/AndroidUtils/blob/master/EncryptedPreferences.kt)

Encrypted Preferences is Kotlin class for Android development which save sensitive data in key-value pair.
EncryptedSharedPreferences is an alternative for using SharedPreferences for sensitive data

### Implementation of EncryptedSharedPrefs with Hilt(Dependency Injection)


Add these dependencies in your app/build.gradle file:

```sh
    // Security Crypto
    implementation "androidx.security:security-crypto:1.1.0-alpha05"
    
    // Dagger Hilt
    implementation "com.google.dagger:hilt-android:2.44"
    kapt "com.google.dagger:hilt-compiler:2.44"

```

Create class EncryptSharedPrefs.kt
This will be responsible to save ecrypted and retreive decrypted preferences.

```
class EncryptSharedPrefs(context: Context) {

    companion object {
        private const val SHARED_PREFS_NAME = "secret_shared_prefs"
        private const val IS_USER_FIRST_TIME = "isUserFirstTime"
    }

    private var preferences: SharedPreferences

    init {
        // Step 1: Create or retrieve the Master Key for encryption/decryption
        val masterKeyAlias = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        // Step 2: Initialize/open an instance of EncryptedSharedPreferences
        val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
            context,
            "${context.packageName}_$SHARED_PREFS_NAME",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        // use the shared preferences and editor as you normally would
        preferences = sharedPreferences
    }

    var isUserFirstTime: Boolean
        get() = preferences.getBoolean(IS_USER_FIRST_TIME, true)
        set(value) = preferences.edit().putBoolean(IS_USER_FIRST_TIME, value).apply()

}
```

Adding EncryptedSharePrefs provider in AppModule class of Hilt for depedency injection

```
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideEncryptedSharedPrefs(@ApplicationContext context: Context) = EncryptSharedPrefs(context)

}
```

How to use EncryptedSharedPrefs in Activity/Fragment

```
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPrefs: EncryptSharedPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isUserFirstTime = sharedPrefs.isUserFirstTime
        //Use fetched preferences
    }
}
```


# Contribute

Would you like to help with this project? Great! You don't have to be a developer, either. If you've found a bug or have an idea for an improvement, please open an issue and tell us about it.

If you are a developer wanting contribute an enhancement, bugfix or other patch to this project, please fork this repository and submit a pull request detailing your changes. We review all PRs!

This open source project is released under the MIT license which means if you would like to use this project's code in your own project you are free to do so. Speaking of, if you have used our code in a cool new project we would like to hear about it! Please send us an email.

