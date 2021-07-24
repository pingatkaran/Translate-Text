package com.app.texttranslate


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.texttranslate.TranslateAPI.TranslateListener
import org.json.JSONException
import org.json.JSONObject
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val json = loadJsonObjectFromAsset()
        val refList: MutableList<LanguageModel> = ArrayList()
        val dropdownList: MutableList<String> = ArrayList()
        try {
            val refArray = json!!.getJSONArray("result")
            for (i in 0 until refArray.length()) {
                val ref = refArray.getJSONObject(i).getString("name")
                val refcode = refArray.getJSONObject(i).getString("code")
                refList.add(LanguageModel(ref,refcode))
                dropdownList.add(ref)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val spinner = findViewById<View>(R.id.spinner) as Spinner
        val arrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            dropdownList
        )
        spinner.adapter = arrayAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val translateAPI = TranslateAPI(
                    Language.AUTO_DETECT,
                    refList[position].code,
                    "A paragraph is a series of related sentences developing a central idea, called the topic. Try to think about paragraphs in terms of thematic unity: a paragraph is a sentence or a group of sentences that supports one central, unified idea. Paragraphs add one idea at a time to your broader argument.\n"
                )

                translateAPI.setTranslateListener(object : TranslateListener {
                    override fun onSuccess(translatedText: String) {
                        Log.d("TAG", "onSuccess: $translatedText")
                        Toast.makeText(this@MainActivity        ,""+translatedText,Toast.LENGTH_LONG).show()
                    }

                    override fun onFailure(ErrorText: String) {
                        Log.d("TAG", "onFailure: $ErrorText")
                    }
                })

            }

        }
    }

    fun loadJsonObjectFromAsset(): JSONObject? {
        try {
            val json: String = loadStringFromAsset()!!
            if (json != null) return JSONObject(json)
        } catch (e: Exception) {
            Log.e("JsonUtils", e.toString())
        }
        return null
    }

    private fun loadStringFromAsset(): String? {
        val `is`: InputStream = assets.open("languages.json")
        val size = `is`.available()
        val buffer = ByteArray(size)
        `is`.read(buffer)
        `is`.close()
        return String(buffer, Charsets.UTF_8)
    }
}