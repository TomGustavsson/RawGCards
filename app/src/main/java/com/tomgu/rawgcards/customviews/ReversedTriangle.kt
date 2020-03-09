package com.tomgu.rawgcards.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.tomgu.rawgcards.R

class ReversedTriangle(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {

    private val myPath = Path()
    var offset : Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    private val paint = Paint().also {
        it.color = resources.getColor(R.color.colorBackground)
        it.strokeWidth = 3f
        it.style = Paint.Style.FILL_AND_STROKE
    }

    override fun onDraw(canvas: Canvas?) {

        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()

        val animatedY = canvasHeight - (canvasHeight * offset)

        myPath.reset()
        myPath.moveTo(canvasWidth, 0f)
        myPath.lineTo(0f, animatedY)
        myPath.lineTo(0f,0f)
        myPath.lineTo(canvasWidth, 0f)
        myPath.close()

        canvas?.drawPath(myPath, paint)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }
}