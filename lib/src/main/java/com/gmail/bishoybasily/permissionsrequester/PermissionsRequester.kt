package com.gmail.bishoybasily.permissionsrequester

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.FragmentActivity
import io.reactivex.Single
import io.reactivex.SingleEmitter

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

        protected fun missingPermissions(permissions: Array<String>): Array<String> {
            return permissions.filter { isMissing(context, it) }.toTypedArray()
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
            else Single.create { getFragment(activity, it).request(missingPermissions(permissions), CODE) }
        }

        protected fun getFragment(activity: FragmentActivity, emitter: SingleEmitter<Array<Boolean>>): PermissionsRequesterFragment {
            val fragmentManager = activity.supportFragmentManager
            var fragment = fragmentManager.findFragmentByTag(TAG)
            if (fragment == null) {
                fragment = PermissionsRequesterFragment(emitter)
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
            val missingPermissions = missingPermissions(permissions)
            return if (missingPermissions.isEmpty()) Single.just(arrayOf(true))
            else return Single.create {
                AlertDialog.Builder(activity)
                        .setTitle(title)
                        .setMessage(message)
                        .setPositiveButton(positive) { dialog, _ ->
                            dialog.dismiss()
                            getFragment(activity, it).request(missingPermissions, CODE)
                        }
                        .setNegativeButton(negative) { dialog, _ ->
                            dialog.dismiss()
                            it.onSuccess(arrayOf(false))
                        }
                        .create()
                        .show()
            }
        }

    }

    companion object {

        val CODE = 42

    }

}














