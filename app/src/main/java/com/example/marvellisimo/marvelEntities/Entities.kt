package com.example.marvellisimo.marvelEntities

import android.os.Parcelable
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

open class CharacterDataWrapper(
    @PrimaryKey
    var searchString: String? = null,
    // val copyright: String, // optional): The copyright notice for the returned result.,
    // val attributionText: String, // optional): The attribution notice for this result. Please display either this notice or the contents of the attributionHTML field on all screens which contain data from the Marvel Comics API.,
    // val attributionHTML: String, // optional): An HTML representation of the attribution notice for this result. Please display either this notice or the contents of the attributionText field on all screens which contain data from the Marvel Comics API.,
    // val etag: String // optional): A digest value of the content returned by the call.
    var code: Int? = null, // optional): The HTTP status code of the returned result.,
    var status: String? = null, // optional): A string description of the call status.,
    var data: CharacterDataContainer? = null // optional): The results returned by the call.,
): RealmObject()

open class CharacterDataContainer(
    // val offset: Int, //, optional): The requested offset (number of skipped results) of the call.,
    // val limit: Int, //, optional): The requested result limit.,
    var total: Int? = null, //, optional): The total number of resources available given the current filter set.,
    var count: Int? = null, //, optional): The total number of results returned by this call.,
    var results: RealmList<Character>? = RealmList() //, optional): The list of characters returned by the call.
): RealmObject()

@Parcelize
open class Character(
    //val modified: Date, //, optional): The date the resource was most recently modified.,
    //val resourceURI: string, //, optional): The canonical URL identifier for this resource.,
    //val urls: Array, //[Url], optional): A set of public web site URLs for the resource.,
    var thumbnail: Image? = null, //, optional): The representative image for this character.,
    /*val comics: ComicList, //, optional): A resource list containing comics which feature this character.,
    val stories: StoryList, //, optional): A resource list of stories in which this character appears.,
    val events: EventList, //, optional): A resource list of events in which this character appears.,*/
    var series: SeriesList? = null, //, optional): A resource list of series in which this character appears.
    @PrimaryKey
    var id: Int? = null, //, optional): The unique ID of the character resource.,
    var name: String? = null, //, optional): The name of the character.,
    var description: String? = null //, optional): A short bio or description of the character.,
): Parcelable, RealmObject()

@Parcelize
open class SeriesList(

    var items: Array<SeriesSummary>? = null
    //, optional): The list of returned series in this collection.//available (int, optional): The number of total available series in this list. Will always be greater than or equal to the "returned" value.,
    //returned (int, optional): The number of series returned in this collection (up to 20).,
    //collectionURI (string, optional): The path to the full list of series in this collection.,
): Parcelable, RealmObject()

//@Parcelize
//open class SeriesList: Parcelable, RealmObject() {
//    @IgnoredOnParcel
//    var items: RealmList<SeriesSummary>? = RealmList()
//    //, optional): The list of returned series in this collection.//available (int, optional): The number of total available series in this list. Will always be greater than or equal to the "returned" value.,
//    //returned (int, optional): The number of series returned in this collection (up to 20).,
//    //collectionURI (string, optional): The path to the full list of series in this collection.,
//}

@Parcelize
open class SeriesSummary (
    //resourceURI (string, optional): The path to the individual series resource.,
    var name: String? = null // (string, optional): The canonical name of the series.
): Parcelable, RealmObject()





open class SeriesDataWrapper(
    @PrimaryKey
    var searchString: String? = null,
    var code: Int? = null, //The HTTP status code of the returned result.,
    var status: String? = null, // (string, optional): A string description of the call status.,
    //copyright (string, optional): The copyright notice for the returned result.,
    //attributionText (string, optional): The attribution notice for this result. Please display either this notice or the contents of the attributionHTML field on all screens which contain data from the Marvel Comics API.,
    //attributionHTML (string, optional): An HTML representation of the attribution notice for this result. Please display either this notice or the contents of the attributionText field on all screens which contain data from the Marvel Comics API.,
    var data: SeriesDataContainer? = null // The results returned by the call.,
    //etag (string, optional): A digest value of the content returned by the call.
    ): RealmObject()

open class SeriesDataContainer (
    //offset (int, optional): The requested offset (number of skipped results) of the call.,
    //limit (int, optional): The requested result limit.,
    var total: Int? = null, //(int, optional): The total number of resources available given the current filter set.,
    var count: Int? = null, // (int, optional): The total number of results returned by this call.,
    var results: RealmList<Series>? = RealmList() // The list of series returned by the call
): RealmObject()

@Parcelize
open class Series (
    @PrimaryKey
    var id: Int? = null, //(int, optional): The unique ID of the series resource.,
    var title: String? = null, // (string, optional): The canonical title of the series.,
    var description: String? = null, // (string, optional): A description of the series.,
    //resourceURI (string, optional): The canonical URL identifier for this resource.,
    //urls (Array[Url], optional): A set of public web site URLs for the resource.,
    var startYear: Int? = null, // (int, optional): The first year of publication for the series.,
    var endYear: Int? = null, // (int, optional): The last year of publication for the series (conventionally, 2099 for ongoing series) .,
    var rating: String? = null, // (string, optional): The age-appropriateness rating for the series.,
    //modified (Date, optional): The date the resource was most recently modified.,*/
    var thumbnail: Image? = null //The representative image for this series.,
    //comics (ComicList, optional): A resource list containing comics in this series.,
    //stories (StoryList, optional): A resource list containing stories which occur in comics in this series.,
    //events (EventList, optional): A resource list containing events which take place in comics in this series.,
    //characters (CharacterList, optional): A resource list containing characters which appear in comics in this series.,
    //creators (CreatorList, optional): A resource list of creators whose work appears in comics in this series.,
    //next (SeriesSummary, optional): A summary representation of the series which follows this series.,
    //previous (SeriesSummary, optional): A summary representation of the series which preceded this series.*/
): Parcelable, RealmObject()

@Parcelize
open class Image (
    var path: String? = null, // The directory path of to the image.,
    var extension: String? = null // The file extension for the image.
): Parcelable, RealmObject()