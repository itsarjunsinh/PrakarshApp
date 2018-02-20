package `in`.ac.svit.prakarsh

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_events_subcategory.*
import kotlinx.android.synthetic.main.item_subcategory.view.*
import org.json.JSONArray

/**
 * Created by itsarjunsinh on 1/16/18.
 */
class EventsSubcategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_subcategory)
        Log.d(javaClass.name, "Started")

        // If dataUrl is received load views otherwise go to Main Activity.
        val dataUrl = intent.getStringExtra("url")
        if (dataUrl != null) {
            updateViewsFromJson(dataUrl)
        } else {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun updateViewsFromJson(url: String) {
        val req = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->

                    var categoryName = ""
                    if (response.has("categoryName")) {
                        categoryName = response.getString("categoryName")
                    }

                    Log.d(javaClass.name, "JSON Successfully fetched - $categoryName")
                    supportActionBar?.title = categoryName

                    val jsonArray: JSONArray = response.getJSONArray("events")
                    var subcategoryList: ArrayList<StackItem> = ArrayList()

                    for (i in 0..(jsonArray.length() - 1)) {

                        var name = ""
                        var tagline = ""
                        var imageUrl = ""
                        var dataUrl = ""

                        if (jsonArray.getJSONObject(i).has("name")) {
                            name = jsonArray.getJSONObject(i).getString("name")
                        }

                        if (jsonArray.getJSONObject(i).has("tagline")) {
                            tagline = jsonArray.getJSONObject(i).getString("tagline")
                        }

                        if (jsonArray.getJSONObject(i).has("imageUrl")) {
                            imageUrl = jsonArray.getJSONObject(i).getString("imageUrl")
                        }

                        if (jsonArray.getJSONObject(i).has("dataUrl")) {
                            dataUrl = jsonArray.getJSONObject(i).getString("dataUrl")
                        }

                        subcategoryList.add(StackItem(name, tagline, imageUrl, dataUrl))
                    }

                    events_subcategory_sv_main.adapter = StackAdapter(applicationContext, subcategoryList)

                }, Response.ErrorListener { error ->
            Log.d(javaClass.name, "Volley Response Error Occurred, URL: $url Error: ${error.message}")
        })

        VolleySingleton.getInstance(applicationContext).requestQueue.add(req.setShouldCache(false))
    }

    private class StackItem(val name: String, val tagline: String, val imageUrl: String, val dataUrl: String)

    private class StackAdapter(val context: Context, val subcategoryList: ArrayList<StackItem>) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = LayoutInflater.from(context).inflate(R.layout.item_subcategory, parent, false)

            view?.subcategory_txt_title?.text = subcategoryList[position].name
            view?.subcategory_txt_tagline?.text = subcategoryList[position].tagline
            view?.subcategory_img_banner?.apply {
                setDefaultImageResId(R.drawable.ic_image_black)
                setErrorImageResId(R.drawable.ic_broken_image_black)
                setImageUrl(subcategoryList[position].imageUrl, VolleySingleton.getInstance(context).imageLoader)
            }

            view?.setOnClickListener { openEventInfo(position) }
            view?.subcategory_btn_info?.setOnClickListener { openEventInfo(position) }

            return view
        }

        override fun getItem(position: Int): Any {
            return subcategoryList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return subcategoryList.size
        }

        fun openEventInfo(position: Int) {
            Log.d(javaClass.name, "${subcategoryList[position].name} Clicked")

            val intent = Intent(context, EventInfoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("url", subcategoryList[position].dataUrl)
            context?.startActivity(intent)
        }

    }

}