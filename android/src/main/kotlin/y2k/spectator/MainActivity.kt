package y2k.spectator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val counterStore = CounterStore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my)

        val counterTextView = findViewById(R.id.counterTextView) as TextView
        val counterButton = findViewById(R.id.counterButton) as Button

        counterButton.setOnClickListener { view ->
            counterStore.add(1)
            counterTextView.text = "Click Nr. " + counterStore.get()
        }
    }
}