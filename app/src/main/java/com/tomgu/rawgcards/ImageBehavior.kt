package com.tomgu.rawgcards

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.github.florent37.diagonallayout.DiagonalLayout

class ImageBehavior(context : Context, attributeSet: AttributeSet): CoordinatorLayout.Behavior<View>(context, attributeSet) {

  /*  override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is DiagonalLayout
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        if(parent == null || child == null || dependency == null)
            return false

        return true
    }

    private fun getViewOffsetForDiagonalLayout(parent: CoordinatorLayout, view: View): Float {
        var maxOffset = 0f
        val dependencies = parent.getDependencies(view)

        dependencies.forEach { dependency ->
            if(dependency is DiagonalLayout && parent.doViewsOverlap(view, dependency ))
        }
    }*/
}