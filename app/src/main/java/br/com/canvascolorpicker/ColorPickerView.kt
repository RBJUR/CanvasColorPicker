package br.com.canvascolorpicker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.ArrayList

class ColorPickerView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val colorList = intArrayOf(
        Color.parseColor("#FEFE33"),
        Color.parseColor("#FABC02"),
        Color.parseColor("#FB9902"),
        Color.parseColor("#FD5308"),
        Color.parseColor("#FE2712"),
        Color.parseColor("#A7194B"),
        Color.parseColor("#8601AF"),
        Color.parseColor("#3D01A4"),
        Color.parseColor("#0247FE"),
        Color.parseColor("#0392CE"),
        Color.parseColor("#66B032"),
        Color.parseColor("#D0EA2B")
    )
    private val NO_SELECTED_INDEX = -999

    private var cirPaint: Paint = Paint()
    private var centerPaint: Paint = Paint()
    private var pieCenterPoint = Point()
    private var cirRect: RectF = RectF()

    private var mViewWidth: Int = 0
    private var mViewHeight: Int = 0
    private var margin: Int = 0
    private var mRadius: Int = 0

    private val colorDataList: ArrayList<ColorData> = arrayListOf()
    private var selectedIndex = NO_SELECTED_INDEX
    private var selectedColor = Color.RED
    private var onColorClickListener: OnColorPickerClickListener? = null


    interface OnColorPickerClickListener {
        fun onColorClicked(index: Int)
    }

    private val animator = object : Runnable {
        override fun run() {
            var needNewFrame = false
            for (color in colorDataList) {
                color.update()
                if (!color.isAtRest) {
                    needNewFrame = true
                }
            }
            if (needNewFrame) {
                postDelayed(this, 10)
            }
            invalidate()
        }
    }

    init {
        //Setup paint
        cirPaint.isAntiAlias = true
        cirPaint.color = Color.GRAY
        cirPaint.style = Paint.Style.STROKE
        cirPaint.strokeWidth = 120f

    }

    fun start() {

        val helperList = arrayListOf<ColorData>()
        val percent = 100f / colorList.size
        for (color in colorList) {
            helperList.add(ColorData(percent, color))
        }

        initColors(helperList)
        colorDataList.clear()

        if (helperList.isNotEmpty()) {
            for (color in helperList) {
                colorDataList.add(ColorData(color.startDegree, color.endDegree, color))
            }
        } else {
            colorDataList.clear()
        }
        removeCallbacks(animator)
        post(animator)


    }

    private fun initColors(helperList: ArrayList<ColorData>) {
        var totalAngel = 270f
        for (color in helperList) {
            color.setDegree(totalAngel, totalAngel + color.sweep)
            totalAngel += color.sweep
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (colorDataList.isEmpty()) {
            return
        }

        var index = 0
        for (color in colorDataList) {
            val rect = cirRect
            cirPaint.color = color.color
            canvas.drawArc(rect, color.startDegree, color.sweep, false, cirPaint)

            index++
        }

        centerPaint.color = selectedColor
        centerPaint.style = Paint.Style.FILL
        canvas.drawCircle(mViewWidth / 2f, mViewHeight / 2f, 220f, centerPaint)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mViewWidth = measureWidth(widthMeasureSpec)
        mViewHeight = measureHeight(heightMeasureSpec)
        margin = mViewWidth / 12
        mRadius = mViewWidth / 2 - margin
        pieCenterPoint.set(mRadius + margin, mRadius + margin)
        cirRect.set(
            (pieCenterPoint.x - mRadius).toFloat(),
            (pieCenterPoint.y - mRadius).toFloat(),
            (pieCenterPoint.x + mRadius).toFloat(),
            (pieCenterPoint.y + mRadius).toFloat()
        )

        setMeasuredDimension(mViewWidth, mViewHeight)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
            selectedIndex = findPointAt(event.x.toInt(), event.y.toInt())
            onColorClickListener?.let {
                onColorClickListener!!.onColorClicked(selectedIndex)
            }
            selectedColor = colorList[selectedIndex]
            postInvalidate()
        }

        performClick()
        return false
    }


    override fun performClick(): Boolean {
        super.performClick()
        return true
    }


    private fun findPointAt(x: Int, y: Int): Int {
        var degree = Math.atan2(
            (x - pieCenterPoint.x).toDouble(),
            (y - pieCenterPoint.y).toDouble()
        ) * 180 / Math.PI
        degree = -(degree - 180) + 270
        for ((index, data) in colorDataList.withIndex()) {
            if (degree >= data.startDegree && degree <= data.endDegree) {
                return index
            }
        }
        return NO_SELECTED_INDEX
    }

    private fun measureWidth(measureSpec: Int): Int {
        val preferred = 3
        return getMeasurement(measureSpec, preferred)
    }


    private fun measureHeight(measureSpec: Int): Int {
        val preferred = mViewWidth
        return getMeasurement(measureSpec, preferred)
    }

    private fun getMeasurement(measureSpec: Int, preferred: Int): Int {
        val specSize = View.MeasureSpec.getSize(measureSpec)
        val measurement: Int

        measurement = when {
            View.MeasureSpec.getMode(measureSpec) == View.MeasureSpec.EXACTLY -> specSize
            View.MeasureSpec.getMode(measureSpec) == View.MeasureSpec.AT_MOST -> Math.min(preferred, specSize)
            else -> preferred
        }
        return measurement
    }
}