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

package com.mycity4kids.tagging.ui.wrappers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.mycity4kids.R;
import com.mycity4kids.tagging.ui.RichEditorView;

/**
 * Convenient fragment wrapper around a {@link RichEditorView}.
 */
public class RichEditorFragment extends Fragment {

    private static final String FRAGMENT_TAG = "fragment_rich_editor";

    private RichEditorView richEditorView;
    private OnCreateViewListener onCreateViewListener;

    public interface OnCreateViewListener {

        void onFragmentCreateView(@NonNull RichEditorFragment fragment);
    }

    public RichEditorFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static RichEditorFragment newInstance(@Nullable Bundle args) {
        RichEditorFragment fragment = new RichEditorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    public static RichEditorFragment getInstance(@NonNull FragmentManager fragmentManager, @Nullable Bundle args) {
        RichEditorFragment instance;
        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (fragment == null) {
            instance = RichEditorFragment.newInstance(args);
        } else {
            instance = (RichEditorFragment) fragment;
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.editor_fragment, container, false);
        if (rootView == null) {
            return null;
        }

        richEditorView = rootView.findViewById(R.id.rich_editor);
        if (onCreateViewListener != null) {
            onCreateViewListener.onFragmentCreateView(this);
        }

        return rootView;
    }

    @Nullable
    public RichEditorView getRichEditor() {
        return richEditorView;
    }

    @NonNull
    public String getFragmentTag() {
        return FRAGMENT_TAG;
    }

    public void setOnCreateViewListener(@Nullable OnCreateViewListener listener) {
        onCreateViewListener = listener;
    }

}
