package ru.yandexpraktikum.cardsanimation.compose

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import ru.yandexpraktikum.cardsanimation.model.CardData

/**
 * Метод для вычисления поворота карты в конкретной позиции
 */
fun calculateCardRotation(
    cardIndex: Int,
    cardCount: Int,
    isRotated: Boolean
): Float {
    if (cardCount <= 1) return 0f

    return if (isRotated) {
        val angleStep = 180f / (cardCount - 1)
        90f - (cardIndex * angleStep)
    } else {
        val angleStep = 45f / (cardCount - 1)
        22.5f - (cardIndex * angleStep)
    }
}

data class CardSwapAnimationState(
    val isAnimating: Boolean = false,
    val animationStep: AnimationStep? = null,
    val swappingCard: CardData? = null
)

@Composable
fun AnimatedCardStack(cards: List<CardData>) {
    val cardCount = cards.size
    var isRotated by remember { mutableStateOf(false) }

    // TODO: [Задание 2] Добавьте обработку жестов
    // Подсказка: Используйте Modifier.pointerInput() с методом detectDragGestures()

    var verticalDragOffset by remember { mutableStateOf(0f) }
    var horizontalDragOffset by remember { mutableStateOf(0f) }
    val threshold = 100f
    val swipeThreshold = 100f
    var animationState by remember { mutableStateOf(CardSwapAnimationState()) }
    var currentCards by remember { mutableStateOf(cards) }

    fun handleVerticalSwipe() {
        when {
            verticalDragOffset < -threshold -> isRotated = true
            verticalDragOffset > threshold -> isRotated = false
        }
    }

    fun handleHorizontalSwipe() {
        val bottomCard = currentCards.firstOrNull() ?: return

        if (animationState.isAnimating) return

        animationState = CardSwapAnimationState(
            isAnimating = true,
            animationStep = AnimationStep.PULL,
            swappingCard = bottomCard
        )
    }

    fun handleAnimationStepComplete(step: AnimationStep) {
        when (step) {
            AnimationStep.PULL -> {
                // Карта уехала вправо: поднимаем её над остальными и возвращаем в центр
                animationState = animationState.copy(animationStep = AnimationStep.UP)
            }
            AnimationStep.UP -> {
                // Карта в центре и приподнята: перемещаем её в конец колоды
                currentCards = reorderCards(currentCards)
                animationState = animationState.copy(animationStep = AnimationStep.DOWN)
            }
            AnimationStep.DOWN -> {
                // Анимация завершена
                animationState = CardSwapAnimationState()
            }
        }
    }

    fun handleDragEnd() {
        if (!animationState.isAnimating) {
            if (Math.abs(verticalDragOffset) > Math.abs(horizontalDragOffset)) {
                handleVerticalSwipe()
            } else if (Math.abs(horizontalDragOffset) > swipeThreshold) {
                handleHorizontalSwipe()
            }
        }
        verticalDragOffset = 0f
        horizontalDragOffset = 0f
    }

    Box(
        modifier = Modifier.pointerInput(Unit) {
            detectDragGestures(
                onDrag = { change, dragAmount ->
                    // Определяем направление свайпа

                    change.consume()

                    val horizontalMovement = dragAmount.x
                    val verticalMovement = dragAmount.y

                    val isVerticalSwipe = Math.abs(verticalMovement) > Math.abs(horizontalMovement)
                    val isHorizontalSwipe = Math.abs(horizontalMovement) > Math.abs(verticalMovement)

                    if (isVerticalSwipe) {
                        verticalDragOffset += verticalMovement
                    }
                    else if (isHorizontalSwipe) {
                        horizontalDragOffset += horizontalMovement
                    }

                },
                onDragEnd = {
                    // TODO: Обработка жестов в заданиях 3 и 4
                    handleDragEnd()
                },
                onDragCancel = {
                    verticalDragOffset = 0f
                    horizontalDragOffset = 0f
                }
            )
        },
        contentAlignment = Alignment.Center
    ) {
        currentCards.forEachIndexed { i, cardData ->
            key(cardData.imageResId) {
                val targetRotation = calculateCardRotation(i, cardCount, isRotated)

                AnimatedCard(
                    cardIndex = i,
                    targetRotation = targetRotation,
                    cardData = cardData,
                    // TODO: [Задание 5] Здесь добавьте параметры анимации карты
                    animationState = animationState,
                    onAnimationStepComplete = { step ->
                        handleAnimationStepComplete(step)
                   }
                )
            }
        }
    }

}

// Простая функция перестановки карт
fun reorderCards(cards: List<CardData>): List<CardData> {
    return cards.drop(1) + cards.first()
}