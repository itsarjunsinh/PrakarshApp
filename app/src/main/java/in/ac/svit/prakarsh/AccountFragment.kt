package `in`.ac.svit.prakarsh

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
import kotlinx.android.synthetic.main.item_promotion_image.view.*
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream


/**
 * Created by itsarjunsinh on 1/28/18.
 */

class AccountFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var promoImage: Bitmap
    private var imageLabel: String? = ""

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {

            var flag = 1

            for (i in 0..(permissions.size - 1)) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) flag = 0
            }

            if (flag == 1) {
                Log.d(javaClass.name, "All permissions were granted.")
                saveImage()
            } else {
                Log.d(javaClass.name, "A permission request was declined.")
            }

        }
    }

    private fun updateUI(user: FirebaseUser?) {

        account_btn_login?.visibility = View.GONE
        account_btn_logout?.visibility = View.GONE
        account_img_user?.setDefaultImageResId(R.drawable.ic_person_black)

        if (user != null) {

            //Make views visible
            account_layout_promotion?.visibility = View.VISIBLE

            //Handle user logout
            account_btn_logout?.apply {
                visibility = View.VISIBLE
                setOnClickListener {
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
            }

            loadData()

        } else {

            //Empty Views (In case user just logged out)
            account_txt_name?.text = ""
            account_txt_department?.text = ""
            account_txt_city?.text = ""
            account_img_user?.setImageUrl(null, VolleySingleton.getInstance(context).imageLoader)
            account_layout_promotion?.visibility = View.GONE

            //Show log in status in TextView for College Name
            account_txt_college?.text = "Not logged in."

            //Show Login button
            account_btn_login?.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    val intent = Intent(context?.applicationContext, SignInActivity::class.java)
                    startActivity(intent)
                }
            }

        }
    }

    private fun loadData() {

        //Fetch user's information from Firestore document and display
        val docRef = FirebaseFirestore.getInstance().collection("users").document("${mAuth.currentUser?.uid}")
        docRef.get().addOnCompleteListener { task ->
            val document = task.result
            account_txt_name?.text = document?.getString("name")
            account_txt_college?.text = document?.getString("collegeName")
            account_txt_department?.text = document?.getString("department")
            account_txt_city?.text = document?.getString("city")
        }

        val imageUrl = mAuth.currentUser?.photoUrl.toString()
        account_img_user?.setImageUrl(imageUrl, VolleySingleton.getInstance(context).imageLoader)

        val url = getString(R.string.url_promotion)
        val req = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response ->

            val promotionImageList: ArrayList<PromotionImage> = ArrayList()
            var promotionImages = JSONArray()

            if (response.has("promotionTitle")) {
                account_txt_promotion_title?.text = response.getString("promotionTitle")
            }

            if (response.has("promotionMessage")) {
                account_txt_promotion_message?.text = response.getString("promotionMessage")
            }

            if (response.has("promotionImagesTitle")) {
                account_txt_promotion_images_title?.text = response.getString("promotionImagesTitle")
            }

            if (response.has("promotionImages")) {
                promotionImages = response.getJSONArray("promotionImages")
            }

            for (i in 0..(promotionImages.length() - 1)) {

                var label = ""
                if (promotionImages.getJSONObject(i).has("label")) {
                    label = promotionImages.getJSONObject(i).getString("label")
                }

                var imageUrl = ""
                if (promotionImages.getJSONObject(i).has("imageUrl")) {
                    imageUrl = promotionImages.getJSONObject(i).getString("imageUrl")
                }

                promotionImageList.add(PromotionImage(label, imageUrl))
            }

            account_rv_promotion_images?.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = PromotionImageAdapter(promotionImageList)
            }

        }, Response.ErrorListener {

            Log.d(javaClass.name, "Failed to load $url")
            account_txt_promotion_message?.text = "Could not retrieve information."

        })

        VolleySingleton.getInstance(context).requestQueue.add(req.setShouldCache(false))
    }

    private class PromotionImage(val label: String, val imageUrl: String)

    class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private inner class PromotionImageAdapter(private val promotionImageList: ArrayList<PromotionImage>) : RecyclerView.Adapter<CustomViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomViewHolder {
            val layoutInflater = LayoutInflater.from(parent?.context)
            val cellForRow = layoutInflater.inflate(R.layout.item_promotion_image, parent, false)
            return CustomViewHolder(cellForRow)
        }

        override fun getItemCount(): Int {
            return promotionImageList.size
        }

        override fun onBindViewHolder(holder: CustomViewHolder?, position: Int) {
            holder?.view?.promotion_image_txt_label?.text = promotionImageList[position].label
            holder?.view?.promotion_image_img_promo?.apply {
                setImageUrl(promotionImageList[position].imageUrl, VolleySingleton.getInstance(context).imageLoader)
                addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                    override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                        holder.view.promotion_image_layout_download?.setOnClickListener {

                            holder.view.promotion_image_img_promo?.buildDrawingCache()
                            val promoImageBitmap = holder.view.promotion_image_img_promo?.drawingCache

                            if (context != null) {

                                Log.d(javaClass.name, "Context is not null.")

                                imageLabel = promotionImageList[position].label
                                promoImage = promoImageBitmap!!

                                val storageRead = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                                val storageWrite = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)

                                if (storageRead == PackageManager.PERMISSION_GRANTED && storageWrite == PackageManager.PERMISSION_GRANTED) {
                                    Log.d(javaClass.name, "Storage permission has already been granted.")
                                    saveImage()
                                } else {
                                    Log.d(javaClass.name, "Requesting storage permission.")
                                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                                }

                            }
                        }
                    }
                })
            }
        }
    }

    private fun saveImage() {
        val fileName = "$imageLabel.png"
        val filePath = "/Pictures/Prakarsh 2018"

        try {
            val storage: File = Environment.getExternalStorageDirectory()
            val dir = File(storage.absolutePath + filePath)
            dir.mkdirs()

            val outFile = File(dir, fileName)
            val outStream = FileOutputStream(outFile)

            promoImage?.compress(Bitmap.CompressFormat.PNG, 100, outStream)

            outStream.flush()
            outStream.close()

            Snackbar.make(account_layout_main, "Image saved in $filePath", Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.d(javaClass.name, "Failed to save image. Exception: ${e.message}", e)
            Snackbar.make(account_layout_main, "Could not save image.", Snackbar.LENGTH_SHORT).show()
        }
    }
}