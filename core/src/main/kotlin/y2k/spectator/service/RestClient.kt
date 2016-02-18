package y2k.spectator.service

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by y2k on 1/3/16.
 */
class RestClient(private val cookieStorage: RestClient.CookieStorage) {

    val api: Api

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor(AddCookiesInterceptor())
            .addInterceptor(ReceiveCookieInterceptor())
            .build()

        api = Retrofit.Builder()
            .client(client)
            .baseUrl("http://192.168.0.28:5000/api/")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .validateEagerly(true)
            .build()
            .create(Api::class.java)
    }

    private inner class AddCookiesInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response? {
            val builder = chain.request().newBuilder()
            val temp = cookieStorage.getAll()
            for (cookie in temp)
                builder.addHeader("Cookie", cookie);
            return chain.proceed(builder.build())
        }
    }

    private inner class ReceiveCookieInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response? {
            val response = chain.proceed(chain.request())
            val cookie = response.headers("Set-Cookie").toHashSet()
            if (cookie.isNotEmpty()) cookieStorage.put(cookie)
            return response
        }
    }

    interface CookieStorage {

        fun put(cookies: Set<String>)

        fun getAll(): Set<String>
    }
}