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

package com.mycity4kids.tagging.tokenization;

import androidx.annotation.NonNull;
import com.mycity4kids.tagging.tokenization.interfaces.QueryTokenReceiver;
import com.mycity4kids.tagging.tokenization.interfaces.Tokenizer;
import java.io.Serializable;

/**
 * Class that represents a token from a {@link Tokenizer} that can be used to query for suggestions. Note that if the
 * query is explicit, the explicit character has not been removed from the start of the token string. To get the string
 * without any explicit character, use {@link #getKeywords()}.
 */
public class QueryToken implements Serializable {

    // what the user typed, exactly, as detected by the tokenizer
    private String tokenString;

    // if the query was explicit, then this was the character the user typed (otherwise, null char)
    private char explicitChar = 0;

    public QueryToken(@NonNull String tokenString) {
        this.tokenString = tokenString;
    }

    public QueryToken(@NonNull String tokenString, char explicitChar) {
        this(tokenString);
        this.explicitChar = explicitChar;
    }

    /*
     * @return query as typed by the user and detected by the {@link Tokenizer}
     */
    @NonNull
    public String getTokenString() {
        return tokenString;
    }

    /**
     * Returns a String that should be used to perform the query. It is equivalent to the token string without an
     * explicit character if it exists.
     *
     * @return one or more words that the {@link QueryTokenReceiver} should use for the query
     */
    @NonNull
    public String getKeywords() {
        return (explicitChar != 0) ? tokenString.substring(1) : tokenString;
    }

    /*
     * @return the explicit character used in the query, or the null character if the query is implicit
     */
    public char getExplicitChar() {
        return explicitChar;
    }

    /*
     * @return true if the query is explicit
     */
    public boolean isExplicit() {
        return explicitChar != 0;
    }

    @Override
    public boolean equals(Object o) {
        QueryToken that = (QueryToken) o;
        return tokenString != null && that != null && tokenString.equals(that.getTokenString());
    }

    @Override
    public int hashCode() {
        return tokenString.hashCode();
    }
}
