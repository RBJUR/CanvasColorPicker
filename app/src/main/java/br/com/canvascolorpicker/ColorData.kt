package br.com.canvascolorpicker


class ColorData {

    var startDegree: Float = 0.toFloat()
    var endDegree: Float = 0.toFloat()
    var sweep: Float = 0.toFloat()
    var color: Int = 0

    private var targetStartDegree: Float = 0.toFloat()
    private var targetEndDegree: Float = 0.toFloat()

    private var velocity = 5

    internal val isAtRest: Boolean
        get() = startDegree == targetStartDegree && endDegree == targetEndDegree


    constructor(percent: Float, color: Int) {
        this.sweep = percent * 360 / 100
        this.color = color
    }


    internal constructor(startDegree: Float, endDegree: Float, targetPie: ColorData) {
        this.startDegree = startDegree
        this.endDegree = endDegree
        targetStartDegree = targetPie.startDegree
        targetEndDegree = targetPie.endDegree
        this.sweep = targetPie.sweep
        this.color = targetPie.color
    }

    internal fun setDegree(startDegree: Float, endDegree: Float) {
        this.startDegree = startDegree
        this.endDegree = endDegree
    }

    internal fun update() {
        this.startDegree = updateSelf(startDegree, targetStartDegree, velocity)
        this.endDegree = updateSelf(endDegree, targetEndDegree, velocity)
        this.sweep = endDegree - startDegree
    }

    private fun updateSelf(origin: Float, target: Float, velocity: Int): Float {
        var origin = origin
        if (origin < target) {
            origin += velocity.toFloat()
        } else if (origin > target) {
            origin -= velocity.toFloat()
        }
        if (Math.abs(target - origin) < velocity) {
            origin = target
        }
        return origin
    }
}
