package com.example.marvellisimo.repository.models.realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Character(
    //val modified: Date, //, optional): The date the resource was most recently modified.,
    //val resourceURI: string, //, optional): The canonical URL identifier for this resource.,
    var urls: RealmList<Url> = RealmList(), //[Url], optional): A set of public web site URLs for the resource.,
    var thumbnail: Image? = Image(
        "",
        ""
    ), //, optional): The representative image for this character.,
    /*val comics: ComicList, //, optional): A resource list containing comics which feature this character.,
    val stories: StoryList, //, optional): A resource list of stories in which this character appears.,
    val events: EventList, //, optional): A resource list of events in which this character appears.,*/
    //@Ignore
    var series: SeriesList? = SeriesList(), //, optional): A resource list of series in which this character appears.
    @PrimaryKey
    var id: Int = 1, //, optional): The unique ID of the character resource.,
    var name: String = "", //, optional): The name of the character.,
    var description: String = "" //, optional): A short bio or description of the character.,
) : RealmObject()