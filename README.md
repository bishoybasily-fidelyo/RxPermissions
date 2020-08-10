# Permissions requester for android

[![](https://jitpack.io/v/bishoybasily/permissions-requester.svg)](https://jitpack.io/#bishoybasily/permissions-requester)

## Overview

This library allows you to request the required android permissions using RxJava.

## Setup
    repositories {
        maven { url 'https://jitpack.io' }
        // other repos
    }

    dependencies {

        implementation 'com.github.bishoybasily:permissions-requester:latest_version'
        // other depndns
    }

## Example android kotlin

**Full example**
``` kotlin

val requester = PermissionsRequester().with(this)
val needed = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)

// to ensure that the permission is granted without requesting
requester.ensure(needed).subscribe { fab.isEnabled = !it }

// to request the permission
requester
    .explain(R.string.title, R.string.message, R.string.allow, R.string.deny) // explanation dialog
    .request(needed) // needed permissions
    .subscribe {
        var res = true
        it.forEach { if (!it) res = false }
        if (res) {
            // all of the requested permissions are granted
        }
    }

// please note neither the explanation dialog nor the permission dialog will be prompted if the permission already granted,
// it means you can safely call request even if the permission already granted

// you can also skip the explanation dialog if you want
        
