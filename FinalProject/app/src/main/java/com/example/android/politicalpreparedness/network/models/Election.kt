package com.example.android.politicalpreparedness.network.models

import androidx.room.*
import java.util.*

@Entity(tableName = "election_table")
data class Election(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "name")val name: String,
        @ColumnInfo(name = "electionDay")val electionDay: Date,
        @ColumnInfo(name = "ocdDivisionId") val ocdDivisionId: String
)