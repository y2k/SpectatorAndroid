package y2k.spectator

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import y2k.spectator.common.ActivityLifecycleCallbacksAdapter
import y2k.spectator.common.startActivity
import y2k.spectator.service.NavigationService

/**
 * Created by y2k on 1/2/16.
 */
class AndroidNavigationService(app: Application) : NavigationService {

    var context: Activity? = null

    init {
        app.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacksAdapter() {

            override fun onActivityResumed(activity: Activity?) {
                context = activity
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                context = activity
            }

            override fun onActivityPaused(activity: Activity?) {
                if (context == activity) context = null
            }
        })
    }

    override fun getArgument(): String? {
        return context?.intent?.getStringExtra("arg")
    }

    override fun <T> open(presenter: Class<T>, arg: String?) {
        context?.startActivity(SnapshotActivity::class) { putExtra("arg", arg) }
    }

    override fun openLogin() {
        context?.startActivity(LoginActivity::class)
    }

    override fun openMain() {
        context?.startActivity(MainActivity::class) {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    override fun openAddSubscription() {
        context?.startActivity(CreateSubscriptionActivity::class)
    }
}