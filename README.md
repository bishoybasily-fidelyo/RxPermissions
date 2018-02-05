# STOMP protocol for android

[![](https://jitpack.io/v/bishoybasily-fidelyo/permissions-requester.svg)](https://jitpack.io/#bishoybasily-fidelyo/permissions-requester)

## Overview

This library allows the usage of RxJava with the new Android M permission model.`

## Setup
    repositories {
        maven { url 'https://jitpack.io' }
        // other repos
    }

    dependencies {

        implementation 'com.github.bishoybasily-fidelyo:permissions-requester:latest_version'
        // other depndns
    }

## Example android kotlin

**Full example**
``` kotlin
import com.fidelyo.stomp.Event
import com.fidelyo.stomp.Stomp
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient

 //...

val requester = PermissionsRequester().with(this)
val needed = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)


// to ensure that the permission is granted without requesting
requester.ensure(needed).subscribe { fab.isEnabled = !it }

// to request the permission
requester
        .explain(R.string.title, R.string.message, R.string.allow, R.string.deny) // explanation dialog
        .request(needed) // needed permissions
        .subscribe {

            if (it) {
                // permission granted
            } else {
                // permission not granted
            }
        } // callback

// please note neither the explanation dialog nor the permission dialog will be prompted if the permission already granted,
// it means you can safely call request even if the permission already granted

// you can also skip the explanation dialog if you want
        
