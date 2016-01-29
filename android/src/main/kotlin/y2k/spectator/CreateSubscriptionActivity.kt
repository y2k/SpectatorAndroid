package y2k.spectator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import y2k.spectator.presenter.CreateSubscriptionPresenter

/**
 * Created by y2k on 1/3/16.
 */
class CreateSubscriptionActivity : AppCompatActivity() {

    lateinit var presenter: CreateSubscriptionPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_subscription)

        presenter = ServiceLocator.resolveCreateSubscriptionPresenter(
                object : CreateSubscriptionPresenter.View {

                    val titleView = findViewById(R.id.title) as EditText
                    val linkView = findViewById(R.id.link) as EditText
                    val createButton = findViewById(R.id.create)

                    init {
                        createButton.setOnClickListener { presenter.create() }
                    }

                    override val title: String
                        get() = "" + titleView.text
                    override val link: String
                        get() = "" + linkView.text

                    override fun setBusy(isBusy: Boolean) {
                        createButton.isEnabled = !isBusy
                    }
                }
        )
    }
}