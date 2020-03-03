package com.tomgu.rawgcards.api

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.io.Serializable

data class GameResponse(@field:Json(name = "results") val games: List<Game>)

data class Game(@field:Json(name = "slug") val slug: String = "",
                @field:Json(name = "name") val name: String = "",
                @field:Json(name = "rating") val rating: String = "",
                @field:Json(name = "background_image") val background_image: String = "") : Serializable

data class GameInfo(@field:Json(name = "description") var description: String,
                    @field:Json(name = "background_image_additional") var background_image_additional: String)

@Entity
data class CompleteGame(@PrimaryKey @NonNull val slug: String = "",
                        @ColumnInfo(name = "GAME") val name: String = "",
                        @ColumnInfo(name = "RATING") val rating: String = "",
                        @ColumnInfo(name = "BACKGROUND_IMAGE") val background_image: String = "",
                        @ColumnInfo(name = "DESCRIPTION") val description: String = "",
                        @ColumnInfo(name = "BACKGROUND_IMAGE_ADDITIONAL") val backgound_image_additional: String = "") : Serializable