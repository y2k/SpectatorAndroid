package y2k.spectator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import y2k.spectator.common.bind
import y2k.spectator.common.find

/**
 * Created by y2k on 1/3/16.
 */
class CreateSubscriptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_subscription)

        ServiceLocator.resolveCreateSubscriptionPresenter().apply {
            findViewById(R.id.create).bind { create() }
            findViewById(R.id.create).bind(isBusy)
            find<EditText>(R.id.title).bind(title)
            find<EditText>(R.id.link).bind(title)
        }
    }
}