package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.local.ElectionsDataSourceImpl

class VoterInfoFragment : Fragment() {

    private val voterInfoViewModel: VoterInfoViewModel by lazy {
        ViewModelProvider(this,
            VoterInfoViewModelFactory(ElectionsDataSourceImpl(ElectionDatabase.getInstance(
                requireContext()).electionDao)))[VoterInfoViewModel::class.java]
    }
    private lateinit var binding: FragmentVoterInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val electionId = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
        val divisionStr = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision
        voterInfoViewModel.getVoterInfo(divisionStr, electionId)

        voterInfoViewModel.voterInfo.observe(viewLifecycleOwner) { votersInfo ->
            votersInfo?.let {
                binding.electionName.title = votersInfo.election.name
                binding.electionDate.text = votersInfo.election.electionDay.toString()
                binding.stateHeader.text = votersInfo.election.ocdDivisionId
                votersInfo.state?.let state@{ state ->
                    if (state.isEmpty()) {
                        return@state
                    }
                    state[0].electionAdministrationBody.ballotInfoUrl?.let { url ->
                        binding.stateBallot.visibility = View.VISIBLE
                        binding.stateBallot.setOnClickListener {
                            handleURL(url)
                        }
                    }
                    state[0].electionAdministrationBody.votingLocationFinderUrl?.let { url ->
                        binding.stateLocations.visibility = View.VISIBLE
                        binding.stateLocations.setOnClickListener {
                            handleURL(url)
                        }
                    }
                }
            }
        }

        voterInfoViewModel.election.observe(viewLifecycleOwner) {
            it?.let {
                binding.followButton.visibility = View.VISIBLE
                return@observe
            }
            binding.followButton.visibility = View.GONE
        }
        voterInfoViewModel.shouldButtonSayToUnfollow(electionId)
        voterInfoViewModel.shouldSayUnfollow.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.followButton.text = getString(R.string.unfollow_election)
            } else {
                binding.followButton.text = getString(R.string.follow_election)
            }
        }
        binding.followButton.setOnClickListener {
            voterInfoViewModel.followElection()
        }
        return binding.root
    }

    private fun handleURL(url: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(url)
        }
        startActivity(intent)
    }
}