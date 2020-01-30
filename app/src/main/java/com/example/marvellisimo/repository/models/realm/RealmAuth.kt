package com.example.marvellisimo.repository.models.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RealmAuth(var id: String = "", @PrimaryKey var primeKey: String = "realm-auth-id") : RealmObject()