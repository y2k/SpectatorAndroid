package y2k.spectator

import com.squareup.okhttp.Interceptor
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Response
import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory
import java.util.*

/**
 * Created by y2k on 1/3/16.
 */
class RestClient(private val cookieStorage: RestClient.CookieStorage) {

    val api: Api

    private val client = OkHttpClient()

    init {
        client.interceptors().add(AddCookiesInterceptor())
        client.interceptors().add(ReceiveCookieInterceptor())

        api = Retrofit.Builder()
                .client(client)
                .baseUrl("http://10.0.0.13:5000/api/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .validateEagerly()
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
            val temp = response.headers("Set-Cookie").toHashSet()
            cookieStorage.put(temp)
            return response
        }
    }

    interface CookieStorage {

        fun put(cookies: HashSet<String>)

        fun getAll(): Set<String>
    }
}