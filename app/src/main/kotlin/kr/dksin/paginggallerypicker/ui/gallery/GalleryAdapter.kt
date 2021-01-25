package kr.dksin.paginggallerypicker.ui.gallery

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.orhanobut.logger.Logger
import kr.dksin.paginggallerypicker.R
import kr.dksin.paginggallerypicker.databinding.ItemGalleryViewBinding
import kr.dksin.paginggallerypicker.domain.model.GalleryData
import javax.inject.Inject

class GalleryAdapter @Inject constructor(
) : PagingDataAdapter<GalleryData, RecyclerView.ViewHolder>(GalleryDataDiffCallback) {

    private val selectList: MutableList<GalleryData> = mutableListOf()

    fun getSelectList() = selectList

    interface GalleryAdapterListener {
        fun onClickItem(galleryData: GalleryData, imageView: ImageView?)
    }

    var galleryAdapterListener: GalleryAdapterListener? = null

//    var tracker: SelectionTracker<GalleryData>? = null

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_gallery_view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_gallery_view -> GalleryItemViewHolder(
                ItemGalleryViewBinding.inflate(
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
            R.layout.item_gallery_view -> (holder as GalleryItemViewHolder).onBind(position)
            else -> (holder as GalleryItemViewHolder).onBind(position)
        }
    }

    inner class GalleryItemViewHolder(private val binding: ItemGalleryViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var galleryData: GalleryData? = null
            private set

        fun onBind(position: Int) {
            this.galleryData = getItem(position)
            with(binding) {
                getItem(position)?.let {

                    for (data in selectList) {
                        if (it.id == data.id) {
                            it.isSelect = true
                        }
                    }
//                    tracker?.let { selectTacker ->
//                        val isActivated = selectTacker.isSelected(it)
//                        if (!isActivated) galleryItemCardView.strokeColor =
//                            Color.TRANSPARENT else galleryItemCardView.strokeColor =
//                            ContextCompat.getColor(galleryItemCardView.context, R.color.purple_500)
//                    }

                    if (!it.isSelect) galleryItemCardView.strokeColor =
                        Color.TRANSPARENT else galleryItemCardView.strokeColor =
                        ContextCompat.getColor(galleryItemCardView.context, R.color.purple_500)

                    Glide.with(galleryItemImageView.context)
                        .load(it.imageUri)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(R.color.black)
                        .error(R.color.black)
                        .into(galleryItemImageView)

                    galleryItemCardView.setOnClickListener { view ->
                        if (it.isSelect) {
                            it.isSelect = false
                            removeItem(it.id)
                            notifyItemChanged(position)
                        } else {
                            if (selectList.size < GalleryFragment.MAX_SIZE) {
                                it.isSelect = true
                                selectList.add(it)
                                notifyItemChanged(position)
                            }else{
                                Toast.makeText(binding.galleryItemCardView.context, "최대 ${GalleryFragment.MAX_SIZE}개의 사진을 포함 할 수 있습니다.", Toast.LENGTH_LONG).show()
                            }
                        }

                        galleryAdapterListener?.onClickItem(it, galleryItemImageView)
                    }
                }
                executePendingBindings()
            }
        }

//        fun getItemDetails(): ItemDetailsLookup.ItemDetails<GalleryData> =
//            object : ItemDetailsLookup.ItemDetails<GalleryData>() {
//                override fun inSelectionHotspot(e: MotionEvent): Boolean = true
//                override fun getSelectionKey(): GalleryData? = galleryData
//                override fun getPosition(): Int = absoluteAdapterPosition
//            }
    }

//    fun getSelectionKeyForPosition(position: Int): GalleryData? = getItem(position)

    fun removeItem(id: Long) {
        val iterator = selectList.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.id == id) {
                iterator.remove()
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