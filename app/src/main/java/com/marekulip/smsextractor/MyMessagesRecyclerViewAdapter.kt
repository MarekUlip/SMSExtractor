package com.marekulip.smsextractor

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.marekulip.smsextractor.listItems.SMS

import kotlinx.android.synthetic.main.fragment_messages.view.*
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [SMS] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class MyMessagesRecyclerViewAdapter(
        var mValues: List<SMS>)
    : RecyclerView.Adapter<MyMessagesRecyclerViewAdapter.ViewHolder>() {

    private val calendar = Calendar.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_messages, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = item.id.toString()
        holder.mContentView.text = item.smsBody
        holder.mIsSender.text = (if (item.isSender) "Recieved" else "Sent")
        calendar.time = Date(item.time)
        holder.mTime.text = calendar.time.toString()
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.sms_id
        val mContentView: TextView = mView.msg_body
        val mIsSender: TextView = mView.is_sender
        val mTime: TextView = mView.time

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
