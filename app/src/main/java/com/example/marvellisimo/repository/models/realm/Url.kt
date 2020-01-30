package com.example.marvellisimo.repository.models.realm

import io.realm.RealmObject

open class Url (
    //var type (string, optional): A text identifier for the URL.,
    var url: String = "" // (string, optional): A full URL (including scheme, domain, and path).
): RealmObject()