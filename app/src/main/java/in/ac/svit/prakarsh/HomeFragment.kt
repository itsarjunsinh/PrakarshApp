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
import kotlinx.android.synthetic.main.item_featured_speakers.view.*
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

        //Featured events section
        url = context?.getString(R.string.url_featured)
        req = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    Log.d(javaClass.name, "JSON Successfully fetched")

                    val speakerList: ArrayList<Speaker> = ArrayList()
                    var featuredSpeakers = JSONArray()

                    if(response.has("featuredSpeakers")){
                        featuredSpeakers = response.getJSONArray("featuredSpeakers")
                    }

                    for(i in 0..(featuredSpeakers.length() - 1)){

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

                        var imageUrl = ""
                        if (featuredSpeakers.getJSONObject(i).has("imageUrl")) {
                            imageUrl = featuredSpeakers.getJSONObject(i).getString("imageUrl")
                        }

                        var dataUrl = ""
                        if (featuredSpeakers.getJSONObject(i).has("dataUrl")) {
                            dataUrl = featuredSpeakers.getJSONObject(i).getString("dataUrl")
                        }

                        speakerList.add(Speaker(name, shortDescription, description, imageUrl, dataUrl))
                    }

                    home_rv_featured_speakers?.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = SpeakerRecyclerAdapter(context, speakerList)
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
            val countDown = object : CountDownTimer(timeInterval, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    home_txt_timer?.text = (remainingTimeText(millisUntilFinished))
                }

                override fun onFinish() {
                    home_txt_timer?.text = finishedText
                }
            }
            countDown.start()
        } else {
            home_txt_timer?.text = finishedText
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

        if (remainingDays > 0) {
            remainingText = "$remainingDays Days\n"
        }

        remainingText += String.format("%02d Hours : %02d Minutes: %02d Seconds", remainingHours, remainingMinutes, remainingSeconds)
        return remainingText
    }

    private class Speaker(val name: String,val shortDescription: String, val description: String, val imageUrl: String, val dataUrl: String)

    private class SpeakerRecyclerAdapter(private val context: Context?, private val speakerList: ArrayList<Speaker>): RecyclerView.Adapter<SpeakerRecyclerAdapter.CustomViewHolder>() {

        class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return speakerList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomViewHolder {
            val layoutInflater = LayoutInflater.from(parent?.context)
            val cellForRow = layoutInflater.inflate(R.layout.item_featured_speakers, parent, false)
            return CustomViewHolder(cellForRow)
        }

        override fun onBindViewHolder(holder: CustomViewHolder?, position: Int) {
            holder?.view?.featured_speakers_txt_name?.text = speakerList[position].name
            holder?.view?.featured_speakers_txt_short_description?.text = speakerList[position].shortDescription
            holder?.view?.featured_speakers_txt_description?.text = speakerList[position].description
            holder?.view?.featured_speakers_img_speaker?.apply {
                setDefaultImageResId(R.drawable.ic_image_black)
                setErrorImageResId(R.drawable.ic_broken_image_black)
                setImageUrl(speakerList[position].imageUrl,VolleySingleton.getInstance(context).imageLoader)
            }

            holder?.view?.setOnClickListener{
                Log.d(javaClass.name,"${speakerList[position].name} Clicked")
                var intent = Intent(context, EventInfoActivity::class.java)
                intent.putExtra("url", speakerList[position].dataUrl)
                context?.startActivity(intent)
            }
        }
    }
}