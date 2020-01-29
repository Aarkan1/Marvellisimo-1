package com.example.marvellisimo.repository.models.realm

import io.realm.RealmObject

open class Image(
    var path: String = "", // The directory path of to the image.,
    var extension: String = "" // The file extension for the image.
) : RealmObject()