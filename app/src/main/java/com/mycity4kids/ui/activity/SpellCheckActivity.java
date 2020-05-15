package com.mycity4kids.ui.activity;

import android.content.Intent;
import android.os.Bundle;
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
import android.text.style.ForegroundColorSpan;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.application.BaseApplication;
import com.mycity4kids.base.BaseActivity;
import com.mycity4kids.models.response.PublishDraftObject;
import com.mycity4kids.retrofitAPIsInterfaces.SpellCheckAPI;
import com.mycity4kids.utils.AppUtils;
import com.mycity4kids.utils.IndexWrapper;
import com.mycity4kids.utils.StringUtils;
import com.mycity4kids.utils.WholeWordIndexFinder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SpellCheckActivity extends BaseActivity implements View.OnClickListener {

    private TextView contentTextView, titleTextView;
    private Map<String, ArrayList<String>> contentSuggestionsMap = new HashMap<>();
    String originalBodyContent, originalTitleContent;
    private Toolbar toolbar;
    private TextView publishTextView;
    private String draftId;
    private LinearLayout bottomSheet;
    private BottomSheetBehavior<LinearLayout> sheetBehavior;
    private Map<String, Integer> contentOffsetMap = new HashMap<>();
    private ProgressBar progressBar;
    private PublishDraftObject draftObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spell_check_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        contentTextView = (TextView) findViewById(R.id.contentTextView);
        bottomSheet = (LinearLayout) findViewById(R.id.bottomSheet);
        publishTextView = (TextView) toolbar.findViewById(R.id.publishTextView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        sheetBehavior = BottomSheetBehavior.from(bottomSheet);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        publishTextView.setOnClickListener(this);

        originalTitleContent = getIntent().getStringExtra("titleContent");
        originalBodyContent = getIntent().getStringExtra("bodyContent");
        draftId = getIntent().getStringExtra("draftId");

        String titleContent = AppUtils.stripHtml(originalTitleContent);
        String bodyContent = AppUtils.stripHtml(originalBodyContent);

        titleTextView.setText(titleContent);
        contentTextView.setText(bodyContent);

        Retrofit retrofit = BaseApplication.getInstance().getAzureRetrofit();

        progressBar.setVisibility(View.VISIBLE);
        SpellCheckAPI spellCheckAPI = retrofit.create(SpellCheckAPI.class);
        Call<ResponseBody> call = spellCheckAPI.getSpellCheck("proof", "en-US", bodyContent);
        call.enqueue(contentSpellCheckReponseCallback);
    }

    private Callback<ResponseBody> contentSpellCheckReponseCallback = new Callback<ResponseBody>() {
        @Override
        public void onResponse(@NonNull Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
            progressBar.setVisibility(View.GONE);
            if (response.body() == null) {
                return;
            }
            try {
                if (response.isSuccessful()) {
                    String resData = new String(response.body().bytes());
                    JSONObject jObject = new JSONObject(resData);
                    JSONArray flaggedArray = jObject.getJSONArray("flaggedTokens");
                    if (flaggedArray.length() > 0) {
                        showToast(getString(R.string.spell_check_error));
                    }
                    for (int i = 0; i < flaggedArray.length(); i++) {
                        ArrayList<String> suggestionList = new ArrayList<>();
                        for (int j = 0; j < flaggedArray.getJSONObject(i).getJSONArray("suggestions").length(); j++) {
                            suggestionList
                                    .add(flaggedArray.getJSONObject(i).getJSONArray("suggestions").getJSONObject(j)
                                            .getString("suggestion"));
                        }
                        contentSuggestionsMap.put(flaggedArray.getJSONObject(i).getString("token"), suggestionList);
                        contentOffsetMap.put(flaggedArray.getJSONObject(i).getString("token"),
                                flaggedArray.getJSONObject(i).getInt("offset"));
                        highlightString(contentOffsetMap, contentSuggestionsMap, contentTextView,
                                flaggedArray.getJSONObject(i).getString("token"),
                                flaggedArray.getJSONObject(i).getInt("offset"));
                    }
                }
            } catch (JSONException jsonexception) {
                FirebaseCrashlytics.getInstance().recordException(jsonexception);
                Log.d("JSONException", Log.getStackTraceString(jsonexception));
            } catch (Exception ex) {
                FirebaseCrashlytics.getInstance().recordException(ex);
                Log.d("MC4kException", Log.getStackTraceString(ex));
            }
        }

        @Override
        public void onFailure(@NonNull Call<ResponseBody> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            FirebaseCrashlytics.getInstance().recordException(t);
            Log.d("JSONException", Log.getStackTraceString(t));
        }
    };

    private void highlightString(Map<String, Integer> contentOffsetMap, Map<String, ArrayList<String>> suggestionsMap,
            TextView textView, String input, int offset) {
        SpannableString spannableString = new SpannableString(textView.getText());
        WholeWordIndexFinder finder = new WholeWordIndexFinder(spannableString.toString());
        List<IndexWrapper> indexes = finder.findIndexesForKeyword(input);
        int occuranceIndex = 0;
        for (int i = 0; i < indexes.size(); i++) {
            if (offset == indexes.get(i).getStart()) {
                occuranceIndex = i;
            }
        }

        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.app_red)), offset,
                offset + input.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString
                .setSpan(new BackgroundColorSpan(ContextCompat.getColor(this, R.color.spell_check_error_highlight)),
                        offset, offset + input.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        int finalOccuranceIndex = occuranceIndex;
        spannableString.setSpan(new TouchableSpan() {
            @Override
            public boolean onTouch(View widget, MotionEvent m) {

                switch (m.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        showBottomSheet(contentOffsetMap, textView, suggestionsMap, input, offset,
                                finalOccuranceIndex);//bottomSheet
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }

            @Override
            public void updateDrawState(TextPaint ds) {

            }
        }, offset, offset + input.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString);
        textView.setMovementMethod(new LinkTouchMovementMethod());
    }

    private void showBottomSheet(Map<String, Integer> contentOffsetMap, TextView textView,
            Map<String, ArrayList<String>> suggestionsMap, String token, int offset, int occuranceIndex) {
        bottomSheet.removeAllViews();
        for (int i = 0; i < suggestionsMap.get(token).size(); i++) {
            TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.spell_check_suggestion_item, null);
            tv.setText(suggestionsMap.get(token).get(i));
            tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            ((LinearLayout) bottomSheet).addView(tv);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String updatedText = textView.getText().toString()
                            .replaceAll("\\b" + token + "\\b", tv.getText().toString());
                    WholeWordIndexFinder finder = new WholeWordIndexFinder(originalBodyContent);
                    List<IndexWrapper> indexes = finder.findIndexesForKeyword(token);
                    System.out.println("Indexes found " + indexes.size() + " keyword found at index : " + indexes.get(0)
                            .getStart());
                    String prefix = originalBodyContent.substring(0, indexes.get(occuranceIndex).getStart());
                    String suffix = originalBodyContent.substring(indexes.get(occuranceIndex).getStart())
                            .replace(token, tv.getText().toString());

                    Log.d("---HTML SUFFIX---", "" + suffix);
                    originalBodyContent = prefix + suffix;
                    Log.d("---HTML Body---", "" + originalBodyContent);
                    for (Map.Entry<String, Integer> offsetEntry : contentOffsetMap.entrySet()) {
                        if (offset < offsetEntry.getValue()) {
                            offsetEntry.setValue(
                                    offsetEntry.getValue() + (tv.getText().toString().length() - token.length()));
                        }
                    }
                    suggestionsMap.remove(token);
                    contentOffsetMap.remove(token);
                    contentTextView.setText(updatedText);

                    for (Map.Entry<String, ArrayList<String>> entry : suggestionsMap.entrySet()) {
                        highlightString(contentOffsetMap, suggestionsMap, textView, entry.getKey(),
                                contentOffsetMap.get(entry.getKey()));
                    }
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });

        }
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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
                publishObject.setBody(contentFormatting(originalBodyContent));
                publishObject.setTitle(titleFormatting(originalTitleContent));
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
        return Html.fromHtml(title).toString();
    }

    public abstract class TouchableSpan extends CharacterStyle implements UpdateAppearance {

        public abstract boolean onTouch(View widget, MotionEvent m);

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
