package kim.bifrost.rain.processbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import kim.bifrost.rain.processbar.PaintBuilder.Companion.buildPaint
import kotlin.properties.Delegates

/**
 * kim.bifrost.rain.processbar.ProcessBarView
 * ProcessBarView
 *
 * @author 寒雨
 * @since 2022/3/8 13:05
 **/
class ProcessBarView : View {

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    // 百分比
    var percent by Delegates.observable(0f) { _, _, _ ->
        invalidate()
    }

//    private var mWidth by Delegates.notNull<Float>()
//    private var mHeight by Delegates.notNull<Float>()
    private val mPaint = buildPaint {
        style = Paint.Style.FILL
    }

    private val Float.dp: Float
        get() = this / (context.resources.displayMetrics.density + 0.5f)

    override fun onDraw(canvas: Canvas?) {
        mPaint.color = Color.WHITE
        canvas!!.drawRect(0f, 0f, width.toFloat(), height.toFloat(), mPaint)
        mPaint.color = BAR_COLOR
        canvas.drawRect(0f, 0f, width.toFloat() * percent, height.toFloat(), mPaint)
    }

    companion object {
        @ColorInt
        const val BAR_COLOR = 0x64B5F600
    }
}