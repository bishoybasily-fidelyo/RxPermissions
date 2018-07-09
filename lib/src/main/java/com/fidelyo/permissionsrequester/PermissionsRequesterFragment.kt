package com.fidelyo.permissionsrequester

import android.annotation.TargetApi
import android.app.Fragment
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import io.reactivex.SingleEmitter

class PermissionsRequesterFragment : Fragment() {

    private var emitter: SingleEmitter<Boolean>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun setEmitter(emitter: SingleEmitter<Boolean>): PermissionsRequesterFragment {
        this.emitter = emitter
        return this
    }


    @TargetApi(Build.VERSION_CODES.M)
    fun request(permissions: Array<String>, requestCode: Int) {
        requestPermissions(permissions, requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != PermissionsRequester.CODE) return

        emitter?.onSuccess(checkResults(grantResults))

    }

    private fun checkResults(grantResults: IntArray): Boolean {
        for (re in grantResults)
            if (re != PackageManager.PERMISSION_GRANTED)
                return false
        return true
    }


}
