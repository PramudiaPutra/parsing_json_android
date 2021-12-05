package com.pramudiaputr.myquote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.pramudiaputr.myquote.databinding.ActivityListQuoteBinding
import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class ListQuoteActivity : AppCompatActivity() {

    companion object {
        private val TAG = ListQuoteActivity::class.java.simpleName
    }

    private lateinit var binding: ActivityListQuoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListQuoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "List of Quotes"

        val layoutManager = LinearLayoutManager(this)
        binding.listQuotes.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.listQuotes.addItemDecoration(itemDecoration)

        getListQuotes()

    }

    private fun getListQuotes() {
        binding.progressBar.visibility = View.VISIBLE

        val client = AsyncHttpClient()
        val url = "https://quote-api.dicoding.dev/list"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                binding.progressBar.visibility = View.INVISIBLE

                val listQuotes = ArrayList<String>()
                val result = String(responseBody!!)
                Log.d(TAG, result)

                try {
                    val jsonArray = JSONArray(result)
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val quote = jsonObject.getString("en")
                        val author = jsonObject.getString("author")
                        listQuotes.add("\n$quote\n$author")
                    }
                    val adapter = QuoteAdapter(listQuotes)
                    binding.listQuotes.adapter = adapter
                } catch (e: Exception) {
                    Toast.makeText(this@ListQuoteActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                binding.progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Toast.makeText(this@ListQuoteActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }
}