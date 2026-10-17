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
    val animationStep: Int = 0
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

    fun handleDragEnd() {
        if (!animationState.isAnimating) {
            if (Math.abs(verticalDragOffset) > Math.abs(horizontalDragOffset)) {
                if (verticalDragOffset < -threshold) {
                    isRotated = true
                } else if (verticalDragOffset > threshold) {
                    isRotated = false
                }
            } else {
                if (Math.abs(horizontalDragOffset) > swipeThreshold) {
                    // TODO: Заменить анимированной версией в следующих этапах
                    animationState = animationState.copy(isAnimating = true, animationStep = 1)
                    // val reorderedCards = reorderCards(cards)
                    // currentCards = reorderedCards
                }
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
                        animationState = animationState.copy(isAnimating = false, animationStep = 0)
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