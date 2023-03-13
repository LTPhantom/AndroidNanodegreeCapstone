package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

interface ElectionsDataSource {
    suspend fun saveElection(election: Election)
    suspend fun getAllSavedElections(): List<Election>
    suspend fun getElection(electionId: Int): Election?
    suspend fun deleteElection(election: Election)
    suspend fun clearAllData()
    suspend fun getUpcomingElections(): List<Election>?
    suspend fun getVotersInfo(address: String, electionId: Int): VoterInfoResponse?
}