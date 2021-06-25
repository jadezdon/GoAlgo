package com.zhouppei.goalgo.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zhouppei.goalgo.databinding.BottomSheetOptionsBinding

class OptionsBottomSheet: BottomSheetDialogFragment() {
    private var _binding: BottomSheetOptionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: OptionsBottomSheetListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomSheetOptionsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.speedSlowButton.setOnClickListener { listener.onChangeSpeed(1600L) }
        binding.speedNormalButton.setOnClickListener { listener.onChangeSpeed(800L) }
        binding.speedFastButton.setOnClickListener { listener.onChangeSpeed(400L) }

        binding.showValuesSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            listener.onShowValuesChanged(isChecked)
        }

        binding.showIndexesSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            listener.onShowIndexesChanged(isChecked)
        }
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
}