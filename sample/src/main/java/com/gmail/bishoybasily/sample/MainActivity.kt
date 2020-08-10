package com.gmail.bishoybasily.sample

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.gmail.bishoybasily.permissionsrequester.PermissionsRequester
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val requester = PermissionsRequester().with(this)
        val needed = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)


//        // to request the permission
//        requester
//                .explain(R.string.title, R.string.message, R.string.allow, R.string.deny) // explanation dialog
//                .request(needed) // needed permissions
//                .subscribe({
//                    Log.i("##", Arrays.toString(it))

//                    var res = true
//                    it.forEach { if (!it) res = false }
//
//                    if (res) {
//                        // all of the requested permissions are granted
//                    }
//
//                }, { it.printStackTrace() })

        // please note neither the explanation dialog nor the permission dialog will be prompted if the permission already granted,
        // it means you can safely call request even if the permission already granted
        fab.setOnClickListener { view ->

            // to ensure that the permission is granted without requesting
            requester.ensure(needed)
                    .subscribe({
                        Log.i("##", Arrays.toString(it))
                        var res = true
                        it.forEach { if (!it) res = false }

                        if (res) {
                            // all of the requested permissions are granted
                        }
                    }, { it.printStackTrace() })

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
