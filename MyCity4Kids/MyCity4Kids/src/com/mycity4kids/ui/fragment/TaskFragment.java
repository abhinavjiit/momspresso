package com.mycity4kids.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;

import com.mycity4kids.application.BaseApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TaskFragment extends Fragment {

    /**
     * Callback interface through which the fragment will report the
     * task's progress and results back to the Activity.
     */
    public interface TaskCallbacks {
        void onPreExecute();

//        void onProgressUpdate(int percent);

        void onCancelled();

        void onPostExecute(Bitmap image);
    }

    private TaskCallbacks mCallbacks;
    private DummyTask mTask;

    /**
     * Hold a reference to the parent Activity so we can report the
     * task's current progress and results. The Android framework
     * will pass us a reference to the newly created Activity after
     * each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (TaskCallbacks) activity;
    }

    /**
     * This method will only be called once when the retained
     * Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        Uri imageUri = getArguments().getParcelable("uri");
        Bitmap imageBitmap = null;
        try {
            if (getActivity() != null) {
                imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } else {
                imageBitmap = MediaStore.Images.Media.getBitmap(BaseApplication.getAppContext().getContentResolver(), imageUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create and execute the background task.
        mTask = new DummyTask();
        mTask.execute(imageBitmap);
    }

    public void launchNewTask(Uri imageUri) {
        Bitmap imageBitmap = null;
        try {
            if (getActivity() != null) {
                imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            } else {
                imageBitmap = MediaStore.Images.Media.getBitmap(BaseApplication.getAppContext().getContentResolver(), imageUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create and execute the background task.
        mTask = new DummyTask();
        mTask.execute(imageBitmap);
    }

    /**
     * Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /**
     * A dummy task that performs some (dumb) background work and
     * proxies progress updates and results back to the Activity.
     * <p>
     * Note that we need to check if the callbacks are null in each
     * method in case they are invoked after the Activity's and
     * Fragment's onDestroy() method have been called.
     */
    private class DummyTask extends AsyncTask<Bitmap, Void, Bitmap> {

//        private final Uri imageUri;
//
//        public DummyTask(Uri imageUri) {
//            this.imageUri = imageUri;
//        }

        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            }
        }

        /**
         * Note that we do NOT call the callback object's methods
         * directly from the background thread, as this could result
         * in a race condition.
         */
        @Override
        protected Bitmap doInBackground(Bitmap... params) {
//            for (int i = 0; !isCancelled() && i < 100; i++) {
//                SystemClock.sleep(100);
//                publishProgress(i);
//            }
            Bitmap bitmap = params[0];
            if (!isCancelled()) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
            }
            return bitmap;
        }

//        @Override
//        protected void onProgressUpdate(Integer... percent) {
//            if (mCallbacks != null) {
//                mCallbacks.onProgressUpdate(percent[0]);
//            }
//        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) {
                mCallbacks.onCancelled();
            }
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            if (mCallbacks != null) {
                mCallbacks.onPostExecute(image);
            }
        }
    }
}