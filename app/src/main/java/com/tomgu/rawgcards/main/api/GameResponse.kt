package com.tomgu.rawgcards.main.api

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

data class GameResponse(@field:Json(name = "results") val games: List<Game>)

@Entity
data class Game(@field:Json(name = "slug") @PrimaryKey @NonNull val slug: String = "",
                @field:Json(name = "name") @ColumnInfo(name = "GAME")val name: String = "",
                @field:Json(name = "rating") @ColumnInfo(name = "RATING")val rating: String = "",
                @field:Json(name = "background_image") @ColumnInfo(name = "BACKGROUND_IMAGE")val background_image: String = "")

data class GameInfo(@field:Json(name = "description") var description: String,
                    @field:Json(name = "background_image_additional") var background_image_additional: String)