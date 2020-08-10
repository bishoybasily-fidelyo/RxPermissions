package com.gmail.bishoybasily.permissionsrequester

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import io.reactivex.SingleEmitter

class PermissionsRequesterFragment : Fragment() {

    lateinit var emitter: SingleEmitter<Array<Boolean>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun request(emitter: SingleEmitter<Array<Boolean>>, permissions: Array<String>, requestCode: Int) {
        this.emitter = emitter
        requestPermissions(permissions, requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != PermissionsRequester.CODE) return
        emitter.onSuccess(grantResults.map { it == PackageManager.PERMISSION_GRANTED }.toTypedArray())
    }

}
