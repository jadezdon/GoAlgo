package com.zhouppei.goalgo.ui

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
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
import com.zhouppei.goalgo.algorithm.graph.*
import com.zhouppei.goalgo.algorithm.maze.RandomizedDFSView
import com.zhouppei.goalgo.algorithm.maze.RandomizedKruskalsView
import com.zhouppei.goalgo.algorithm.maze.RandomizedPrimsView
import com.zhouppei.goalgo.algorithm.maze.RecursiveDivisionView
import com.zhouppei.goalgo.algorithm.rootfinding.InverseInterpolationMethodView
import com.zhouppei.goalgo.algorithm.rootfinding.SecantMethodView
import com.zhouppei.goalgo.algorithm.rootfinding.SteffensensMethodView
import com.zhouppei.goalgo.algorithm.sorting.*
import com.zhouppei.goalgo.databinding.FragmentAlgorithmBinding
import com.zhouppei.goalgo.model.GraphSearchAlgorithm
import com.zhouppei.goalgo.model.MazeGenerationAlgorithm
import com.zhouppei.goalgo.model.RootFindingAlgorithm
import com.zhouppei.goalgo.model.SortingAlgorithm
import com.zhouppei.goalgo.util.Constants
import com.zhouppei.goalgo.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class AlgorithmFragment : Fragment() {

    private val args: AlgorithmFragmentArgs by navArgs()

    private var _binding: FragmentAlgorithmBinding? = null
    private val binding get() = _binding!!

    private lateinit var algorithmView: AlgorithmView

    private var sortingConfigListener: SortingConfigListener? = null
    private var graphConfigListener: GraphConfigListener? = null
    private var gridConfigListener: GridConfigListener? = null
    private var functionConfigListener: FunctionConfigListener? = null

    private lateinit var descriptionDialog: DescriptionDialog

    private var sharedPreferences: SharedPreferences? = null
    private val gson = Gson()
    private lateinit var algorithmJob: Job

    private var currentOrientation: Int? = null

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
                    currentOrientation?.let { orientation ->  activity?.requestedOrientation = orientation }

                    binding.newButton.apply {
                        isEnabled = true
                        alpha = 1f
                    }
                    binding.showCodeButton.apply {
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
                is GraphView -> {
                    getGraphViewConfig()?.let { setConfig(it) }
                    initGraphConfigListener()
                }
                is SortView -> {
                    getSortViewConfig()?.let { setConfig(it) }
                    initSortingConfigListener()
                }
                is GridView -> {
                    getGridViewConfig()?.let { setConfig(it) }
                    initGridConfigListener()
                }
                is FunctionView -> {
                    getFunctionViewConfig()?.let { setConfig(it) }
                    initFunctionConfigListener()
                }
            }
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
            GraphSearchAlgorithm.DFS.str -> DFSView(requireContext())
            GraphSearchAlgorithm.BFS.str -> BFSView(requireContext())
            GraphSearchAlgorithm.Dijkstras.str -> DijsktrasView(requireContext())
            MazeGenerationAlgorithm.RandomizedDFS.str -> RandomizedDFSView(requireContext())
            MazeGenerationAlgorithm.RandomizedKruskals.str -> RandomizedKruskalsView(requireContext())
            MazeGenerationAlgorithm.RandomizedPrims.str -> RandomizedPrimsView(requireContext())
            MazeGenerationAlgorithm.RecursiveDivision.str -> RecursiveDivisionView(requireContext())
            RootFindingAlgorithm.SecantMethod.str -> SecantMethodView(requireContext())
            RootFindingAlgorithm.SteffensensMethod.str -> SteffensensMethodView(requireContext())
            RootFindingAlgorithm.InverseInterpolation.str -> InverseInterpolationMethodView(requireContext())
            else -> {
                Toast.makeText(context, "${args.algorithmName} is not implemented yet", Toast.LENGTH_SHORT).apply {
                    setGravity(Gravity.CENTER, 0, 0)
                }.show()
                findNavController().navigateUp()

                BubbleSortView(requireContext())
            }
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

    private fun getFunctionViewConfig(): FunctionViewConfig? {
        sharedPreferences = context?.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        sharedPreferences?.let {
            if (it.contains(Constants.SHARED_PREF_FUNCTIONVIEW_CONFIGURATION)) {
                val str = it.getString(Constants.SHARED_PREF_FUNCTIONVIEW_CONFIGURATION, "")
                val itemType = object : TypeToken<FunctionViewConfig>() {}.type
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
            currentOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

            algorithmJob = GlobalScope.launch(Dispatchers.Main) {
                algorithmView.run()
            }
            it.visibility = View.GONE
            binding.stopButton.visibility = View.VISIBLE
            binding.newButton.apply {
                isEnabled = false
                alpha = 0.5f
            }
            binding.showCodeButton.apply {
                isEnabled = false
                alpha = 0.5f
            }
        }

        binding.stopButton.setOnClickListener {
            currentOrientation?.let { orientation ->  activity?.requestedOrientation = orientation }

            algorithmJob.cancel()
            it.apply {
                isEnabled = false
                alpha = 0.5f
            }
            binding.newButton.apply {
                isEnabled = true
                alpha = 1f
            }
            binding.showCodeButton.apply {
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

        binding.showCodeButton.setOnClickListener {
            descriptionDialog = DescriptionDialog(requireContext(), algorithmView.sourceCode(), algorithmView.description())
            descriptionDialog.show()
        }
    }

    private fun initGraphConfigListener() {
        graphConfigListener = object : GraphConfigListener {
            override fun setConfig(config: GraphViewConfig) {
                (algorithmView as? GraphView)?.setConfig(config)
            }
        }
    }

    private fun initGridConfigListener() {
        gridConfigListener = object : GridConfigListener {
            override fun setConfig(config: GridViewConfig) {
                (algorithmView as? GridView)?.setConfig(config)
            }
        }
    }

    private fun initSortingConfigListener() {
        sortingConfigListener = object : SortingConfigListener {
            override fun setConfig(config: SortViewConfig) {
                (algorithmView as? SortView)?.setConfig(config)
            }
        }
    }

    private fun initFunctionConfigListener() {
        functionConfigListener = object : FunctionConfigListener {
            override fun setConfig(config: FunctionViewConfig) {
                (algorithmView as? FunctionView)?.setConfig(config)
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
                is FunctionView -> functionConfigListener?.let { listener ->
                    FunctionConfigBottomSheet.newInstance(listener).apply { show(it, tag) }
                }
                else -> {}
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::algorithmJob.isInitialized && algorithmJob.isActive) algorithmJob.cancel()
        if (this::descriptionDialog.isInitialized) descriptionDialog.dismiss()
        currentOrientation?.let { orientation ->  activity?.requestedOrientation = orientation }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val LOG_TAG = AlgorithmFragment::class.qualifiedName
    }
}