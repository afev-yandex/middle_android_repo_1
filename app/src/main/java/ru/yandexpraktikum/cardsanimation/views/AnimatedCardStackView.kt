package ru.yandexpraktikum.cardsanimation.views

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.Toast
import ru.yandexpraktikum.cardsanimation.model.CardData

class AnimatedCardStackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var cardDataList: List<CardData> = emptyList()
    private val cards = mutableListOf<AnimatedCardView>()
    private var isRotated = false

    fun setCards(newCardDataList: List<CardData>) {
        cardDataList = newCardDataList
        setupCards()
    }

    private fun setupCards() {
        clearCards()
        cardDataList.forEachIndexed { index, cardData ->
            val cardView = AnimatedCardView(context).apply {
                setCardData(cardData)
                setStackPosition(index)
            }
            cards.add(cardView)
            addView(cardView)
        }
        // Возврат в исходное положение
        isRotated = false
        updateCardPositions()
    }

    private fun clearCards() {
        cards.clear()
        removeAllViews()
    }

    private fun updateCardPositions() {
        val cardCount = cards.size

        cards.forEachIndexed { index, cardView ->
            // Расчёт расположения карт в исходной позиции
            val baseRotation = if (cardCount > 1) {
                val angleStep = 45f / (cardCount - 1)
                22.5f - (index * angleStep)
            } else {
                0f
            }

            // Расчёт финальной позиции (для эффекта раскрытой колоды карт)
            val targetRotation = if (isRotated) {
                val angleStep = if (cardCount > 1) 180f / (cardCount - 1) else 0f
                90f - (index * angleStep)
            } else {
                baseRotation
            }

            val cardWidth = 100f * resources.displayMetrics.density
            val cardHeight = 160f * resources.displayMetrics.density
            val sharedX = width / 2f - cardWidth / 2f
            val sharedY = height / 2f - cardHeight / 2f

            cardView.x = sharedX
            cardView.y = sharedY

            cardView.pivotX = cardWidth / 2f
            cardView.pivotY = cardHeight

            // TODO: [Задание 1] Замените на метод, который анимирует движение карты
            cardView.animateToRotation(targetRotation)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            updateCardPositions()
        }
    }

    private var isAnimating = false
    private var animationStep = 0

    private fun startCardSwapAnimation(bottomCard: AnimatedCardView) {
        // TODO: [Задание 5] Добавьте анимацию перетасовки карт

        if (isAnimating) return

        isAnimating = true
        animationStep = 1

        // TODO: Реализовать 3-шаговую анимацию в следующих этапах
        // Пока оставляем простую перестановку
        val reorderedCards = cardDataList.drop(1) + cardDataList.first()
        cardDataList = reorderedCards
        setupCards()

        isAnimating = false
        animationStep = 0
    }

    private fun handleHorizontalSwipe() {
        val bottomCard = cards.firstOrNull() ?: return
        startCardSwapAnimation(bottomCard)
    }

    // Простая функция перестановки карт
    fun reorderCards(cards: List<CardData>): List<CardData> {
        return cards.drop(1) + cards.first()
    }
    // TODO: [Задание 2] Добавьте обработку жестов
    // Подсказка: Используйте GestureDetector с методом onFling для обработки свайпов

    val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

            override fun onDown(e: MotionEvent): Boolean {
                verticalDragOffset = 0f
                horizontalDragOffset = 0f
                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                if (e1 == null) return false

                val horizontalMovement = -distanceX
                val verticalMovement = -distanceY

                val isVerticalSwipe = Math.abs(verticalMovement) > Math.abs(horizontalMovement)
                val isHorizontalSwipe = Math.abs(horizontalMovement) > Math.abs(verticalMovement)

                if (isVerticalSwipe) {
                    verticalDragOffset += verticalMovement
                }
                else if (isHorizontalSwipe) {
                    horizontalDragOffset += horizontalMovement
                }

                return true
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null) return false

                handleDragEnd()
                return true
            }

        })

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return true
    }

    // TODO: [Задание 3] Добавьте обработку вертикальных свайпов (вверх/вниз)

    var verticalDragOffset: Float = 0f
    val threshold = 100f

    // TODO: [Задание 4] Добавьте обработку горизонтальных свайпов (влево/вправо)

    var horizontalDragOffset: Float = 0f
    val swipeThreshold = 100f

    fun handleDragEnd() {

        if (Math.abs(verticalDragOffset) > Math.abs(horizontalDragOffset)) {
            if (verticalDragOffset < -threshold) {
                isRotated = true
            } else if (verticalDragOffset > threshold) {
                isRotated = false
            }
        }
        else {
            if (Math.abs(horizontalDragOffset) > swipeThreshold) {
                val bottomCard = cards.first()
                startCardSwapAnimation(bottomCard)
            }
        }

        updateCardPositions()

    }

}