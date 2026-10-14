package ru.yandexpraktikum.cardsanimation.views

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.cardview.widget.CardView
import ru.yandexpraktikum.cardsanimation.R
import ru.yandexpraktikum.cardsanimation.model.CardData

class AnimatedCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val cardView: CardView
    private val cardImageView: ImageView

    init {
        LayoutInflater.from(context).inflate(R.layout.card_view, this, true)

        cardView = this.getChildAt(0) as CardView
        cardImageView = findViewById(R.id.cardImage)

        pivotX = width / 2f
        pivotY = height.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        pivotX = w / 2f
        pivotY = h.toFloat()
    }

    fun setCardData(cardData: CardData) {
        cardImageView.setImageResource(cardData.imageResId)
    }

    fun setStackPosition(index: Int) {
        cardView.cardElevation = (4 + index * 1).toFloat() * resources.displayMetrics.density
    }

    // TODO: [Задание 1] Добавьте методы для анимации
    // Подсказка: используйте ObjectAnimator для плавной анимации

    private var rotationAnimator: ObjectAnimator? = null

    // TODO: [Задание 1] Добавьте метод для анимации поворота карты (чтобы был плавный эффект раскрытия/закрытия колоды)

    fun animateToRotation(targetRotation: Float, duration: Long = 300) {
        rotationAnimator?.cancel()
        rotationAnimator = ObjectAnimator.ofFloat(this, View.ROTATION, targetRotation).apply {
            this.duration = duration
            start()
        }
    }

    // TODO: [Задание 5, шаг 1] Добавьте метод для анимации перетасовки карт (первым шагом нижняя карта двигается вправо)

    fun moveCardRight(onComplete: (() -> Unit)? = null) {
        val moveDistance = 50f * resources.displayMetrics.density
        val currentRotationRad = Math.toRadians(rotation.toDouble())

        val deltaX = moveDistance * Math.cos(currentRotationRad).toFloat()
        val deltaY = moveDistance * Math.sin(currentRotationRad).toFloat()

        val currentX = x
        val currentY = y

        val animatorX = ObjectAnimator.ofFloat(this, "x", currentX, currentX + deltaX)
        val animatorY = ObjectAnimator.ofFloat(this, "y", currentY, currentY + deltaY)

        val animatorSet = android.animation.AnimatorSet().apply {
            playTogether(animatorX, animatorY)
            duration = 300
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onComplete?.invoke()
                }
            })
        }

        animatorSet.start()
    }

    // TODO: [Задание 5, шаг 2] Добавьте метод для анимации выдвижения нижней карты наверх
    // fun moveCardToTop(onComplete: (() -> Unit)? = null) { ... }

    // TODO: [Задание 5, шаг 3] Добавьте анимацию перемещения всей колоды карты в желаемую позицию
    // fun adjustToFinalPosition(finalRotation: Float, finalZOrder: Int, onComplete: (() -> Unit)? = null) { ... }
} 