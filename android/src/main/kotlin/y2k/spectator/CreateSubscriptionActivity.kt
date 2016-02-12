package y2k.spectator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import y2k.spectator.common.bind
import y2k.spectator.common.bindEditText
import y2k.spectator.presenter.CreateSubscriptionViewModel

/**
 * Created by y2k on 1/3/16.
 */
class CreateSubscriptionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_subscription)
        ServiceLocator
            .resolve(CreateSubscriptionViewModel::class)
            .apply {
                bind(R.id.create) { create() }
                bind(R.id.create, isBusy)
                bindEditText(R.id.title, title)
                bindEditText(R.id.link, link)
            }
    }
}