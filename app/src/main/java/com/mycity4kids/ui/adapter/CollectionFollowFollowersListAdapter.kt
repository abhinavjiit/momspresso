package com.mycity4kids.ui.adapter

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.mycity4kids.R
import com.mycity4kids.application.BaseApplication
import com.mycity4kids.constants.AppConstants
import com.mycity4kids.constants.Constants
import com.mycity4kids.gtmutils.Utils
import com.mycity4kids.models.request.FollowUnfollowUserRequest
import com.mycity4kids.models.response.FollowUnfollowUserResponse
import com.mycity4kids.models.response.FollowersFollowingResult
import com.mycity4kids.preference.SharedPrefUtils
import com.mycity4kids.utils.StringUtils
import com.squareup.picasso.Picasso
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.android.synthetic.main.follower_following_list_item.view.*

class CollectionFollowFollowersListAdapter(val mContext: Context, val listType: String) :
    RecyclerView.Adapter<CollectionFollowFollowersListAdapter.ViewHolder>() {
    var position: Int = -1
    private var mDataList: ArrayList<FollowersFollowingResult>? = null
    val currentUserId = SharedPrefUtils.getUserDetailModel(mContext).dynamoId

    private var mInflater: LayoutInflater =
        BaseApplication.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.follower_following_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mDataList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.imgLoader.startAnimation(
            AnimationUtils.loadAnimation(
                mContext,
                R.anim.rotate_indefinitely
            )
        )
        holder.authorNameTextView.text =
            mDataList?.get(position)?.firstName + " " + mDataList?.get(position)?.lastName

        if (!StringUtils.isNullOrEmpty(mDataList?.get(position)?.profilePicUrl?.clientApp)) {
            Picasso.get().load(mDataList?.get(position)?.profilePicUrl?.clientApp)
                .placeholder(R.drawable.default_commentor_img)
                .error(R.drawable.default_commentor_img).into(holder.authorImageView)
        } else {
            Picasso.get().load(R.drawable.default_commentor_img).into(holder.authorImageView)
        }

        if (mDataList?.get(position)?.userId == currentUserId) {
            holder.followingTextView.visibility = View.INVISIBLE
            holder.followTextView.visibility = View.INVISIBLE
        } else {
            if (!mDataList?.get(position)?.isFollowed!!) {
                holder.followingTextView.visibility = View.INVISIBLE
                holder.followTextView.visibility = View.VISIBLE
            } else {
                holder.followingTextView.visibility = View.VISIBLE
                holder.followTextView.visibility = View.INVISIBLE
            }
        }

        holder.followingTextView.setOnClickListener {
            Log.d("Unfollow", "Unfollow")
            followUserAPI(position, holder)
        }
    }

    fun setData(mDatalist: ArrayList<FollowersFollowingResult>) {
        this.mDataList = mDatalist
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var authorImageView: ImageView = view.authorImageView
        var authorNameTextView: TextView = view.authorNameTextView
        var followTextView: TextView = view.followTextView
        var followingTextView: TextView = view.followingTextView
        var imgLoader: ImageView = view.imgLoader
        var relativeLoadingView: RelativeLayout = view.relativeLoadingView
    }

    private fun followUserAPI(position: Int, holder: ViewHolder) {
        val followUnfollowUserRequest = FollowUnfollowUserRequest()
        followUnfollowUserRequest.followee_id = mDataList?.get(position)?.userId
        var screenName = ""
        if (AppConstants.FOLLOWER_LIST == listType) {
            screenName = "FollowersListingScreen"
        } else {
            screenName = "FollowingListingScreen"
        }
        if (!mDataList?.get(position)?.isFollowed!!) {
            holder.relativeLoadingView.visibility = View.VISIBLE
            holder.followingTextView.visibility = View.INVISIBLE
            holder.followTextView.visibility = View.INVISIBLE
            val jsonString = Gson().toJson(followUnfollowUserRequest)
            Utils.pushGenericEvent(
                mContext,
                "CTA_Follow_Collection_Followers",
                SharedPrefUtils.getUserDetailModel(mContext).dynamoId,
                "CollectionFollowFollowersListAdapter"
            )
            FollowUnfollowAsyncTask(holder, "follow", position).execute(jsonString, "follow")
        } else {
            holder.relativeLoadingView.visibility = View.VISIBLE
            holder.followingTextView.visibility = View.INVISIBLE
            holder.followTextView.visibility = View.INVISIBLE
            val jsonString = Gson().toJson(followUnfollowUserRequest)
            Utils.pushGenericEvent(
                mContext,
                "CTA_Unfollow_Collection_Followers",
                SharedPrefUtils.getUserDetailModel(mContext).dynamoId,
                "CollectionFollowFollowersListAdapter"
            )
            FollowUnfollowAsyncTask(holder, "unfollow", position).execute(jsonString, "unfollow")
        }
    }

    private inner class FollowUnfollowAsyncTask(
        // The variable is moved here, we only need it here while displaying the
        // progress dialog.
        internal var viewHolder: ViewHolder,
        internal var type: String,
        internal var pos: Int
    ) : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg strings: String): String? {

            var JsonResponse: String? = null
            val JsonDATA = strings[0]

            var urlConnection: HttpURLConnection? = null
            var reader: BufferedReader? = null
            try {
                val url: URL
                if ("follow" == strings[1]) {
                    url = URL(AppConstants.BASE_URL + "follow/v2/users/follow")
                } else {
                    url = URL(AppConstants.BASE_URL + "follow/v2/users/unfollow")
                }

                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.doOutput = true
                // is output buffer writter
                urlConnection.requestMethod = "POST"
                urlConnection.setRequestProperty("Content-Type", "application/json")
                urlConnection.setRequestProperty("Accept", "application/json")
                urlConnection.addRequestProperty(
                    "id",
                    SharedPrefUtils.getUserDetailModel(mContext).dynamoId
                )
                urlConnection.addRequestProperty(
                    "mc4kToken",
                    SharedPrefUtils.getUserDetailModel(mContext).mc4kToken
                )

                // set headers and method
                val writer = BufferedWriter(OutputStreamWriter(urlConnection.outputStream, "UTF-8"))
                writer.write(JsonDATA)
                // json data
                writer.close()
                val inputStream = urlConnection.inputStream
                // input stream
                val buffer = StringBuffer()
                if (inputStream == null) {
                    // Nothing to do.
                    return null
                }
                reader = BufferedReader(InputStreamReader(inputStream))

                var inputLine: String
                while (reader.readLine() != null) {
                    inputLine = reader.readLine()
                    buffer.append(inputLine + "\n")
                }
                if (buffer.length == 0) {
                    // Stream was empty. No point in parsing.
                    return null
                }
                JsonResponse = buffer.toString()

                Log.i("RESPONSE $type", JsonResponse)
                // send to post execute
                return JsonResponse
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                urlConnection?.disconnect()
                if (reader != null) {
                    try {
                        reader.close()
                    } catch (e: IOException) {
                        Log.e("TAAGG", "Error closing stream", e)
                    }
                }
            }
            return null
        }

        override fun onPostExecute(result: String?) {

            if (result == null) {
                resetFollowUnfollowStatus()
                return
            }
            try {
                val responseData = Gson().fromJson(result, FollowUnfollowUserResponse::class.java)
                if ((responseData.code == 200) and (Constants.SUCCESS == responseData.status)) {
                    for (i in mDataList?.indices!!) {
                        if (mDataList?.get(i)?.userId == responseData.data.result) {
                            if ("follow" == type) {
                                mDataList?.get(i)?.isFollowed = true
                                viewHolder.relativeLoadingView.visibility = View.GONE
                                viewHolder.followingTextView.visibility = View.VISIBLE
                                viewHolder.followTextView.visibility = View.INVISIBLE
                            } else {
                                mDataList?.get(i)?.isFollowed = false
                                viewHolder.relativeLoadingView.visibility = View.GONE
                                viewHolder.followTextView.visibility = View.VISIBLE
                                viewHolder.followingTextView.visibility = View.INVISIBLE
                            }
                        }
                    }
                } else {
                    resetFollowUnfollowStatus()
                }
            } catch (e: Exception) {
                resetFollowUnfollowStatus()
            }
        }

        internal fun resetFollowUnfollowStatus() {
            viewHolder.relativeLoadingView.visibility = View.GONE
            if (type == "follow") {
                viewHolder.followingTextView.visibility = View.INVISIBLE
                viewHolder.followTextView.visibility = View.VISIBLE
            } else {
                viewHolder.followingTextView.visibility = View.VISIBLE
                viewHolder.followTextView.visibility = View.INVISIBLE
            }
        }
    }
}
