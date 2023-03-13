package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListener
import java.util.Locale

class DetailFragment : Fragment() {

    private lateinit var representativeViewModel: RepresentativeViewModel
    private lateinit var binding: FragmentRepresentativeBinding
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLocation()
        } else {
            Toast.makeText(requireContext(),
                getString(R.string.permissions_are_needed),
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        representativeViewModel = ViewModelProvider(this)[RepresentativeViewModel::class.java]
        binding.viewModel = representativeViewModel

        val statesAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.states))
        binding.state.adapter = statesAdapter

        val adapter = RepresentativeListAdapter(RepresentativeListener { })
        binding.representativesList.adapter = adapter

        representativeViewModel.representatives.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            hideKeyboard()
        }

        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions()
        }

        return binding.root
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            getLocation()
            true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            false
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val locationManager =
            getSystemService(requireContext(), LocationManager::class.java) ?: return
        val criteria = Criteria()
        val location = locationManager.getLastKnownLocation(locationManager
            .getBestProvider(criteria, false)!!) ?: return
        val address = geoCodeLocation(location)
        binding.viewModel!!.address.value = address
        binding.viewModel!!.refreshRepresentatives()
    }

    private fun geoCodeLocation(location: Location): Address? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            ?.map { address ->
                val states = resources.getStringArray(R.array.states)
                val selectedStateIndex = states.indexOf(address.adminArea)
                binding.state.setSelection(selectedStateIndex)
                Address(address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode)
            }
            ?.first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}