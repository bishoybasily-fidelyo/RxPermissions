package com.fidelyo.permissionsrequester

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import io.reactivex.Observable

class PermissionsRequester {

    fun with(activity: Activity): Requester {
        return Requester(activity)
    }

    open class Requester(val activity: Activity) {

        open fun explain(title: Int,
                         message: Int,
                         positive: Int,
                         negative: Int): ExplainedRequester {
            return ExplainedRequester(activity, title, message, positive, negative)
        }

        open fun request(permissions: Array<String>): Observable<Boolean> {
            val missingPermissions = missingPermissions(permissions)
            return if (missingPermissions.isEmpty()) {
                Observable.just(true)
            } else {
                Observable.create { emitter ->
                    getFragment(activity).requestPermissions(emitter, missingPermissions)
                }
            }
        }

        open fun missingPermissions(permissions: Array<String>): Array<String> {
            val missingPermissions = permissions.filter { isMissing(activity, it) }.toTypedArray()
            return missingPermissions
        }

        open class ExplainedRequester(activity: Activity,
                                      val title: Int,
                                      val message: Int,
                                      val positive: Int,
                                      val negative: Int) : Requester(activity) {
            override fun request(permissions: Array<String>): Observable<Boolean> {
                val missingPermissions = missingPermissions(permissions)
                return if (missingPermissions.isEmpty()) {
                    Observable.just(true)
                } else {
                    return Observable.create { emitter ->
                        AlertDialog.Builder(activity)
                                .setTitle(title)
                                .setMessage(message)
                                .setPositiveButton(positive) { dialog, whichButton ->
                                    getFragment(activity).requestPermissions(emitter, missingPermissions)
                                }
                                .setNegativeButton(negative) { dialog, which ->
                                    emitter.onNext(false)
                                    emitter.onComplete()
                                }
                                .create()
                                .show()
                    }
                }

            }
        }

        fun isMissing(activity: Activity, permission: String): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED
            else false
        }

        fun getFragment(activity: Activity): PermissionsRequesterFragment {
            val fragmentManager = activity.fragmentManager
            var fragment = fragmentManager.findFragmentByTag(PermissionsRequesterFragment.TAG)
            if (fragment == null) {
                fragment = PermissionsRequesterFragment()
                fragmentManager
                        .beginTransaction()
                        .add(fragment, PermissionsRequesterFragment.TAG)
                        .commitAllowingStateLoss()
                fragmentManager.executePendingTransactions()
            }
            return (fragment as PermissionsRequesterFragment)
        }

    }

}














