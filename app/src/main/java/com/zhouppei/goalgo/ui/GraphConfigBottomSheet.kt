package com.zhouppei.goalgo.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.zhouppei.goalgo.algorithms.graph.GraphViewConfig
import com.zhouppei.goalgo.databinding.BottomSheetGraphConfigBinding
import com.zhouppei.goalgo.ui.colorpicker.ColorPickerDialog
import com.zhouppei.goalgo.utils.Constants


class GraphConfigBottomSheet: BottomSheetDialogFragment() {
    private var _binding: BottomSheetGraphConfigBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: GraphConfigListener
    private lateinit var colorPickerDialog: ColorPickerDialog
    private var sharedPreferences: SharedPreferences? = null

    private var graphViewConfig = GraphViewConfig()
    private val gson = Gson()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomSheetGraphConfigBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
        }

//        setupSortViewParameters()
//
//        binding.speedSlowButton.setOnClickListener {
//            graphViewConfig.sortingSpeed = Constants.SORTING_SPEED_SLOW
//            listener.onChangeSpeed(Constants.SORTING_SPEED_SLOW)
//        }
//        binding.speedNormalButton.setOnClickListener {
//            graphViewConfig.sortingSpeed = Constants.SORTING_SPEED_NORMAL
//            listener.onChangeSpeed(Constants.SORTING_SPEED_NORMAL)
//        }
//        binding.speedFastButton.setOnClickListener {
//            graphViewConfig.sortingSpeed = Constants.SORTING_SPEED_FAST
//            listener.onChangeSpeed(Constants.SORTING_SPEED_FAST)
//        }

//        binding.showValuesSwitch.setOnCheckedChangeListener { _, isChecked ->
//            graphViewConfig.isShowItemValues = isChecked
//            listener.onShowValuesChanged(isChecked)
//        }
//
//        binding.showIndexesSwitch.setOnCheckedChangeListener { _, isChecked ->
//            graphViewConfig.isShowItemIndexes = isChecked
//            listener.onShowIndexesChanged(isChecked)
//        }
//
//        binding.toggleCompleteAnimationSwitch.setOnCheckedChangeListener { _, isChecked ->
//            graphViewConfig.isCompleteAnimationEnabled = isChecked
//            listener.toggleCompleteAnimation()
//        }

//        setupPickCurrentStateColorButton()
//        setupPickUnsortedStateColorButton()
//        setupPickSortedStateColorButton()
//        setupPickPivotColorButton()
    }

//    private fun setupSortViewParameters() {
//        sharedPreferences = context?.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
//
//        sharedPreferences?.let {
//            if (it.contains(Constants.SHARED_PREF_SORTVIEW_CONFIGURATION)) {
//                val str = it.getString(Constants.SHARED_PREF_SORTVIEW_CONFIGURATION, "")
//                val itemType = object : TypeToken<graphViewConfig>() {}.type
//                graphViewConfig = gson.fromJson(str, itemType)
//            }
//        }
//
//        binding.apply {
//            showCurrentColorPickerButton.setBackgroundColor(Color.parseColor(graphViewConfig.currentStateColorString))
//            showUnsortedColorPickerButton.setBackgroundColor(Color.parseColor(graphViewConfig.unsortedStateColorString))
//            showSortedColorPickerButton.setBackgroundColor(Color.parseColor(graphViewConfig.sortedStateColorString))
//            showPivotColorPickerButton.setBackgroundColor(Color.parseColor(graphViewConfig.pivotColorString))
//
//            when (graphViewConfig.sortingSpeed) {
//                Constants.SORTING_SPEED_SLOW -> speedButtonGroup.check(R.id.speed_slow_button)
//                Constants.SORTING_SPEED_NORMAL -> speedButtonGroup.check(R.id.speed_normal_button)
//                Constants.SORTING_SPEED_FAST -> speedButtonGroup.check(R.id.speed_fast_button)
//            }
//
//            showValuesSwitch.isChecked = graphViewConfig.isShowItemValues
//            showIndexesSwitch.isChecked = graphViewConfig.isShowItemIndexes
//            toggleCompleteAnimationSwitch.isChecked = graphViewConfig.isCompleteAnimationEnabled
//        }
//    }
//
//    private fun setupPickCurrentStateColorButton() {
//        binding.showCurrentColorPickerButton.setOnClickListener {
//            colorPickerDialog = ColorPickerDialog(
//                requireContext(),
//                graphViewConfig.currentStateColorString,
//                object : ColorPickerDialogListener {
//                    override fun setSelectedColorString(colorString: String) {
//                        graphViewConfig.currentStateColorString = colorString
//                        binding.showCurrentColorPickerButton.setBackgroundColor(Color.parseColor(colorString))
//                        listener.onChangeCurrentStateColor(colorString)
//                    }
//                }
//            )
//            colorPickerDialog.show()
//        }
//    }
//
//    private fun setupPickUnsortedStateColorButton() {
//        binding.showUnsortedColorPickerButton.setOnClickListener {
//            colorPickerDialog = ColorPickerDialog(
//                requireContext(),
//                graphViewConfig.unsortedStateColorString,
//                object : ColorPickerDialogListener {
//                    override fun setSelectedColorString(colorString: String) {
//                        graphViewConfig.unsortedStateColorString = colorString
//                        binding.showUnsortedColorPickerButton.setBackgroundColor(Color.parseColor(colorString))
//                        listener.onChangeUnsortedStateColor(colorString)
//                    }
//                }
//            )
//            colorPickerDialog.show()
//        }
//    }
//
//    private fun setupPickSortedStateColorButton() {
//        binding.showSortedColorPickerButton.setOnClickListener {
//            colorPickerDialog = ColorPickerDialog(
//                requireContext(),
//                graphViewConfig.sortedStateColorString,
//                object : ColorPickerDialogListener {
//                    override fun setSelectedColorString(colorString: String) {
//                        graphViewConfig.sortedStateColorString = colorString
//                        binding.showSortedColorPickerButton.setBackgroundColor(Color.parseColor(colorString))
//                        listener.onChangeSortedStateColor(colorString)
//                    }
//                }
//            )
//            colorPickerDialog.show()
//        }
//    }
//
//    private fun setupPickPivotColorButton() {
//        binding.showPivotColorPickerButton.setOnClickListener {
//            colorPickerDialog = ColorPickerDialog(
//                requireContext(),
//                graphViewConfig.pivotColorString,
//                object : ColorPickerDialogListener {
//                    override fun setSelectedColorString(colorString: String) {
//                        graphViewConfig.pivotColorString = colorString
//                        binding.showPivotColorPickerButton.setBackgroundColor(Color.parseColor(colorString))
//                        listener.onChangePivotColor(colorString)
//                    }
//                }
//            )
//            colorPickerDialog.show()
//        }
//    }

    private fun saveSortViewParameters() {
        sharedPreferences?.let {
            it.edit().apply {
                putString(Constants.SHARED_PREF_SORTVIEW_CONFIGURATION, gson.toJson(graphViewConfig))
                apply()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        if (this::colorPickerDialog.isInitialized) colorPickerDialog.dismiss()
        saveSortViewParameters()
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: GraphConfigListener): GraphConfigBottomSheet {
            val fragment = GraphConfigBottomSheet()
            fragment.listener = listener
            return fragment
        }
    }
}
