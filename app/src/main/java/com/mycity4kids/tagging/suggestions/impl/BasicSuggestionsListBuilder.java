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

package com.mycity4kids.tagging.suggestions.impl;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.mycity4kids.R;
import com.mycity4kids.tagging.Mentions;
import com.mycity4kids.tagging.suggestions.SuggestionsResult;
import com.mycity4kids.tagging.suggestions.interfaces.Suggestible;
import com.mycity4kids.tagging.suggestions.interfaces.SuggestionsListBuilder;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Basic implementation of the {@link SuggestionsListBuilder} interface.
 */
public class BasicSuggestionsListBuilder implements SuggestionsListBuilder {

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public List<Suggestible> buildSuggestions(final @NonNull Map<String, SuggestionsResult> latestResults,
            final @NonNull String currentTokenString) {
        List<Suggestible> results = new ArrayList<>();
        for (Map.Entry<String, SuggestionsResult> entry : latestResults.entrySet()) {
            SuggestionsResult result = entry.getValue();
            if (currentTokenString.equalsIgnoreCase(result.getQueryToken().getTokenString())) {
                results.addAll(result.getSuggestions());
            }
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public View getView(final @NonNull Suggestible suggestion,
            @Nullable View convertView,
            @Nullable ViewGroup parent,
            final @NonNull Context context,
            final @NonNull LayoutInflater inflater,
            final @NonNull Resources resources) {
        View view;
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.mention_suggestion_item, parent, false);
            holder = new ViewHolder();
            holder.userHandleTextView = convertView.findViewById(R.id.userHandleTextView);
            holder.userImageView = convertView.findViewById(R.id.userImageView);
            holder.userNameTextView = convertView.findViewById(R.id.userNameTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Mentions mention = (Mentions) suggestion;
        try {
            holder.userNameTextView.setText(mention.getName());
            holder.userHandleTextView.setText(mention.getUserHandle());
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
        }

        try {
            Picasso.get().load(mention.getProfileUrl()).placeholder(R.drawable.default_commentor_img)
                    .into((holder.userImageView));
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MC4kException", Log.getStackTraceString(e));
            Picasso.get().load(R.drawable.default_commentor_img).into(holder.userImageView);
        }

        return convertView;
    }

    class ViewHolder {

        TextView userNameTextView;
        ImageView userImageView;
        TextView userHandleTextView;
    }

}
