
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MockApiServiceFactory {

    val mockWebServer = MockWebServer()

    fun <T> createService(serviceClass: Class<T>): T {
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(serviceClass)
    }

    fun shutdown() {
        mockWebServer.shutdown()
    }

    fun successResponse(body: String, headers: Map<String, String> = emptyMap()) {
        val response = MockResponse()
            .setResponseCode(200)
            .setBody(body)

        headers.forEach { (key, value) -> response.addHeader(key, value) }

        mockWebServer.enqueue(response)
    }

    fun errorResponse(code: Int = 400, errorBody: String = "Error occurred") {
        val response = MockResponse()
            .setResponseCode(code)
            .setBody(errorBody)
        mockWebServer.enqueue(response)
    }

    fun customResponse(code: Int, body: String, headers: Map<String, String> = emptyMap()) {
        val response = MockResponse()
            .setResponseCode(code)
            .setBody(body)

        headers.forEach { (key, value) -> response.addHeader(key, value) }

        mockWebServer.enqueue(response)
    }

}
