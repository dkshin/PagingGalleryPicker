package kr.dksin.paginggallerypicker.ui.gallery

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.dksin.paginggallerypicker.base.BaseViewModel
import kr.dksin.paginggallerypicker.domain.model.GalleryAlbum
import kr.dksin.paginggallerypicker.domain.model.GalleryData
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class GalleryViewModel @ViewModelInject constructor(private val context: Context): BaseViewModel(){

    private val _galleryAlbumList = MutableLiveData<List<GalleryAlbum>>()
    val galleryAlbumList: LiveData<List<GalleryAlbum>> get() = _galleryAlbumList

    init{
        viewModelScope.launch {
            val result = getGalleryAlbums()
            _galleryAlbumList.postValue(result)
        }
    }

    fun getImagesAsFlow(pageSize: Int = PAGE_SIZE, albumPath: String): Flow<PagingData<GalleryData>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                GalleryDataSource(context, albumPath)
            }
        ).flow.cachedIn(viewModelScope)
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private suspend fun getGalleryAlbums(): MutableList<GalleryAlbum> =
        withContext(Dispatchers.IO) {
            var result: MutableList<GalleryAlbum> = mutableListOf()

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.DATE_TAKEN
            )

            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                result = addAlbumsFromCursor(cursor)
            }

            return@withContext result
        }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun addAlbumsFromCursor(cursor: Cursor): MutableList<GalleryAlbum> {
        val galleryAlbums = mutableListOf<GalleryAlbum>()
        val imagePaths = mutableListOf<String>()

        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        val folderColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

        var allImageCount = 0
        while (cursor.moveToNext()) {

            val id = cursor.getLong(idColumn)
            val data = cursor.getString(dataColumn)
            val dateTaken = Date(cursor.getLong(dateTakenColumn))
            val displayName = cursor.getString(nameColumn)
            val folderName = cursor.getString(folderColumn)

            val contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )

            data?.let{
                val folderPath = "${File(it).parent}${File.separator}"

                if (!imagePaths.contains(folderPath)) {
                    imagePaths.add(folderPath)
                    val galleryAlbum = GalleryAlbum().apply {
                        this.albumName = folderName
                        this.path = folderPath
                        this.firstImageUri = contentUri
                        addCount()
                    }
                    galleryAlbums.add(galleryAlbum)

                } else {
                    for (galleryAlbum in galleryAlbums) {
                        if (galleryAlbum.path == folderPath) {
                            galleryAlbum.firstImageUri = contentUri
                            galleryAlbum.addCount()
                        }
                    }
                }

                allImageCount++
            }
        }
        galleryAlbums.add(0, GalleryAlbum(albumName = "All", path = "All", firstImageUri = null, imageCount = allImageCount))
        return galleryAlbums.also { cursor.close() }
    }


    @Suppress("SameParameterValue")
    @SuppressLint("SimpleDateFormat")
    private fun dateToTimestamp(day: Int, month: Int, year: Int): Long =
        SimpleDateFormat("dd.MM.yyyy").let { formatter ->
            formatter.parse("$day.$month.$year")?.time ?: 0
        }

    companion object{
        const val PAGE_SIZE = 30
    }
}