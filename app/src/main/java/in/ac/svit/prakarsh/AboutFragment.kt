package `in`.ac.svit.prakarsh


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
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
        about_btn_team.setOnClickListener {
            startActivity(Intent(context, TeamCategoryActivity::class.java))
        }
        about_btn_sponsors.setOnClickListener {
            startActivity(Intent(context, SponsorsActivity::class.java))
        }
    }

    private fun updateViewsFromJson() {
        var url: String? = context?.getString(R.string.url_about)
        val req = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    Log.d(javaClass.name, "JSON Successfully fetched")

                    if (response.has("about")) {
                        about_txt_description?.text = response["about"]?.toString()
                    }

                    about_img_video?.setDefaultImageResId(R.drawable.ic_image_black)
                    about_img_video?.setErrorImageResId(R.drawable.ic_broken_image_black)
                    if (response.has("youtubeVideoId")) {
                        val youtubeVideoId = response["youtubeVideoId"].toString()
                        about_img_video?.setImageUrl("https://img.youtube.com/vi/$youtubeVideoId/maxresdefault.jpg", VolleySingleton.getInstance(context).imageLoader)
                        about_img_video?.setOnClickListener {
                            launchYouTube(youtubeVideoId)
                        }
                        about_img_play?.setOnClickListener {
                            launchYouTube(youtubeVideoId)
                        }
                    }

                }, Response.ErrorListener { error ->
            Log.d(javaClass.name, "Volley Response Error Occurred, URL: $url Error: ${error.message}")
        })

        VolleySingleton.getInstance(context?.applicationContext).requestQueue.add(req)
    }

    private fun launchYouTube(videoId: String) {
        var intent: Intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse("https://youtube.com/watch?v=$videoId"))
        startActivity(intent)
    }
}
