package com.zhouppei.goalgo.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialog
import com.zhouppei.goalgo.R
import com.zhouppei.goalgo.databinding.DialogDescriptionBinding


/**
 * @author zhouppei
 * Created 7/6/2021 at 09:37
 */
class DescriptionDialog(
    context: Context,
    private var sourceCode: String,
    private var description: String
) : AppCompatDialog(context) {

    private var _binding: DialogDescriptionBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DialogDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val metrics = context.resources.displayMetrics
        window?.setLayout((metrics.widthPixels * 0.8).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawableResource(R.drawable.background_dialog)

        if (description.isNotBlank()) {
            binding.descriptionTextView.text = if (Build.VERSION.SDK_INT >= 24)
                Html.fromHtml("<pre>$description</pre>".replace("    ", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"), Html.FROM_HTML_MODE_LEGACY)
            else
                Html.fromHtml("<pre>$description</pre>".replace("    ", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"))
        } else {
            binding.descriptionTextView.visibility = View.GONE
        }

        if (sourceCode.isNotBlank()) {
            binding.sourceCodeTextView.text = if (Build.VERSION.SDK_INT >= 24)
                Html.fromHtml("<pre>$sourceCode</pre>".replace("    ", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"), Html.FROM_HTML_MODE_LEGACY)
            else
                Html.fromHtml("<pre>$sourceCode</pre>".replace("    ", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"))
        } else {
            binding.sourceCodeTextView.visibility = View.GONE
        }

        binding.closeButton.setOnClickListener { dismiss() }
    }
}