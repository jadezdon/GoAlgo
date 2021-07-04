package com.zhouppei.goalgo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.zhouppei.goalgo.R
import com.zhouppei.goalgo.databinding.FragmentHomeBinding
import com.zhouppei.goalgo.model.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupRecyclerViews()
        return binding.root
    }

    private fun setupRecyclerViews() {
        val algorithmGroupList = mutableListOf<AlgorithmGroup>().apply {
            add(AlgorithmGroup("Graph search", R.drawable.ic_graph,  GraphSearchAlgorithm.valuesToNameList()))
            add(AlgorithmGroup("Sorting", R.drawable.ic_sort, SortingAlgorithm.valuesToNameList()))
            add(AlgorithmGroup("Maze generation", R.drawable.ic_maze, MazeGenerationAlgorithm.valuesToNameList()))
            add(AlgorithmGroup("Root-finding (math)", R.drawable.ic_root_finding, RootFindingAlgorithm.valuesToNameList()))
        }
        binding.algoGroupRecyclerview.adapter = AlgorithmGroupListAdapter(
            algorithmGroupList,
            object : NameClickListener {
                override fun onClick(name: String) {
                    goToRunAlgorithmPage(name)
                }
            }
        )
    }

    private fun goToRunAlgorithmPage(name: String) {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAlgorithmFragment(name))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val LOG_TAG = HomeFragment::class.qualifiedName
    }
}