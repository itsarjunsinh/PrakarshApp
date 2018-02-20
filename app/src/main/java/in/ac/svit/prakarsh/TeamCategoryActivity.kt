package `in`.ac.svit.prakarsh

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_team_category.*
import kotlinx.android.synthetic.main.item_team_category.view.*
import org.json.JSONArray
import java.util.*

/**
 * Created by itsarjunsinh on 1/16/18.
 */
class TeamCategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_category)
        Log.d(javaClass.name, "Started")

        supportActionBar?.title = getString(R.string.title_team)
        updateViewFromJson()
    }

    private fun updateViewFromJson() {

        val url = getString(R.string.url_team_category)
        val req = JsonObjectRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    Log.d(javaClass.name, "JSON Successfully fetched")

                    val jsonArray: JSONArray = response.getJSONArray("category")
                    var dataAdapterList: ArrayList<TeamCategoryDataAdapter> = ArrayList()

                    for (i in 0..(jsonArray.length() - 1)) {

                        var name: String = ""
                        var iconUrl: String = ""
                        var dataUrl: String = ""

                        if (jsonArray.getJSONObject(i).has("name")) {
                            name = jsonArray.getJSONObject(i).getString("name")
                        }

                        if (jsonArray.getJSONObject(i).has("iconUrl")) {
                            iconUrl = jsonArray.getJSONObject(i).getString("iconUrl")
                        }

                        if (jsonArray.getJSONObject(i).has("dataUrl")) {
                            dataUrl = jsonArray.getJSONObject(i).getString("dataUrl")
                        }

                        dataAdapterList.add(TeamCategoryDataAdapter(name, iconUrl, dataUrl))
                    }

                    team_category_rv_main?.apply {
                        layoutManager = GridLayoutManager(applicationContext, 2)
                        adapter = TeamCategoryRecyclerAdapter(applicationContext, dataAdapterList)
                    }
                }, Response.ErrorListener { error ->
            Log.d(javaClass.name, "Volley Response Error Occurred, URL: $url Error: ${error.message}")
        })

        VolleySingleton.getInstance(applicationContext).requestQueue.add(req.setShouldCache(false))
    }

    class TeamCategoryDataAdapter(val name: String, val iconUrl: String, val dataUrl: String)

    class TeamCategoryRecyclerAdapter(private val context: Context, private val dataAdapterList: ArrayList<TeamCategoryDataAdapter>) : RecyclerView.Adapter<TeamCategoryRecyclerAdapter.CustomViewHolder>() {

        class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return dataAdapterList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomViewHolder {
            val layoutInflater = LayoutInflater.from(parent?.context)
            val cellForRow = layoutInflater.inflate(R.layout.item_team_category, parent, false)
            return CustomViewHolder(cellForRow)
        }

        override fun onBindViewHolder(holder: CustomViewHolder?, position: Int) {
            holder?.view?.team_category_txt_name?.text = dataAdapterList[position].name
            holder?.view?.team_category_img_icon?.apply {
                setDefaultImageResId(R.drawable.ic_image_black)
                setErrorImageResId(R.drawable.ic_broken_image_black)
                setImageUrl(dataAdapterList[position].iconUrl, VolleySingleton.getInstance(context).imageLoader)
            }

            holder?.view?.setOnClickListener {
                Log.d(javaClass.name, "${dataAdapterList[position].name} Clicked")
                var intent = Intent(context, TeamInfoActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("url", dataAdapterList[position].dataUrl)
                context?.startActivity(intent)
            }
        }
    }
}