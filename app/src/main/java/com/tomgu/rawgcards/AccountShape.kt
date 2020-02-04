package com.tomgu.rawgcards

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


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
        val canvasWidth = canvas!!.width.toFloat()


        myPath.reset()
        myPath.moveTo(0f, canvasHeight)
        myPath.lineTo(canvasWidth - (canvasWidth / 4), canvasHeight/2)
        myPath.lineTo(0f, 0f)
        myPath.lineTo(0f, 0f + (canvasHeight / 3))              //Move
        myPath.lineTo(0f + (canvasWidth / 4), canvasHeight/2)   //Move
        myPath.lineTo(0f, canvasHeight - (canvasHeight / 3))     //Move
        myPath.lineTo(0f, canvasHeight)


        canvas?.drawPath(myPath, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }
}