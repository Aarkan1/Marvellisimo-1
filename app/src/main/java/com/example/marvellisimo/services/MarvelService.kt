package com.example.marvellisimo.services

import com.example.marvellisimo.repository.models.retrofit.CharacterDataWrapper
import com.example.marvellisimo.repository.models.retrofit.SeriesDataWrapper
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarvelService {

    @GET("characters")
    suspend fun getAllCharacters(
        @Query("nameStartsWith") nameStartsWith: String? = null,
        @Query("name") byExactName: String? = null,
        @Query("orderBy") orderBy: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): CharacterDataWrapper


    @GET("characters/{characterId}")
    suspend fun getCharacterById(@Path("characterId") characterId: String): CharacterDataWrapper

    @GET("series")
    suspend fun getAllSeries(
        @Query("titleStartsWith") titleStartsWith: String? = null,
        @Query("title") byExactTitle: String? = null,
        @Query("startYear") startYear: Int? = null,
        @Query("orderBy") orderBy: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null
    ): SeriesDataWrapper

    @GET("series/{seriesId}")
    suspend fun getSeriesById(@Path("seriesId") seriesId: String): SeriesDataWrapper
}