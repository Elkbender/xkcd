package no.elkbender.xkcd

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Comic(
    @PrimaryKey val num: Int,
    @ColumnInfo val year: String,
    @ColumnInfo val month: String,
    @ColumnInfo val day: String,
    @ColumnInfo val safe_title: String,
    @ColumnInfo val img: String,
    @ColumnInfo val alt: String,
    @ColumnInfo val link: String,
    @ColumnInfo val news: String,
    @ColumnInfo val transcript: String,
    @ColumnInfo val title: String
) : Parcelable
