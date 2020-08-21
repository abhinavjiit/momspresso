/*
 * Copyright 2015 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.mycity4kids.tagging.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mycity4kids.R;
import com.mycity4kids.tagging.mentions.MentionSpan;
import com.mycity4kids.tagging.mentions.MentionSpanConfig;
import com.mycity4kids.tagging.mentions.Mentionable;
import com.mycity4kids.tagging.mentions.MentionsEditable;
import com.mycity4kids.tagging.suggestions.SuggestionsAdapter;
import com.mycity4kids.tagging.suggestions.SuggestionsResult;
import com.mycity4kids.tagging.suggestions.impl.BasicSuggestionsListBuilder;
import com.mycity4kids.tagging.suggestions.interfaces.OnSuggestionsVisibilityChangeListener;
import com.mycity4kids.tagging.suggestions.interfaces.SuggestionsListBuilder;
import com.mycity4kids.tagging.suggestions.interfaces.SuggestionsResultListener;
import com.mycity4kids.tagging.suggestions.interfaces.SuggestionsVisibilityManager;
import com.mycity4kids.tagging.tokenization.QueryToken;
import com.mycity4kids.tagging.tokenization.impl.WordTokenizer;
import com.mycity4kids.tagging.tokenization.impl.WordTokenizerConfig;
import com.mycity4kids.tagging.tokenization.interfaces.QueryTokenReceiver;
import com.mycity4kids.tagging.tokenization.interfaces.Tokenizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Custom view for the RichEditor. Manages three subviews:
 * <p/>
 * 1. EditText - contains text typed by user <br/> 2. TextView - displays count of the number of characters in the
 * EditText <br/> 3. ListView - displays mention suggestions when relevant
 * <p/>
 * <b>XML attributes</b>
 * <p/>
 * See {@link R.styleable#RichEditorView Attributes}
 *
 * @attr ref R.styleable#RichEditorView_mentionTextColor
 * @attr ref R.styleable#RichEditorView_mentionTextBackgroundColor
 * @attr ref R.styleable#RichEditorView_selectedMentionTextColor
 * @attr ref R.styleable#RichEditorView_selectedMentionTextBackgroundColor
 */
public class RichEditorView extends RelativeLayout implements TextWatcher, QueryTokenReceiver,
        SuggestionsResultListener, SuggestionsVisibilityManager {

    private MentionsEditText mentionsEditText;
    private int originalInputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE; // Default to plain text
    private TextView textCounterView;
    private ListView suggestionsList;

    private QueryTokenReceiver hostQueryTokenReceiver;
    private SuggestionsAdapter suggestionsAdapter;
    private OnSuggestionsVisibilityChangeListener actionListener;

    private ViewGroup.LayoutParams prevEditTextParams;
    private boolean editTextShouldWrapContent = true; // Default to match parent in height
    private int prevEditTextBottomPadding;

    private int textCountLimit = -1;
    private int withinCountLimitTextColor = Color.BLACK;
    private int beyondCountLimitTextColor = Color.RED;

    private boolean waitingForFirstResult = false;
    private boolean displayTextCount = true;

    // --------------------------------------------------
    // Constructors & Initialization
    // --------------------------------------------------

    public RichEditorView(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public RichEditorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public RichEditorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        // Inflate view from XML layout file
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.editor_view, this, true);

        // Get the inner views
        mentionsEditText = findViewById(R.id.text_editor);
        textCounterView = findViewById(R.id.text_counter);
        suggestionsList = findViewById(R.id.suggestions_list);

        // Get the MentionSpanConfig from custom XML attributes and set it
        MentionSpanConfig mentionSpanConfig = parseMentionSpanConfigFromAttributes(attrs, defStyleAttr);
        mentionsEditText.setMentionSpanConfig(mentionSpanConfig);

        // Create the tokenizer to use for the editor
        // TODO: Allow customization of configuration via XML attributes
        WordTokenizerConfig tokenizerConfig = new WordTokenizerConfig.Builder().build();
        WordTokenizer tokenizer = new WordTokenizer(tokenizerConfig);
        mentionsEditText.setTokenizer(tokenizer);

        // Set various delegates on the MentionEditText to the RichEditorView
        mentionsEditText.setSuggestionsVisibilityManager(this);
        mentionsEditText.addTextChangedListener(this);
        mentionsEditText.setQueryTokenReceiver(this);
        mentionsEditText.setAvoidPrefixOnTap(true);
        mentionsEditText.setBackgroundColor(getResources().getColor(R.color.transparent));

        // Set the suggestions adapter
        SuggestionsListBuilder listBuilder = new BasicSuggestionsListBuilder();
        suggestionsAdapter = new SuggestionsAdapter(context, this, listBuilder);
        suggestionsList.setAdapter(suggestionsAdapter);

        // Set the item click listener
        suggestionsList.setOnItemClickListener((parent, view, position, id) -> {
            Mentionable mention = (Mentionable) suggestionsAdapter.getItem(position);
            if (mentionsEditText != null) {
                mentionsEditText.insertMention(mention);
                suggestionsAdapter.clear();
            }
        });

        // Display and update the editor text counter (starts it at 0)
        updateEditorTextCount();

        // Wrap the EditText content height if necessary (ideally, allow this to be controlled via custom XML attribute)
        setEditTextShouldWrapContent(editTextShouldWrapContent);
        prevEditTextBottomPadding = mentionsEditText.getPaddingBottom();
    }

    private MentionSpanConfig parseMentionSpanConfigFromAttributes(@Nullable AttributeSet attrs, int defStyleAttr) {
        final Context context = getContext();
        MentionSpanConfig.Builder builder = new MentionSpanConfig.Builder();
        if (attrs == null) {
            return builder.build();
        }

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.RichEditorView,
                defStyleAttr,
                0);
        @ColorInt int normalTextColor = attributes.getColor(R.styleable.RichEditorView_mentionTextColor, -1);
        builder.setMentionTextColor(normalTextColor);
        @ColorInt int normalBgColor = attributes.getColor(R.styleable.RichEditorView_mentionTextBackgroundColor, -1);
        builder.setMentionTextBackgroundColor(normalBgColor);
        @ColorInt int selectedTextColor = attributes.getColor(R.styleable.RichEditorView_selectedMentionTextColor, -1);
        builder.setSelectedMentionTextColor(selectedTextColor);
        @ColorInt int selectedBgColor = attributes
                .getColor(R.styleable.RichEditorView_selectedMentionTextBackgroundColor, -1);
        builder.setSelectedMentionTextBackgroundColor(selectedBgColor);

        attributes.recycle();

        return builder.build();
    }

    // --------------------------------------------------
    // Public Span & UI Methods
    // --------------------------------------------------

    /**
     * Allows filters in the input element. Example: obj.setInputFilters(new InputFilter[]{new
     * InputFilter.LengthFilter(30)});
     *
     * @param filters the list of filters to apply
     */
    public void setInputFilters(@Nullable InputFilter... filters) {
        mentionsEditText.setFilters(filters);

    }

    /*
     * @return a list of {@link MentionSpan} objects currently in the editor.
     */
    @NonNull
    public List<MentionSpan> getMentionSpans() {
        return (mentionsEditText != null) ? mentionsEditText.getMentionsText().getMentionSpans() : new ArrayList<>();
    }

    /**
     * Determine whether the internal {@link EditText} should match the full height of the {@link RichEditorView}
     * initially or if it should wrap its content in height and expand to fill it as the user types. Note: The {@link
     * EditText} will always match the parent (i.e. the {@link RichEditorView} in width. Additionally, the {@link
     * ListView} containing mention suggestions will always fill the rest of the height in the {@link RichEditorView}.
     *
     * @param enabled true if the {@link EditText} should wrap its content in height
     */
    public void setEditTextShouldWrapContent(boolean enabled) {
        editTextShouldWrapContent = enabled;
        if (mentionsEditText == null) {
            return;
        }
        prevEditTextParams = mentionsEditText.getLayoutParams();
        int wrap = (enabled) ? LayoutParams.WRAP_CONTENT : LayoutParams.MATCH_PARENT;
        if (prevEditTextParams != null && prevEditTextParams.height == wrap) {
            return;
        }
        ViewGroup.LayoutParams newParams = new LayoutParams(LayoutParams.MATCH_PARENT, wrap);
        mentionsEditText.setLayoutParams(newParams);
        requestLayout();
        invalidate();
    }

    /*
     * @return current line number of the cursor, or -1 if no cursor
     */
    public int getCurrentCursorLine() {
        int selectionStart = mentionsEditText.getSelectionStart();
        Layout layout = mentionsEditText.getLayout();
        if (layout != null && !(selectionStart == -1)) {
            return layout.getLineForOffset(selectionStart);
        }
        return -1;
    }

    public void setMaxLines() {
        mentionsEditText.setMaxLines(4);
    }


    /*
     * Show or hide the text counter view.
     *
     * @param display true to display the text counter view
     */
    public void displayTextCounter(boolean display) {
        displayTextCount = display;
        if (display) {
            textCounterView.setVisibility(TextView.VISIBLE);
        } else {
            textCounterView.setVisibility(TextView.GONE);
        }
    }

    /*
     * @return true if the text counter view is currently visible to the user
     */
    public boolean isDisplayingTextCounter() {
        return textCounterView != null && textCounterView.getVisibility() == TextView.VISIBLE;
    }

    // --------------------------------------------------
    // TextWatcher Implementation
    // --------------------------------------------------

    /*
     * {@inheritDoc}
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Do nothing
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Do nothing
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public void afterTextChanged(Editable s) {
        updateEditorTextCount();
    }

    // --------------------------------------------------
    // QueryTokenReceiver Implementation
    // --------------------------------------------------

    /*
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public List<String> onQueryReceived(@NonNull QueryToken queryToken) {
        // Pass the query token to a host receiver
        if (hostQueryTokenReceiver != null) {
            List<String> buckets = hostQueryTokenReceiver.onQueryReceived(queryToken);
            suggestionsAdapter.notifyQueryTokenReceived(queryToken, buckets);
        }
        return Collections.emptyList();
    }

    // --------------------------------------------------
    // SuggestionsResultListener Implementation
    // --------------------------------------------------

    /*
     * {@inheritDoc}
     */
    @Override
    public void onReceiveSuggestionsResult(final @NonNull SuggestionsResult result, final @NonNull String bucket) {
        // Add the mentions and notify the editor/dropdown of the changes on the UI thread
        post(() -> {
            if (suggestionsAdapter != null) {
                suggestionsAdapter.addSuggestions(result, bucket, mentionsEditText);
            }
            // Make sure the list is scrolled to the top once you receive the first query result
            if (waitingForFirstResult && suggestionsList != null) {
                suggestionsList.setSelection(0);
                waitingForFirstResult = false;
            }
        });
    }

    // --------------------------------------------------
    // SuggestionsManager Implementation
    // --------------------------------------------------

    /*
     * {@inheritDoc}
     */
    public void displaySuggestions(boolean display) {

        // If nothing to change, return early
        if (display == isDisplayingSuggestions() || mentionsEditText == null) {
            return;
        }

        // Change view depending on whether suggestions are being shown or not
        if (display) {
            disableSpellingSuggestions(true);
            textCounterView.setVisibility(View.GONE);
            suggestionsList.setVisibility(View.VISIBLE);
            prevEditTextParams = mentionsEditText.getLayoutParams();
            prevEditTextBottomPadding = mentionsEditText.getPaddingBottom();
            mentionsEditText.setPaddingRelative(mentionsEditText.getPaddingStart(), mentionsEditText.getPaddingTop(),
                    mentionsEditText.getPaddingEnd(), mentionsEditText.getPaddingTop());
            int height = mentionsEditText.getPaddingTop() + mentionsEditText.getLineHeight() + mentionsEditText
                    .getPaddingBottom();
            mentionsEditText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
            mentionsEditText.setVerticalScrollBarEnabled(false);
            int cursorLine = getCurrentCursorLine();
            Layout layout = mentionsEditText.getLayout();
            if (layout != null) {
                int lineTop = layout.getLineTop(cursorLine);
                mentionsEditText.scrollTo(0, lineTop);
            }
            // Notify action listener that list was shown
            if (actionListener != null) {
                actionListener.onSuggestionsDisplayed();
            }
        } else {
            disableSpellingSuggestions(false);
            textCounterView.setVisibility(displayTextCount ? View.VISIBLE : View.GONE);
            suggestionsList.setVisibility(View.GONE);
            mentionsEditText.setPaddingRelative(mentionsEditText.getPaddingStart(), mentionsEditText.getPaddingTop(),
                    mentionsEditText.getPaddingEnd(), prevEditTextBottomPadding);
            if (prevEditTextParams == null) {
                prevEditTextParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            }
            mentionsEditText.setLayoutParams(prevEditTextParams);
            mentionsEditText.setVerticalScrollBarEnabled(true);
            // Notify action listener that list was hidden
            if (actionListener != null) {
                actionListener.onSuggestionsHidden();
            }
        }

        requestLayout();
        invalidate();
    }

    /*
     * {@inheritDoc}
     */
    public boolean isDisplayingSuggestions() {
        return suggestionsList.getVisibility() == View.VISIBLE;
    }

    /**
     * Disables spelling suggestions from the user's keyboard. This is necessary because some keyboards will replace the
     * input text with spelling suggestions automatically, which changes the suggestion results. This results in a
     * confusing user experience.
     *
     * @param disable {@code true} if spelling suggestions should be disabled, otherwise {@code false}
     */
    private void disableSpellingSuggestions(boolean disable) {
        // toggling suggestions often resets the cursor location, but we don't want that to happen
        int start = mentionsEditText.getSelectionStart();
        int end = mentionsEditText.getSelectionEnd();
        // -1 means there is no selection or cursor.
        if (start == -1 || end == -1) {
            return;
        }
        if (disable) {
            // store the previous input type
            originalInputType = mentionsEditText.getInputType();
        }
        mentionsEditText.setRawInputType(disable ? InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS : originalInputType);
        mentionsEditText.setSelection(start, end);
    }

    // --------------------------------------------------
    // Private Methods
    // --------------------------------------------------

    /**
     * Updates the TextView counting the number of characters in the editor. Sets not only the content of the TextView,
     * but also the color of the text depending if the limit has been reached.
     */
    private void updateEditorTextCount() {
        if (mentionsEditText != null && textCounterView != null) {
            int textCount = mentionsEditText.getMentionsText().length();
            textCounterView.setText(String.valueOf(textCount));

            if (textCountLimit > 0 && textCount > textCountLimit) {
                textCounterView.setTextColor(beyondCountLimitTextColor);
            } else {
                textCounterView.setTextColor(withinCountLimitTextColor);
            }
        }
    }

    // --------------------------------------------------
    // Pass-Through Methods to the MentionsEditText
    // --------------------------------------------------

    /**
     * Convenience method for {@link MentionsEditText#getCurrentTokenString()}.
     *
     * @return a string representing currently being considered for a possible query, as the user typed it
     */
    @NonNull
    public String getCurrentTokenString() {
        if (mentionsEditText == null) {
            return "";
        }
        return mentionsEditText.getCurrentTokenString();
    }

    /**
     * Convenience method for {@link MentionsEditText#getCurrentKeywordsString()}.
     *
     * @return a String representing current keywords in the underlying {@link EditText}
     */
    @NonNull
    public String getCurrentKeywordsString() {
        if (mentionsEditText == null) {
            return "";
        }
        return mentionsEditText.getCurrentKeywordsString();
    }

    /**
     * Resets the given {@link MentionSpan} in the editor, forcing it to redraw with its latest drawable state.
     *
     * @param span the {@link MentionSpan} to update
     */
    public void updateSpan(@NonNull MentionSpan span) {
        if (mentionsEditText != null) {
            mentionsEditText.updateSpan(span);
        }
    }

    /**
     * Deselects any spans in the editor that are currently selected.
     */
    public void deselectAllSpans() {
        if (mentionsEditText != null) {
            mentionsEditText.deselectAllSpans();
        }
    }

    /**
     * Adds an {@link TextWatcher} to the internal {@link MentionsEditText}.
     *
     * @param hostTextWatcher the {TextWatcher} to add
     */
    public void addTextChangedListener(@NonNull final TextWatcher hostTextWatcher) {
        if (mentionsEditText != null) {
            mentionsEditText.addTextChangedListener(hostTextWatcher);
        }
    }

    /*
     * @return the {@link MentionsEditable} within the embedded {@link MentionsEditText}
     */
    @NonNull
    public MentionsEditable getText() {
        return (mentionsEditText != null) ? ((MentionsEditable) mentionsEditText.getText())
                : new MentionsEditable("");
    }

    /*
     * @return the {@link Tokenizer} in use
     */
    @Nullable
    public Tokenizer getTokenizer() {
        return (mentionsEditText != null) ? mentionsEditText.getTokenizer() : null;
    }

    /**
     * Sets the text being displayed within the {@link RichEditorView}. Note that this removes the {@link TextWatcher}
     * temporarily to avoid changing the text while listening to text changes (which could result in an infinite loop).
     *
     * @param text the text to display
     */
    public void setText(final @NonNull CharSequence text) {
        if (mentionsEditText != null) {
            mentionsEditText.setText(text);
        }
    }

    /**
     * Sets the text hint to use within the embedded {@link MentionsEditText}.
     *
     * @param hint the text hint to use
     */
    public void setHint(final @NonNull CharSequence hint) {
        if (mentionsEditText != null) {
            mentionsEditText.setHint(hint);
        }
    }

    /**
     * Sets the input type of the embedded {@link MentionsEditText}.
     *
     * @param type the input type of the {@link MentionsEditText}
     */
    public void setInputType(final int type) {
        if (mentionsEditText != null) {
            mentionsEditText.setInputType(type);
        }
    }

    /**
     * Sets the selection within the embedded {@link MentionsEditText}.
     *
     * @param index the index of the selection within the embedded {@link MentionsEditText}
     */
    public void setSelection(final int index) {
        if (mentionsEditText != null) {
            mentionsEditText.setSelection(index);
        }
    }

    /**
     * Sets the {@link Tokenizer} for the {@link MentionsEditText} to use.
     *
     * @param tokenizer the {@link Tokenizer} to use
     */
    public void setTokenizer(final @NonNull Tokenizer tokenizer) {
        if (mentionsEditText != null) {
            mentionsEditText.setTokenizer(tokenizer);
        }
    }

    /**
     * Sets the factory used to create MentionSpans within this class.
     *
     * @param factory the {@link MentionsEditText.MentionSpanFactory} to use
     */
    public void setMentionSpanFactory(@NonNull final MentionsEditText.MentionSpanFactory factory) {
        if (mentionsEditText != null) {
            mentionsEditText.setMentionSpanFactory(factory);
        }
    }

    /**
     * Register a {@link com.mycity4kids.tagging.ui.MentionsEditText.MentionWatcher} in order to receive callbacks when
     * mentions are changed.
     *
     * @param watcher the {@link com.mycity4kids.tagging.ui.MentionsEditText.MentionWatcher} to add
     */
    public void addMentionWatcher(@NonNull MentionsEditText.MentionWatcher watcher) {
        if (mentionsEditText != null) {
            mentionsEditText.addMentionWatcher(watcher);
        }
    }

    /**
     * Remove a {@link com.mycity4kids.tagging.ui.MentionsEditText.MentionWatcher} from receiving anymore callbacks when
     * mentions are changed.
     *
     * @param watcher the {@link com.mycity4kids.tagging.ui.MentionsEditText.MentionWatcher} to remove
     */
    public void removeMentionWatcher(@NonNull MentionsEditText.MentionWatcher watcher) {
        if (mentionsEditText != null) {
            mentionsEditText.removeMentionWatcher(watcher);
        }
    }

    // --------------------------------------------------
    // RichEditorView-specific Setters
    // --------------------------------------------------

    /**
     * Sets the limit on the maximum number of characters allowed to be entered into the {@link MentionsEditText} before
     * the text counter changes color.
     *
     * @param limit the maximum number of characters allowed before the text counter changes color
     */
    public void setTextCountLimit(final int limit) {
        textCountLimit = limit;
    }

    /**
     * Sets the color of the text within the text counter while the user has entered fewer than the limit of
     * characters.
     *
     * @param color the color of the text within the text counter below the limit
     */
    public void setWithinCountLimitTextColor(final int color) {
        withinCountLimitTextColor = color;
    }

    /**
     * Sets the color of the text within the text counter while the user has entered more text than the current limit.
     *
     * @param color the color of the text within the text counter beyond the limit
     */
    public void setBeyondCountLimitTextColor(final int color) {
        beyondCountLimitTextColor = color;
    }

    /**
     * Sets the receiver of any tokens generated by the embedded {@link MentionsEditText}. The receive should act on the
     * queries as they are received and call {@link #onReceiveSuggestionsResult(SuggestionsResult, String)} once the
     * suggestions are ready.
     *
     * @param client the object that can receive {@link QueryToken} objects and generate suggestions from them
     */
    public void setQueryTokenReceiver(final @Nullable QueryTokenReceiver client) {
        hostQueryTokenReceiver = client;
    }

    /**
     * Sets a listener for anyone interested in specific actions of the {@link RichEditorView}.
     *
     * @param listener the object that wants to listen to specific actions of the {@link RichEditorView}
     */
    public void setOnRichEditorActionListener(final @Nullable OnSuggestionsVisibilityChangeListener listener) {
        actionListener = listener;
    }

    /**
     * Sets the {@link com.mycity4kids.tagging.suggestions.interfaces.SuggestionsVisibilityManager} to use (determines
     * which and how the suggestions are displayed).
     *
     * @param suggestionsVisibilityManager the {@link com.mycity4kids.tagging.suggestions.interfaces.SuggestionsVisibilityManager}
     * to use
     */
    public void setSuggestionsManager(final @NonNull SuggestionsVisibilityManager suggestionsVisibilityManager) {
        if (mentionsEditText != null && suggestionsAdapter != null) {
            mentionsEditText.setSuggestionsVisibilityManager(suggestionsVisibilityManager);
            suggestionsAdapter.setSuggestionsManager(suggestionsVisibilityManager);
        }
    }

    /**
     * Sets the {@link SuggestionsListBuilder} to use.
     *
     * @param suggestionsListBuilder the {@link SuggestionsListBuilder} to use
     */
    public void setSuggestionsListBuilder(final @NonNull SuggestionsListBuilder suggestionsListBuilder) {
        if (suggestionsAdapter != null) {
            suggestionsAdapter.setSuggestionsListBuilder(suggestionsListBuilder);
        }
    }
}
