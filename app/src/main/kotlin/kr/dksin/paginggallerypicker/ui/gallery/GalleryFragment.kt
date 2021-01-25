package kr.dksin.paginggallerypicker.ui.gallery

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.DefaultItemAnimator
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kr.dksin.paginggallerypicker.R
import kr.dksin.paginggallerypicker.base.BaseFragment
import kr.dksin.paginggallerypicker.databinding.FragmentGalleryBinding
import kr.dksin.paginggallerypicker.domain.model.GalleryAlbum
import kr.dksin.paginggallerypicker.domain.model.GalleryData
import kr.dksin.paginggallerypicker.ext.dpToPx
import kr.dksin.paginggallerypicker.ext.getSupportActionBar
import kr.dksin.paginggallerypicker.ext.setSupportActionBar
import kr.dksin.paginggallerypicker.util.SCommonItemDecoration
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class GalleryFragment : BaseFragment<GalleryViewModel, FragmentGalleryBinding>(
    R.layout.fragment_gallery,
    GalleryViewModel::class.java
) {

    @Inject
    lateinit var galleryAdapter: GalleryAdapter

//    private var tracker: SelectionTracker<GalleryData>? = null

//    private var currentGalleryData: GalleryData? = null

    private val selectList: MutableList<GalleryData> = mutableListOf()

    var galleryAlbums = listOf<GalleryAlbum>()

    override fun initView(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        with(binding) {
            toolbar.apply {
                setSupportActionBar(this)
                getSupportActionBar()?.run {
                    setDisplayShowTitleEnabled(false)
                    setDisplayShowHomeEnabled(false)
                    setDisplayHomeAsUpEnabled(false)
                }

                setBackgroundColor(Color.WHITE)
                setNavigationIcon(R.drawable.baseline_arrow_back_black_24dp)
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
            }

            galleryRecyclerView.apply {
                setHasFixedSize(true)
                itemAnimator = DefaultItemAnimator()
//                addItemDecoration(GridSpacingItemDecoration(this.context, 2, R.dimen.margin_16dp, true))
//                addItemDecoration(ListSpacingDecoration(this.context, R.dimen.margin_16dp))
                addItemDecoration(
                    SCommonItemDecoration.builder()
                        .type(R.layout.item_gallery_view)
                        .prop(
                            this.context.resources.dpToPx(8),
                            this.context.resources.dpToPx(8),
                            true,
                            true
                        )
                        .buildType()
                        .build()
                )
                adapter = galleryAdapter.apply {
                    galleryAdapterListener = object : GalleryAdapter.GalleryAdapterListener {

                        override fun onClickItem(galleryData: GalleryData, imageView: ImageView?) {
                            val sdf = SimpleDateFormat("yyyy년 MM월 dd일 E요일 HH:mm:ss", Locale.getDefault())
                            Logger.w("path : ${galleryData.path}, dateAdded : ${sdf.format(galleryData.dateAdded)}, lastModified : ${sdf.format(galleryData.lastModified)}"
                            )
                        }

                    }
                }

//                val builder = SelectionTracker.Builder(
//                    "GalleryData",
//                    this,
//                    GalleryItemKeyProvider(this),
//                    GalleryDetailsLookup(this),
//                    StorageStrategy.createParcelableStorage(GalleryData::class.java)
//                ).withSelectionPredicate(SelectionPredicates.createSelectAnything())
//
//                tracker = builder.build().apply {
//                    onRestoreInstanceState(savedInstanceState)
//                    addObserver(object : SelectionTracker.SelectionObserver<GalleryData>() {
//                        override fun onItemStateChanged(key: GalleryData, selected: Boolean) {
//                            super.onItemStateChanged(key, selected)
//                            Logger.w("My Item State Changed key : ${key.id}, selected : $selected")
//                            if (selected) {
//                                if (selectList.size < MAX_SIZE) {
//                                    selectList.add(key)
//                                } else {
//                                    val firstItemKey = selectList[0]
//                                    if (key != firstItemKey) {
//                                        tracker?.deselect(firstItemKey)
//                                        val iterator = selectList.iterator()
//                                        while(iterator.hasNext()){
//                                            val item = iterator.next()
//                                            if(item == firstItemKey){
//                                                iterator.remove()
//                                            }
//                                        }
//
//                                        selectList.add(key)
//                                    }
//
//                                }
//
////                                currentGalleryData = selectList[selectList.size - 1]
//                            }
//                        }
//
//                        override fun onSelectionChanged() {
//                            super.onSelectionChanged()
////                            if (tracker?.selection?.size() == 1) {
////                                currentGalleryData?.let {
////                                    galleryRecyclerView.post {
////                                        tracker?.select(it)
////                                    }
////                                }
////                            }
//                        }
//                    })
//                }
//                galleryAdapter.tracker = tracker
            }

//            myAdapter = ArrayAdapter(
//                toolbarSpinner.context,
//                android.R.layout.simple_spinner_dropdown_item,
//                arrayOf<String>()
//            )
            toolbarSpinner.apply {
//                this.adapter = myAdapter
                this.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
//                            selectList.clear()
//                            currentGalleryData = null
                            val galleryAlbum = galleryAlbums[position]
                            lifecycleScope.launch {
                                this@GalleryFragment.viewModel.getImagesAsFlow(albumPath = galleryAlbum.path)
                                    .collectLatest { pagingData ->
                                        galleryAdapter.submitData(pagingData)
                                    }
                            }

                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {

                        }
                    }
            }
        }
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        tracker?.onSaveInstanceState(outState)
//    }



    override fun initObserve() {
        with(viewModel) {
//            lifecycleScope.launch {
//                viewModel.getImagesAsFlow().collectLatest { pagingData ->
//                    galleryAdapter.submitData(pagingData)
//                }
//            }
            galleryAlbumList.observe(viewLifecycleOwner) {
//                Logger.w("galleryDataList : $it")
                galleryAlbums = it
                val items = mutableListOf<String>()
                for (galleryAlbum in it) {
                    items.add("${galleryAlbum.albumName}(${galleryAlbum.imageCount})")
                    Logger.w("${galleryAlbum.albumName}(${galleryAlbum.imageCount}) = ${galleryAlbum.path}")
                }

                binding.toolbarSpinner.adapter = ArrayAdapter(
                    binding.toolbarSpinner.context,
                    android.R.layout.simple_spinner_dropdown_item,
                    items
                )

//                val galleryAlbum = it[0]
//                lifecycleScope.launch {
//                    viewModel.getImagesAsFlow(albumPath = galleryAlbum.path)
//                        .collectLatest { pagingData ->
//                            galleryAdapter.submitData(pagingData)
//                        }
////                                Logger.w("galleryAdapter submitData 2")
////                                galleryAdapter.getSelectionKeyForPosition(0)?.let {
////                                    Logger.w("galleryAdapter submitData 3 : ${galleryAdapter.getSelectionKeyForPosition(0)}")
////                                    galleryAdapter.tracker?.select(it)
////                                }
//                }

//                val myAdapter = ArrayAdapter(
//                    binding.toolbarSpinner.context,
//                    android.R.layout.simple_spinner_dropdown_item,
//                    items
//                )
//                binding.toolbarSpinner.apply {
//                    adapter = myAdapter
//                    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                        override fun onItemSelected(
//                            parent: AdapterView<*>,
//                            view: View,
//                            position: Int,
//                            id: Long
//                        ) {
////                            selectList.clear()
////                            currentGalleryData = null
//                            val galleryAlbum = it[position]
//                            lifecycleScope.launch {
//                                viewModel.getImagesAsFlow(albumPath = galleryAlbum.path)
//                                    .collectLatest { pagingData ->
//                                        galleryAdapter.submitData(pagingData)
//                                    }
////                                Logger.w("galleryAdapter submitData 2")
////                                galleryAdapter.getSelectionKeyForPosition(0)?.let {
////                                    Logger.w("galleryAdapter submitData 3 : ${galleryAdapter.getSelectionKeyForPosition(0)}")
////                                    galleryAdapter.tracker?.select(it)
////                                }
//                            }
//
//                        }
//
//                        override fun onNothingSelected(parent: AdapterView<*>) {
//
//                        }
//                    }
//                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_gallery, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_gallery_done -> {
                if(galleryAdapter.getSelectList().size == 0){
                    Toast.makeText(binding.galleryRecyclerView.context, "이미지를 선택하세요.", Toast.LENGTH_LONG).show()
                }else{
                    findNavController().navigate(GalleryFragmentDirections.actionGalleryToResult(galleryAdapter.getSelectList().toTypedArray()))
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

//    private class GalleryDetailsLookup(
//        private val recyclerView: RecyclerView
//    ) : ItemDetailsLookup<GalleryData>() {
//        override fun getItemDetails(event: MotionEvent): ItemDetails<GalleryData>? {
//            val view = recyclerView.findChildViewUnder(event.x, event.y)
//            return if (view != null)
//                (recyclerView.getChildViewHolder(view) as GalleryAdapter.GalleryItemViewHolder)
//                    .getItemDetails()
//            else null
//        }
//    }
//
//    private class GalleryItemKeyProvider(
//        private val recyclerView: RecyclerView
//    ) : ItemKeyProvider<GalleryData>(SCOPE_MAPPED) {
//        private val adapter: GalleryAdapter = (recyclerView.adapter as? GalleryAdapter)
//            ?: throw IllegalStateException("RecyclerView must have LibraryAdapter set")
//
//        private val positionToKey = mutableMapOf<GalleryData, Int>()
//
//        fun reset() {
//            positionToKey.clear()
//        }
//
//        override fun getKey(position: Int): GalleryData? =
//            adapter.getSelectionKeyForPosition(position)
//
//        override fun getPosition(key: GalleryData): Int {
//            var position = positionToKey[key]
//            if (position == null) {
//                recyclerView.forEach {
//                    val vh =
//                        recyclerView.getChildViewHolder(it) as GalleryAdapter.GalleryItemViewHolder
//                    if (key == vh.galleryData) {
//                        position = vh.bindingAdapterPosition
//                        positionToKey[key] = position!!
//
//                    }
//                }
//            }
//            return position ?: RecyclerView.NO_POSITION
//        }
//    }

    companion object {
        const val REQUEST_KEY_GALLERY_DATA = "REQUEST_KEY_GALLERY_DATA"
        const val DATA_KEY = "DATA_KEY"
        const val MAX_SIZE = 5
    }
}