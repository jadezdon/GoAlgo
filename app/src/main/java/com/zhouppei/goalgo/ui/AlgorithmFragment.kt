package com.zhouppei.goalgo.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zhouppei.goalgo.algorithms.graph.*
import com.zhouppei.goalgo.algorithms.sorting.*
import com.zhouppei.goalgo.databinding.FragmentAlgorithmBinding
import com.zhouppei.goalgo.models.GraphAlgorithm
import com.zhouppei.goalgo.models.SortingAlgorithm
import com.zhouppei.goalgo.utils.Constants
import com.zhouppei.goalgo.views.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class AlgorithmFragment : Fragment() {

    private val args: AlgorithmFragmentArgs by navArgs()

    private var _binding: FragmentAlgorithmBinding? = null
    private val binding get() = _binding!!

    private lateinit var algorithmView: AlgorithmView

    private lateinit var sortingConfigBottomSheet: SortingConfigBottomSheet
    private var sortingConfigListener: SortingConfigListener? = null

    private lateinit var graphConfigBottomSheet: GraphConfigBottomSheet
    private var graphConfigListener: GraphConfigListener? = null

    private lateinit var gridConfigBottomSheet: GridConfigBottomSheet
    private var gridConfigListener: GridConfigListener? = null

    private var sharedPreferences: SharedPreferences? = null
    private val gson = Gson()
    private lateinit var algorithmJob: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlgorithmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = args.algorithmName

        initAlgorithmView()

        initAlgorithmConfigListener()

        binding.algorithmViewLayout.addView(algorithmView)

        setupControlButtonsListeners()

        binding.goBackButton.setOnClickListener { findNavController().navigateUp() }

        binding.openOptionsDialogButton.setOnClickListener { showAlgorithmConfigBottomSheet() }
    }

    private fun initAlgorithmView() {
        algorithmView = mapAlgorithmNameToView(args.algorithmName).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setPadding(40, 50, 40, 50)
            setOnCompleteListener(object : OnCompleteListener {
                override fun onAlgorithmComplete() {
                    binding.newButton.apply {
                        isEnabled = true
                        alpha = 1f
                    }
                    binding.runButton.apply {
                        visibility = View.VISIBLE
                        isEnabled = false
                        alpha = 0.5f
                    }
                    binding.stopButton.visibility = View.GONE
                    Toast.makeText(context, "Complete!", Toast.LENGTH_SHORT).apply {
                        setGravity(Gravity.CENTER, 0, 0)
                    }.show()
                }
            })

            when (this) {
                is GraphView -> getGraphViewConfig()?.let { setGraphViewConfig(it) }
                is SortView -> getSortViewConfig()?.let { setSortViewConfig(it) }
                is GridView -> getGridViewConfig()?.let {  }
            }

            if (this is DFSView) setIsTargetExist(false)
        }
    }

    private fun mapAlgorithmNameToView(name: String): AlgorithmView {
        return when (name) {
            SortingAlgorithm.BubbleSort.str -> BubbleSortView(requireContext())
            SortingAlgorithm.InsertionSort.str -> InsertionSortView(requireContext())
            SortingAlgorithm.QuickSort.str -> QuickSortView(requireContext())
            SortingAlgorithm.SelectionSort.str -> SelectionSortView(requireContext())
            SortingAlgorithm.MergeSort.str -> MergeSortView(requireContext())
            SortingAlgorithm.ShellSort.str -> ShellSortView(requireContext())
            SortingAlgorithm.CocktailShakerSort.str -> CocktailShakerSortView(requireContext())
            GraphAlgorithm.DFS.str -> DFSView(requireContext())
            GraphAlgorithm.BFS.str -> BFSView(requireContext())
            GraphAlgorithm.ASTAR.str -> AStarView(requireContext())
            else -> BubbleSortView(requireContext())
        }
    }

    private fun getGridViewConfig(): GridViewConfig? {
        sharedPreferences = context?.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        sharedPreferences?.let {
            if (it.contains(Constants.SHARED_PREF_GRIDVIEW_CONFIGURATION)) {
                val str = it.getString(Constants.SHARED_PREF_GRIDVIEW_CONFIGURATION, "")
                val itemType = object : TypeToken<GridViewConfig>() {}.type
                return gson.fromJson(str, itemType)
            }
        }
        return null
    }

    private fun getGraphViewConfig(): GraphViewConfig? {
        sharedPreferences = context?.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        sharedPreferences?.let {
            if (it.contains(Constants.SHARED_PREF_GRAPHVIEW_CONFIGURATION)) {
                val str = it.getString(Constants.SHARED_PREF_GRAPHVIEW_CONFIGURATION, "")
                val itemType = object : TypeToken<GraphViewConfig>() {}.type
                return gson.fromJson(str, itemType)
            }
        }
        return null
    }

    private fun getSortViewConfig(): SortViewConfig? {
        sharedPreferences = context?.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        sharedPreferences?.let {
            if (it.contains(Constants.SHARED_PREF_SORTVIEW_CONFIGURATION)) {
                val str = it.getString(Constants.SHARED_PREF_SORTVIEW_CONFIGURATION, "")
                val itemType = object : TypeToken<SortViewConfig>() {}.type
                return gson.fromJson(str, itemType)
            }
        }
        return null
    }

    private fun setupControlButtonsListeners() {
        binding.runButton.setOnClickListener {
            algorithmJob = GlobalScope.launch(Dispatchers.Main) {
                binding.newButton.apply {
                    isEnabled = false
                    alpha = 0.5f
                }
                algorithmView.run()
            }
            it.visibility = View.GONE
            binding.stopButton.visibility = View.VISIBLE
            binding.newButton.apply {
                isEnabled = false
                alpha = 0.5f
            }
        }

        binding.stopButton.setOnClickListener {
            algorithmJob.cancel()
            it.apply {
                isEnabled = false
                alpha = 0.5f
            }
            binding.newButton.apply {
                isEnabled = true
                alpha = 1f
            }
        }

        binding.newButton.setOnClickListener {
            binding.stopButton.apply {
                visibility = View.GONE
                isEnabled = true
                alpha = 1f
            }
            binding.runButton.apply {
                visibility = View.VISIBLE
                isEnabled = true
                alpha = 1f
            }

            algorithmView.new()
        }
    }

    private fun initAlgorithmConfigListener() {
        when (algorithmView) {
            is GraphView -> initGraphConfigListener()
            is SortView -> initSortingConfigListener()
            is GridView -> initGridConfigListener()
        }
    }

    private fun initGraphConfigListener() {
        graphConfigListener = object : GraphConfigListener {
            override fun setConfig(config: GraphViewConfig) {
                (algorithmView as? GraphView)?.setGraphViewConfig(config)
            }
        }
    }

    private fun initGridConfigListener() {
        gridConfigListener = object : GridConfigListener {
            override fun setConfig(config: GridViewConfig) {
                (algorithmView as? GridView)?.setGridViewConfig(config)
            }
        }
    }

    private fun initSortingConfigListener() {
        sortingConfigListener = object : SortingConfigListener {
            override fun setConfig(config: SortViewConfig) {
                (algorithmView as? SortView)?.setSortViewConfig(config)
            }
        }
    }

    private fun showAlgorithmConfigBottomSheet() {
        activity?.supportFragmentManager?.let {
            when (algorithmView) {
                is GraphView -> graphConfigListener?.let { listener ->
                    GraphConfigBottomSheet.newInstance(listener).apply { show(it, tag) }
                }
                is SortView -> sortingConfigListener?.let { listener ->
                    SortingConfigBottomSheet.newInstance(listener).apply { show(it, tag) }
                }
                is GridView -> gridConfigListener?.let { listener ->
                    GridConfigBottomSheet.newInstance(listener).apply { show(it, tag) }
                }
                else -> {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::sortingConfigBottomSheet.isInitialized) sortingConfigBottomSheet.dismiss()
        if (this::graphConfigBottomSheet.isInitialized) graphConfigBottomSheet.dismiss()
        if (this::gridConfigBottomSheet.isInitialized) gridConfigBottomSheet.dismiss()
        if (this::algorithmJob.isInitialized && !algorithmJob.isCancelled) algorithmJob.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val LOG_TAG = AlgorithmFragment::class.qualifiedName
    }
}