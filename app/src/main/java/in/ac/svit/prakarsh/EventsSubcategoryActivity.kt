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
import com.android.volley.toolbox.Volley
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

        updateViewsFromJson(intent.getStringExtra("url"))
    }

    private fun updateViewsFromJson(url: String) {
        try {
            val que = Volley.newRequestQueue(applicationContext)
            val req = JsonObjectRequest(Request.Method.GET,url,null,
                    Response.Listener {
                        response ->
                        Log.d(javaClass.name,"JSON Successfully fetched")

                        val jsonArray: JSONArray = response.getJSONArray("events")
                        var subcategoryList: ArrayList<StackItem> = ArrayList()

                        for (i in 0..(jsonArray.length()-1)) {
                            val name = jsonArray.getJSONObject(i).getString("name")
                            val tagline = jsonArray.getJSONObject(i).getString("tagline")
                            val imageUrl = jsonArray.getJSONObject(i).getString("imageUrl")
                            val dataUrl = jsonArray.getJSONObject(i).getString("dataUrl")
                            subcategoryList.add(StackItem(name,tagline,imageUrl,dataUrl))
                        }

                        events_subcategory_sv_main.adapter = StackAdapter(this, subcategoryList)

                    }, Response.ErrorListener {
                error ->
                Log.d(javaClass.name,"Volley Response Error Occurred, URL: $url Error: ${error.message}")
            })
            que.add(req)
        }catch (e: Exception){
            Log.d(javaClass.name,"Exception caught during Volley Request.")
        }
    }

    private class StackItem(val name: String, val tagline: String, val imageUrl: String, val dataUrl: String)

    private inner class StackAdapter(val context: Context, val subcategoryList: ArrayList<StackItem>): BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = LayoutInflater.from(context).inflate(R.layout.item_subcategory, parent, false)

            view?.subcategory_txt_title?.text=subcategoryList[position].name
            view?.subcategory_txt_tagline?.text=subcategoryList[position].tagline
            view?.subcategory_img_banner?.setDefaultImageResId(R.drawable.ic_image_black)
            view?.subcategory_img_banner?.setImageUrl(subcategoryList[position].imageUrl,VolleySingleton.getInstance(applicationContext).imageLoader)
            view?.subcategory_img_banner?.setErrorImageResId(R.drawable.ic_broken_image_black)
            view?.setOnClickListener{ openEventInfo(position) }
            view?.subcategory_btn_info?.setOnClickListener{ openEventInfo(position) }

            Log.d(javaClass.name,"Returning ${subcategoryList[position].name} view")
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

        fun openEventInfo(position: Int){
            Log.d(javaClass.name,"${subcategoryList[position].name} Clicked")

            val intent = Intent(applicationContext, EventInfoActivity::class.java)
            intent.putExtra("url", subcategoryList[position].dataUrl)
            startActivity(intent)
        }

    }

}