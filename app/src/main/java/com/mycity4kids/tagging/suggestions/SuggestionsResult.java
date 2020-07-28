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

import androidx.annotation.NonNull;
import com.mycity4kids.tagging.suggestions.interfaces.Suggestible;
import com.mycity4kids.tagging.tokenization.QueryToken;
import java.util.List;

/**
 * Class representing the results of a query for suggestions.
 */
public class SuggestionsResult {

    private final QueryToken queryToken;
    private final List<? extends Suggestible> suggestibles;

    public SuggestionsResult(@NonNull QueryToken queryToken,
            @NonNull List<? extends Suggestible> suggestions) {
        this.queryToken = queryToken;
        suggestibles = suggestions;
    }

    /**
     * Get the {@link QueryToken} used to generate the mention suggestions.
     *
     * @return a {@link QueryToken}
     */
    @NonNull
    public QueryToken getQueryToken() {
        return queryToken;
    }

    /**
     * Get the list of mention suggestions corresponding to the {@link QueryToken}.
     *
     * @return List of {@link com.mycity4kids.tagging.suggestions.interfaces.Suggestible} mention suggestions
     */
    @NonNull
    public List<? extends Suggestible> getSuggestions() {
        return suggestibles;
    }
}
