package `in`.ac.svit.prakarsh

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.fragment_events_category.*
import kotlinx.android.synthetic.main.item_category.view.*
import org.json.JSONArray
import java.util.ArrayList

/**
 * Created by itsarjunsinh on 1/15/18.
 */
class EventsCategoryFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_events_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.name,"Started")

        updateViewsFromJson()
    }

    private fun updateViewsFromJson() {
        val url = context?.getString(R.string.url_events_category)
        events_category_rv_main?.layoutManager = GridLayoutManager(context,2)
        val que = Volley.newRequestQueue(context)
        val req = JsonObjectRequest(Request.Method.GET,url,null,
                Response.Listener {
                    response ->
                    Log.d(javaClass.name,"JSON Successfully fetched")

                    val jsonArray: JSONArray = response.getJSONArray("category")
                    var dataAdapterList: ArrayList<CategoryDataAdapter> = ArrayList()

                    for (i in 0..(jsonArray.length()-1)){

                        var name = ""
                        var tagline = ""
                        var department = ""
                        var iconUrl = ""
                        var dataUrl = ""

                        if(jsonArray.getJSONObject(i).has("name")) {
                            name = jsonArray.getJSONObject(i).getString("name")
                        }

                        if(jsonArray.getJSONObject(i).has("tagline")) {
                            tagline = jsonArray.getJSONObject(i).getString("tagline")
                        }

                        if(jsonArray.getJSONObject(i).has("department")) {
                            department = jsonArray.getJSONObject(i).getString("department")
                        }

                        if(jsonArray.getJSONObject(i).has("iconUrl")) {
                            iconUrl = jsonArray.getJSONObject(i).getString("iconUrl")
                        }

                        if(jsonArray.getJSONObject(i).has("dataUrl")) {
                            dataUrl = jsonArray.getJSONObject(i).getString("dataUrl")
                        }

                        dataAdapterList.add(CategoryDataAdapter(name, tagline, department, iconUrl, dataUrl))
                    }
                    events_category_rv_main?.adapter = CategoryRecyclerAdapter(context, dataAdapterList)
                }, Response.ErrorListener {
            error ->
            Log.d(javaClass.name,"Volley Response Error Occurred, URL: $url Error: ${error.message}")
        })
        que.add(req)
    }

    class CategoryDataAdapter(val title: String, val tagline: String, val department: String, val iconUrl: String, val dataUrl: String)

    class CategoryRecyclerAdapter(private val context: Context?, private val dataAdapterList: ArrayList<CategoryDataAdapter>): RecyclerView.Adapter<CategoryRecyclerAdapter.CustomViewHolder>() {

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
            holder?.view?.category_txt_title?.text = dataAdapterList[position].title
            holder?.view?.category_txt_tagline?.text = dataAdapterList[position].tagline
            holder?.view?.category_txt_department?.text = dataAdapterList[position].department
            holder?.view?.category_img_icon?.setDefaultImageResId(R.drawable.ic_image_black)
            holder?.view?.category_img_icon?.setErrorImageResId(R.drawable.ic_broken_image_black)
            holder?.view?.category_img_icon?.setImageUrl(dataAdapterList[position].iconUrl,VolleySingleton.getInstance(context).imageLoader)
            holder?.view?.setOnClickListener{
                Log.d(javaClass.name,"${dataAdapterList[position].title} Clicked")
                var intent = Intent(context, EventsSubcategoryActivity::class.java)
                intent.putExtra("url", dataAdapterList[position].dataUrl)
                context?.startActivity(intent)
            }
        }
    }
}