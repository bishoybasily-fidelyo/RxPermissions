package com.gmail.bishoybasily.permissionsrequester

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.FragmentActivity
import io.reactivex.Single

class PermissionsRequester {

    fun with(context: Context): Handler {
        return Handler(context)
    }

    fun with(activity: FragmentActivity): ActivityHandler {
        return ActivityHandler(activity)
    }

    open class Handler(val context: Context) {

        private fun isMissing(context: Context, permission: String): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED
            else false
        }

        fun ensure(permissions: Array<String>): Single<Array<Boolean>> {
            return if (permissions.isEmpty()) Single.error(Throwable("No permissions to request"))
            else Single.fromCallable { permissions.map { !isMissing(context, it) }.toTypedArray() }
        }

    }

    open class ActivityHandler(val activity: FragmentActivity) : Handler(activity) {

        val TAG = javaClass.simpleName

        fun explain(title: Int,
                    message: Int,
                    positive: Int,
                    negative: Int): ExplainedActivityHandler {
            return ExplainedActivityHandler(activity, title, message, positive, negative)
        }

        open fun request(permissions: Array<String>): Single<Array<Boolean>> {
            return if (permissions.isEmpty()) Single.error(Throwable("No permissions to request"))
            else Single.create { getFragment(activity).request(it, permissions, CODE) }
        }

        protected fun getFragment(activity: FragmentActivity): PermissionsRequesterFragment {
            val fragmentManager = activity.supportFragmentManager
            var fragment = fragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = PermissionsRequesterFragment()
                fragmentManager
                        .beginTransaction()
                        .add(fragment, TAG)
                        .commitAllowingStateLoss()
                fragmentManager.executePendingTransactions()
            }
            return fragment as PermissionsRequesterFragment
        }

    }

    open class ExplainedActivityHandler(activity: FragmentActivity,
                                        val title: Int,
                                        val message: Int,
                                        val positive: Int,
                                        val negative: Int) : ActivityHandler(activity) {

        override fun request(permissions: Array<String>): Single<Array<Boolean>> {
            return if (permissions.isEmpty()) Single.error(Throwable("No permissions to request"))
            else return ensure(permissions)
                    .flatMap { results ->
                        var hasMissing = false
                        results.forEach { if (!it) hasMissing = true }
                        return@flatMap if (hasMissing) Single.create<Array<Boolean>> {
                            AlertDialog.Builder(activity)
                                    .setTitle(title)
                                    .setMessage(message)
                                    .setPositiveButton(positive) { dialog, _ ->
                                        dialog.dismiss()
                                        getFragment(activity).request(it, permissions, CODE)
                                    }
                                    .setNegativeButton(negative) { dialog, _ ->
                                        dialog.dismiss()
                                        it.onSuccess(results)
                                    }
                                    .create()
                                    .show()
                        }
                        else super.request(permissions)
                    }
        }

    }

    companion object {

        val CODE = 42

    }

}














