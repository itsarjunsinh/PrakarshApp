package `in`.ac.svit.prakarsh

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

/**
 * Created by itsarjunsinh on 20-02-2018.
 */

class TransportationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_transportation)
        Log.d(javaClass.name, "Started")

    }
}