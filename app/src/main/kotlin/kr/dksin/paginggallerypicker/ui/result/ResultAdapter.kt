package kr.dksin.paginggallerypicker.ui.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kr.dksin.paginggallerypicker.R
import kr.dksin.paginggallerypicker.databinding.ItemResultViewBinding
import kr.dksin.paginggallerypicker.domain.model.GalleryData
import javax.inject.Inject

class ResultAdapter @Inject constructor(
) : ListAdapter<GalleryData, RecyclerView.ViewHolder>(GalleryDataDiffCallback) {


    override fun getItemViewType(position: Int): Int {
        return R.layout.item_result_view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_result_view -> ResultItemViewHolder(
                ItemResultViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Unknown view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return when (getItemViewType(position)) {
            R.layout.item_result_view -> (holder as ResultItemViewHolder).onBind(position)
            else -> (holder as ResultItemViewHolder).onBind(position)
        }
    }

    inner class ResultItemViewHolder(private val binding: ItemResultViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var galleryData: GalleryData? = null
            private set

        fun onBind(position: Int) {
            this.galleryData = getItem(position)
            with(binding) {
                getItem(position)?.let {

                    Glide.with(resultItemImageView.context)
                        .load(it.imageUri)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.color.white)
                        .error(R.color.black)
                        .into(resultItemImageView)

                }
                executePendingBindings()
            }
        }

    }

    object GalleryDataDiffCallback : DiffUtil.ItemCallback<GalleryData>() {

        override fun areContentsTheSame(oldItem: GalleryData, newItem: GalleryData) =
            oldItem == newItem

        override fun areItemsTheSame(oldItem: GalleryData, newItem: GalleryData) =
            oldItem.id == newItem.id

    }
}