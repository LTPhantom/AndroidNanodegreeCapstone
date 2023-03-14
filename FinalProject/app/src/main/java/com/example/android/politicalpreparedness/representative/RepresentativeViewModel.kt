package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private var _representatives: MutableLiveData<List<Representative>> =
        savedStateHandle.getLiveData("rep", listOf())
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    var address: MutableLiveData<Address> = MutableLiveData()

    init {
        address.value = Address("", "", "", "", "")
    }

    fun refreshRepresentatives() {
        viewModelScope.launch {
            val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(address.value!!.toFormattedString())
            _representatives.value = offices.flatMap { office ->
                office.getRepresentatives(officials)
            }
        }
    }
}

