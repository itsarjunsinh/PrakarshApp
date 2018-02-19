package `in`.ac.svit.prakarsh

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_featured.view.*
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by itsarjunsinh on 1/11/18.
 */

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.name, "Started")

        updateViewsFromJson()
    }

    private fun updateViewsFromJson() {

        //Timer section
        var url = context?.getString(R.string.url_main)
        var req = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    Log.d(javaClass.name, "JSON Successfully fetched")

                    val prakarshDate = Calendar.getInstance()
                    var year = 0
                    var month = 0
                    var date = 0
                    var hours = 0
                    var minutes = 0

                    if (response.has("eventDate")) {

                        if (response.getJSONObject("eventDate").has("year")) {
                            year = response.getJSONObject("eventDate").getInt("year")
                        }

                        if (response.getJSONObject("eventDate").has("month")) {
                            month = response.getJSONObject("eventDate").getInt("month") - 1
                        }

                        if (response.getJSONObject("eventDate").has("date")) {
                            date = response.getJSONObject("eventDate").getInt("date")
                        }

                        if (response.getJSONObject("eventDate").has("hours")) {
                            hours = response.getJSONObject("eventDate").getInt("hours")
                        }
                        if (response.getJSONObject("eventDate").has("minutes")) {
                            minutes = response.getJSONObject("eventDate").getInt("minutes")
                        }

                    }

                    prakarshDate.set(year, month, date, hours, minutes, 0)
                    startCountdown(prakarshDate)

                }, Response.ErrorListener { error ->
            Log.d(javaClass.name, "Volley Response Error Occurred, URL: $url Error: ${error.message}")
        })

        VolleySingleton.getInstance(context?.applicationContext).requestQueue.add(req)

        // Featured events section
        url = context?.getString(R.string.url_featured)
        req = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    Log.d(javaClass.name, "JSON Successfully fetched")

                    val featuredSpeakerList: ArrayList<FeaturedItem> = ArrayList()
                    var featuredSpeakers = JSONArray()

                    if (response.has("featuredSpeakers")) {
                        featuredSpeakers = response.getJSONArray("featuredSpeakers")
                    }

                    for (i in 0..(featuredSpeakers.length() - 1)) {

                        var name = ""
                        if (featuredSpeakers.getJSONObject(i).has("name")) {
                            name = featuredSpeakers.getJSONObject(i).getString("name")
                        }

                        var shortDescription = ""
                        if (featuredSpeakers.getJSONObject(i).has("shortDescription")) {
                            shortDescription = featuredSpeakers.getJSONObject(i).getString("shortDescription")
                        }

                        var description = ""
                        if (featuredSpeakers.getJSONObject(i).has("description")) {
                            description = featuredSpeakers.getJSONObject(i).getString("description")
                        }

                        var dataUrl: String? = null
                        if (featuredSpeakers.getJSONObject(i).has("dataUrl")) {
                            dataUrl = featuredSpeakers.getJSONObject(i).getString("dataUrl")
                        }

                        var imageUrl: String? = null
                        if (featuredSpeakers.getJSONObject(i).has("imageUrl")) {
                            imageUrl = featuredSpeakers.getJSONObject(i).getString("imageUrl")
                        }

                        featuredSpeakerList.add(FeaturedItem(name, shortDescription, description, dataUrl, imageUrl))
                    }

                    home_rv_featured_speakers?.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = FeaturedRecyclerAdapter(context, featuredSpeakerList)
                    }

                }, Response.ErrorListener { error ->
            Log.d(javaClass.name, "Volley Response Error Occurred, URL: $url Error: ${error.message}")
        })

        VolleySingleton.getInstance(context?.applicationContext).requestQueue.add(req.setShouldCache(false))
    }

    private fun startCountdown(eventDate: Calendar) {
        val finishedText = "${context?.getString(R.string.timer_finished)}!"
        val today = Calendar.getInstance()
        val timeInterval = eventDate.timeInMillis - today.timeInMillis

        if (timeInterval > 1) {

            // Show Timer if the event is yet to start.
            home_txt_timer?.visibility = View.VISIBLE

            val countDown = object : CountDownTimer(timeInterval, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    home_txt_timer?.text = (remainingTimeText(millisUntilFinished))
                }

                override fun onFinish() {
                    // When timer completed show finished text. Will only be shown once if the user sees the countdown go to 0.
                    home_txt_timer?.text = finishedText
                }
            }
            countDown.start()
        }
    }

    private fun remainingTimeText(millisLeft: Long): String {
        val SECOND = 1000
        val MINUTE = SECOND * 60
        val HOUR = MINUTE * 60
        val DAY = HOUR * 24

        val remainingDays = (millisLeft / DAY)
        val remainingHours = (millisLeft % DAY) / HOUR
        val remainingMinutes = (millisLeft % HOUR) / MINUTE
        val remainingSeconds = (millisLeft % MINUTE) / SECOND
        var remainingText = ""

        if (remainingDays > 1) {
            remainingText = "$remainingDays Days\n"
        } else if (remainingDays == 1L) {
            remainingText = "$remainingDays Day\n"
        }

        remainingText += String.format("%02d Hours : %02d Minutes: %02d Seconds", remainingHours, remainingMinutes, remainingSeconds)
        return remainingText
    }

    private class FeaturedItem(val heading: String, val subHeading: String, val description: String, val dataUrl: String? = null, val imageUrl: String? = null)

    private class FeaturedRecyclerAdapter(private val context: Context?, private val featuredItemList: ArrayList<FeaturedItem>) : RecyclerView.Adapter<FeaturedRecyclerAdapter.CustomViewHolder>() {

        class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return featuredItemList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomViewHolder {
            val layoutInflater = LayoutInflater.from(parent?.context)
            val cellForRow = layoutInflater.inflate(R.layout.item_featured, parent, false)
            return CustomViewHolder(cellForRow)
        }

        override fun onBindViewHolder(holder: CustomViewHolder?, position: Int) {
            holder?.view?.featured_txt_heading?.text = featuredItemList[position].heading
            holder?.view?.featured_txt_subheading?.text = featuredItemList[position].subHeading
            holder?.view?.featured_txt_description?.text = featuredItemList[position].description

            if (featuredItemList[position].imageUrl != null) {
                // Show image if image url exists.
                holder?.view?.featured_card_image?.visibility = View.VISIBLE
                holder?.view?.featured_img_speaker?.apply {
                    setDefaultImageResId(R.drawable.ic_image_black)
                    setErrorImageResId(R.drawable.ic_broken_image_black)
                    setImageUrl(featuredItemList[position].imageUrl, VolleySingleton.getInstance(context).imageLoader)
                }
            }

            if (featuredItemList[position].dataUrl != null) {
                // Change to EventInfo activity on click event if data url exists.
                holder?.view?.setOnClickListener {
                    Log.d(javaClass.name, "${featuredItemList[position].heading} Clicked")
                    val intent = Intent(context, EventInfoActivity::class.java)
                    intent.putExtra("url", featuredItemList[position].dataUrl)
                    context?.startActivity(intent)
                }
            }

        }
    }
}