package com.zhouppei.goalgo.ui

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zhouppei.goalgo.R
import com.zhouppei.goalgo.algorithms.sorting.*
import com.zhouppei.goalgo.databinding.FragmentSortingBinding
import com.zhouppei.goalgo.models.SortingAlgorithm
import com.zhouppei.goalgo.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class SortingFragment : Fragment() {

    private val args: SortingFragmentArgs by navArgs()

    private var _binding: FragmentSortingBinding? = null
    private val binding get() = _binding!!

    private lateinit var sortView: SortView
    private lateinit var optionsBottomSheet: OptionsBottomSheet
    private var sharedPreferences: SharedPreferences? = null
    private val gson = Gson()
    private lateinit var sortingJob: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSortingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = args.algorithmName

        sortView = when (args.algorithmName) {
            SortingAlgorithm.BubbleSort.str -> BubbleSortView(requireContext())
            SortingAlgorithm.InsertionSort.str -> InsertionSortView(requireContext())
            SortingAlgorithm.QuickSort.str -> QuickSortView(requireContext())
            SortingAlgorithm.SelectionSort.str -> SelectionSortView(requireContext())
            SortingAlgorithm.MergeSort.str -> MergeSortView(requireContext())
            else -> BubbleSortView(requireContext())
        }.apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setPadding(40, 50, 40, 50)
            setOnCompleteListener(object : OnCompleteListener {
                override fun onComplete() {
                    binding.newButton.isEnabled = true
                    binding.sortButton.visibility = View.VISIBLE
                    binding.sortButton.isEnabled = false
                    binding.stopButton?.visibility = View.GONE
                    Toast.makeText(context, "Complete!", Toast.LENGTH_SHORT).apply {
                        setGravity(Gravity.CENTER, 0, 0)
                    }.show()
                }
            })
            getSortViewConfig()?.let { setSortViewConfiguration(it) }
        }

        binding.sortViewLayout.addView(sortView)

        binding.sortButton.setOnClickListener {
            sortingJob = GlobalScope.launch(Dispatchers.Main) {
                binding.newButton.isEnabled = false
                sortView.sort()
            }
            it.visibility = View.GONE
            binding.stopButton?.visibility = View.VISIBLE
            binding.newButton.isEnabled = false
        }

        binding.stopButton?.setOnClickListener {
            sortingJob.cancel()
            it.visibility = View.GONE
            binding.sortButton.visibility = View.VISIBLE
            binding.sortButton.isEnabled = false
            binding.newButton.isEnabled = true
        }

        binding.newButton.setOnClickListener {
            binding.sortButton.isEnabled = true
            sortView.new()
        }

        binding.goBackButton.setOnClickListener { findNavController().navigateUp() }

        binding.openOptionsDialogButton.setOnClickListener { showOptionsBottomSheet() }
    }

    private fun getSortViewConfig(): SortViewConfiguration? {
        sharedPreferences = context?.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        sharedPreferences?.let {
            if (it.contains(Constants.SHARED_PREF_SORTVIEW_CONFIGURATION)) {
                val str = it.getString(Constants.SHARED_PREF_SORTVIEW_CONFIGURATION, "")
                val itemType = object : TypeToken<SortViewConfiguration>() {}.type
                return gson.fromJson(str, itemType)
            }
        }
        return null
    }

    private val optionsBottomSheetListener = object : OptionsBottomSheetListener {
        override fun onChangeSpeed(speedInMiliSec: Long) {
            sortView.setSortingSpeed(speedInMiliSec)
        }

        override fun onShowValuesChanged(isShow: Boolean) {
            sortView.setIsShowValues(isShow)
        }

        override fun onShowIndexesChanged(isShow: Boolean) {
            sortView.setIsShowIndexes(isShow)
        }

        override fun onChangeCurrentStateColor(colorString: String) {
            sortView.setCurrentStateColor(colorString)
        }

        override fun onChangeUnsortedStateColor(colorString: String) {
            sortView.setUnsortedStateColor(colorString)
        }

        override fun onChangeSortedStateColor(colorString: String) {
            sortView.setSortedStateColor(colorString)
        }

        override fun onChangePivotColor(colorString: String) {
            sortView.setPivotColor(colorString)
        }
    }

    private fun showOptionsBottomSheet() {
        activity?.supportFragmentManager?.let {
            OptionsBottomSheet.newInstance(optionsBottomSheetListener).apply {
                show(it, tag)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::optionsBottomSheet.isInitialized) optionsBottomSheet.dismiss()
        if (this::sortingJob.isInitialized && !sortingJob.isCancelled) sortingJob.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val LOG_TAG = SortingFragment::class.qualifiedName
    }
}