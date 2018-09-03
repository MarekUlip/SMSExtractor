package com.marekulip.smsextractor

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText

import kotlinx.android.synthetic.main.activity_messages.*

class MessagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            (supportFragmentManager.findFragmentById(R.id.fragment) as MessagesFragment).exportToJSON()
        }
        val lastId = PreferenceManager.getDefaultSharedPreferences(this).getLong(intent.getStringExtra(MessagesFragment.NAME),0)
        if(lastId > 0.toLong()){
            findViewById<EditText>(R.id.last_collected).text.insert(0,lastId.toString())
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment,
                MessagesFragment.newInstance(intent.getStringExtra(MessagesFragment.NUMBER),intent.getStringExtra(MessagesFragment.NAME)))
                .commit()

    }

    /**
     * Button action for getting smses based on provided id
     */
    fun getSMSes(view:View){
        val idText = findViewById<EditText>(R.id.last_collected).text.toString()
        val id = if (idText.isNotEmpty()) idText else "0"
        (supportFragmentManager.findFragmentById(R.id.fragment) as MessagesFragment).loadSMSes(id.toLong())
    }

}
