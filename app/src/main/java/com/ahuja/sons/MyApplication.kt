import android.app.Activity
import android.app.Application
import android.os.Bundle

class MyApplication : Application() {

    companion object {
        var currentActivity: Activity? = null
            private set

        var currentApp: Application? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        currentApp= Application()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                currentActivity = activity
            }

            override fun onActivityStarted(activity: Activity) {
                currentActivity = activity
            }

            override fun onActivityResumed(activity: Activity) {
                currentActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
                // No need to update currentActivity
            }

            override fun onActivityStopped(activity: Activity) {
                // No need to update currentActivity
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                // No need to update currentActivity
            }

            override fun onActivityDestroyed(activity: Activity) {
                // No need to update currentActivity
            }
        })
    }
}
