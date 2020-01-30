package com.example.marvellisimo.repository.models.retrofit

import com.example.marvellisimo.repository.models.realm.Series

open class SeriesDataContainer(
    //offset (int, optional): The requested offset (number of skipped results) of the call.,
    //limit (int, optional): The requested result limit.,
    val total: Int, //(int, optional): The total number of resources available given the current filter set.,
    val count: Int, // (int, optional): The total number of results returned by this call.,
    val results: Array<Series> // The list of series returned by the call
)