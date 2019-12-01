package com.mycity4kids.ui.campaign

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.mycity4kids.R
import com.mycity4kids.ui.campaign.PaymentModesModal
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.paymets_modes_adapter.view.*


class PaymentModesAdapter(private val paymentModeList: List<PaymentModesModal>, private val context: Fragment)
    : RecyclerView.Adapter<PaymentModesAdapter.ViewHolder>() {

    private var paymentMode: List<PaymentModesModal> = paymentModeList
    private var clickListener = context as ClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.paymets_modes_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.selectOptionPaymentTextView.visibility = View.VISIBLE


        } else {
            holder.selectOptionPaymentTextView.visibility = View.GONE
        }
        Picasso.with(context.context).load(paymentMode[position].icon).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                .fit().into(holder.nonDefaultModeImageView)

        var item = paymentMode.get(holder.adapterPosition)

        if (item != null) {
            holder.nonDefaultRadioButton.isChecked = item.isDefault

            if (!item.accountNumber.isNullOrBlank()) {
                holder.accountNumberTextView.text = item.accountNumber
                holder.nonDefaultEditTextView.visibility = View.VISIBLE
                holder.viewBaseLine.visibility = View.VISIBLE
            } else {

                holder.accountNumberTextView.visibility = View.GONE
                holder.nonDefaultEditTextView.visibility = View.INVISIBLE
                holder.viewBaseLine.visibility = View.INVISIBLE

            }
            holder.nonDefaultModeImageView.setOnClickListener {
                clickListener.onRadioButton(position)

            }
            holder.nonDefaultRadioButton.setOnClickListener {
                clickListener.onRadioButton(position)
            }
            holder.nonDefaultEditTextView.setOnClickListener {
                clickListener.onCellClick(paymentMode[position].type_id, position, paymentMode[position].id)
            }
        }


    }

    override fun getItemCount(): Int = paymentMode.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView), View.OnClickListener {
        override fun onClick(p0: View?) {
        }

        init {
            mView.setOnClickListener(this)
        }

        val nonDefaultModeImageView: ImageView = mView.nonDefaultModeImageView
        val defaultdataRelativeLayout: RelativeLayout = mView.defaultdataRelativeLayout
        val paymentDefaultModeImageView: ImageView = mView.paymentDefaultModeImageView
        val tagTextView: TextView = mView.tagTextView
        val selectOptionPaymentTextView: TextView = mView.selectOptionPaymentTextView
        val nonDefaultRadioButton: RadioButton = mView.nonDefaultRadioButton
        val defaultRadioButton: RadioButton = mView.defaultRadioButton
        val accountNumberTextView: TextView = mView.accountNumberTextView
        val nonDefaultEditTextView: TextView = mView.nonDefaultEditTextView
        val viewBaseLine: View = mView.viewBaseLine

    }

    interface ClickListener {
        fun onCellClick(paymentModeId: Int, position: Int, id: Int)
        fun onRadioButton(position: Int)
    }
}

