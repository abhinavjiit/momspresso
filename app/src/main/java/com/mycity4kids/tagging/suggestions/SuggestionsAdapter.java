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

package com.mycity4kids.tagging.suggestions;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import androidx.annotation.NonNull;
import com.mycity4kids.tagging.suggestions.interfaces.Suggestible;
import com.mycity4kids.tagging.suggestions.interfaces.SuggestionsListBuilder;
import com.mycity4kids.tagging.suggestions.interfaces.SuggestionsVisibilityManager;
import com.mycity4kids.tagging.tokenization.QueryToken;
import com.mycity4kids.tagging.tokenization.interfaces.TokenSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Adapter class for displaying suggestions.
 */
public class SuggestionsAdapter extends BaseAdapter {

    private final Object mlock = new Object();
    private final Context context;
    private final Resources resources;
    private final LayoutInflater layoutInflater;

    private SuggestionsVisibilityManager suggestionsVisibilityManager;
    private SuggestionsListBuilder suggestionsListBuilder;
    private final List<Suggestible> suggestibleList;

    // Map from a given bucket (defined by a unique string) to the latest query result for that bucket
    // Example buckets: "Person-Database", "Person-Network", "Companies-Database", "Companies-Network"
    private final Map<String, SuggestionsResult> suggestionsResultHashMap = new HashMap<>();
    private final Map<QueryToken, Set<String>> waitingForResults = new HashMap<>();

    public SuggestionsAdapter(final @NonNull Context context,
            final @NonNull SuggestionsVisibilityManager suggestionsVisibilityManager,
            final @NonNull SuggestionsListBuilder suggestionsListBuilder) {
        super();
        this.context = context;
        resources = context.getResources();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.suggestionsVisibilityManager = suggestionsVisibilityManager;
        this.suggestionsListBuilder = suggestionsListBuilder;
        suggestibleList = new ArrayList<>();
    }

    // --------------------------------------------------
    // Public Methods
    // --------------------------------------------------

    /**
     * Method to notify the adapter that a new {@link QueryToken} has been received and that suggestions will be added
     * to the adapter once generated.
     *
     * @param queryToken the {@link QueryToken} that has been received
     * @param buckets a list of string dictating which buckets the future query results will go into
     */

    public void notifyQueryTokenReceived(@NonNull QueryToken queryToken,
            @NonNull List<String> buckets) {
        synchronized (mlock) {
            Set<String> currentBuckets = waitingForResults.get(queryToken);
            if (currentBuckets == null) {
                currentBuckets = new HashSet<>();
            }
            currentBuckets.addAll(buckets);
            waitingForResults.put(queryToken, currentBuckets);
        }
    }

    /**
     * Add mention suggestions to a given bucket in the adapter. The adapter tracks the latest result for every given
     * bucket, and passes this information to the SuggestionsManager to construct the list of suggestions in the
     * appropriate order. Note: This should be called exactly once for every bucket returned from the query client.
     *
     * @param result a {@link SuggestionsResult} containing the suggestions to add
     * @param bucket a string representing the group to place the {@link SuggestionsResult} into
     * @param source the associated {@link TokenSource} to use for reference
     */
    public void addSuggestions(final @NonNull SuggestionsResult result,
            final @NonNull String bucket,
            final @NonNull TokenSource source) {
        // Add result to proper bucket and remove from waiting
        QueryToken query = result.getQueryToken();
        synchronized (mlock) {
            suggestionsResultHashMap.put(bucket, result);
            Set<String> waitingForBuckets = waitingForResults.get(query);
            if (waitingForBuckets != null) {
                waitingForBuckets.remove(bucket);
                if (waitingForBuckets.size() == 0) {
                    waitingForResults.remove(query);
                }
            }
        }

        // Rebuild the list of suggestions in the appropriate order
        String currentTokenString = source.getCurrentTokenString();
        synchronized (mlock) {
            suggestibleList.clear();
            List<Suggestible> suggestions = suggestionsListBuilder
                    .buildSuggestions(suggestionsResultHashMap, currentTokenString);

            // If we have suggestions, add them to the adapter and display them
            if (suggestions.size() > 0) {
                suggestibleList.addAll(suggestions);
                suggestionsVisibilityManager.displaySuggestions(true);
            } else {
                hideSuggestionsIfNecessary(result.getQueryToken(), source);
            }
        }

        notifyDataSetChanged();
    }

    /**
     * Clear all data from adapter.
     */
    public void clear() {
        suggestionsResultHashMap.clear();
        notifyDataSetChanged();
    }

    // --------------------------------------------------
    // Private Helper Methods
    // --------------------------------------------------

    /**
     * Hides the suggestions if there are no more incoming queries.
     *
     * @param currentQuery the most recent {@link QueryToken}
     * @param source the associated {@link TokenSource} to use for reference
     */
    private void hideSuggestionsIfNecessary(final @NonNull QueryToken currentQuery,
            final @NonNull TokenSource source) {
        String queryTS = currentQuery.getTokenString();
        String currentTS = source.getCurrentTokenString();
        if (!isWaitingForResults(currentQuery) && queryTS != null && queryTS.equals(currentTS)) {
            suggestionsVisibilityManager.displaySuggestions(false);
        }
    }

    /**
     * Determines if the adapter is still waiting for results for a given {@link QueryToken}.
     *
     * @param currentQuery the {@link QueryToken} to check if waiting for results on
     * @return true if still waiting for the results of the current query
     */
    private boolean isWaitingForResults(QueryToken currentQuery) {
        synchronized (mlock) {
            Set<String> buckets = waitingForResults.get(currentQuery);
            return buckets != null && buckets.size() > 0;
        }
    }

    // --------------------------------------------------
    // BaseAdapter Overrides
    // --------------------------------------------------

    @Override
    public int getCount() {
        return suggestibleList.size();
    }

    @Override
    public Suggestible getItem(int position) {
        Suggestible mention = null;
        if (position >= 0 && position < suggestibleList.size()) {
            mention = suggestibleList.get(position);
        }
        return mention;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Suggestible suggestion = getItem(position);
        View v = null;
        if (suggestionsVisibilityManager != null) {
            v = suggestionsListBuilder.getView(suggestion, convertView, parent, context, layoutInflater, resources);
        }
        return v;
    }

    // --------------------------------------------------
    // Setters
    // --------------------------------------------------

    /**
     * Sets the {@link com.mycity4kids.tagging.suggestions.interfaces.SuggestionsVisibilityManager} to use.
     *
     * @param suggestionsVisibilityManager
     * the {@link com.mycity4kids.tagging.suggestions.interfaces.SuggestionsVisibilityManager}to use
     */
    public void setSuggestionsManager(final @NonNull SuggestionsVisibilityManager suggestionsVisibilityManager) {
        this.suggestionsVisibilityManager = suggestionsVisibilityManager;
    }

    /**
     * Sets the {@link SuggestionsListBuilder} to use.
     *
     * @param suggestionsListBuilder the {@link SuggestionsListBuilder} to use
     */
    public void setSuggestionsListBuilder(final @NonNull SuggestionsListBuilder suggestionsListBuilder) {
        this.suggestionsListBuilder = suggestionsListBuilder;
    }

}