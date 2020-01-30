package com.example.marvellisimo.repository.models.retrofit

open class SeriesDataWrapper(
    val code: Int, //The HTTP status code of the returned result.,
    val status: String, // (string, optional): A string description of the call status.,
    //copyright (string, optional): The copyright notice for the returned result.,
    //attributionText (string, optional): The attribution notice for this result. Please display either this notice or the contents of the attributionHTML field on all screens which contain data from the Marvel Comics API.,
    //attributionHTML (string, optional): An HTML representation of the attribution notice for this result. Please display either this notice or the contents of the attributionText field on all screens which contain data from the Marvel Comics API.,
    val data: SeriesDataContainer // The results returned by the call.,
    //etag (string, optional): A digest value of the content returned by the call.
)