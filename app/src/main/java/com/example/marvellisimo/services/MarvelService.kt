package com.example.marvellisimo.services

import com.example.marvellisimo.marvelEntities.CharacterDataWrapper
import com.example.marvellisimo.marvelEntities.SeriesDataWrapper
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MarvelService {

    @GET("characters")
    fun getAllCharacters(
        @Query("nameStartsWith") nameStartsWith: String? = null,
        @Query("name") byExactName: String? = null,
        @Query("orderBy") orderBy: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Single<CharacterDataWrapper>


    @GET("series")
    fun getAllSeries(
        @Query("titleStartsWith") titleStartsWith: String? = null,
        @Query("title") byExactTitle: String? = null,
        @Query("startYear") startYear: Int? = null,
        @Query("orderBy") orderBy: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): Single<SeriesDataWrapper>

}