package `in`.ac.svit.prakarsh

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_account.*

/**
 * Created by itsarjunsinh on 1/28/18.
 */

class AccountFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        updateUI(mAuth.currentUser)
    }

    override fun onResume() {
        super.onResume()

        mAuth = FirebaseAuth.getInstance()
        updateUI(mAuth.currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {

        account_txt_name?.text = ""
        account_txt_college?.text = ""
        account_txt_department?.text = ""
        account_txt_city?.text = ""

        account_btn_login?.visibility = View.GONE
        account_btn_logout?.visibility = View.GONE
        account_card_promotion?.visibility = View.GONE

        account_img_user?.setDefaultImageResId(R.drawable.ic_person_black)
        account_img_user?.setImageUrl(null, VolleySingleton.getInstance(context).imageLoader)

        Log.d(javaClass.name,"UID: ${user?.uid}")
        if(user != null){

            account_card_promotion?.visibility = View.VISIBLE
            account_btn_logout?.visibility = View.VISIBLE

            account_btn_logout?.setOnClickListener{
                val alertDialog = AlertDialog.Builder(context)
                with(alertDialog) {
                    setTitle("Log Out")
                    setMessage("Are you sure?")

                    setPositiveButton("Yes") { _, _ ->
                        Log.d(javaClass.name, "Trying to logout")
                        mAuth.signOut()
                        updateUI(null)
                    }
                    setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                }
                alertDialog.show()
            }

            loadData()

        } else {

            //Show log in status in TextView for College Name
            account_txt_college?.text = "Not logged in."

            account_btn_login?.visibility = View.VISIBLE
            account_btn_login?.setOnClickListener{
                val intent = Intent(context?.applicationContext, SignInActivity::class.java)
                startActivity(intent)
            }

        }
    }

    private fun loadData(){

        val docRef = FirebaseFirestore.getInstance().collection("users").document("${mAuth.currentUser?.uid}")
        docRef.get().addOnCompleteListener {
            task ->
            val document = task.result
            account_txt_name?.text = document?.getString("name")
            account_txt_college?.text = document?.getString("collegeName")
            account_txt_department?.text = document?.getString("department")
            account_txt_city?.text = document?.getString("city")
        }

        val imageUrl = mAuth.currentUser?.photoUrl.toString()
        account_img_user?.setImageUrl(imageUrl, VolleySingleton.getInstance(context).imageLoader)

        val url = getString(R.string.url_promotion)
        val req = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
            response ->

                if(response.has("promotionTitle")) {
                    account_txt_promotion_title?.text = response.getString("promotionTitle")
                }
                if(response.has("promotionMessage")) {
                    account_txt_promotion_message?.text = response.getString("promotionMessage")
                }

        }, Response.ErrorListener {

            Log.d(javaClass.name, "Failed to load $url")
            account_txt_promotion_message?.text = "Could not retrieve information."

        })

        VolleySingleton.getInstance(context).requestQueue.add(req.setShouldCache(false))
    }
}