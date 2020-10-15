package com.mycity4kids.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mycity4kids.R


class VlogLanguageSelectionAdapter(
    context: Context,
    resourceLayout: Int,
    private val list: Array<String>
) :
    ArrayAdapter<String>(context, resourceLayout, list) {

    private var mContext: Context = context

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return vlogLanguageSelectionAdapter(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return vlogLanguageSelectionAdapter(position, convertView, parent)
    }

    private fun vlogLanguageSelectionAdapter(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.vlog_lang_drop_down_item_layout,
            parent,
            false
        )

        val text = view.findViewById<TextView>(R.id.languageTextView)
        if (position == 0) {
            text.setTextColor(ContextCompat.getColor(mContext, R.color.hint_txt_color))

        }
        text.text = list[position]
        return view
    }

}