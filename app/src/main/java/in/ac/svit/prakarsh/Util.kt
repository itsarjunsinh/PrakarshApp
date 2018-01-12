package `in`.ac.svit.prakarsh

import android.os.CountDownTimer
import android.widget.TextView
import java.util.*

/**
 * Created by itsarjunsinh on 1/12/18.
 */

open class Util {
    companion object {
        fun startCountdown(txt_timer: TextView, eventDate: Calendar) {
            val finishedText = "Prakarsh ${eventDate.get(Calendar.YEAR)}!"
            val today = Calendar.getInstance()
            val timeInterval = eventDate.timeInMillis - today.timeInMillis
            if (timeInterval>1){
                val countDown = object : CountDownTimer(timeInterval, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        txt_timer.text = (remainingTimeText(millisUntilFinished))
                    }

                    override fun onFinish() {
                        txt_timer.text = finishedText
                    }
                }
                countDown.start()
            }
            else{
                txt_timer.text=finishedText
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
                remainingText="$remainingDays days : "
            }
            remainingText+= String.format("%02d hours : %02d minutes: %02d seconds",remainingHours,remainingMinutes,remaingSeconds)
            return remainingText
        }
    }
}