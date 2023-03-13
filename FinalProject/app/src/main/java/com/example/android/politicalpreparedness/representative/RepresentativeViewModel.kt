package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel : ViewModel() {

    private var _representatives: MutableLiveData<List<Representative>> = MutableLiveData()
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

