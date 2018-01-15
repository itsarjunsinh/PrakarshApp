package `in`.ac.svit.prakarsh

import android.content.Context
import android.os.CountDownTimer
import android.widget.TextView
import java.util.*

/**
 * Created by itsarjunsinh on 1/12/18.
 */

open class Util {

    fun startCountdown(timer: TextView?, eventDate: Calendar, context: Context?) {
        val finishedText = "${context?.getString(R.string.event_name)}!"
        val today = Calendar.getInstance()
        val timeInterval = eventDate.timeInMillis - today.timeInMillis
        if (timeInterval>1){
            val countDown = object : CountDownTimer(timeInterval, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timer?.text = (remainingTimeText(millisUntilFinished))
                }

                override fun onFinish() {
                    timer?.text = finishedText
                }
            }
            countDown.start()
        }
        else{
            timer?.text=finishedText
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
        val remaingSeconds = (millisLeft % MINUTE) / SECOND
        var remainingText = ""

        if (remainingDays>0)
        {
            remainingText="$remainingDays Days"
        }

        remainingText += "\n"

        remainingText += String.format("%02d Hours : %02d Minutes: %02d Seconds",remainingHours,remainingMinutes,remaingSeconds)
        return remainingText
    }
}