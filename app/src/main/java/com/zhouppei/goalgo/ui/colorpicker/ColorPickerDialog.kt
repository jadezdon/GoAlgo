package com.zhouppei.goalgo.ui.colorpicker

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zhouppei.goalgo.R
import com.zhouppei.goalgo.databinding.DialogColorPickerBinding
import com.zhouppei.goalgo.utils.Constants
import java.util.*

class ColorPickerDialog(
    context: Context,
    private var colorSelected: String,
    private val colorPickerDialogListener: ColorPickerDialogListener
) : AppCompatDialog(context) {

    private var _binding: DialogColorPickerBinding? = null
    private val binding get() = _binding!!

    private var colorPresets = MutableList(10) { "" }
    private var sharedPreferences: SharedPreferences? = null
    private lateinit var colorListAdapter: ColorListAdapter
    private val gson = Gson()

    companion object {
        private val TAG = ColorPickerDialog::class.qualifiedName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DialogColorPickerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val metrics = context.resources.displayMetrics
        window?.setLayout((metrics.widthPixels * 0.8).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawableResource(R.drawable.background_dialog)

        setProgress(colorSelected.replace("#", "").toUpperCase(Locale.getDefault()))
        binding.colorStringEditText.setText(
            colorSelected.replace("#", "").toUpperCase(Locale.getDefault())
        )
        binding.colorPreviewButton.setBackgroundColor(Color.parseColor(colorSelected))

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.applyButton.setOnClickListener {
            colorPickerDialogListener.setSelectedColorString(colorSelected)
            saveColorPresets(colorSelected)
            dismiss()
        }

        binding.colorStringEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let { setProgress(it.toString()) }
            }
        })

        setupSeekBars()
        setupColorPresets()
    }

    private fun setupSeekBars() {
        binding.apply {
            colorASeekBar.setOnSeekBarChangeListener(ColorSeekBarChangeListener())
            colorRSeekBar.setOnSeekBarChangeListener(ColorSeekBarChangeListener())
            colorGSeekBar.setOnSeekBarChangeListener(ColorSeekBarChangeListener())
            colorBSeekBar.setOnSeekBarChangeListener(ColorSeekBarChangeListener())
        }
    }

    private fun setupColorPresets() {
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        sharedPreferences?.let {
            if (it.contains(Constants.SHARED_PREF_COLOR_PRESET)) {
                val str = it.getString(Constants.SHARED_PREF_COLOR_PRESET, "")
                val itemType = object : TypeToken<List<String>>() {}.type
                colorPresets = gson.fromJson(str, itemType)
            }
        }

        colorListAdapter = ColorListAdapter(
            object : ColorClickListener {
                override fun onColorClick(color: String) {
                    colorPickerDialogListener.setSelectedColorString(color)
                    saveColorPresets(color)
                    dismiss()
                }
            }
        )

        binding.colorPresetsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = colorListAdapter
        }

        colorListAdapter.submitList(colorPresets)
    }

    private fun saveColorPresets(lastColor: String) {
        if (colorPresets.remove(lastColor)) {
            colorPresets.add(0, lastColor)
        } else {
            for (index in colorPresets.size - 1 downTo 1) {
                colorPresets[index] = colorPresets[index - 1]
            }
            colorPresets[0] = lastColor
        }

        sharedPreferences?.let {
            it.edit().apply {
                putString(Constants.SHARED_PREF_COLOR_PRESET, gson.toJson(colorPresets))
                apply()
            }
        }
    }

    inner class ColorSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            colorSelected = getColorString()
            binding.colorStringEditText.setText(
                colorSelected.replace("#", "").toUpperCase(Locale.getDefault())
            )
            binding.colorPreviewButton.setBackgroundColor(Color.parseColor(colorSelected))
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    private fun setProgress(s: String) {
        if (s.length == 6) {
            binding.apply {
                colorASeekBar.progress = 255
                colorRSeekBar.progress = Integer.parseInt(s.substring(0..1), 16)
                colorGSeekBar.progress = Integer.parseInt(s.substring(2..3), 16)
                colorBSeekBar.progress = Integer.parseInt(s.substring(4..5), 16)
            }
        } else if (s.length == 8) {
            binding.apply {
                colorASeekBar.progress = Integer.parseInt(s.substring(0..1), 16)
                colorRSeekBar.progress = Integer.parseInt(s.substring(2..3), 16)
                colorGSeekBar.progress = Integer.parseInt(s.substring(4..5), 16)
                colorBSeekBar.progress = Integer.parseInt(s.substring(6..7), 16)
            }
        }
    }

    private fun getColorString(): String {
        var colorA =
            Integer.toHexString(((255 * binding.colorASeekBar.progress) / binding.colorASeekBar.max))
        if (colorA.length == 1) colorA = "0$colorA"

        var colorR =
            Integer.toHexString(((255 * binding.colorRSeekBar.progress) / binding.colorRSeekBar.max))
        if (colorR.length == 1) colorR = "0$colorR"

        var colorG =
            Integer.toHexString(((255 * binding.colorGSeekBar.progress) / binding.colorGSeekBar.max))
        if (colorG.length == 1) colorG = "0$colorG"

        var colorB =
            Integer.toHexString(((255 * binding.colorBSeekBar.progress) / binding.colorBSeekBar.max))
        if (colorB.length == 1) colorB = "0$colorB"

        return "#$colorA$colorR$colorG$colorB"
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        super.setOnDismissListener(listener)
        _binding = null
    }
}

interface ColorPickerDialogListener {
    fun setSelectedColorString(colorString: String)
}