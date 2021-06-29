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
import com.zhouppei.goalgo.views.GraphViewConfig
import com.zhouppei.goalgo.databinding.BottomSheetGraphConfigBinding
import com.zhouppei.goalgo.ui.colorpicker.ColorPickerDialog
import com.zhouppei.goalgo.ui.colorpicker.ColorPickerDialogListener
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

        getConfigFromSharedPref()
        setupGraphViewParams()

        binding.speedSlowButton.setOnClickListener {
            graphViewConfig.animationSpeed = Constants.ANIMATION_SPEED_SLOW
            listener.setConfig(graphViewConfig)
        }

        binding.speedNormalButton.setOnClickListener {
            graphViewConfig.animationSpeed = Constants.ANIMATION_SPEED_NORMAL
            listener.setConfig(graphViewConfig)
        }

        binding.speedFastButton.setOnClickListener {
            graphViewConfig.animationSpeed = Constants.ANIMATION_SPEED_FAST
            listener.setConfig(graphViewConfig)
        }

        binding.showLabelsSwitch.setOnCheckedChangeListener { _, isChecked ->
            graphViewConfig.isLabelVisible = isChecked
            listener.setConfig(graphViewConfig)
        }

        binding.toggleCompleteAnimationSwitch.setOnCheckedChangeListener { _, isChecked ->
            graphViewConfig.isCompleteAnimationEnabled = isChecked
            listener.setConfig(graphViewConfig)
        }

        binding.resetButton.setOnClickListener {
            graphViewConfig = GraphViewConfig()
            setupGraphViewParams()
            listener.setConfig(graphViewConfig)
        }

        setupPickCurrentStateColorButton()
        setupPickUnvisitedStateColorButton()
        setupPickVisitedStateColorButton()
        setupPickStartVertexColorButton()
        setupPickTargetVertexColorButton()
        setupPickEdgeDefaultColorButton()
        setupPickEdgeHighlightedColorButton()
    }

    private fun getConfigFromSharedPref() {
        sharedPreferences = context?.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        sharedPreferences?.let {
            if (it.contains(Constants.SHARED_PREF_GRAPHVIEW_CONFIGURATION)) {
                val str = it.getString(Constants.SHARED_PREF_GRAPHVIEW_CONFIGURATION, "")
                val itemType = object : TypeToken<GraphViewConfig>() {}.type
                graphViewConfig = gson.fromJson(str, itemType)
            }
        }
    }

    private fun setupGraphViewParams() {
        binding.apply {
            showCurrentColorPickerButton.setBackgroundColor(Color.parseColor(graphViewConfig.currentStateColor))
            showUnvisitedColorPickerButton.setBackgroundColor(Color.parseColor(graphViewConfig.unvisitedStateColor))
            showVisitedColorPickerButton.setBackgroundColor(Color.parseColor(graphViewConfig.visitedStateColor))
            showStartVertexColorPickerButton.setBackgroundColor(Color.parseColor(graphViewConfig.startVertexColor))
            showTargetVertexColorPickerButton.setBackgroundColor(Color.parseColor(graphViewConfig.targetVertexColor))
            showEdgeDefaultColorPickerButton.setBackgroundColor(Color.parseColor(graphViewConfig.edgeDefaultColor))
            showEdgeHighlightedColorPickerButton.setBackgroundColor(Color.parseColor(graphViewConfig.edgeHighlightedColor))

            when (graphViewConfig.animationSpeed) {
                Constants.ANIMATION_SPEED_SLOW -> speedButtonGroup.check(R.id.speed_slow_button)
                Constants.ANIMATION_SPEED_NORMAL -> speedButtonGroup.check(R.id.speed_normal_button)
                Constants.ANIMATION_SPEED_FAST -> speedButtonGroup.check(R.id.speed_fast_button)
            }

            showLabelsSwitch.isChecked = graphViewConfig.isLabelVisible
            toggleCompleteAnimationSwitch.isChecked = graphViewConfig.isCompleteAnimationEnabled
        }
    }

    private fun setupPickCurrentStateColorButton() {
        binding.showCurrentColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                graphViewConfig.currentStateColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        graphViewConfig.currentStateColor = colorString
                        it.setBackgroundColor(Color.parseColor(colorString))
                        listener.setConfig(graphViewConfig)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun setupPickUnvisitedStateColorButton() {
        binding.showUnvisitedColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                graphViewConfig.unvisitedStateColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        graphViewConfig.unvisitedStateColor = colorString
                        it.setBackgroundColor(Color.parseColor(colorString))
                        listener.setConfig(graphViewConfig)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun setupPickVisitedStateColorButton() {
        binding.showVisitedColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                graphViewConfig.visitedStateColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        graphViewConfig.visitedStateColor = colorString
                        it.setBackgroundColor(Color.parseColor(colorString))
                        listener.setConfig(graphViewConfig)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun setupPickEdgeDefaultColorButton() {
        binding.showEdgeDefaultColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                graphViewConfig.edgeDefaultColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        graphViewConfig.edgeDefaultColor = colorString
                        it.setBackgroundColor(Color.parseColor(colorString))
                        listener.setConfig(graphViewConfig)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun setupPickEdgeHighlightedColorButton() {
        binding.showEdgeHighlightedColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                graphViewConfig.edgeHighlightedColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        graphViewConfig.edgeHighlightedColor = colorString
                        it.setBackgroundColor(Color.parseColor(colorString))
                        listener.setConfig(graphViewConfig)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun setupPickStartVertexColorButton() {
        binding.showStartVertexColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                graphViewConfig.startVertexColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        graphViewConfig.startVertexColor = colorString
                        it.setBackgroundColor(Color.parseColor(colorString))
                        listener.setConfig(graphViewConfig)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun setupPickTargetVertexColorButton() {
        binding.showTargetVertexColorPickerButton.setOnClickListener {
            colorPickerDialog = ColorPickerDialog(
                requireContext(),
                graphViewConfig.targetVertexColor,
                object : ColorPickerDialogListener {
                    override fun setSelectedColorString(colorString: String) {
                        graphViewConfig.targetVertexColor = colorString
                        it.setBackgroundColor(Color.parseColor(colorString))
                        listener.setConfig(graphViewConfig)
                    }
                }
            )
            colorPickerDialog.show()
        }
    }

    private fun saveGraphViewConfig() {
        sharedPreferences?.let {
            it.edit().apply {
                putString(Constants.SHARED_PREF_GRAPHVIEW_CONFIGURATION, gson.toJson(graphViewConfig))
                apply()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        if (this::colorPickerDialog.isInitialized) colorPickerDialog.dismiss()
        saveGraphViewConfig()
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
