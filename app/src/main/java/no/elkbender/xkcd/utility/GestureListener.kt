package no.elkbender.xkcd.utility

import android.view.MotionEvent
import android.view.GestureDetector

open class GestureListener : GestureDetector.SimpleOnGestureListener() {
    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        val direction = getDirection(e1.x, e1.y, e2.x, e2.y)
        return onSwipe(direction)
    }

    open fun onSwipe(direction: Direction): Boolean {
        return false
    }

    private fun getDirection(x1: Float, y1: Float, x2: Float, y2: Float): Direction {
        val angle = getAngle(x1, y1, x2, y2)
        return Direction.fromAngle(angle)
    }

    private fun getAngle(x1: Float, y1: Float, x2: Float, y2: Float): Double {
        val rad = Math.atan2((y1 - y2).toDouble(), (x2 - x1).toDouble()) + Math.PI
        return (rad * 180 / Math.PI + 180) % 360
    }

    enum class Direction {
        Up, Down, Left, Right;

        companion object {
            fun fromAngle(angle: Double): Direction {
                return when {
                    (inRange(angle, 45f, 135f)) -> Up
                    (inRange(angle, 0f, 45f) || inRange(angle, 315f, 360f)) -> Right
                    (inRange(angle, 225f, 315f)) -> Down
                    else -> Left
                }
            }

            private fun inRange(angle: Double, init: Float, end: Float): Boolean {
                return angle >= init && angle < end
            }
        }
    }
}