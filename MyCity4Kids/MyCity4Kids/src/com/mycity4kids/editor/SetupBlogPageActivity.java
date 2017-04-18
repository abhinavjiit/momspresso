package com.mycity4kids.editor;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.ConnectivityUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.constants.Constants;
import com.mycity4kids.gtmutils.GTMEventType;
import com.mycity4kids.gtmutils.Utils;
import com.mycity4kids.models.response.SetupBlogResponse;
import com.mycity4kids.preference.SharedPrefUtils;
import com.mycity4kids.retrofitAPIsInterfaces.BlogPageAPI;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by anshul on 3/18/16.
 */
public class SetupBlogPageActivity extends BaseActivity {
    private static final int MAX_WORDS = 200;
    Toolbar mToolbar;
    ImageView blogImage;
    EditText blogTitle, bloggerBio;
    Button createBlog;
    private InputFilter filter;
    private TextView textLimit;

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog_page);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        bloggerBio = (EditText) findViewById(R.id.bloggerBio);
        blogTitle = (EditText) findViewById(R.id.blogTitle);
        createBlog = (Button) findViewById(R.id.createBlog);
        textLimit = (TextView) findViewById(R.id.textLimit);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Setup your Blog");
        blogImage = (ImageView) findViewById(R.id.blogImage);
        Utils.pushOpenScreenEvent(SetupBlogPageActivity.this, "Setup Blog", SharedPrefUtils.getUserDetailModel(this).getDynamoId() + "");

        if (!StringUtils.isNullOrEmpty(getIntent().getStringExtra("userBio"))) {
            bloggerBio.setText(getIntent().getStringExtra("userBio"));
        }
        if (!StringUtils.isNullOrEmpty(getIntent().getStringExtra("blogTitle"))) {
            blogTitle.setText(getIntent().getStringExtra("blogTitle"));
        }

        bloggerBio.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int wordsLength = countWords(s.toString());// words.length;
                Log.d("onTextChanged", "" + wordsLength);
                if (wordsLength > MAX_WORDS) {
                    bloggerBio.setText(s.toString().replaceAll(" [^ ]+$", ""));
                    bloggerBio.setSelection(bloggerBio.length());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int wordsLength = countWords(s.toString());// words.length;
                // count == 0 means a new word is going to start
                if (count == 0 && wordsLength >= MAX_WORDS) {
//                    setCharLimit(bloggerBio, bloggerBio.getText().length());
                } else {
                    removeFilter(bloggerBio);
                }

                textLimit.setText(String.valueOf(wordsLength) + "/" + MAX_WORDS);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                int wLength = countWords(s.toString());
//                Log.d("afterTextChanged", "Length = " + wLength + " --- " + s.toString());
//                if (wLength > MAX_WORDS) {
//                    bloggerBio.setText(removeExtraWords(s.toString()));
//                }

            }
        });


        createBlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.pushEvent(SetupBlogPageActivity.this, GTMEventType.SETUP_BLOG_CLICKED_EVENT, SharedPrefUtils.getUserDetailModel(SetupBlogPageActivity.this).getDynamoId() + "", "Set Up Blog");
                if (blogTitle.getText().toString().isEmpty()) {
                    blogTitle.setFocusableInTouchMode(true);
                    blogTitle.setError("Please enter Blog Title");
                    blogTitle.requestFocus();
                } else if (bloggerBio.getText().toString().isEmpty()) {
                    bloggerBio.setFocusableInTouchMode(true);
                    bloggerBio.setError("Please enter your Bio");
                    bloggerBio.requestFocus();
                } else if (!blogTitle.getText().toString().matches("[a-zA-Z0-9 ]+")) {
                    blogTitle.setFocusableInTouchMode(true);
                    blogTitle.setError("Special characters are not allowed!");
                    blogTitle.requestFocus();
                } else if (countWords(bloggerBio.getText().toString()) > MAX_WORDS) {
                    bloggerBio.setFocusableInTouchMode(true);
                    bloggerBio.setError("Maximum limit for bio is " + MAX_WORDS + " words");
                    bloggerBio.requestFocus();
                } else {
                    showProgressDialog(getResources().getString(R.string.please_wait));
                    Retrofit retrofit = BaseApplication.getInstance().getRetrofit();
                    // prepare call in Retrofit 2.0
                    BlogPageAPI blogSetupAPI = retrofit.create(BlogPageAPI.class);
                    if (!ConnectivityUtils.isNetworkEnabled(SetupBlogPageActivity.this)) {
                        removeProgressDialog();
                        showToast(getString(R.string.error_network));
                        return;
                    }
                    Call<SetupBlogResponse> call = blogSetupAPI.createBlogPage(blogTitle.getText().toString(), bloggerBio.getText().toString());

                    //asynchronous call
                    call.enqueue(new Callback<SetupBlogResponse>() {
                                     @Override
                                     public void onResponse(Call<SetupBlogResponse> call, retrofit2.Response<SetupBlogResponse> response) {
                                         removeProgressDialog();
                                         if (response == null || response.body() == null) {
                                             showToast(getString(R.string.went_wrong));
                                             return;
                                         }

                                         SetupBlogResponse responseData = (SetupBlogResponse) response.body();
                                         if (responseData.getCode() == 200 && Constants.SUCCESS.equals(responseData.getStatus())) {
                                             showToast(responseData.getData().getMsg());
                                             finish();
                                         } else {
                                             showToast(responseData.getReason());
                                         }
                                     }

                                     @Override
                                     public void onFailure(Call<SetupBlogResponse> call, Throwable t) {
                                         Crashlytics.logException(t);
                                         removeProgressDialog();
                                         Log.d("MC4KException", Log.getStackTraceString(t));
                                         showToast(getString(R.string.went_wrong));
                                     }
                                 }
                    );

                }
            }
        });
    }

    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length; // separate string around spaces
    }

    private String removeExtraWords(String s) {
        String trim = s.trim();
        String[] strArr = Arrays.copyOfRange(trim.split("\\s+"), 0, 199);
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < strArr.length; i++) {
            strBuilder.append(strArr[i] + " ");
        }
        String newString = strBuilder.toString();
        return newString;
    }

    private void setCharLimit(EditText et, int max) {
        filter = new InputFilter.LengthFilter(max);
        et.setFilters(new InputFilter[]{filter});
    }

    private void removeFilter(EditText et) {
        if (filter != null) {
            et.setFilters(new InputFilter[0]);
            filter = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // according to fragment change it

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }

}
