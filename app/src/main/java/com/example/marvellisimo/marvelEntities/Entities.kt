package com.example.marvellisimo.marvelEntities

import android.os.Parcelable
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.NotNull

data class CharacterDataWrapper(
    // val copyright: String, // optional): The copyright notice for the returned result.,
    // val attributionText: String, // optional): The attribution notice for this result. Please display either this notice or the contents of the attributionHTML field on all screens which contain data from the Marvel Comics API.,
    // val attributionHTML: String, // optional): An HTML representation of the attribution notice for this result. Please display either this notice or the contents of the attributionText field on all screens which contain data from the Marvel Comics API.,
    // val etag: String // optional): A digest value of the content returned by the call.
    val code: Int, // optional): The HTTP status code of the returned result.,
    val status: String, // optional): A string description of the call status.,
    val data: CharacterDataContainer // optional): The results returned by the call.,
)

data class CharacterDataContainer(
    // val offset: Int, //, optional): The requested offset (number of skipped results) of the call.,
    // val limit: Int, //, optional): The requested result limit.,
    val total: Int, //, optional): The total number of resources available given the current filter set.,
    val count: Int, //, optional): The total number of results returned by this call.,
    val results: Array<Character> //, optional): The list of characters returned by the call.
)

open class Character(
    //val modified: Date, //, optional): The date the resource was most recently modified.,
    //val resourceURI: string, //, optional): The canonical URL identifier for this resource.,
    //val urls: Array, //[Url], optional): A set of public web site URLs for the resource.,
    var thumbnail: Image? = Image("", ""), //, optional): The representative image for this character.,
    /*val comics: ComicList, //, optional): A resource list containing comics which feature this character.,
    val stories: StoryList, //, optional): A resource list of stories in which this character appears.,
    val events: EventList, //, optional): A resource list of events in which this character appears.,*/
    //@Ignore
    var series: SeriesList? = SeriesList(), //, optional): A resource list of series in which this character appears.
    @PrimaryKey
    var id: Int = 1, //, optional): The unique ID of the character resource.,
    var name: String = "", //, optional): The name of the character.,
    var description: String = "" //, optional): A short bio or description of the character.,
): RealmObject()

open class SeriesList (
    var available: Int = 0, //(int, optional): The number of total available series in this list. Will always be greater than or equal to the "returned" value.,
    //returned (int, optional): The number of series returned in this collection (up to 20).,
    //collectionURI (string, optional): The path to the full list of series in this collection.,
    //@Ignore
    var items: RealmList<SeriesSummary>?  = RealmList()//, optional): The list of returned series in this collection.
): RealmObject()

open class SeriesSummary (
    //resourceURI (string, optional): The path to the individual series resource.,
    var name: String = "" // (string, optional): The canonical name of the series.
): RealmObject()





open class SeriesDataWrapper(
    val code: Int, //The HTTP status code of the returned result.,
    val status: String, // (string, optional): A string description of the call status.,
    //copyright (string, optional): The copyright notice for the returned result.,
    //attributionText (string, optional): The attribution notice for this result. Please display either this notice or the contents of the attributionHTML field on all screens which contain data from the Marvel Comics API.,
    //attributionHTML (string, optional): An HTML representation of the attribution notice for this result. Please display either this notice or the contents of the attributionText field on all screens which contain data from the Marvel Comics API.,
    val data: SeriesDataContainer // The results returned by the call.,
    //etag (string, optional): A digest value of the content returned by the call.
    )

open class SeriesDataContainer (
    //offset (int, optional): The requested offset (number of skipped results) of the call.,
    //limit (int, optional): The requested result limit.,
    val total: Int, //(int, optional): The total number of resources available given the current filter set.,
    val count: Int, // (int, optional): The total number of results returned by this call.,
    val results: Array<Series> // The list of series returned by the call
)

open class Series (
    @PrimaryKey
    var id: Int = 1, //(int, optional): The unique ID of the series resource.,
    var title: String = "", // (string, optional): The canonical title of the series.,
    var description: String? = "hej", // (string, optional): A description of the series.,
    //resourceURI (string, optional): The canonical URL identifier for this resource.,
    //urls (Array[Url], optional): A set of public web site URLs for the resource.,
    var startYear: Int = 1, // (int, optional): The first year of publication for the series.,
    var endYear: Int = 1, // (int, optional): The last year of publication for the series (conventionally, 2099 for ongoing series) .,
    var rating: String = "", // (string, optional): The age-appropriateness rating for the series.,
    //modified (Date, optional): The date the resource was most recently modified.,*/
    var thumbnail: Image? = Image("", "") //The representative image for this series.,
    //comics (ComicList, optional): A resource list containing comics in this series.,
    //stories (StoryList, optional): A resource list containing stories which occur in comics in this series.,
    //events (EventList, optional): A resource list containing events which take place in comics in this series.,
    //characters (CharacterList, optional): A resource list containing characters which appear in comics in this series.,
    //creators (CreatorList, optional): A resource list of creators whose work appears in comics in this series.,
    //next (SeriesSummary, optional): A summary representation of the series which follows this series.,
    //previous (SeriesSummary, optional): A summary representation of the series which preceded this series.*/
): RealmObject()

open class Image (
    var path: String= "", // The directory path of to the image.,
    var extension: String = "" // The file extension for the image.
): RealmObject()