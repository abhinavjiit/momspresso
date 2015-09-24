package com.mycity4kids.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseFragment;
import com.mycity4kids.R;
import com.mycity4kids.newmodels.AttendeeModel;
import com.mycity4kids.ui.adapter.AttendeeCustomAdapter;

import java.util.ArrayList;

/**
 * Created by user on 08-06-2015.
 */
public class FragmentViewPager extends BaseFragment {

    ArrayList<AttendeeModel> data;
    ListView listView;
    TextView cancel, done;
    AttendeeCustomAdapter adapter;
    public ArrayList<Integer> chklist;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.view_pager, container,
                false);

        ViewPager gridGallery = (ViewPager)rootView.findViewById(R.id.myviewpager);
        Bundle extras = getArguments();
        if (extras != null) {

//            ArrayList<String> _ImagesAraryList = extras.getStringArrayList("imagelist");
//            int position = extras.getInt("position");
//
//            ImagesViewPagerAdapter mPhotosAdapter = new ImagesViewPagerAdapter(getActivity(), _ImagesAraryList);
//            gridGallery.setAdapter(mPhotosAdapter);
//            gridGallery.setCurrentItem(position);
        }



        return  rootView;
    }



    @Override
    protected void updateUi(Response response) {

    }
}
