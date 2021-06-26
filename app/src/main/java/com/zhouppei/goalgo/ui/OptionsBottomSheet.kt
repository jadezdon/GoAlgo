package com.zhouppei.goalgo.ui

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zhouppei.goalgo.R
import com.zhouppei.goalgo.algorithms.sorting.SortViewConfiguration
import com.zhouppei.goalgo.databinding.BottomSheetOptionsBinding
import com.zhouppei.goalgo.ui.colorpicker.ColorPickerDialog
import com.zhouppei.goalgo.ui.colorpicker.ColorPickerDialogListener
import com.zhouppei.goalgo.utils.Constants


class OptionsBottomSheet: BottomSheetDialogFragment() {
    private var _binding: BottomSheetOptionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: OptionsBottomSheetListener
    private lateinit var colorPickerDialog: ColorPickerDialog
    private var sharedPreferences: SharedPreferences? = null

    private var sortViewConfig = SortViewConfiguration()
    private val gson = Gson()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomSheetOptionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        setupSortViewParameters()

        binding.speedSlowButton.setOnClickListener {
            sortViewConfig.sortingSpeed = Constants.SORTING_SPEED_SLOW
            listener.onChangeSpeed(Constants.SORTING_SPEED_SLOW)
        }
        binding.speedNormalButton.setOnClickListener {
            sortViewConfig.sortingSpeed = Constants.SORTING_SPEED_NORMAL
            listener.onChangeSpeed(Constants.SORTING_SPEED_NORMAL)
        }
        binding.speedFastButton.setOnClickListener {
            sortViewConfig.sortingSpeed = Constants.SORTING_SPEED_FAST
            listener.onChangeSpeed(Constants.SORTING_SPEED_FAST)
        }

        binding.showValuesSwitch.setOnCheckedChangeListener { _, isChecked ->
            sortViewConfig.isShowItemValues = isChecked
            listener.onShowValuesChanged(isChecked)
        }

        binding.showIndexesSwitch.setOnCheckedChangeListener { _, isChecked ->
            sortViewConfig.isShowItemIndexes = isChecked
            listener.onShowIndexesChanged(isChecked)
        }

        setupPickCurrentStateColorButton()
        setupPickUnsortedStateColorButton()
        setupPickSortedStateColorButton()
    }

    private fun setupSortViewParameters() {
        sharedPreferences = context?.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        sharedPreferences?.let {
            if (it.contains(Constants.SHARED_PREF_SORTVIEW_CONFIGURATION)) {
                val str = it.getString(Constants.SHARED_PREF_SORTVIEW_CONFIGURATION, "")
                val itemType = object : TypeToken<SortViewConfiguration>() {}.type
                sortViewConfig = gson.fromJson(str, itemType)
            }
        }

        binding.apply {
            showCurrentColorPickerButton.setBackgroundColor(Color.parseColor(sortViewConfig.currentStateColorString))
            showUnsortedColorPickerButton.setBackgroundColor(Color.parseColor(sortViewConfig.unsortedStateColorString))
            showSortedColorPickerButton.setBackgroundColor(Color.parseColor(sortViewConfig.sortedStateColorString))

            when (sortViewConfig.sortingSpeed) {
                Constants.SORTING_SPEED_SLOW -> speedButtonGroup.check(R.id.speed_slow_button)
                Constants.SORTING_SPEED_NORMAL -> speedButtonGroup.check(R.id.speed_normal_button)
                Constants.SORTING_SPEED_FAST -> speedButtonGroup.check(R.id.speed_fast_button)
            }

            showValuesSwitch.isChecked = sortViewConfig.isShowItemValues
            showIndexesSwitch.isChecked = sortViewConfig.isShowItemIndexes
        }
    }

    private fun setupPickCurrentStateColorButton() {
        binding.showCurrentColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                sortViewConfig.currentStateColorString,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        sortViewConfig.currentStateColorString = colorString
                        binding.showCurrentColorPickerButton.setBackgroundColor(Color.parseColor(colorString))
                        listener.onChangeCurrentStateColor(colorString)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun setupPickUnsortedStateColorButton() {
        binding.showUnsortedColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                sortViewConfig.unsortedStateColorString,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        sortViewConfig.unsortedStateColorString = colorString
                        binding.showUnsortedColorPickerButton.setBackgroundColor(Color.parseColor(colorString))
                        listener.onChangeUnsortedStateColor(colorString)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun setupPickSortedStateColorButton() {
        binding.showSortedColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                sortViewConfig.sortedStateColorString,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        sortViewConfig.sortedStateColorString = colorString
                        binding.showSortedColorPickerButton.setBackgroundColor(Color.parseColor(colorString))
                        listener.onChangeSortedStateColor(colorString)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun saveSortViewParameters() {
        sharedPreferences?.let {
            it.edit().apply {
                putString(Constants.SHARED_PREF_SORTVIEW_CONFIGURATION, gson.toJson(sortViewConfig))
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
        fun newInstance(listener: OptionsBottomSheetListener): OptionsBottomSheet {
            val fragment = OptionsBottomSheet()
            fragment.listener = listener
            return fragment
        }
    }
}

interface OptionsBottomSheetListener {
    fun onChangeSpeed(speedInMiliSec: Long)
    fun onShowValuesChanged(isShow: Boolean)
    fun onShowIndexesChanged(isShow: Boolean)
    fun onChangeCurrentStateColor(colorString: String)
    fun onChangeUnsortedStateColor(colorString: String)
    fun onChangeSortedStateColor(colorString: String)
}