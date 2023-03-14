package com.example.android.politicalpreparedness.network.jsonadapter

import com.example.android.politicalpreparedness.network.models.Division
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

class ElectionAdapter {
    @FromJson
    fun divisionFromJson(ocdDivisionId: String): Division {
        val countryDelimiter = "country:"
        val stateDelimiter = "state:"
        val country = ocdDivisionId.substringAfter(countryDelimiter, "")
            .substringBefore("/")
        var state = ocdDivisionId.substringAfter(stateDelimiter, "")
            .substringBefore("/")
        if (state.isEmpty()) {
            val districtDelimiter = "district:"
            state = ocdDivisionId.substringAfter(districtDelimiter, "")
                .substringBefore("/")
        }
        return Division(ocdDivisionId, country, state)
    }

    @ToJson
    fun divisionToJson(division: Division): String {
        return division.id
    }
}