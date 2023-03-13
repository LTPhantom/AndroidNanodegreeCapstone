package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.local.ElectionsDataSourceImpl

class ElectionsFragment : Fragment() {

    private val electionsViewModel: ElectionsViewModel by lazy {
        ViewModelProvider(this,
            ElectionsViewModelFactory(ElectionsDataSourceImpl(ElectionDatabase.getInstance(
                requireContext()).electionDao)))[ElectionsViewModel::class.java]
    }
    private lateinit var binding: FragmentElectionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val upcomingElectionsAdapter = ElectionListAdapter(ElectionListener { election: Election ->
            onElectionClickListener(election)
        })
        binding.upcomingElectionsList.adapter = upcomingElectionsAdapter

        val savedElectionsAdapter = ElectionListAdapter(ElectionListener { election: Election ->
            onElectionClickListener(election)
        })
        binding.savedElectionsList.adapter = savedElectionsAdapter


        electionsViewModel.upcomingElectionsList.observe(viewLifecycleOwner) { elections ->
            elections?.let {
                upcomingElectionsAdapter.submitList(it)
            }
        }

        electionsViewModel.savedElectionsList.observe(viewLifecycleOwner) { elections ->
            elections?.let {
                savedElectionsAdapter.submitList(it)
            }
        }

        return binding.root
    }

    private fun navToVotersInfo(electionId: Int, division: String) {
        this.findNavController()
            .navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                electionId,
                division))
    }

    private fun onElectionClickListener(election: Election) {
        navToVotersInfo(election.id, election.ocdDivisionId)
    }

    override fun onStart() {
        super.onStart()
        electionsViewModel.getUpcomingElections()
        electionsViewModel.getSavedElections()
    }
}