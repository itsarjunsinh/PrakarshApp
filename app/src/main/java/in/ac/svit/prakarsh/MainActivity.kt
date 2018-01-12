package `in`.ac.svit.prakarsh

import `in`.ac.svit.prakarsh.R.id.action_home
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_navigation.selectedItemId=action_home
        startFragmentTransaction(HomeFragment())
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.action_events -> {
                //startFragmentTransaction(EventsFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_home -> {
                startFragmentTransaction(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_about -> {
                //startFragmentTransaction(AboutFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun startFragmentTransaction(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .replace(container.id, fragment)
                .commit()
    }
}
