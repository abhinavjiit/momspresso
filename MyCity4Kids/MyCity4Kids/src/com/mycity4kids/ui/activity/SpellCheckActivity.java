package com.mycity4kids.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.models.response.PublishDraftObject;
import com.mycity4kids.retrofitAPIsInterfaces.SpellCheckAPI;
import com.mycity4kids.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SpellCheckActivity extends BaseActivity implements View.OnClickListener {

    private TextView contentTextView, titleTextView;
    private Map<String, ArrayList<String>> contentSuggestionsMap = new HashMap<>();
    private Map<String, ArrayList<String>> titleSuggestionsMap = new HashMap<>();
    private View centerView;
    String originalBodyContent, originalTitleContent;
    private String newHtmlTitle, newHtmlBody;
    private Toolbar toolbar;
    private TextView publishTextView;
    private String draftId;
    private float touchCoordinateX, touchCoordinateY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spell_check_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        contentTextView = (TextView) findViewById(R.id.contentTextView);
        centerView = (View) findViewById(R.id.centerView);
        publishTextView = (TextView) toolbar.findViewById(R.id.publishTextView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        publishTextView.setOnClickListener(this);

        draftId = getIntent().getStringExtra("draftId");
        originalTitleContent = getIntent().getStringExtra("titleContent");
        originalBodyContent = getIntent().getStringExtra("bodyContent");

        String titleContent = AppUtils.stripHtml(originalTitleContent);
        String bodyContent = AppUtils.stripHtml(originalBodyContent);

        Log.d("HTML stripped", bodyContent);

        titleTextView.setText(titleContent);
        contentTextView.setText(bodyContent);

        Retrofit retrofit = BaseApplication.getInstance().getAzureRetrofit();

        SpellCheckAPI spellCheckAPI = retrofit.create(SpellCheckAPI.class);
        Call<ResponseBody> call = spellCheckAPI.getSpellCheck("proof", "en-US", bodyContent);
        call.enqueue(contentSpellCheckReponseCallback);

        Call<ResponseBody> call1 = spellCheckAPI.getSpellCheck("proof", "en-US", titleContent);
        call1.enqueue(titleSpellCheckReponseCallback);
    }

    private Callback<ResponseBody> contentSpellCheckReponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response.body() == null) {
                return;
            }
            try {
                if (response.isSuccessful()) {
                    String resData = new String(response.body().bytes());
                    JSONObject jObject = new JSONObject(resData);
                    JSONArray flaggedArray = jObject.getJSONArray("flaggedTokens");
                    for (int i = 0; i < flaggedArray.length(); i++) {
                        ArrayList<String> suggestionList = new ArrayList<>();
                        for (int j = 0; j < flaggedArray.getJSONObject(i).getJSONArray("suggestions").length(); j++) {
                            suggestionList.add(flaggedArray.getJSONObject(i).getJSONArray("suggestions").getJSONObject(j).getString("suggestion"));
                        }
                        contentSuggestionsMap.put(flaggedArray.getJSONObject(i).getString("token"), suggestionList);
                        highlightString(contentSuggestionsMap, contentTextView, flaggedArray.getJSONObject(i).getString("token"));
                    }
                }
            } catch (JSONException jsonexception) {
                Crashlytics.logException(jsonexception);
                Log.d("JSONException", Log.getStackTraceString(jsonexception));
            } catch (Exception ex) {
                Crashlytics.logException(ex);
                Log.d("MC4kException", Log.getStackTraceString(ex));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

        }
    };

    private Callback<ResponseBody> titleSpellCheckReponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            if (response.body() == null) {
                return;
            }
            try {
                if (response.isSuccessful()) {
                    String resData = new String(response.body().bytes());
                    JSONObject jObject = new JSONObject(resData);
                    JSONArray flaggedArray = jObject.getJSONArray("flaggedTokens");
                    for (int i = 0; i < flaggedArray.length(); i++) {
                        ArrayList<String> suggestionList = new ArrayList<>();
                        for (int j = 0; j < flaggedArray.getJSONObject(i).getJSONArray("suggestions").length(); j++) {
                            suggestionList.add(flaggedArray.getJSONObject(i).getJSONArray("suggestions").getJSONObject(j).getString("suggestion"));
                        }
                        titleSuggestionsMap.put(flaggedArray.getJSONObject(i).getString("token"), suggestionList);
                        highlightString(titleSuggestionsMap, titleTextView, flaggedArray.getJSONObject(i).getString("token"));
                    }
                }
            } catch (JSONException jsonexception) {
                Crashlytics.logException(jsonexception);
                Log.d("JSONException", Log.getStackTraceString(jsonexception));
            } catch (Exception ex) {
                Crashlytics.logException(ex);
                Log.d("MC4kException", Log.getStackTraceString(ex));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {

        }
    };

    private void highlightString(Map<String, ArrayList<String>> suggestionsMap, TextView textView, String input) {
        SpannableString spannableString = new SpannableString(textView.getText());
//Get the previous spans and remove them
//        BackgroundColorSpan[] backgroundSpans = spannableString.getSpans(0, spannableString.length(), BackgroundColorSpan.class);
//
//        for (BackgroundColorSpan span : backgroundSpans) {
//            spannableString.removeSpan(span);
//        }
//
//Search for all occurrences of the keyword in the string
        int indexOfKeyword = spannableString.toString().indexOf(input);

        while (indexOfKeyword > 0) {
            //Create a background color span on the keyword
            spannableString.setSpan(new BackgroundColorSpan(Color.RED), indexOfKeyword, indexOfKeyword + input.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannableString.setSpan(new TouchableSpan() {
                @Override
                public boolean onTouch(View widget, MotionEvent m) {

                    Log.d("Spannable Click", "Spannable Click");
                    TextView tv = (TextView) widget;
                    Spanned s = (Spanned) tv.getText();
                    int start = s.getSpanStart(this);
                    int end = s.getSpanEnd(this);
                    Log.d("Spannable Click", "onClick [" + s.subSequence(start, end) + "]");

//                    showPopUpMenu(suggestionsMap, textView, centerView, input);
                    show(SpellCheckActivity.this, m.getX(), m.getY(), suggestionsMap, input);
                    return false;
                }

                @Override
                public void updateDrawState(TextPaint ds) {

                }
            }, indexOfKeyword, indexOfKeyword + input.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //Get the next index of the keyword
            indexOfKeyword = spannableString.toString().indexOf(input, indexOfKeyword + input.length());

        }

//Set the final text on TextView
        textView.setText(spannableString);
        textView.setMovementMethod(new LinkTouchMovementMethod());
//        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @SuppressLint("RestrictedApi")
    public void showPopUpMenu(Map<String, ArrayList<String>> suggestionsMap, TextView textView, View view, String token) {
        final PopupMenu popup = new PopupMenu(this, view);
        for (int i = 0; i < suggestionsMap.get(token).size(); i++) {
            popup.getMenu().add(suggestionsMap.get(token).get(i));
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String updatedText = textView.getText().toString().replaceAll("\\b" + token + "\\b", item.getTitle().toString());
                if (textView.getId() == R.id.titleTextView) {
                    newHtmlTitle = originalTitleContent.replaceAll("\\b" + token + "\\b", item.getTitle().toString());
                    suggestionsMap.remove(token);
                    titleTextView.setText(updatedText);
                } else {
                    newHtmlBody = originalBodyContent.replaceAll("\\b" + token + "\\b", item.getTitle().toString());
                    suggestionsMap.remove(token);
                    contentTextView.setText(updatedText);
                }

                for (Map.Entry<String, ArrayList<String>> entry : suggestionsMap.entrySet()) {
                    highlightString(suggestionsMap, textView, entry.getKey());
                }
                return true;
            }

        });
        popup.show();
    }

    public void show(Activity activity, float x, float y, Map<String, ArrayList<String>> suggestionsMap, String token) {
        Log.d("---POPUPMENU---", "Show me the money X=" + x + " Y=" + y);
        final ViewGroup root = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);

        final View view = new View(activity);
        view.setLayoutParams(new ViewGroup.LayoutParams(1, 1));
        view.setBackgroundColor(Color.TRANSPARENT);

        root.addView(view);

        view.setX(x);
        view.setY(y+100);

        PopupMenu popupMenu = new PopupMenu(activity, view, Gravity.CENTER);
        for (int i = 0; i < suggestionsMap.get(token).size(); i++) {
            popupMenu.getMenu().add(suggestionsMap.get(token).get(i));
        }
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                root.removeView(view);
            }
        });

        popupMenu.show();
    }

    @Override
    protected void updateUi(Response response) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publishTextView:
                PublishDraftObject publishObject = new PublishDraftObject();
                publishObject.setBody(contentFormatting(newHtmlBody));
                publishObject.setTitle(titleFormatting(newHtmlTitle));
                Intent intent_3 = new Intent(SpellCheckActivity.this, AddArticleTopicsActivityNew.class);
                if (!StringUtils.isNullOrEmpty(draftId)) {
                    publishObject.setId(draftId);
                }
                intent_3.putExtra("draftItem", publishObject);
                intent_3.putExtra("from", "editor");
                startActivity(intent_3);
                break;
        }
    }

    public String contentFormatting(String content) {
        String pTag = "<p>";
        String newString = pTag.concat(content);
        String formattedString = newString.replace("\n\n", "</p><p>");
        formattedString = formattedString.concat("</p>");
        return formattedString;
    }

    public String titleFormatting(String title) {
        String htmlStrippedTitle = Html.fromHtml(title).toString();
        return htmlStrippedTitle;
    }

    public abstract class TouchableSpan extends CharacterStyle implements UpdateAppearance {

        /**
         * Performs the touch action associated with this span.
         *
         * @return
         */
        public abstract boolean onTouch(View widget, MotionEvent m);

        /**
         * Could make the text underlined or change link color.
         */
        @Override
        public abstract void updateDrawState(TextPaint ds);

    }

    public class LinkTouchMovementMethod extends LinkMovementMethod {

        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer,
                                    MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                TouchableSpan[] link = buffer.getSpans(off, off, TouchableSpan.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onTouch(widget, event); //////// CHANGED HERE
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        link[0].onTouch(widget, event); //////// ADDED THIS
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(link[0]),
                                buffer.getSpanEnd(link[0]));
                    }

                    return true;
                } else {
                    Selection.removeSelection(buffer);
                }
            }

            return super.onTouchEvent(widget, buffer, event);
        }

    }
}
