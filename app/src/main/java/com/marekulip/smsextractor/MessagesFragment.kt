package com.marekulip.smsextractor

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.marekulip.smsextractor.exporter.Exporter
import com.marekulip.smsextractor.listItems.SMS
import java.util.ArrayList


/**
 * Fragment that displays all messages from provided contact
 */
class MessagesFragment : Fragment() {

    /**
     * Name of the contact
     */
    private var name = ""
    /**
     * Phone number of the contact
     */
    private var number = ""
    /**
     * List of messages
     */
    private var smsmes: MutableList<SMS> = ArrayList()
    /**
     * Adapter that displays the messags
     */
    private var mAdapter:MyMessagesRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(NAME,"")
            number = it.getString(NUMBER,"")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_messages_list, container, false)
        smsmes = getSMSes(PreferenceManager.getDefaultSharedPreferences(context).getLong(name,0))
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = MyMessagesRecyclerViewAdapter(smsmes,context)
                mAdapter = adapter as MyMessagesRecyclerViewAdapter
            }
        }
        return view
    }

    /**
     * Get all messages starting from specified id
     */
    private fun getSMSes(lastId:Long): MutableList<SMS>{
        val message = Uri.parse("content://sms/")
        val projection = arrayOf(android.provider.Telephony.Sms._ID,
                android.provider.Telephony.Sms.ADDRESS, android.provider.Telephony.Sms.BODY, android.provider.Telephony.Sms.DATE,android.provider.Telephony.Sms.TYPE)
        val selection = android.provider.Telephony.Sms.ADDRESS+ " LIKE ?" + " AND " + android.provider.Telephony.Sms._ID+" >= ?"
        val selectionArgs = arrayOf("%"+number+"%"/*"%+420732130927%"*/,lastId.toString())
        val c = context?.contentResolver?.query(message, projection, selection, selectionArgs, null)
        val smses: MutableList<SMS> = ArrayList()
        if (c != null && c.moveToFirst()) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(name,(c.getString(c.getColumnIndexOrThrow(android.provider.Telephony.Sms._ID))).toLong()+1).apply()
            do{
                smses.add(SMS(c.getString(c.getColumnIndexOrThrow(android.provider.Telephony.Sms._ID)).toLong(),name,c.getString(c.getColumnIndexOrThrow(android.provider.Telephony.Sms.BODY)),c.getString(c.getColumnIndexOrThrow(android.provider.Telephony.Sms.TYPE)).contains("1"),c.getLong(c.getColumnIndexOrThrow(android.provider.Telephony.Sms.DATE))))

            }while(c.moveToNext())
        }
        c?.close()
        return smses
    }

    /**
     * Load all messages starting from specified id and update adapter
     */
    fun loadSMSes(lastId:Long){
        smsmes = getSMSes(lastId)
        mAdapter?.mValues = smsmes
        mAdapter?.notifyDataSetChanged()
    }

    /**
     * Export loaded messages to JSON file
     */
    fun exportToJSON(){
        Exporter(activity as Context).exportSMSes(smsmes,number,name)
    }

    companion object {
        /**
         * Contact name
         */
        const val NAME = "name"
        /**
         * Contact phone number
         */
        const val NUMBER = "number"

        @JvmStatic
        fun newInstance(number: String, name: String) =
                MessagesFragment().apply {
                    arguments = Bundle().apply {
                        putString(NAME, name)
                        putString(NUMBER, number)
                    }
                }
    }
}
