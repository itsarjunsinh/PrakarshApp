package `in`.ac.svit.prakarsh

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

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
        var url: String? = context?.getString(R.string.url_main)
        val req = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    Log.d(javaClass.name, "JSON Successfully fetched")

                    if (response.has("heading")) {
                        home_txt_share_heading?.text = response["heading"]?.toString()
                    }

                    if (response.has("subHeading")) {
                        home_txt_share_subheading?.text = response["subHeading"]?.toString()
                    }

                    if (response.has("shareContent")) {
                        home_txt_share_content?.text = response["shareContent"]?.toString()
                    }

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
    }

    private fun startCountdown(eventDate: Calendar) {
        val finishedText = "${context?.getString(R.string.event_name)}!"
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
}