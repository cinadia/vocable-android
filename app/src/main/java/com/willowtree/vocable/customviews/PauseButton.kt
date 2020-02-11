package com.willowtree.vocable.customviews

import android.content.Context
import android.util.AttributeSet
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.willowtree.vocable.R

/**
 * A subclass of VocableButton that will toggle the pause state for head tracking
 */
class PauseButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : VocableButton(context, attrs, defStyle) {

    private val liveIsPaused = MutableLiveData<Boolean>().apply {
        value = false
    }
    var isPaused: LiveData<Boolean> = liveIsPaused

    override fun onPointerEnter() {
        if (isPaused.value == false) {
            setText(R.string.button_pause)
        }
        super.onPointerEnter()
    }

    override fun onPointerExit() {
        super.onPointerExit()
        if (isPaused.value == false) {
            text = null
        }
    }

    override fun performAction() {
        liveIsPaused.value?.let {
            liveIsPaused.value = !it
        }
        if (liveIsPaused.value == true) {
            setText(R.string.button_resume)
        } else {
            setText(R.string.button_pause)
        }
    }
}