package `in`.ac.svit.prakarsh

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_newsfeed.*
import kotlinx.android.synthetic.main.item_newsfeed_post.view.*


/**
 * Created by arjun on 20-02-2018.
 */

class NewsfeedFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_newsfeed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPosts()
    }

    private fun loadPosts() {
        val colRef = FirebaseFirestore.getInstance().collection("newsfeed")
        colRef.get().addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val postList = ArrayList<Post>()

                for (document in task.result.reversed()) {

                    var caption = ""
                    if (document.contains("caption")) {
                        caption = document.getString("caption")
                    }
                    var date = ""
                    if (document.contains("date")) {
                        date = document.getString("date")
                    }

                    var time = ""
                    if (document.contains("time")) {
                        time = document.getString("time")
                    }

                    var imageUrl = ""
                    if (document.contains("imageUrl")) {
                        imageUrl = document.getString("imageUrl")
                    }

                    postList.add(Post(caption, date, time, imageUrl))
                }

                newsfeed_rv_posts?.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = PostsRecyclerAdapter(postList)
                }

                newsfeed_layout_feed?.visibility = View.VISIBLE
                newsfeed_txt_label_loading?.visibility = View.GONE
            } else {
                Log.d(javaClass.name, "Error getting documents: ", task.exception)
            }
        }
    }

    private class Post(val caption: String, val date: String, val time: String, val imageUrl: String)

    private class PostsRecyclerAdapter(val postList: ArrayList<Post>) : RecyclerView.Adapter<PostsRecyclerAdapter.CustomViewHolder>() {

        class CustomViewHolder(val view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomViewHolder {
            val layoutInflater = LayoutInflater.from(parent?.context)
            val cellForRow = layoutInflater.inflate(R.layout.item_newsfeed_post, parent, false)
            return CustomViewHolder(cellForRow)
        }

        override fun getItemCount(): Int {
            return postList.size
        }

        override fun onBindViewHolder(holder: CustomViewHolder?, position: Int) {
            holder?.view?.newsfeed_post_txt_caption?.text = postList[position].caption
            holder?.view?.newsfeed_post_txt_date?.text = postList[position].date
            holder?.view?.newsfeed_post_txt_time?.text = postList[position].time

            holder?.view?.newsfeed_post_img_main?.apply {
                setErrorImageResId(R.drawable.ic_broken_image_black)
                setImageUrl(postList[position].imageUrl, VolleySingleton.getInstance(context).imageLoader)
            }

        }
    }
}