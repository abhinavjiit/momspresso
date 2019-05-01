package com.mycity4kids.ui.campaign.fragment

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.mycity4kids.R
import com.mycity4kids.ui.campaign.PaymentModeListModal
import com.mycity4kids.ui.campaign.PaymentModesModal
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.paymets_modes_adapter.view.*


class PaymentModesAdapter(private val paymentModeList: List<PaymentModesModal>, private val context: Fragment)
    : RecyclerView.Adapter<PaymentModesAdapter.ViewHolder>() {

    private var paymentMode: List<PaymentModesModal> = paymentModeList
    // private var dataDefault: DefaultData? = dataDefaultData
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



            holder.nonDefaultRadioButton.setOnClickListener {
                clickListener.onRadioButton(position)
            }
            holder.nonDefaultEditTextView.setOnClickListener {


                clickListener.onCellClick(paymentMode[position].type_id,position)
            }
        }


        /* if (position == 0) {
             holder.selectOptionPaymentTextView.visibility = View.VISIBLE
         } else {
             holder.selectOptionPaymentTextView.visibility = View.GONE

         }
         holder.nonDefaultRadioButton.isSelected = allPaymentDataAdapter.available!![position].isChecked

         if (paymentMode[position].isDefault) {

             holder.accountNumberTextView.visibility = View.VISIBLE
             holder.accountNumberTextView.text = allPaymentDataAdapter.default!!.account_number
         } else {
             holder.accountNumberTextView.visibility = View.GONE
         }

         holder.nonDefaultRadioButton.setOnClickListener {
             allPaymentDataAdapter.available!![position].isChecked = true


             notifyDataSetChanged()
         }
         Picasso.with(context.context).load(paymentMode[position].icon).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                 .fit().into(holder.nonDefaultModeImageView)
         holder.nonDefaultModeImageView.setOnClickListener {


             clickListener.onCellClick(paymentMode[position].type_id)
         }*/

        /*if (position == 0) {
            if (allPaymentDataAdapter.default == null) {

            }



            holder.selectOptionPaymentTextView.visibility = View.VISIBLE
            if (dataDefault != null && dataDefault!!.account_type != null && dataDefault!!.account_type!!.icon != null) {
                holder.defaultdataRelativeLayout.visibility = View.VISIBLE
                Picasso.with(context.context).load(dataDefault!!.account_type!!.icon).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                        .fit().into(holder.paymentDefaultModeImageView)
                holder.tagTextView.text = dataDefault!!.account_number
                holder.defaultRadioButton.isSelected = dataDefault!!.account_type!!.isChecked

                holder.paymentDefaultModeImageView.setOnClickListener()
                {
                    dataDefault!!.account_type!!.isChecked = true
                    notifyDataSetChanged()
                    clickListener.onCellClick(dataDefault!!.account_type!!.id) *//*pass your payment id here*//*


                }

            } else {
                holder.defaultdataRelativeLayout.visibility = View.GONE
            }

            holder.nonDefaultRadioButton.isSelected = paymentMode[position].isChecked

            val item = paymentMode[position].icon
            Picasso.with(context.context).load(item).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                    .fit().into(holder.nonDefaultModeImageView)

            holder.nonDefaultModeImageView.setOnClickListener()
            {
                paymentMode[position].isChecked = true
                notifyDataSetChanged()
                clickListener.onCellClick(paymentMode[position].id)

            }

        } else {
            holder.selectOptionPaymentTextView.visibility = View.GONE
            holder.nonDefaultRadioButton.isSelected = paymentMode[position].isChecked

            val item = paymentMode[position].icon
            Picasso.with(context.context).load(item).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                    .fit().into(holder.nonDefaultModeImageView)
            holder.defaultdataRelativeLayout.visibility = View.GONE
            holder.nonDefaultModeImageView.setOnClickListener()
            {
                paymentMode[position].isChecked = true
                notifyDataSetChanged()

                clickListener.onCellClick(paymentMode[position].id)
            }

        }*/

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
        fun onCellClick(paymentModeId: Int,position: Int)
        fun onRadioButton(position: Int)

    }


}

