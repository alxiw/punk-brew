package io.github.alxiw.simplesearchview.listeners

import android.view.View
import io.github.alxiw.simplesearchview.utils.SimpleAnimationUtils

internal abstract class SimpleAnimationListener : SimpleAnimationUtils.AnimationListener {
    override fun onAnimationStart(view: View): Boolean = false
    override fun onAnimationEnd(view: View): Boolean = false
    override fun onAnimationCancel(view: View): Boolean = false
}
