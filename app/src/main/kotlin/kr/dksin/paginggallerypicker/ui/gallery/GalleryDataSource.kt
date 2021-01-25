package kr.dksin.paginggallerypicker.ui.gallery

import android.annotation.TargetApi
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.AnyThread
import androidx.annotation.WorkerThread
import androidx.paging.PagingSource
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.dksin.paginggallerypicker.domain.model.GalleryData
import java.io.File
import java.util.*
class GalleryDataSource(private val context: Context, private val albumPath: String) : PagingSource<Int, GalleryData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryData> {
        return try {
            return loadImagesSuspend(params.key ?: 0, context.contentResolver)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    @AnyThread
    suspend fun loadImagesSuspend(
        page: Int,
        contentResolver: ContentResolver
    ): PagingSource.LoadResult<Int, GalleryData> {
        return withContext(Dispatchers.IO) {
            imagesToLoadResult(loadImages(page, contentResolver), page)
        }
    }

    @AnyThread
    private fun imagesToLoadResult(
        contacts: List<GalleryData>,
        page: Int
    ): PagingSource.LoadResult.Page<Int, GalleryData> {

        val offset = contacts.size

        /**
         * set prevKey so pagination works when scrolling up
         * set nextKey so this paging source knows when there is no more data left to fetch
         * and for the next page to be properly passed up
         */
        return PagingSource.LoadResult.Page(
            data = contacts,
            prevKey = if (page == 0) null else page - offset,
            nextKey = if (contacts.isEmpty() || offset < GalleryViewModel.PAGE_SIZE) null else page + offset
        )
    }


    @TargetApi(Build.VERSION_CODES.Q)
    @WorkerThread
    private fun loadImages(page: Int, contentResolver: ContentResolver): MutableList<GalleryData> {
        var result: MutableList<GalleryData> = mutableListOf()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN
        )

//            val selection = "${MediaStore.Images.Media.DATE_TAKEN} >= ?"
//
//            val selectionArgs = arrayOf(
//                dateToTimestamp(day = 1, month = 1, year = 2020).toString()
//            )
//
//            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val orderBy = MediaStore.Images.Media.DATE_TAKEN
        val sortOrder = "$orderBy DESC LIMIT ${GalleryViewModel.PAGE_SIZE} OFFSET $page"
        Logger.w("sortOrder : $sortOrder")

        if(albumPath == "All"){
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                result = addImagesFromCursor(cursor)
            }
        }else{
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                "${MediaStore.Images.Media.DATA} like ? ",
                arrayOf("%$albumPath%"),
                sortOrder
            )?.use { cursor ->
                result = addImagesFromCursor(cursor)
            }
        }


        return result
    }

    @TargetApi(Build.VERSION_CODES.Q)
    @WorkerThread
    private fun addImagesFromCursor(cursor: Cursor): MutableList<GalleryData> {
        val datas = mutableListOf<GalleryData>()

        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
        val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

        while (cursor.moveToNext()) {

            val id = cursor.getLong(idColumn)
            val data = cursor.getString(dataColumn)
            val dateTaken = Date(cursor.getLong(dateTakenColumn))
            val displayName = cursor.getString(displayNameColumn)

            val contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )

            val galleryData = GalleryData().apply {
                this.id = id
                this.imageUri = contentUri
                this.path = data
                this.imageName = displayName
                this.mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                this.dateAdded = dateTaken
                this.lastModified = File(data).lastModified()
            }
            datas.add(galleryData)
        }

//        datas.sortWith(compareByDescending { File(it.path).lastModified() })
        datas.sortWith(compareByDescending { it.lastModified     })
        return datas.also { cursor.close() }
    }
}