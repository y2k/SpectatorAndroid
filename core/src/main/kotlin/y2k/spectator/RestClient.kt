package y2k.spectator

import retrofit.GsonConverterFactory
import retrofit.Retrofit
import retrofit.RxJavaCallAdapterFactory

/**
 * Created by y2k on 1/3/16.
 */
class RestClient {

    val api: Api

    init {
        api = Retrofit.Builder()
                .baseUrl("http://10.0.0.13:5000/api/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .validateEagerly()
                .build()
                .create(Api::class.java)
    }
}