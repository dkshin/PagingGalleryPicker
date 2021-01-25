package kr.dksin.paginggallerypicker.domain.model

import android.net.Uri

data class GalleryAlbum(
    var path: String = "",
    var albumName: String = "",
    var firstImageUri: Uri? = null,
    var imageCount: Int = 0
) {
    fun addCount() {
        this.imageCount++
    }
}