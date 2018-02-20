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
                    Log.d(javaClass.name, "Main JSON Successfully fetched")

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

        // Featured events & speakers section.
        url = context?.getString(R.string.url_featured)
        req = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    Log.d(javaClass.name, "Featured JSON Successfully fetched")

                    // Get featured events & speakers label and get boolean for checking whether they should be displayed.
                    var showFeaturedEvents = false
                    var showFeaturedSpeakers = false
                    var featuredEventsLabel = ""
                    var featuredSpeakersLabel = ""

                    if (response.has("show") && response.has("labels")) {
                        if (response.getJSONObject("show").has("featuredEvents") && response.getJSONObject("labels").has("featuredEvents")) {
                            showFeaturedEvents = response.getJSONObject("show").getBoolean("featuredEvents")
                            featuredEventsLabel = response.getJSONObject("labels").getString("featuredEvents")
                        }
                        if (response.getJSONObject("show").has("featuredSpeakers") && response.getJSONObject("labels").has("featuredSpeakers")) {
                            showFeaturedSpeakers = response.getJSONObject("show").getBoolean("featuredSpeakers")
                            featuredSpeakersLabel = response.getJSONObject("labels").getString("featuredSpeakers")
                        }
                    }

                    // Featured events section.
                    if (showFeaturedEvents) {
                        home_txt_featured_events?.text = featuredEventsLabel

                        if (response.has("featuredEvents")) {
                            val featuredEvents = response.getJSONArray("featuredEvents")
                            home_rv_featured_events?.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = FeaturedRecyclerAdapter(context, getFeaturedList(featuredEvents))
                            }
                        }
                    }

                    // Featured speakers section.
                    if (showFeaturedSpeakers) {
                        home_txt_featured_speakers?.text = featuredSpeakersLabel

                        if (response.has("featuredSpeakers")) {
                            val featuredSpeakers = response.getJSONArray("featuredSpeakers")
                            home_rv_featured_speakers?.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = FeaturedRecyclerAdapter(context, getFeaturedList(featuredSpeakers))
                            }
                        }
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

        // Convert remaining milliseconds to human comprehensible form.
        val remainingDays = (millisLeft / DAY)
        val remainingHours = (millisLeft % DAY) / HOUR
        val remainingMinutes = (millisLeft % HOUR) / MINUTE
        val remainingSeconds = (millisLeft % MINUTE) / SECOND

        // Generate the text to be displayed in the timer.
        var remainingText = ""
        if (remainingDays > 1) {
            remainingText = "$remainingDays Days\n"
        } else if (remainingDays == 1L) {
            remainingText = "$remainingDays Day\n"
        }
        remainingText += String.format("%02d Hours : %02d Minutes: %02d Seconds", remainingHours, remainingMinutes, remainingSeconds)

        return remainingText
    }

    private class FeaturedItem(var heading: String = "", var subHeading: String = "", var description: String = "", var dataUrl: String? = null, var imageUrl: String? = null, var date: String = "", var time: String = "")

    private fun getFeaturedList(jsonArray: JSONArray): ArrayList<FeaturedItem> {

        val featuredList = ArrayList<FeaturedItem>()

        for (i in 0..(jsonArray.length() - 1)) {

            val featuredEventsItem = FeaturedItem()

            if (jsonArray.getJSONObject(i).has("name")) {
                featuredEventsItem.heading = jsonArray.getJSONObject(i).getString("name")
            }

            if (jsonArray.getJSONObject(i).has("shortDescription")) {
                featuredEventsItem.subHeading = jsonArray.getJSONObject(i).getString("shortDescription")
            }

            if (jsonArray.getJSONObject(i).has("description")) {
                featuredEventsItem.description = jsonArray.getJSONObject(i).getString("description")
            }

            if (jsonArray.getJSONObject(i).has("dataUrl")) {
                featuredEventsItem.dataUrl = jsonArray.getJSONObject(i).getString("dataUrl")
            }

            if (jsonArray.getJSONObject(i).has("imageUrl")) {
                featuredEventsItem.imageUrl = jsonArray.getJSONObject(i).getString("imageUrl")
            }

            if (jsonArray.getJSONObject(i).has("date")) {
                featuredEventsItem.date = jsonArray.getJSONObject(i).getString("date")
            }

            if (jsonArray.getJSONObject(i).has("time")) {
                featuredEventsItem.time = jsonArray.getJSONObject(i).getString("time")
            }

            featuredList.add(featuredEventsItem)
        }

        return featuredList
    }

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

            // Show event info text.
            holder?.view?.featured_txt_heading?.text = featuredItemList[position].heading
            holder?.view?.featured_txt_subheading?.text = featuredItemList[position].subHeading
            holder?.view?.featured_txt_description?.text = featuredItemList[position].description
            holder?.view?.featured_txt_date?.text = featuredItemList[position].date
            holder?.view?.featured_txt_time?.text = featuredItemList[position].time

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