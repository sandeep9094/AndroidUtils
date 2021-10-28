# AndroidUtils
Android Development Helper Classes for improving code structure.

<h4> Logger </h4>
Logger is Kotlin class for Android development which show logs only in debug mode.
In release mode app logs will not shown in usb debugging.

<h4> EncryptedPreferences </h4>

Encrypted Preferences is Kotlin class for Android development which save sensitive data in key-value pair.
EncryptedSharedPreferences is an alternative for using SharedPreferences for sensitive data

<h4> Webview Error Page </h4>

Android webview error page always shows error page with its url. We should always try to hide domain with some error page.
This is just a sample to load default html error page on webview

<h4> ApiResult </h4>

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

<h4> LoadingState </h4>

LoadingState is sealed class which is used to update data loading status on ui, user should be aware of data loading state.
LoadingState have three stages : Loading, Success and Error
