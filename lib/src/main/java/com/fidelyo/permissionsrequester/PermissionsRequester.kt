package com.fidelyo.permissionsrequester

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import io.reactivex.Observable

class PermissionsRequester {

    fun with(context: Context): Handler {
        return Handler(context)
    }

    fun with(activity: Activity): ActivityHandler {
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

    open class ActivityHandler(val activity: Activity) : Handler(activity) {

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
                Observable.just(true)
            } else {
                Observable.create { getFragment(activity).setEmitter(it).request(missingPermissions, CODE) }
            }
        }

        fun getFragment(activity: Activity): PermissionsRequesterFragment {
            val fragmentManager = activity.fragmentManager
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

    open class ExplainedActivityHandler(activity: Activity,
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
                                getFragment(activity).setEmitter(it).request(missingPermissions, CODE)
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














