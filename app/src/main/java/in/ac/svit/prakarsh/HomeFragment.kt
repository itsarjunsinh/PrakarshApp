package `in`.ac.svit.prakarsh

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by itsarjunsinh on 1/11/18.
 */

class HomeFragment : Fragment()  {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        inflater.inflate(R.layout.fragment_home,container)
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}