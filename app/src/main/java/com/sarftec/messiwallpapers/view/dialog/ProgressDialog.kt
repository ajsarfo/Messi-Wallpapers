package com.sarftec.messiwallpapers.view.dialog

import android.app.Activity
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import com.sarftec.messiwallpapers.databinding.LayoutProgressDialogBinding

class ProgressDialog(
    parent: ViewGroup,
    activity: Activity,
    private val onCancel: () -> Unit,
    private val onFinished: () -> Unit
) : AlertDialog(parent.context) {

    private val layoutBinding = LayoutProgressDialogBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    )

    init {
        val displayRect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(displayRect)
        layoutBinding.root.apply {
            minimumWidth = (displayRect.width() * 1f).toInt()
            minimumHeight = (displayRect.height() * 1f).toInt()
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setView(layoutBinding.root)
        layoutBinding.cancelUpload.setOnClickListener {
            onCancel()
            dismiss()
        }
        layoutBinding.loadingSpinner.playAnimation()
        setCancelable(false)
    }

    override fun show() {
        layoutBinding.progress.text = "0% Completed"
        super.show()
    }

    fun setProgress(progress: Int) {
        layoutBinding.progress.text = "$progress% Completed"
        if(progress == 100) {
            onFinished()
            dismiss()
        }
    }
}