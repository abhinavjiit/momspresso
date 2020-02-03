package com.mycity4kids.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.mycity4kids.R
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import java.util.*

/**
 * View Pager Adapter to be used in search result
 *
 * @author kapil.vij
 */
class GroupMediaPostViewPagerAdapter(var mContext: Context) : PagerAdapter(), View.OnClickListener {
    private var mediaList: ArrayList<String?>? = null
    fun setDataList(mediaList: ArrayList<String?>?) {
        this.mediaList = mediaList
    }

    override fun getCount(): Int {
        return mediaList!!.size
    }

    override fun isViewFromObject(view: View, mObject: Any): Boolean {
        return view === mObject
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = container.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater.inflate(R.layout.group_media_pager_item, container, false)
        val iv = v.findViewById<View>(R.id.mediaImageView) as ImageView
        if (mediaList!![position] != null && !mediaList!![position]!!.trim { it <= ' ' }.isEmpty()) Picasso.with(mContext).load(mediaList!![position]).error(R.drawable.default_article).into(iv) else {
            iv.setBackgroundResource(R.drawable.default_article)
            iv.visibility = View.GONE
        }
        iv.tag = position
        iv.setOnClickListener(this)
        container.addView(v)
        return v
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View)
    }

    private lateinit var viewer: StfalconImageViewer<String>

    override fun onClick(v: View) {
        viewer = StfalconImageViewer.Builder<String>(mContext, mediaList, ::loadImage)
                .withStartPosition(v.tag as Int)
                .withTransitionFrom(v as ImageView)
                .show()
    }

    private fun loadImage(imageView: ImageView, url: String?) {
        imageView.apply {
            Picasso.with(mContext).load(url).into(this)
        }
    }
}