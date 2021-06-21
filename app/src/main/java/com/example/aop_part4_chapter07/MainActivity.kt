package com.example.aop_part4_chapter07

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.aop_part4_chapter07.Retrofit.RetrofitClient
import com.example.aop_part4_chapter07.SearchResult.SearchResponseItem
import com.example.aop_part4_chapter07.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
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
				showDialogForSavePhoto(photo)
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

	private fun showDialogForSavePhoto(photo : SearchResponseItem) {
		//TODO: 선택한 사진에 대해 저장 다이얼로그 표출
	 // 저장 버튼 클릭 -> 다운로드 이미지

		AlertDialog.Builder(this)
			.setMessage("이 사진을 저장하시겠습니까?")
			.setPositiveButton("저장"){ dialog, _ ->
				//TODO: 이미지 다운로드
				downloadPhoto(photo.urls?.full)
				dialog.dismiss()
			}
			.setNegativeButton("취소"){ dialog, _ ->
				dialog.dismiss()
			}
			.create()
			.show()
	}

	private fun downloadPhoto(photoUrl : String?) {
		photoUrl ?: return

		Glide.with(this)
			.asBitmap()
			.load(photoUrl)
			.diskCacheStrategy(DiskCacheStrategy.NONE)
			.into(
				object : CustomTarget<Bitmap> (SIZE_ORIGINAL, SIZE_ORIGINAL) {
					override fun onResourceReady(
						resource: Bitmap,
						transition: Transition<in Bitmap>?
					) {
						saveBitmap(resource)
					}

					override fun onLoadCleared(placeholder: Drawable?) = Unit

					override fun onLoadStarted(placeholder: Drawable?) {
						super.onLoadStarted(placeholder)
						Snackbar.make(
							binding.root,
							"다운로드 중..",
							Snackbar.LENGTH_INDEFINITE
						).show()
					}

					override fun onLoadFailed(errorDrawable: Drawable?) {
						super.onLoadFailed(errorDrawable)
						Snackbar.make(
							binding.root,
							"다운로드 실패",
							Snackbar.LENGTH_INDEFINITE
						).show()
					}
				}
			)

	}

	private fun saveBitmap(resource: Bitmap) {
		val fileName = "${System.currentTimeMillis()}.jpg"
		val resolver = applicationContext.contentResolver
		val imageCollectionUri = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
		} else {
			MediaStore.Images.Media.EXTERNAL_CONTENT_URI
		}
		val imageDetails = ContentValues().apply {
			put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
			put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")

			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				put(MediaStore.Images.Media.IS_PENDING, 1)
			}
		}

		val imageUri = resolver.insert(imageCollectionUri, imageDetails)
		imageUri ?: return

		resolver.openOutputStream(imageUri).use { outputStream ->
			resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
		}

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			imageDetails.clear()
			imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
			resolver.update(imageUri, imageDetails, null, null)
		}
	}

	override val coroutineContext: CoroutineContext
		get() = Dispatchers.Main + job

}