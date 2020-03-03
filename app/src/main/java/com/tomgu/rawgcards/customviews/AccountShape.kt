package com.tomgu.rawgcards.customviews

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.tomgu.rawgcards.R


class AccountShape(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {

    private val myPath = Path()

    val paint = Paint().also {
        it.color = resources.getColor(R.color.colorBackground)
        it.strokeWidth = 3f
        it.style = Paint.Style.FILL_AND_STROKE
    }

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, paint)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val canvasHeight = canvas!!.height.toFloat()
        val canvasWidth = canvas.width.toFloat()
        val radius = 150.0f

        val corEffect = CornerPathEffect(radius)
        paint.setPathEffect(corEffect)

        myPath.moveTo(-50f, canvasHeight + 50f)
        myPath.lineTo(0f, canvasHeight)
        myPath.lineTo(0f + (canvasWidth / 4), canvasHeight - (canvasHeight / 6))
        myPath.lineTo(0f + (canvasWidth / 2), canvasHeight)
        myPath.lineTo(canvasWidth - (canvasWidth / 4), canvasHeight)
        myPath.lineTo(canvasWidth + 50f, canvasHeight - (canvasHeight / 4))
        myPath.lineTo(canvasWidth + 50f, canvasHeight + 20f)

        canvas.drawPath(myPath, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }
}