package com.example.aop_part4_chapter07

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aop_part4_chapter07.Retrofit.RetrofitClient
import com.example.aop_part4_chapter07.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

	private lateinit var binding: ActivityMainBinding
	private var unsplashAdapter: UnsplashAdapter? = null
	private val job = Job()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		displayImage(null)
		initRecyclerView()
		bindView()
	}

	private fun initRecyclerView() = with(binding) {
		if (unsplashAdapter == null) {
			unsplashAdapter = UnsplashAdapter()
		}

		imageRecyclerView.adapter = unsplashAdapter
		imageRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
	}

	private fun bindView() = with(binding) {
		searchKeywordEditText.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

			}

			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

			}

			override fun afterTextChanged(s: Editable?) {
				//TODO: 한글 검색 예외처리
				displayImage(s?.toString())
			}
		})
	}

	private fun displayImage(keyword: String?) {
		try {
			launch {
				withContext(Dispatchers.IO) {
					val response = RetrofitClient.getUnsplashApiService.getImageList(
						BuildConfig.UNSPLASH_ACCESS_KEY,
						keyword
					)
					Log.i("get_data", response.size.toString())
					withContext(Dispatchers.Main) {
						unsplashAdapter?.submitList(response)
					}
				}
			}
		} catch (e : Exception) {
			Log.e("Error:: ", e.toString())
		}
	}

	override val coroutineContext: CoroutineContext
		get() = Dispatchers.Main + job

}