package org.wordpress.android.editor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class EditorWebView extends EditorWebViewAbstract {

    public EditorWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("NewApi")
    public void execJavaScriptFromString(String javaScript) {
        if (Build.VERSION.SDK_INT >= 19) {
            this.evaluateJavascript(javaScript, null);
        } else {
            this.loadUrl("javascript:" + javaScript);
        }
    }

    // Disable/Filter Keyboard inputs
//    @Override
//    public InputConnection onCreateInputConnection(EditorInfo attrs) {
//        InputConnection inputConnection = super.onCreateInputConnection(attrs);
//
//        if ((attrs.inputType & InputType.TYPE_CLASS_TEXT) == InputType.TYPE_CLASS_TEXT) {
//            attrs.inputType |= InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
//            attrs.inputType &= ~InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE;
//            attrs.inputType &= ~InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;
//
//            attrs.inputType &= ~EditorInfo.TYPE_MASK_VARIATION;
//            attrs.inputType |= InputType.TYPE_TEXT_VARIATION_PASSWORD;
//        }
//
//        return inputConnection;
//    }
}