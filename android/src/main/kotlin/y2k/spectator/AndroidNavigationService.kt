package y2k.spectator

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle

/**
 * Created by y2k on 1/2/16.
 */
class AndroidNavigationService(app: Application) : NavigationService, Application.ActivityLifecycleCallbacks {

    var activity: Activity? = null

    init {
        app.registerActivityLifecycleCallbacks(this)
    }

    override fun navigateToLogin() {
        activity?.startActivity(Intent(activity, LoginActivity::class.java))
    }

    override fun navigateToMain() {
        activity?.startActivity(Intent(activity, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    override fun onActivityResumed(activity: Activity?) {
        this.activity = activity
    }

    override fun onActivityPaused(activity: Activity?) {
        this.activity = null
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }
}