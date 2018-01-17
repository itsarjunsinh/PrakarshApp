package `in`.ac.svit.prakarsh

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_events_category.*

/**
 * Created by itsarjunsinh on 1/15/18.
 */
class EventsCategoryFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_events_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val url = context?.getString(R.string.url_events_category)
        val intent = Intent(context, EventsSubcategoryActivity::class.java)

        Util.loadCategoryRecycler(events_category_rv_main, url, context, intent)
    }
}