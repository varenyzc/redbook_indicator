package top.varenyzc.redbookindicator

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.TransitionDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import kotlin.math.abs
import kotlin.math.roundToInt

class DotIndicatorView @JvmOverloads constructor(
    context: Context, attr: AttributeSet?, def: Int = 0
): LinearLayout(context, attr, def) {
    
    companion object {
        private const val MAX_DOT_SIZE = 5
    }

    private val normalSize = 5.dp

    private val smallSize = 3.dp

    private var realPos: Int = 0
    private var curPos: Int = 0

    private var imageSize = 0

    private val dotList = arrayListOf<ImageView>()

    private val res = R.drawable.indicator_bg

    private fun createDotView(index: Int): ImageView {
        val lp = LayoutParams(normalSize, normalSize)
        if (index > 0) {
            lp.setMargins(smallSize, 0, 0, 0)
        }
        val imageView = ImageView(context)
        imageView.setImageResource(res)
        imageView.layoutParams = lp
        return imageView
    }

    private fun jumpToIndex(index: Int) {
        if (index == realPos) return
        if (index !in 0 until imageSize) return

        var targetTransition = 0
        if (imageSize <= MAX_DOT_SIZE) {
            curPos = index
        } else {
            when (index) {
                in imageSize - 4 until imageSize -> {
                    targetTransition = (imageSize - 5) * (normalSize + smallSize)
                    curPos = index - imageSize + 5
                    shrinkDot(imageSize - 5)
                    for (i in imageSize - 4 until imageSize) {
                        expandDot(i)
                    }
                }
                in 2 until imageSize - 4 -> {
                    val leftIndex = index - 1
                    targetTransition = (normalSize + smallSize) * leftIndex
                    this.curPos = 1
                    shrinkDot(leftIndex)

                    val rightIndex = index + 3
                    shrinkDot(rightIndex)

                    for (i in index until rightIndex) {
                        dotList[i].scaleX = 1f
                        dotList[i].scaleY = 1f
                    }
                }
                in 0..2 -> {
                    curPos = index
                    for (i in 0 until (MAX_DOT_SIZE - 1)) {
                        expandDot(i)
                    }
                    shrinkDot(MAX_DOT_SIZE - 1)
                    targetTransition = 0
                }
            }
            val x = (-targetTransition) - dotList[0].x
            for (i in 0 until imageSize) {
                val imageView = dotList[i]
                imageView.x = imageView.x + x
            }
        }
        val drawable = dotList[realPos].drawable
        (drawable as? TransitionDrawable)?.reverseTransition(0)
        val drawable2 = dotList[index].drawable
        (drawable2 as? TransitionDrawable)?.startTransition(0)
        realPos = index
    }

    private fun expandDot(index: Int) {
        if (index < dotList.size) {
            dotList[index].scaleX = 1f
            dotList[index].scaleY = 1f
        }
    }

    private fun shrinkDot(index: Int) {
        if (index < dotList.size) {
            dotList[index].scaleX = 0.6f
            dotList[index].scaleY = 0.6f
        }
    }

    private fun stepBack() {
        val drawable = dotList[realPos].drawable
        (drawable as? TransitionDrawable)?.reverseTransition(200)
        val drawable2 = dotList[realPos - 1].drawable
        (drawable2 as? TransitionDrawable)?.startTransition(200)
        if (curPos == 1 && realPos != 1) {
            playAnimation(false)
            if (realPos != 2) {
                startDotAnimationForUnSelected(realPos - 2)
            }
            startDotAnimationForSelected(realPos - 1)
            startDotAnimationForUnSelected(realPos + 2)
        } else {
            curPos--
        }
        realPos--
    }

    private fun stepNext() {
        val drawable = dotList[realPos].drawable
        (drawable as? TransitionDrawable)?.reverseTransition(200)
        val drawable2 = dotList[realPos + 1].drawable
        (drawable2 as? TransitionDrawable)?.startTransition(200)
        val i = curPos
        if (i == 3 && realPos != imageSize - 2) {
            playAnimation(true)
            if (realPos != imageSize - 3) {
                startDotAnimationForUnSelected(realPos + 2)
            }
            startDotAnimationForSelected(realPos + 1)
            startDotAnimationForUnSelected(realPos - 2)
        } else {
            curPos = i + 1
        }
        realPos++
    }

    private fun startDotAnimationForSelected(index: Int) {
        if (index !in 0 until dotList.size) return
        val ofFloat = ObjectAnimator.ofFloat(dotList[index], "scaleX", 0.6f, 1f)
        val ofFloat2 = ObjectAnimator.ofFloat(dotList[index], "scaleY", 0.6f, 1f)
        val animatorSet = AnimatorSet()
        animatorSet.play(ofFloat).with(ofFloat2)
        animatorSet.duration = 200L
        animatorSet.start()
    }

    private fun startDotAnimationForUnSelected(index: Int) {
        if (index !in 0 until dotList.size) return
        val ofFloat = ObjectAnimator.ofFloat(dotList[index], "scaleX", 1.0f, 0.6f)
        val ofFloat2 = ObjectAnimator.ofFloat(dotList[index], "scaleY", 1.0f, 0.6f)
        val animatorSet = AnimatorSet()
        animatorSet.play(ofFloat).with(ofFloat2)
        animatorSet.duration = 200L
        animatorSet.start()
    }

    private fun playAnimation(forward: Boolean) {
        val animatorSet = AnimatorSet()
        val transition = if (forward) {
            - smallSize - normalSize
        } else {
            smallSize + normalSize
        }
        for (i in 0 until imageSize) {
            animatorSet.playTogether(ObjectAnimator.ofFloat(dotList[i], "translationX", dotList[i].translationX, dotList[i].translationX + transition))
        }
        animatorSet.duration = 200L
        animatorSet.start()
    }

    fun setCount(count: Int) {
        if (count <= 1) {
            visibility = View.GONE
            return
        }
        visibility = View.VISIBLE

        removeAllViews()
        dotList.clear()
        curPos = 0
        realPos = 0
        imageSize = count
        val width = if (count >= MAX_DOT_SIZE) {
            normalSize * MAX_DOT_SIZE + (MAX_DOT_SIZE - 1) * smallSize
        } else {
            (count - 1) * smallSize + normalSize * count
        }
        layoutParams.width = width

        for (i in 0 until count) {
            val dot = createDotView(i)
            addView(dot)
            dotList.add(dot)
        }
        val drawable = dotList[0].drawable
        (drawable as? TransitionDrawable)?.startTransition(0)
        if (count <= MAX_DOT_SIZE) return
        shrinkDot(MAX_DOT_SIZE - 1)
    }

    fun setSelectedIndex(index: Int) {
        if (index == realPos) return
        if (index !in 0 until imageSize) {
            return
        }

        if (abs(index - realPos) > 1) {
            jumpToIndex(index)
        } else if (imageSize <= MAX_DOT_SIZE) {
            val drawable = dotList[curPos].drawable
            (drawable as? TransitionDrawable)?.reverseTransition(200)
            val drawable2 = dotList[index].drawable
            (drawable2 as? TransitionDrawable)?.startTransition(200)
            if (index > realPos) {
                realPos++
                curPos++
                return
            }
            this.realPos = realPos - 1
            this.curPos--
        } else if (index > realPos) {
            stepNext()
        } else {
            stepBack()
        }
    }

}

inline val Number.dpFloat
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        toFloat(),
        Resources.getSystem().displayMetrics
    )


inline val Number.dp
    get() = dpFloat.roundToInt()