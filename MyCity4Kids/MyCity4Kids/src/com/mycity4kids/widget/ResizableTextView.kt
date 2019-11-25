package com.mycity4kids.widget

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import com.crashlytics.android.Crashlytics
import com.mycity4kids.R
import com.mycity4kids.utils.AppUtils


class ResizableTextView : CustomFontTextView {
    private var userBio: String = ""
    lateinit var seeMore: SeeMore
    var _maxLines = 2

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    fun setUserBio(userBio: String, activity: Activity) {
        try {
            seeMore = activity as SeeMore
        } catch (e: ClassCastException) {
            Crashlytics.logException(e)
            Log.d("MC4KException", Log.getStackTraceString(e))
            return
        }
        this.userBio = userBio

        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val obs = viewTreeObserver
                val expandText = "See More"
                obs.removeOnGlobalLayoutListener(this)
                if (_maxLines == 0) {
                    val lineEndIndex = layout.getLineEnd(0)
                    val text1 = text.subSequence(0, lineEndIndex - expandText.length + 1).toString() + " " + expandText
                    text = text1
                    movementMethod = LinkMovementMethod.getInstance()
                    setText(
                            addClickablePartTextViewResizable(AppUtils.fromHtml(text.toString()), expandText,
                                    userBio), BufferType.SPANNABLE)
                } else if (_maxLines > 0 && lineCount > _maxLines) {
                    val lineEndIndex = layout.getLineEnd(_maxLines - 1)
                    if (lineEndIndex - expandText.length + 1 > 10) {
                        val text1 = text.subSequence(0, lineEndIndex - expandText.length + 1).toString() + " " + expandText
                        text = text1
                        movementMethod = LinkMovementMethod.getInstance()
                        setText(
                                addClickablePartTextViewResizable(AppUtils.fromHtml(text.toString()), expandText,
                                        userBio), BufferType.SPANNABLE)
                    } else {
                        val text1 = text.subSequence(0, lineEndIndex).toString() + " " + expandText
                        text = text1
                        movementMethod = LinkMovementMethod.getInstance()
                        setText(
                                addClickablePartTextViewResizable(AppUtils.fromHtml(text.toString()), expandText,
                                        userBio), BufferType.SPANNABLE)
                    }
                } else {
                }
            }
        })
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ResizableTextView, defStyle, 0)

        //get the value of "etw_maxLines" attribute if it has been specified

        try {
            _maxLines = a.getInt(R.styleable.ResizableTextView_maxLines, maxLines)
        } finally {
            a.recycle()
        }
    }

    private fun addClickablePartTextViewResizable(
            strSpanned: Spanned, spanableText: String, userBio: String): SpannableStringBuilder {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        if (str.contains(spanableText)) {
            ssb.setSpan(object : MySpannable(false) {
                override fun onClick(widget: View) {
                    seeMore.onSeeMoreClick(userBio)
//                    val userBioDialogFragment = UserBioDialogFragment()
//                    val fm = supportFragmentManager
//                    val _args = Bundle()
//                    _args.putString("userBio", userBio)
//                    userBioDialogFragment.arguments = _args
//                    userBioDialogFragment.isCancelable = true
//                    userBioDialogFragment.show(fm, "Choose video option")
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
        }
        return ssb
    }

    open inner class MySpannable(isUnderline: Boolean) : ClickableSpan() {
        private var isUnderline = true

        init {
            this.isUnderline = isUnderline
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = isUnderline
            ds.color = Color.parseColor("#1b76d3")
        }

        override fun onClick(widget: View) {
        }
    }

    interface SeeMore {
        fun onSeeMoreClick(userBio: String)
    }
}