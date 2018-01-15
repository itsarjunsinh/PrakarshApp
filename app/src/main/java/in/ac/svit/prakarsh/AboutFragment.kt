package `in`.ac.svit.prakarsh


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_about.*


/**
 * Created by itsarjunsinh on 1/12/18.
 */

class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateViewsFromJson()
    }

    private fun updateViewsFromJson() {
        try {
            var url: String? = context?.getString(R.string.url_about)
            val que = Volley.newRequestQueue(context)
            val req = JsonObjectRequest(Request.Method.GET,url,null,
                    Response.Listener {
                        response ->
                        Log.d(javaClass.name,"JSON Successfully fetched")
                        about_txt_description.text=response["about"].toString()
                    }, Response.ErrorListener {
                error ->
                Log.d(javaClass.name,"Volley Response Error Occurred, URL: $url Error: ${error.message}")
            })
            que.add(req)
        }catch (e: Exception){
            Log.d(javaClass.name,"Exception caught during Volley Request.")
        }
    }
}
