package com.mycity4kids.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mycity4kids.R;
import com.mycity4kids.models.Topics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 08-06-2015.
 */
public class AudioPickerDialogFragment extends DialogFragment implements OnClickListener {

    private Toolbar mToolbar;
    private TextView audioTextView1, audioTextView2, audioTextView3, audioTextView4, audioTextView5, audioTextView6, audioTextView7, audioTextView8, audioTextView9;
    private ProgressDialog mProgressDialog;

    private File file1, file2, file3, file4, file5, file6, file7, file8;

    private IAudioSelectionComplete iAudioSelectionComplete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.audio_picker_dialog_fragment, container,
                false);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mToolbar.setTitle("Filter");
        mToolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.colorControlNormal));
        Drawable upArrow = ContextCompat.getDrawable(getActivity(), R.drawable.back_arroow);
        upArrow.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorControlNormal), PorterDuff.Mode.SRC_ATOP);
        mToolbar.setNavigationIcon(upArrow);
        mToolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // handle back button naviagtion
                dismiss();
            }
        });
        audioTextView1 = (TextView) rootView.findViewById(R.id.audioTextView1);
        audioTextView2 = (TextView) rootView.findViewById(R.id.audioTextView2);
        audioTextView3 = (TextView) rootView.findViewById(R.id.audioTextView3);
        audioTextView4 = (TextView) rootView.findViewById(R.id.audioTextView4);
        audioTextView5 = (TextView) rootView.findViewById(R.id.audioTextView5);
        audioTextView6 = (TextView) rootView.findViewById(R.id.audioTextView6);
        audioTextView7 = (TextView) rootView.findViewById(R.id.audioTextView7);
        audioTextView8 = (TextView) rootView.findViewById(R.id.audioTextView8);
        audioTextView1.setOnClickListener(this);
        audioTextView2.setOnClickListener(this);
        audioTextView3.setOnClickListener(this);
        audioTextView4.setOnClickListener(this);
        audioTextView5.setOnClickListener(this);
        audioTextView6.setOnClickListener(this);
        audioTextView7.setOnClickListener(this);
        audioTextView8.setOnClickListener(this);

        InputStream inputStream1 = getResources().openRawResource(R.raw.audio1);
        InputStream inputStream2 = getResources().openRawResource(R.raw.audio2);
        InputStream inputStream3 = getResources().openRawResource(R.raw.audio3);
        InputStream inputStream4 = getResources().openRawResource(R.raw.audio4);
        InputStream inputStream5 = getResources().openRawResource(R.raw.audio5);
        InputStream inputStream6 = getResources().openRawResource(R.raw.audio6);
        InputStream inputStream7 = getResources().openRawResource(R.raw.audio7);
        InputStream inputStream8 = getResources().openRawResource(R.raw.audio8);

        file1 = createFileFromInputStream(inputStream1, "audio1.aac");
        file2 = createFileFromInputStream(inputStream2, "audio2.aac");
        file3 = createFileFromInputStream(inputStream3, "audio3.aac");
        file4 = createFileFromInputStream(inputStream4, "audio4.aac");
        file5 = createFileFromInputStream(inputStream5, "audio5.aac");
        file6 = createFileFromInputStream(inputStream6, "audio6.aac");
        file7 = createFileFromInputStream(inputStream7, "audio7.aac");
        file8 = createFileFromInputStream(inputStream8, "audio8.aac");

        return rootView;
    }

    private File createFileFromInputStream(InputStream inputStream, String my_file_name) {

        try {
            File f = new File(Environment.getExternalStorageDirectory() + "/MyCity4Kids/" + my_file_name);
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        } catch (IOException e) {
            //Logging exception
        }

        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.iAudioSelectionComplete = (IAudioSelectionComplete) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_bg_rounded_corners));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onClick(View v) {
        showProgressDialog("please wait");
        switch (v.getId()) {
            case R.id.audioTextView1:
                iAudioSelectionComplete.onAudioSelectionComplete("audio1.aac");
                break;
            case R.id.audioTextView2:
                iAudioSelectionComplete.onAudioSelectionComplete("audio2.aac");
                break;
            case R.id.audioTextView3:
                iAudioSelectionComplete.onAudioSelectionComplete("audio3.aac");
                break;
            case R.id.audioTextView4:
                iAudioSelectionComplete.onAudioSelectionComplete("audio4.aac");
                break;
            case R.id.audioTextView5:
                iAudioSelectionComplete.onAudioSelectionComplete("audio5.aac");
                break;
            case R.id.audioTextView6:
                iAudioSelectionComplete.onAudioSelectionComplete("audio6.aac");
                break;
            case R.id.audioTextView7:
                iAudioSelectionComplete.onAudioSelectionComplete("audio7.aac");
                break;
            case R.id.audioTextView8:
                iAudioSelectionComplete.onAudioSelectionComplete("audio8.aac");
                break;
        }
        removeProgressDialog();
        dismiss();
    }

    public void showProgressDialog(String bodyText) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.setMessage(bodyText);

        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    public void removeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface IAudioSelectionComplete {
        void onAudioSelectionComplete(String topics);
    }

}