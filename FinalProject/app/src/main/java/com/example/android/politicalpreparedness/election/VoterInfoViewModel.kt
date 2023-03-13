package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.ElectionsDataSource
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val dataSource: ElectionsDataSource,
) : ViewModel() {

    private var _voterInfo: MutableLiveData<VoterInfoResponse?> = MutableLiveData()
    val voterInfo: LiveData<VoterInfoResponse?>
        get() = _voterInfo

    private var _shouldSayUnfollow: MutableLiveData<Boolean?> = MutableLiveData()
    val shouldSayUnfollow: LiveData<Boolean?>
        get() = _shouldSayUnfollow

    private var _election: MutableLiveData<Election?> = MutableLiveData()
    val election: LiveData<Election?>
        get() = _election

    init {
        _election.value = null
        _voterInfo.value = null
    }

    fun getVoterInfo(address: String, electionId: Int) {
        val division = ElectionAdapter().divisionFromJson(address)
        val completeAddress = division.country + if (division.state.isNotEmpty()) {
            " - ${division.state}"
        } else {
            ""
        }
        viewModelScope.launch {
            _voterInfo.value = dataSource.getVotersInfo(completeAddress, electionId)
            _voterInfo.value?.let {
                _election.value = it.election
            }
        }
    }

    fun followElection() {
        if (_shouldSayUnfollow.value == true) {
            unfollowElection()
            return
        }
        viewModelScope.launch {
            dataSource.saveElection(election.value!!)
            shouldButtonSayToUnfollow(election.value!!.id)
        }
    }

    private fun unfollowElection() {
        if (election.value == null) {
            return
        }
        viewModelScope.launch {
            election.value?.let {
                dataSource.deleteElection(election.value!!)
                shouldButtonSayToUnfollow(election.value!!.id)
            }
        }
    }

    fun shouldButtonSayToUnfollow(electionId: Int) {
        viewModelScope.launch {
            val election = dataSource.getElection(electionId)
            _shouldSayUnfollow.value = election != null
        }
    }
}