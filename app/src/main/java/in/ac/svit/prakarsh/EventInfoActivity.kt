package `in`.ac.svit.prakarsh

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_event_info.*
import kotlinx.android.synthetic.main.item_event_contact_details.view.*
import kotlinx.android.synthetic.main.item_event_details.view.*
import org.json.JSONArray

class EventInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_info)
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
                    var eventName = ""
                    if (response.has("eventName")) {
                        eventName = response.getString("eventName")
                    }

                    Log.d(javaClass.name, "JSON Successfully fetched - $eventName")
                    supportActionBar?.title = eventName

                    val eventDetailsList: ArrayList<EventDetails> = ArrayList()
                    val contactDetailsList: ArrayList<ContactDetails> = ArrayList()
                    var jsonArray = JSONArray()

                    if (response.has("details")) {
                        jsonArray = response.getJSONArray("details")
                    }

                    for (i in 0..(jsonArray.length() - 1)) {

                        var sectionHeader = ""
                        if (jsonArray.getJSONObject(i).has("sectionHeader")) {
                            sectionHeader = jsonArray.getJSONObject(i).getString("sectionHeader")
                        }

                        var sectionContent = ""
                        if (jsonArray.getJSONObject(i).has("sectionContent")) {
                            sectionContent = jsonArray.getJSONObject(i).getString("sectionContent")
                        }

                        eventDetailsList.add(EventDetails(sectionHeader, sectionContent))
                    }

                    jsonArray = response.getJSONArray("contactDetails")
                    for (i in 0..(jsonArray.length() - 1)) {

                        var name = ""
                        if (jsonArray.getJSONObject(i).has("name")) {
                            name = jsonArray.getJSONObject(i).getString("name")
                        }

                        var number = jsonArray.getJSONObject(i).getString("number")
                        if (jsonArray.getJSONObject(i).has("number")) {
                            number = jsonArray.getJSONObject(i).getString("number")
                            number = number.replace(" ", "")
                        }

                        contactDetailsList.add(ContactDetails(name, number))
                    }

                    event_info_rv_details?.apply {
                        layoutManager = LinearLayoutManager(applicationContext)
                        adapter = DetailsRecyclerAdapter(eventDetailsList)
                    }

                    event_info_rv_contact_details?.apply {
                        layoutManager = LinearLayoutManager(applicationContext)
                        adapter = ContactDetailsRecyclerAdapter(applicationContext, contactDetailsList)
                    }

                }, Response.ErrorListener { error ->
            Log.d(javaClass.name, "Volley Response Error Occurred, URL: $url Error: ${error.message}")
        })

        VolleySingleton.getInstance(applicationContext).requestQueue.add(req.setShouldCache(false))
    }

    private class EventDetails(val sectionHeader: String, val sectionContent: String)
    private class ContactDetails(val name: String, val number: String)

    private class DetailsRecyclerAdapter(private val eventDetailsList: ArrayList<EventDetails>) : RecyclerView.Adapter<DetailsRecyclerAdapter.CustomViewHolder>() {

        class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return eventDetailsList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomViewHolder {
            val layoutInflater = LayoutInflater.from(parent?.context)
            val cellForRow = layoutInflater.inflate(R.layout.item_event_details, parent, false)
            return CustomViewHolder(cellForRow)
        }

        override fun onBindViewHolder(holder: CustomViewHolder?, position: Int) {
            Log.d(javaClass.name, "$position - ${eventDetailsList[position].sectionHeader} adding views")
            holder?.view?.event_details_txt_title?.text = eventDetailsList[position].sectionHeader
            if (eventDetailsList[position].sectionContent == "") holder?.view?.event_details_txt_details?.visibility = View.GONE
            else holder?.view?.event_details_txt_details?.text = eventDetailsList[position].sectionContent
        }
    }

    private class ContactDetailsRecyclerAdapter(private val context: Context, private val contactDetailsList: ArrayList<ContactDetails>) : RecyclerView.Adapter<ContactDetailsRecyclerAdapter.CustomViewHolder>() {

        class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contactDetailsList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomViewHolder {
            val layoutInflater = LayoutInflater.from(parent?.context)
            val cellForRow = layoutInflater.inflate(R.layout.item_event_contact_details, parent, false)
            return CustomViewHolder(cellForRow)
        }

        override fun onBindViewHolder(holder: CustomViewHolder?, position: Int) {
            holder?.view?.event_contact_details_txt_name?.text = contactDetailsList[position].name
            holder?.view?.event_contact_details_txt_number?.text = contactDetailsList[position].number
            holder?.view?.event_contact_details_btn_call?.setOnClickListener {
                val callIntent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contactDetailsList[position].number, null))
                callIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context?.startActivity(callIntent)
            }
        }
    }

}
