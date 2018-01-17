package `in`.ac.svit.prakarsh

import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.util.*
import kotlinx.android.synthetic.main.item_category.view.*
import org.json.JSONArray

/**
 * Created by itsarjunsinh on 1/12/18.
 */

open class Util {
    companion object {

        fun startCountdown(timer: TextView?, eventDate: Calendar, context: Context?) {
            val finishedText = "${context?.getString(R.string.event_name)}!"
            val today = Calendar.getInstance()
            val timeInterval = eventDate.timeInMillis - today.timeInMillis
            if (timeInterval > 1) {
                val countDown = object : CountDownTimer(timeInterval, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        timer?.text = (remainingTimeText(millisUntilFinished))
                    }

                    override fun onFinish() {
                        timer?.text = finishedText
                    }
                }
                countDown.start()
            } else {
                timer?.text = finishedText
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

        fun loadCategoryRecycler(recyclerView: RecyclerView?, url: String?, context: Context?, intent: Intent) {
            recyclerView?.layoutManager = GridLayoutManager(context,2)
            val que = Volley.newRequestQueue(context)
            val req = JsonObjectRequest(Request.Method.GET,url,null,
                    Response.Listener {
                        response ->
                        Log.d(javaClass.name,"JSON Successfully fetched")
                        val jsonArray: JSONArray = response.getJSONArray("category")
                        var dataAdapterList: ArrayList<CategoryDataAdapter> = ArrayList()
                        for (i in 0..(jsonArray.length()-1)){
                            val eventTitle = jsonArray.getJSONObject(i).getString("name")
                            dataAdapterList.add(CategoryDataAdapter(eventTitle))
                        }
                        recyclerView?.adapter = CategoryRecyclerAdapter(intent, context, dataAdapterList)
                    }, Response.ErrorListener {
                error ->
                Log.d(javaClass.name,"Volley Response Error Occurred, URL: $url Error: ${error.message}")
            })
            que.add(req)
        }

        class CategoryDataAdapter(val title: String)

        class CategoryRecyclerAdapter(private val intent: Intent, private val context: Context?, private val dataAdapterList: ArrayList<CategoryDataAdapter>): RecyclerView.Adapter<CategoryRecyclerAdapter.CustomViewHolder>() {

            class CustomViewHolder(val view: View): RecyclerView.ViewHolder(view)

            override fun getItemCount(): Int {
                return dataAdapterList.size
            }

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomViewHolder {
                val layoutInflater = LayoutInflater.from(parent?.context)
                val cellForRow = layoutInflater.inflate(R.layout.item_category, parent, false)
                return CustomViewHolder(cellForRow)
            }

            override fun onBindViewHolder(holder: CustomViewHolder?, position: Int) {
                val tempDescription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
                holder?.view?.category_txt_title?.text = dataAdapterList[position].title
                holder?.view?.category_txt_description?.text = tempDescription
                holder?.view?.setOnClickListener{
                    // Implement onClickListener
                    Log.d(javaClass.name,"${dataAdapterList[position].title} Clicked")
                    intent.putExtra("url", "temp")
                    context?.startActivity(intent)
                }
            }
        }
    }
}