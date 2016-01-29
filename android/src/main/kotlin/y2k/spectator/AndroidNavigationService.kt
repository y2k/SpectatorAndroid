package y2k.spectator

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import y2k.spectator.service.NavigationService

/**
 * Created by y2k on 1/2/16.
 */
class AndroidNavigationService(app: Application) : NavigationService {

    var context: Activity? = null

    init {
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {

            override fun onActivityResumed(activity: Activity?) {
                context = activity
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                context = activity
            }

            override fun onActivityPaused(activity: Activity?) {
                if (context == activity) context = null
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }
        })
    }

    override fun getArgument(): String? {
        return context?.intent?.getStringExtra("arg")
    }

    override fun <T> open(presenter: Class<T>, arg: String?) {
        context?.startActivity(
            Intent(context, SnapshotActivity::class.java).putExtra("arg", arg))
    }

    override fun openLogin() {
        context?.startActivity(Intent(context, LoginActivity::class.java))
    }

    override fun openMain() {
        context?.startActivity(Intent(context, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun openAddSubscription() {
        context?.startActivity(Intent(context, CreateSubscriptionActivity::class.java))
    }
}