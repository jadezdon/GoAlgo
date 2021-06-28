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
import com.zhouppei.goalgo.algorithms.sorting.SortViewConfig
import com.zhouppei.goalgo.databinding.BottomSheetSortingConfigBinding
import com.zhouppei.goalgo.ui.colorpicker.ColorPickerDialog
import com.zhouppei.goalgo.ui.colorpicker.ColorPickerDialogListener
import com.zhouppei.goalgo.utils.Constants


class SortingConfigBottomSheet: BottomSheetDialogFragment() {
    private var _binding: BottomSheetSortingConfigBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: SortingConfigListener
    private lateinit var colorPickerDialog: ColorPickerDialog
    private var sharedPreferences: SharedPreferences? = null

    private var sortViewConfig = SortViewConfig()
    private val gson = Gson()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomSheetSortingConfigBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        setupSortViewParams()

        binding.speedSlowButton.setOnClickListener {
            sortViewConfig.animationSpeed = Constants.ANIMATION_SPEED_SLOW
            listener.onChangeSpeed(Constants.ANIMATION_SPEED_SLOW)
        }
        binding.speedNormalButton.setOnClickListener {
            sortViewConfig.animationSpeed = Constants.ANIMATION_SPEED_NORMAL
            listener.onChangeSpeed(Constants.ANIMATION_SPEED_NORMAL)
        }
        binding.speedFastButton.setOnClickListener {
            sortViewConfig.animationSpeed = Constants.ANIMATION_SPEED_FAST
            listener.onChangeSpeed(Constants.ANIMATION_SPEED_FAST)
        }

        binding.showValuesSwitch.setOnCheckedChangeListener { _, isChecked ->
            sortViewConfig.isItemValuesVisible = isChecked
            listener.onChangeItemValuesVisibility(isChecked)
        }

        binding.showIndexesSwitch.setOnCheckedChangeListener { _, isChecked ->
            sortViewConfig.isItemIndexesVisible = isChecked
            listener.onChangeItemIndexesVisibility(isChecked)
        }

        binding.toggleCompleteAnimationSwitch.setOnCheckedChangeListener { _, isChecked ->
            sortViewConfig.isCompleteAnimationEnabled = isChecked
            listener.toggleCompleteAnimation()
        }

        setupPickCurrentStateColorButton()
        setupPickUnsortedStateColorButton()
        setupPickSortedStateColorButton()
        setupPickPivotColorButton()
    }

    private fun setupSortViewParams() {
        sharedPreferences = context?.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        sharedPreferences?.let {
            if (it.contains(Constants.SHARED_PREF_SORTVIEW_CONFIGURATION)) {
                val str = it.getString(Constants.SHARED_PREF_SORTVIEW_CONFIGURATION, "")
                val itemType = object : TypeToken<SortViewConfig>() {}.type
                sortViewConfig = gson.fromJson(str, itemType)
            }
        }

        binding.apply {
            showCurrentColorPickerButton.setBackgroundColor(Color.parseColor(sortViewConfig.currentStateColor))
            showUnsortedColorPickerButton.setBackgroundColor(Color.parseColor(sortViewConfig.unsortedStateColor))
            showSortedColorPickerButton.setBackgroundColor(Color.parseColor(sortViewConfig.sortedStateColor))
            showPivotColorPickerButton.setBackgroundColor(Color.parseColor(sortViewConfig.pivotColor))

            when (sortViewConfig.animationSpeed) {
                Constants.ANIMATION_SPEED_SLOW -> speedButtonGroup.check(R.id.speed_slow_button)
                Constants.ANIMATION_SPEED_NORMAL -> speedButtonGroup.check(R.id.speed_normal_button)
                Constants.ANIMATION_SPEED_FAST -> speedButtonGroup.check(R.id.speed_fast_button)
            }

            showValuesSwitch.isChecked = sortViewConfig.isItemValuesVisible
            showIndexesSwitch.isChecked = sortViewConfig.isItemIndexesVisible
            toggleCompleteAnimationSwitch.isChecked = sortViewConfig.isCompleteAnimationEnabled
        }
    }

    private fun setupPickCurrentStateColorButton() {
        binding.showCurrentColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                sortViewConfig.currentStateColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        sortViewConfig.currentStateColor = colorString
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
                sortViewConfig.unsortedStateColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        sortViewConfig.unsortedStateColor = colorString
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
                sortViewConfig.sortedStateColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        sortViewConfig.sortedStateColor = colorString
                        binding.showSortedColorPickerButton.setBackgroundColor(Color.parseColor(colorString))
                        listener.onChangeSortedStateColor(colorString)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun setupPickPivotColorButton() {
        binding.showPivotColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                sortViewConfig.pivotColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        sortViewConfig.pivotColor = colorString
                        binding.showPivotColorPickerButton.setBackgroundColor(Color.parseColor(colorString))
                        listener.onChangePivotColor(colorString)
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
        fun newInstance(listener: SortingConfigListener): SortingConfigBottomSheet {
            val fragment = SortingConfigBottomSheet()
            fragment.listener = listener
            return fragment
        }
    }
}
