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
import com.zhouppei.goalgo.databinding.BottomSheetGridConfigBinding
import com.zhouppei.goalgo.ui.colorpicker.ColorPickerDialog
import com.zhouppei.goalgo.ui.colorpicker.ColorPickerDialogListener
import com.zhouppei.goalgo.util.Constants
import com.zhouppei.goalgo.view.GridViewConfig


class GridConfigBottomSheet: BottomSheetDialogFragment() {
    private var _binding: BottomSheetGridConfigBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: GridConfigListener
    private lateinit var colorPickerDialog: ColorPickerDialog
    private var sharedPreferences: SharedPreferences? = null

    private var gridViewConfig = GridViewConfig()
    private val gson = Gson()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomSheetGridConfigBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        getConfigFromSharedPref()
        setupGridViewParams()

        binding.speedSlowButton.setOnClickListener {
            gridViewConfig.animationSpeed = Constants.ANIMATION_SPEED_SLOW
            listener.setConfig(gridViewConfig)
        }
        binding.speedNormalButton.setOnClickListener {
            gridViewConfig.animationSpeed = Constants.ANIMATION_SPEED_NORMAL
            listener.setConfig(gridViewConfig)
        }
        binding.speedFastButton.setOnClickListener {
            gridViewConfig.animationSpeed = Constants.ANIMATION_SPEED_FAST
            listener.setConfig(gridViewConfig)
        }

        binding.toggleCompleteAnimationSwitch.setOnCheckedChangeListener { _, isChecked ->
            gridViewConfig.isCompleteAnimationEnabled = isChecked
            listener.setConfig(gridViewConfig)
        }

        binding.resetButton.setOnClickListener {
            gridViewConfig = GridViewConfig()
            setupGridViewParams()
            listener.setConfig(gridViewConfig)
        }

        setupPickUnvisitedCellColorButton()
        setupPickCurrentCellColorButton()
        setupPickVisitedCellColorButton()
        setupPickCellWallColorButton()
    }

    private fun getConfigFromSharedPref() {
        sharedPreferences = context?.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        sharedPreferences?.let {
            if (it.contains(Constants.SHARED_PREF_GRIDVIEW_CONFIGURATION)) {
                val str = it.getString(Constants.SHARED_PREF_GRIDVIEW_CONFIGURATION, "")
                val itemType = object : TypeToken<GridViewConfig>() {}.type
                gridViewConfig = gson.fromJson(str, itemType)
            }
        }
    }

    private fun setupGridViewParams() {
        binding.apply {
            showUnvisitedColorPickerButton.setBackgroundColor(Color.parseColor(gridViewConfig.unvisitedCellColor))
            showVisitedColorPickerButton.setBackgroundColor(Color.parseColor(gridViewConfig.visitedCellColor))
            showCurrentColorPickerButton.setBackgroundColor(Color.parseColor(gridViewConfig.currentCellColor))
            showCellWallColorPickerButton.setBackgroundColor(Color.parseColor(gridViewConfig.wallColor))

            when (gridViewConfig.animationSpeed) {
                Constants.ANIMATION_SPEED_SLOW -> speedButtonGroup.check(R.id.speed_slow_button)
                Constants.ANIMATION_SPEED_NORMAL -> speedButtonGroup.check(R.id.speed_normal_button)
                Constants.ANIMATION_SPEED_FAST -> speedButtonGroup.check(R.id.speed_fast_button)
            }

            toggleCompleteAnimationSwitch.isChecked = gridViewConfig.isCompleteAnimationEnabled
        }
    }

    private fun setupPickUnvisitedCellColorButton() {
        binding.showUnvisitedColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                gridViewConfig.unvisitedCellColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        gridViewConfig.unvisitedCellColor = colorString
                        it.setBackgroundColor(Color.parseColor(colorString))
                        listener.setConfig(gridViewConfig)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun setupPickCurrentCellColorButton() {
        binding.showCurrentColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                gridViewConfig.currentCellColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        gridViewConfig.currentCellColor = colorString
                        it.setBackgroundColor(Color.parseColor(colorString))
                        listener.setConfig(gridViewConfig)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun setupPickVisitedCellColorButton() {
        binding.showVisitedColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                gridViewConfig.visitedCellColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        gridViewConfig.visitedCellColor = colorString
                        it.setBackgroundColor(Color.parseColor(colorString))
                        listener.setConfig(gridViewConfig)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun setupPickCellWallColorButton() {
        binding.showCellWallColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                gridViewConfig.wallColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        gridViewConfig.wallColor = colorString
                        it.setBackgroundColor(Color.parseColor(colorString))
                        listener.setConfig(gridViewConfig)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun saveGridViewParams() {
        sharedPreferences?.let {
            it.edit().apply {
                putString(Constants.SHARED_PREF_GRIDVIEW_CONFIGURATION, gson.toJson(gridViewConfig))
                apply()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        if (this::colorPickerDialog.isInitialized) colorPickerDialog.dismiss()
        saveGridViewParams()
    }

    companion object {
        @JvmStatic
        fun newInstance(listener: GridConfigListener): GridConfigBottomSheet {
            val fragment = GridConfigBottomSheet()
            fragment.listener = listener
            return fragment
        }
    }
}
