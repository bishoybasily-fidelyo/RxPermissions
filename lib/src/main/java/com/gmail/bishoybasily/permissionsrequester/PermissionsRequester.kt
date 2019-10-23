package com.gmail.bishoybasily.permissionsrequester

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.fragment.app.FragmentActivity
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

class PermissionsRequester {

    fun with(context: Context): Handler {
        return Handler(context)
    }

    fun with(activity: FragmentActivity): ActivityHandler {
        return ActivityHandler(activity)
    }

    open class Handler(val context: Context) {

        fun isMissing(context: Context, permission: String): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED
            else false
        }

        open fun missingPermissions(permissions: Array<String>): Array<String> {
            return permissions.filter { isMissing(context, it) }.toTypedArray()
        }

        open fun ensure(permissions: Array<String>): Observable<Boolean> {
            return Observable.just(missingPermissions(permissions).isEmpty())
        }

    }

    open class ActivityHandler(val activity: FragmentActivity) : Handler(activity) {

        val TAG = javaClass.simpleName

        open fun explain(title: Int,
                         message: Int,
                         positive: Int,
                         negative: Int): ExplainedActivityHandler {
            return ExplainedActivityHandler(activity, title, message, positive, negative)
        }

        open fun request(permissions: Array<String>): Observable<Boolean> {
            val missingPermissions = missingPermissions(permissions)
            return if (missingPermissions.isEmpty()) {
                Observable.error(Throwable("No permissions to request"))
            } else {
                Observable.create { getFragment(activity, it).request(missingPermissions, CODE) }
            }
        }

        protected fun getFragment(activity: FragmentActivity, emitter: ObservableEmitter<Boolean>): PermissionsRequesterFragment {
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

        override fun request(permissions: Array<String>): Observable<Boolean> {
            val missingPermissions = missingPermissions(permissions)
            return if (missingPermissions.isEmpty()) {
                Observable.just(true)
            } else {
                return Observable.create {
                    AlertDialog.Builder(activity)
                            .setTitle(title)
                            .setMessage(message)
                            .setPositiveButton(positive) { dialog, _ ->
                                dialog.dismiss()
                                getFragment(activity, it).request(missingPermissions, CODE)
                            }
                            .setNegativeButton(negative) { dialog, _ ->
                                dialog.dismiss()
                                it.onNext(false)
                                it.onComplete()
                            }
                            .create()
                            .show()
                }
            }
        }

    }

    companion object {

        val CODE = 42

    }

}














