package y2k.spectator

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by y2k on 1/2/16.
 */
class MenuFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View(context).apply {
            setBackgroundColor(Color.RED)
        }
    }
}