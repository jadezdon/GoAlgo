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
import com.zhouppei.goalgo.databinding.BottomSheetFunctionConfigBinding
import com.zhouppei.goalgo.ui.colorpicker.ColorPickerDialog
import com.zhouppei.goalgo.ui.colorpicker.ColorPickerDialogListener
import com.zhouppei.goalgo.util.Constants
import com.zhouppei.goalgo.view.FunctionViewConfig


class FunctionConfigBottomSheet: BottomSheetDialogFragment() {
    private var _binding: BottomSheetFunctionConfigBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: FunctionConfigListener
    private lateinit var colorPickerDialog: ColorPickerDialog
    private var sharedPreferences: SharedPreferences? = null

    private var functionViewConfig = FunctionViewConfig()
    private val gson = Gson()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomSheetFunctionConfigBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        getConfigFromSharedPref()
        setupGraphViewParams()

        binding.speedSlowButton.setOnClickListener {
            functionViewConfig.animationSpeed = Constants.ANIMATION_SPEED_SLOW
            listener.setConfig(functionViewConfig)
        }

        binding.speedNormalButton.setOnClickListener {
            functionViewConfig.animationSpeed = Constants.ANIMATION_SPEED_NORMAL
            listener.setConfig(functionViewConfig)
        }

        binding.speedFastButton.setOnClickListener {
            functionViewConfig.animationSpeed = Constants.ANIMATION_SPEED_FAST
            listener.setConfig(functionViewConfig)
        }

        binding.showXAxisLabelsSwitch.setOnCheckedChangeListener { _, isChecked ->
            functionViewConfig.isXAxisLabelVisible = isChecked
            listener.setConfig(functionViewConfig)
        }

        binding.showYAxisLabelsSwitch.setOnCheckedChangeListener { _, isChecked ->
            functionViewConfig.isYAxisLabelVisible = isChecked
            listener.setConfig(functionViewConfig)
        }

        binding.resetButton.setOnClickListener {
            functionViewConfig = FunctionViewConfig()
            setupGraphViewParams()
            listener.setConfig(functionViewConfig)
        }

        setupPickCurrentStateColorButton()
        setupPickUnvisitedStateColorButton()
        setupPickVisitedStateColorButton()
    }

    private fun getConfigFromSharedPref() {
        sharedPreferences = context?.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        sharedPreferences?.let {
            if (it.contains(Constants.SHARED_PREF_FUNCTIONVIEW_CONFIGURATION)) {
                val str = it.getString(Constants.SHARED_PREF_FUNCTIONVIEW_CONFIGURATION, "")
                val itemType = object : TypeToken<FunctionViewConfig>() {}.type
                functionViewConfig = gson.fromJson(str, itemType)
            }
        }
    }

    private fun setupGraphViewParams() {
        binding.apply {
            showAxisColorPickerButton.setBackgroundColor(Color.parseColor(functionViewConfig.axisColor))
            showFunctionColorPickerButton.setBackgroundColor(Color.parseColor(functionViewConfig.functionColor))
            showLineColorPickerButton.setBackgroundColor(Color.parseColor(functionViewConfig.lineColor))

            when (functionViewConfig.animationSpeed) {
                Constants.ANIMATION_SPEED_SLOW -> speedButtonGroup.check(R.id.speed_slow_button)
                Constants.ANIMATION_SPEED_NORMAL -> speedButtonGroup.check(R.id.speed_normal_button)
                Constants.ANIMATION_SPEED_FAST -> speedButtonGroup.check(R.id.speed_fast_button)
            }

            showXAxisLabelsSwitch.isChecked = functionViewConfig.isXAxisLabelVisible
            showYAxisLabelsSwitch.isChecked = functionViewConfig.isYAxisLabelVisible
        }
    }

    private fun setupPickCurrentStateColorButton() {
        binding.showAxisColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                functionViewConfig.axisColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        functionViewConfig.axisColor = colorString
                        it.setBackgroundColor(Color.parseColor(colorString))
                        listener.setConfig(functionViewConfig)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun setupPickUnvisitedStateColorButton() {
        binding.showFunctionColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                functionViewConfig.functionColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        functionViewConfig.functionColor = colorString
                        it.setBackgroundColor(Color.parseColor(colorString))
                        listener.setConfig(functionViewConfig)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun setupPickVisitedStateColorButton() {
        binding.showLineColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                functionViewConfig.lineColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        functionViewConfig.lineColor = colorString
                        it.setBackgroundColor(Color.parseColor(colorString))
                        listener.setConfig(functionViewConfig)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun saveConfigToSharedPref() {
        sharedPreferences?.let {
            it.edit().apply {
                putString(Constants.SHARED_PREF_FUNCTIONVIEW_CONFIGURATION, gson.toJson(functionViewConfig))
                apply()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        if (this::colorPickerDialog.isInitialized) colorPickerDialog.dismiss()
        saveConfigToSharedPref()
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: FunctionConfigListener): FunctionConfigBottomSheet {
            val fragment = FunctionConfigBottomSheet()
            fragment.listener = listener
            return fragment
        }
    }
}
