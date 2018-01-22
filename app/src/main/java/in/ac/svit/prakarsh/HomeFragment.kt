package `in`.ac.svit.prakarsh

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
        Log.d(javaClass.name,"Started")

        val prakarshDate = Calendar.getInstance()
        prakarshDate.set(2018,1,21,10,0,0)

        startCountdown(prakarshDate)
    }

    fun startCountdown(eventDate: Calendar) {
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

    fun remainingTimeText(millisLeft: Long): String {
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
            remainingText = "$remainingDays Days"
        }

        remainingText += "\n"

        remainingText += String.format("%02d Hours : %02d Minutes: %02d Seconds", remainingHours, remainingMinutes, remainingSeconds)
        return remainingText
    }
}