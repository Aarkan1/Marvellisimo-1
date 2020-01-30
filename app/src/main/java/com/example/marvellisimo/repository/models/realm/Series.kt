package com.example.marvellisimo.repository.models.realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Series (
    @PrimaryKey
    var id: Int = 1, //(int, optional): The unique ID of the series resource.,
    var title: String = "", // (string, optional): The canonical title of the series.,
    var description: String? = "hej", // (string, optional): A description of the series.,
    //resourceURI (string, optional): The canonical URL identifier for this resource.,
    var urls: RealmList<Url> = RealmList(),
    var startYear: Int = 1, // (int, optional): The first year of publication for the series.,
    var endYear: Int = 1, // (int, optional): The last year of publication for the series (conventionally, 2099 for ongoing series) .,
    var rating: String = "", // (string, optional): The age-appropriateness rating for the series.,
    //modified (Date, optional): The date the resource was most recently modified.,*/
    var thumbnail: Image? = Image(
        "",
        ""
    ) //The representative image for this series.,
    //comics (ComicList, optional): A resource list containing comics in this series.,
    //stories (StoryList, optional): A resource list containing stories which occur in comics in this series.,
    //events (EventList, optional): A resource list containing events which take place in comics in this series.,
    //characters (CharacterList, optional): A resource list containing characters which appear in comics in this series.,
    //creators (CreatorList, optional): A resource list of creators whose work appears in comics in this series.,
    //next (SeriesSummary, optional): A summary representation of the series which follows this series.,
    //previous (SeriesSummary, optional): A summary representation of the series which preceded this series.*/
): RealmObject()