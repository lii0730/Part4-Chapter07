package com.example.aop_part4_chapter07

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.aop_part4_chapter07.SearchResult.SearchResponseItem

class UnsplashAdapter(val onClicked: (SearchResponseItem) -> Unit) : ListAdapter<SearchResponseItem, UnsplashAdapter.ViewHolder>(differ) {

	inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

		private val item_imageView : ImageView by lazy {
			itemView.findViewById(R.id.item_imageView)
		}

		private val userProfileImageView : ImageView by lazy {
			itemView.findViewById(R.id.userProfileImageView)
		}

		private val userNameTextView : TextView by lazy {
			itemView.findViewById(R.id.userNameTextView)
		}

		private val descriptionTextView : TextView by lazy {
			itemView.findViewById(R.id.descriptionTextView)
		}

		fun bind(item: SearchResponseItem?) {
			item?.let { item ->
				Glide.with(item_imageView)
					.load(item.urls?.regular)
					.thumbnail(
						Glide.with(item_imageView)
							.load(item.urls?.thumb)
							.transition(DrawableTransitionOptions.withCrossFade())
					)
					.into(item_imageView)

				Glide.with(userProfileImageView)
					.load(item.user?.profileImage?.medium)
					.circleCrop()
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(userProfileImageView)

				userNameTextView.text = item.user?.name
				if(item.description.isNullOrBlank()) {
					descriptionTextView.visibility = View.GONE
				} else {
					descriptionTextView.text = item.description
					descriptionTextView.visibility = View.VISIBLE
				}

				itemView.setOnClickListener {
					onClicked(item)
				}
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_recyclerview_item, parent, false))
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(currentList[position])
	}

	companion object {
		val differ = object : DiffUtil.ItemCallback<SearchResponseItem>() {
			override fun areItemsTheSame(
				oldItem: SearchResponseItem,
				newItem: SearchResponseItem
			): Boolean {
				return oldItem.id == newItem.id
			}

			override fun areContentsTheSame(
				oldItem: SearchResponseItem,
				newItem: SearchResponseItem
			): Boolean {
				return oldItem == newItem
			}
		}
	}

}