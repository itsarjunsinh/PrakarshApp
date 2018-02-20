package `in`.ac.svit.prakarsh


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        about_btn_team?.setOnClickListener {
            startActivity(Intent(context, TeamCategoryActivity::class.java))
        }

        about_btn_sponsors?.setOnClickListener {
            startActivity(Intent(context, SponsorsActivity::class.java))
        }

    }

    private fun updateViewsFromJson() {

        // Fetch data from JSON
        var url: String? = context?.getString(R.string.url_about)
        val req = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    Log.d(javaClass.name, "JSON Successfully fetched")

                    if (response.has("about")) {
                        // Display about Prakarsh text.
                        about_txt_description?.text = response["about"]?.toString()
                    }

                    // Set up YouTube Video Thumbnail and launch video when clicked.
                    if (response.has("youtubeVideoId")) {
                        val youtubeVideoId = response["youtubeVideoId"].toString()

                        about_img_video?.apply {
                            setImageUrl("https://img.youtube.com/vi/$youtubeVideoId/maxresdefault.jpg", VolleySingleton.getInstance(context).imageLoader)
                            setOnClickListener {
                                launchYouTube(youtubeVideoId)
                            }
                        }
                        about_img_play?.setOnClickListener {
                            launchYouTube(youtubeVideoId)
                        }
                    }

                    // Open Transportation PDF in web browser
                    if (response.has("transportationPdfUrl")) {
                        about_btn_transportation?.setOnClickListener {
                            try {
                                val pdfBrowserIntent = Intent(Intent.ACTION_VIEW)
                                pdfBrowserIntent.data = Uri.parse(response.getString("transportationPdfUrl"))
                                startActivity(pdfBrowserIntent)
                            } catch (e: Exception) {
                                Log.d(javaClass.name, "Exception occurred while opening pdf in web browser.", e)
                                Toast.makeText(context, "No suitable application found to open file.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    // Set up Google Maps launch.
                    if (response.has("mapsAppQuery") && response.has("mapsWebUrl")) {
                        about_btn_maps?.setOnClickListener {

                            val gmmIntentUri = Uri.parse(response.getString("mapsAppQuery"))
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.`package` = "com.google.android.apps.maps"

                            // If Google Maps app is installed open place in it otherwise open in web browser.
                            if (mapIntent.resolveActivity(context?.packageManager) != null) {
                                startActivity(mapIntent)
                            } else {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(response.getString("mapsWebUrl"))))
                            }
                        }
                    }

                    // Set up Facebook Page launch.
                    if (response.has("facebookId") && response.has("facebookUrl")) {
                        about_btn_facebook?.setOnClickListener {
                            val facebookId = response.getString("facebookId")
                            val facebookUrl = response.getString("facebookUrl")
                            launchFacebook(facebookId, facebookUrl)
                        }
                    }

                    // Set up Instagram page launch.
                    if (response.has("instagramUrl")) {
                        about_btn_instagram?.setOnClickListener {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(response.getString("instagramUrl"))))
                        }
                    }

                }, Response.ErrorListener { error ->
            Log.d(javaClass.name, "Volley Response Error Occurred, URL: $url Error: ${error.message}")
        })

        VolleySingleton.getInstance(context?.applicationContext).requestQueue.add(req.setShouldCache(false))
    }

    private fun launchYouTube(videoId: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://youtube.com/watch?v=$videoId")
        startActivity(intent)
    }

    private fun launchFacebook(facebookId: String, facebookUrl: String) {
        // If Facebook app is installed open Prakarsh Facebook page in the app else open page in browser.
        try {
            context?.packageManager?.getPackageInfo("com.facebook.katana", 0)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/$facebookId")))
        } catch (e: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)))
        }
    }
}
