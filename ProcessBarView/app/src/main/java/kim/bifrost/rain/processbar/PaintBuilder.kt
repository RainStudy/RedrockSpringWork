package kim.bifrost.rain.processbar

import android.graphics.Paint

/**
 * kim.bifrost.rain.processbar.PaintBuilder
 * ProcessBarView
 * Paint Builder
 * 链式构造Paint
 *
 * @author 寒雨
 * @since 2022/3/8 13:20
 **/
class PaintBuilder private constructor(){
    private val paint = Paint()

    fun color(color: Int): PaintBuilder {
        paint.color = color
        return this
    }

    fun style(style: Paint.Style): PaintBuilder {
        paint.style = style
        return this
    }

    fun strokeWidth(width: Float): PaintBuilder {
        paint.strokeWidth = width
        return this
    }

    fun build(): Paint {
        return paint
    }

    companion object {
        fun newBuilder(): PaintBuilder {
            return PaintBuilder()
        }

        fun buildPaint(builder: Paint.() -> Unit): Paint {
            return Paint().apply(builder)
        }
    }
}