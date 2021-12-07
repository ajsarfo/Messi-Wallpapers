package com.sarftec.messiwallpapers.data.firebase.model

import com.google.firebase.firestore.Exclude

class FirebaseWallpaper(
    @Exclude
    var id: Long? = null,
    var views: Long? = null,
    var image: String? = null,
    var section: String? = null,
    var isApproved: Boolean? = true
) {
    companion object {
        const val FIELD_ID = "id"
        const val FIELD_SECTION = "section"
        const val FIELD_IS_APPROVED = "isApproved"
        const val FIELD_VIEWS = "views"
    }
}