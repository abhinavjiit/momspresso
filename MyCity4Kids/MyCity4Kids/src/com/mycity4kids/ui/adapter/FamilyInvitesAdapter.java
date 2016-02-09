package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.controller.FamilyInvitationController;
import com.mycity4kids.newmodels.FamilyInvites;

import java.util.List;

/**
 * Created by hemant on 13/1/16.
 */
public class FamilyInvitesAdapter extends ArrayAdapter<FamilyInvites> {

    List<FamilyInvites> list;
    Context mContext;
    LayoutInflater mInflater;
    FamilyInvitationController familyInvitationController;
    InvitationResponse invitationResponse;

    public FamilyInvitesAdapter(Context context, int resource, List<FamilyInvites> objects, InvitationResponse invitationResponse) {
        super(context, resource, objects);
        mContext = context;
        list = objects;
        mInflater = LayoutInflater.from(context);
        this.invitationResponse = invitationResponse;
    }

    static class ViewHolder {
        TextView name;
        TextView email;
        TextView kidsName;
        TextView cancelInvitation;
        TextView acceptInvitation;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.invite_item, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.inviteeName);
            holder.email = (TextView) convertView.findViewById(R.id.inviteeEmail);
            holder.kidsName = (TextView) convertView.findViewById(R.id.inviteeKids);
            holder.cancelInvitation = (TextView) convertView.findViewById(R.id.cancelInvitation);
            holder.acceptInvitation = (TextView) convertView.findViewById(R.id.acceptInvitation);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText(list.get(position).getFirstName());
        holder.email.setText(list.get(position).getEmail());
        holder.kidsName.setText(list.get(position).getKidName());
        holder.cancelInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Cancel Invitation", "Position = " + position);
                invitationResponse.onInvitationResponse("cancel", position);
            }
        });

        holder.acceptInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Accept Invitation", "Position = " + position);
                invitationResponse.onInvitationResponse("accept", position);
            }
        });

        return convertView;
    }

    public interface InvitationResponse {
        public void onInvitationResponse(String response, int position);
    }
}