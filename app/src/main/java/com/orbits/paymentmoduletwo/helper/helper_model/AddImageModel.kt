package com.orbits.paymentmoduletwo.helper.helper_model

import android.graphics.Bitmap
import java.io.Serializable

class AddImageModel(
    val image: Bitmap? = null,
    val title: String? = null,
    val imgBase64: String? = null,
    val filePath: String? = null,
    val imageID: String? = null, // id will be used to delete it from server while editing
    val imageUrl: String? = null , //imageUrl means coming from edit and image is already uploaded so it has url
    var isSelected :Boolean = false

) : Serializable