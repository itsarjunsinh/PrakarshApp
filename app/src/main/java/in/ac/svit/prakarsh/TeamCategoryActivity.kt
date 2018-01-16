package `in`.ac.svit.prakarsh

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_team_category.*

/**
 * Created by itsarjunsinh on 1/16/18.
 */
class TeamCategoryActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_category)

        val url = getString(R.string.url_team_category)

        Util.loadCategoryRecycler(team_category_rv_main, url, applicationContext)
    }
}