package com.anwesh.uiprojects.linkedgridballview

/**
 * Created by anweshmishra on 02/07/18.
 */

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.MotionEvent

val GRID_NODES : Int = 5

class LinkedGridBallView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var j : Int = 0, var prevScale : Float = 0f, var dir : Float = 0f) {

        val scales : Array<Float> = arrayOf(0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += 0.1f * dir
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j += dir.toInt()
                if (j == scales.size || j == -1) {
                    j -= dir.toInt()
                    dir = 0f
                    prevScale = scales[j]
                    stopcb(prevScale)
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }

        fun update(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch (ex : Exception) {

                }
            }
        }
    }

    data class GridNode (var i : Int, val state : State = State()) {

        private var next : GridNode? = null

        private var prev : GridNode? = null

        fun update(stopcb : (Int, Float) -> Unit) {
            state.update {
                stopcb(i, it)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun addNeighbor() {
            if (i < GRID_NODES - 1) {
                next = GridNode(i + 1)
                next?.prev = this
            }
        }

        fun getNext(dir : Int, cb : () -> Unit) : GridNode {
            var curr : GridNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }

        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val xGap : Float = (0.8f * w) / GRID_NODES
            val yGap : Float = (0.8f * h) / GRID_NODES
            val r : Float = Math.min(w, h) / 30
            paint.color = Color.parseColor("#311B92")
            paint.strokeWidth = Math.min(w, h) / 80
            paint.strokeCap = Paint.Cap.ROUND
            for (i in 0..GRID_NODES - 1) {
                canvas.save()
                canvas.translate(this.i * xGap, this.i * yGap + yGap * this.state.scales[0])
                canvas.drawCircle(0f, 0f, r, paint)
                if (i != GRID_NODES - 1) {
                    canvas.drawLine(0f, 0f, xGap * this.state.scales[1], 0f, paint)
                }
                canvas.restore()
            }
        }
    }

    data class LinkedGridBall(var i : Int) {

        private var curr : GridNode = GridNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Int, Float) -> Unit) {
            curr.update {j, scale ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                stopcb(j, scale)
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }

    data class Renderer(var view : LinkedGridBallView) {

        private var animator : Animator = Animator(view)

        private var linkedGridBall : LinkedGridBall = LinkedGridBall(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            linkedGridBall.draw(canvas, paint)
            animator.update {
                linkedGridBall.update {j, scale ->
                    animator.stop()
                    when(scale) {
                        1f -> {

                        }
                    }
                }
            }
        }

        fun handleTap() {
            linkedGridBall.startUpdating {
                animator.start()
            }
        }
    }
}