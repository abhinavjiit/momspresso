package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.UserDetailResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hemant on 23/1/17.
 */
public class ChooseLoginAccountAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflator;
    private ArrayList<UserDetailResult> userDetailList;
    private int currentCityId;
    private Typeface font;

    public ChooseLoginAccountAdapter(Context pContext, ArrayList<UserDetailResult> userDetailList) {
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = pContext;
        this.userDetailList = userDetailList;
        font = Typeface.createFromAsset(pContext.getAssets(), "fonts/" + "oswald.ttf");
    }

    @Override
    public int getCount() {
        return userDetailList == null ? 0 : userDetailList.size();
    }

    @Override
    public Object getItem(int position) {
        return userDetailList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        try {
            final ViewHolder holder;
            if (view == null) {
                view = mInflator.inflate(R.layout.choose_login_account_list_item, null);
                holder = new ViewHolder();
                holder.userNameTextView = (TextView) view.findViewById(R.id.userNameTextView);
                holder.userImageView = (ImageView) view.findViewById(R.id.userImageView);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            try {
                Picasso.get().load(userDetailList.get(position).getProfilePicUrl().getClientApp())
                        .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img).into(holder.userImageView);
            } catch (Exception e) {
                Picasso.get().load(R.drawable.default_commentor_img).into(holder.userImageView);
            }

            holder.userNameTextView.setText(userDetailList.get(position).getFirstName());

        } catch (Exception ex) {
            Crashlytics.logException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }

        return view;
    }

    class ViewHolder {
        ImageView userImageView;
        TextView userNameTextView;
    }

    public interface IOtherCity {
        void onOtherCityAdd(String cityName);
    }
}
