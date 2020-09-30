package com.mycity4kids.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.models.response.UserDetailResult;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

/**
 * Created by hemant on 23/1/17.
 */
public class ChooseLoginAccountAdapter extends BaseAdapter {

    private ArrayList<UserDetailResult> userDetailList;

    public ChooseLoginAccountAdapter(ArrayList<UserDetailResult> userDetailList) {
        this.userDetailList = userDetailList;
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
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_login_account_list_item, null);
                holder = new ViewHolder();
                holder.userNameTextView = view.findViewById(R.id.userNameTextView);
                holder.userImageView = view.findViewById(R.id.userImageView);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            try {
                Picasso.get().load(userDetailList.get(position).getProfilePicUrl().getClientApp())
                        .placeholder(R.drawable.default_commentor_img).error(R.drawable.default_commentor_img)
                        .into(holder.userImageView);
            } catch (Exception e) {
                Picasso.get().load(R.drawable.default_commentor_img).into(holder.userImageView);
            }
            holder.userNameTextView.setText(userDetailList.get(position).getFirstName());
        } catch (Exception ex) {
            FirebaseCrashlytics.getInstance().recordException(ex);
            Log.d("MC4kException", Log.getStackTraceString(ex));
        }
        return view;
    }

    class ViewHolder {

        ImageView userImageView;
        TextView userNameTextView;
    }
}
