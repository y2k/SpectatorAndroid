package y2k.spectator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by y2k on 1/2/16.
 */
class SnapshotListFragment : Fragment() {

    lateinit var presenter: SnapshotsPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_snapshots, container, false)

        presenter = ServiceLocator.resolveSnapshotsPresenter(
                object : SnapshotsPresenter.View {

                    val login = view.findViewById(R.id.login)

                    init {
                        login.setOnClickListener { presenter.login() }
                    }

                    override fun showLogin() {
                        login.visibility = View.VISIBLE
                    }
                })

        return view
    }
}