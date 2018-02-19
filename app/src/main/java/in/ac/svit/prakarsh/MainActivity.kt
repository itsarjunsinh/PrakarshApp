package `in`.ac.svit.prakarsh

import `in`.ac.svit.prakarsh.R.id.action_home
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.util.Log


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        disableNavigationShiftMode(main_navigation)
        main_navigation?.selectedItemId = action_home
        startFragmentTransaction(HomeFragment())
        main_navigation?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.action_events -> {
                startFragmentTransaction(EventsCategoryFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_newsfeed -> {
                startFragmentTransaction(NewsfeedFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_home -> {
                startFragmentTransaction(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_about -> {
                startFragmentTransaction(AboutFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_account -> {
                startFragmentTransaction(AccountFragment())
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

    private fun disableNavigationShiftMode(view: BottomNavigationView?) {
        val menuView = view?.getChildAt(0) as BottomNavigationMenuView
        try {
            val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
            shiftingMode.isAccessible = true
            shiftingMode.setBoolean(menuView, false)
            shiftingMode.isAccessible = false
            for (i in 0 until menuView.childCount) {
                val item = menuView.getChildAt(i) as BottomNavigationItemView

                item.setShiftingMode(false)
                // set once again checked value, so view will be updated

                item.setChecked(item.itemData.isChecked)
            }
        } catch (e: NoSuchFieldException) {
            Log.e(javaClass.name, "Unable to get shift mode field", e)
        } catch (e: IllegalAccessException) {
            Log.e(javaClass.name, "Unable to change value of shift mode", e)
        }

    }
}
