package com.example.aop_part4_chapter07

import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aop_part4_chapter07.Retrofit.RetrofitClient
import com.example.aop_part4_chapter07.SearchResult.SearchResponseItem
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

		displayImage()
		initRecyclerView()
		bindView()
	}

	private fun initRecyclerView() = with(binding) {
		if (unsplashAdapter == null) {
			unsplashAdapter = UnsplashAdapter(onClicked = { photo ->
				downloadImage(photo)
			})
		}

		imageRecyclerView.adapter = unsplashAdapter
		imageRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
	}

	private fun bindView() = with(binding) {
		searchKeywordEditText.setOnEditorActionListener { v, actionId, event ->
			if(actionId == EditorInfo.IME_ACTION_SEARCH) {
				displayImage(v.text.toString())
			}

			(getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(v.windowToken, 0)

			v.clearFocus()
			true
		}

		refreshLayout.setOnRefreshListener {
			displayImage(searchKeywordEditText.text.toString())
		}
	}

	private fun displayImage(keyword: String? = null) {
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
						binding.refreshLayout.isRefreshing = false
						binding.imageRecyclerView.visibility = View.VISIBLE
						binding.shimmerFrameLayout.visibility = View.GONE
					}
				}
			}
		} catch (e : Exception) {
			Log.e("Error:: ", e.toString())
		}
	}

	private fun downloadImage(photo : SearchResponseItem) {
		//TODO: 선택 사진 다운로드
		// 다운로드 완료 -> 배경화면 지정 액션 설정
	}

	override val coroutineContext: CoroutineContext
		get() = Dispatchers.Main + job

}