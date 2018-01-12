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
        inflater.inflate(R.layout.fragment_home,container)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        var timer = activity!!.home_txt_timer

        val prakarshDate = Calendar.getInstance()
        prakarshDate.set(2018,0,27,10,0)

        Util.startCountdown(timer,prakarshDate)
    }
}