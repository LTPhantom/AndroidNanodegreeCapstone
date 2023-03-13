package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionsDataSource
import kotlinx.coroutines.launch

class ElectionsViewModel(
    private val dataSource: ElectionsDataSource
): ViewModel() {

    private var _upcomingElectionsList: MutableLiveData<List<Election>?> = MutableLiveData()
    val upcomingElectionsList: LiveData<List<Election>?>
        get() = _upcomingElectionsList

    private var _savedElectionsList: MutableLiveData<List<Election>?> = MutableLiveData()
    val savedElectionsList: LiveData<List<Election>?>
        get() = _savedElectionsList

    fun getUpcomingElections() {
        viewModelScope.launch {
            _upcomingElectionsList.value = dataSource.getUpcomingElections()
        }
    }

    fun getSavedElections() {
        viewModelScope.launch {
            _savedElectionsList.value = dataSource.getAllSavedElections()
        }
    }
}