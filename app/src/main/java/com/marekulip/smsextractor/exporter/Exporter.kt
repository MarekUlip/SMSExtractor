package com.marekulip.smsextractor.exporter

import android.content.Context
import com.marekulip.smsextractor.listItems.SMS
import org.json.JSONArray
import org.json.JSONObject

/**
 * Class that exports specified items into JSON format.
 */
class Exporter(private val context: Context){
    /**
     * Exports sms items provided in list into JSON file on sdcard
     */
    public fun exportSMSes(smses: MutableList<SMS>, phoneNumber:String, contactName:String){
        val json = processItems(smses, phoneNumber, contactName)
        if(FileExporter.checkForWritePermission(context)){
            FileExporter.writeToFile(json,phoneNumber+"-"+contactName+".json",context.applicationContext)
        }
    }

    /**
     * Prepares sms items for exporting into JSON format
     */
    private fun processItems(smses: MutableList<SMS>,phoneNumber:String, contactName: String):String{
        val jsonArray = JSONArray()
        var jsonObject: JSONObject = JSONObject()
        jsonObject.put("number",phoneNumber)
        jsonObject.put("name",contactName)
        jsonArray.put(jsonObject)
        for (item in smses) {
            jsonObject = JSONObject()
            jsonObject.put("id",item.id)
            jsonObject.put("body", item.smsBody)
            jsonObject.put("isSender", item.isSender)
            jsonObject.put("msgTime", item.time)
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }
}
