package `in`.ac.svit.prakarsh

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

/**
 * Created by itsarjunsinh on 1/11/18.
 */

class HomeFragment : Fragment()  {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home,container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prakarshDate = Calendar.getInstance()
        prakarshDate.set(2018,1,21,10,0,0)

        Util.startCountdown(home_txt_timer, prakarshDate, context)
    }
}