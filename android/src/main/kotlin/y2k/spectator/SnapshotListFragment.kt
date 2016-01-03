package y2k.spectator

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
                    val list = view.findViewById(R.id.list) as RecyclerView

                    init {
                        view.findViewById(R.id.add).setOnClickListener { presenter.add() }
                        list.layoutManager = LinearLayoutManager(activity)
                        login.setOnClickListener { presenter.login() }
                    }

                    override fun showLogin() {
                        login.visibility = View.VISIBLE
                    }
                })

        return view
    }
}