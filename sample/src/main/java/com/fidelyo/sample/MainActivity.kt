package com.fidelyo.sample

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.fidelyo.permissionsrequester.PermissionsRequester
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val requester = PermissionsRequester().with(this)
        val needed = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)

        // to ensure that the permission is granted without requesting
        requester.ensure(needed).subscribe({ fab.isEnabled = it }, {})

        // to request the permission
        requester
                .explain(R.string.title, R.string.message, R.string.allow, R.string.deny) // explanation dialog
                .request(needed) // needed permissions
                .subscribe({

                    if (it) {
                        // permission granted
                    } else {
                        // permission not granted
                    }
                }, {}) // callback

        // please note neither the explanation dialog nor the permission dialog will be prompted if the permission already granted,
        // it means you can safely call request even if the permission already granted
        fab.setOnClickListener { view ->

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
