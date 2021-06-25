package com.zhouppei.goalgo.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.zhouppei.goalgo.databinding.FragmentHomeBinding
import com.zhouppei.goalgo.models.GraphAlgorithm
import com.zhouppei.goalgo.models.SortingAlgorithm

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var graphAlgoListAdapter: AlgorithmNameListAdapter
    private lateinit var sortingAlgoListAdapter: AlgorithmNameListAdapter
    private val graphAlgorithmNames = mutableListOf<String>()
    private val sortingAlgorithmNames = mutableListOf<String>()

    init {
        GraphAlgorithm.values().forEach {
            graphAlgorithmNames.add(it.str)
        }
        SortingAlgorithm.values().forEach {
            sortingAlgorithmNames.add(it.str)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupRecyclerViews()
        return binding.root
    }

    private fun setupRecyclerViews() {
        graphAlgoListAdapter = AlgorithmNameListAdapter(object : NameClickListener {
            override fun onClick(name: String) {
                goToGraphAnimationView(name)
            }
        })
        binding.graphAlgoRecyclerview.adapter = graphAlgoListAdapter
        graphAlgoListAdapter.submitList(graphAlgorithmNames)

        sortingAlgoListAdapter = AlgorithmNameListAdapter(object : NameClickListener {
            override fun onClick(name: String) {
                goToSortingAnimationView(name)
            }
        })
        binding.sortingAlgoRecyclerview.adapter = sortingAlgoListAdapter
        sortingAlgoListAdapter.submitList(sortingAlgorithmNames)
    }

    private fun goToGraphAnimationView(name: String) {
    }

    private fun goToSortingAnimationView(name: String) {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSortingFragment(name))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.graphExpandButton.tag = false
        binding.sortingExpandButton.tag = false

        binding.graphExpandButton.setOnClickListener {
            binding.graphAlgoRecyclerview.visibility = if (it.tag as Boolean) View.GONE else View.VISIBLE
            it.tag = !(it.tag as Boolean)
        }

        binding.sortingExpandButton.setOnClickListener {
            binding.sortingAlgoRecyclerview.visibility = if (it.tag as Boolean) View.GONE else View.VISIBLE
            it.tag = !(it.tag as Boolean)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val LOG_TAG = HomeFragment::class.qualifiedName
    }
}