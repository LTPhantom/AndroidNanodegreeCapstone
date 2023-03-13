package com.example.android.politicalpreparedness.repository.local

import android.util.Log
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionsDataSource

class ElectionsDataSourceImpl(
    private val electionsDao: ElectionDao,
) : ElectionsDataSource {
    override suspend fun saveElection(election: Election) {
        electionsDao.insertElection(election)
    }

    override suspend fun getAllSavedElections(): List<Election> =
        electionsDao.getAllSavedElections()

    override suspend fun getElection(electionId: Int): Election =
        electionsDao.getElectionById(electionId)

    override suspend fun deleteElection(election: Election) {
        electionsDao.deleteSavedElection(election)
    }

    override suspend fun clearAllData() {
        electionsDao.clearAllData()
    }

    override suspend fun getUpcomingElections(): List<Election>? {
        return try {
            CivicsApi.retrofitService.getElections().elections
        } catch (error: Exception) {
            Log.e("ElectionRepository", "Upcoming Elections could not be loaded: ${error.message}")
            null
        }
    }

    override suspend fun getVotersInfo(address: String, electionId: Int): VoterInfoResponse? {
        return try {
            CivicsApi.retrofitService.getVoterInfo(address, electionId)
        } catch (error: Exception) {
            Log.e("ElectionRepository", "VotersInfo could not be loaded: ${error.message}")
            null
        }
    }
}