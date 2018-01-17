package `in`.ac.svit.prakarsh

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

/**
 * Created by itsarjunsinh on 1/16/18.
 */
class TeamInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_subcategory)

        val url = intent.getStringExtra("url")
        Log.d(javaClass.name,"URL received: $url")
    }
}