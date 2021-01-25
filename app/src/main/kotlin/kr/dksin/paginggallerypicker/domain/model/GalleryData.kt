package kr.dksin.paginggallerypicker.domain.model

import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import java.util.*

@Keep
@Parcelize
data class GalleryData(
        var id: Long = 0,
        var imageUri: Uri? = null,
        var path: String = "",
        var imageName: String = "",
        var mediaType: Int = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
        var dateAdded: Date = Date(),
        var lastModified: Long = 0L,
        var isSelect: Boolean = false
) : Parcelable