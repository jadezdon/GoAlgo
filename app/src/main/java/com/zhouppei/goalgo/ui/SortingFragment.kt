package com.zhouppei.goalgo.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.zhouppei.goalgo.algorithms.sorting.BubbleSortView
import com.zhouppei.goalgo.algorithms.sorting.SortView
import com.zhouppei.goalgo.databinding.FragmentSortingBinding
import com.zhouppei.goalgo.models.SortingAlgorithm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SortingFragment : Fragment() {

    private val args: SortingFragmentArgs by navArgs()

    private var _binding: FragmentSortingBinding? = null
    private val binding get() = _binding!!

    private lateinit var sortView: SortView
    private lateinit var optionsBottomSheet: OptionsBottomSheet

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
            else -> BubbleSortView(requireContext())
        }.apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setPadding(40, 30, 40, 30)
        }

        binding.sortViewLayout.addView(sortView)

        binding.sortButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                sortView.sort()
            }
        }

        binding.newButton.setOnClickListener {
            sortView.new()
        }

        binding.goBackButton.setOnClickListener { findNavController().navigateUp() }

        binding.openOptionsDialogButton.setOnClickListener { showOptionsBottomSheet() }
    }

    private val optionsBottomSheetListener = object : OptionsBottomSheetListener {
        override fun onChangeSpeed(speedInMiliSec: Long) {
            sortView.speedInMiliSeconds = speedInMiliSec
        }

        override fun onShowValuesChanged(isShow: Boolean) {
            sortView.isShowValueText = isShow
        }

        override fun onShowIndexesChanged(isShow: Boolean) {
            sortView.isShowIndexText = isShow
        }
    }

    private fun showOptionsBottomSheet() {
//        optionsBottomSheet = OptionsBottomSheet(
//            requireContext(),
//            object : OptionsBottomSheetListener {
//                override fun onChangeSpeed(speedInMiliSec: Long) {
//                    sortView.speedInMiliSeconds = speedInMiliSec
//                }
//
//                override fun onShowValuesChanged(isShow: Boolean) {
//                    sortView.isShowValueText = isShow
//                }
//
//                override fun onShowIndexesChanged(isShow: Boolean) {
//                    sortView.isShowIndexText = isShow
//                }
//            }
//        )
//
//        optionsBottomSheet.show()
        activity?.supportFragmentManager?.let {
            OptionsBottomSheet.newInstance(optionsBottomSheetListener).apply {
                show(it, tag)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::optionsBottomSheet.isInitialized) optionsBottomSheet.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}