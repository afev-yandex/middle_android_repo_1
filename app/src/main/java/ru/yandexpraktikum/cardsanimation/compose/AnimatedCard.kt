package ru.yandexpraktikum.cardsanimation.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ru.yandexpraktikum.cardsanimation.model.CardData

@Composable
fun AnimatedCard(
    cardIndex: Int,
    cardData: CardData,
    targetRotation: Float,
    animationState: CardSwapAnimationState,
    onAnimationStepComplete: ((Int) -> Unit)? = null
) {
    // TODO: [Задание 1] Добавьте анимацию поворота карты
    // Подсказка: используйте animateFloatAsState для плавной анимации

    val isSwappingCard = animationState.swappingCard == cardData
    val density = LocalDensity.current

    val moveDistance = with(density) { 50.dp.toPx() }

    val rotationRad = Math.toRadians(targetRotation.toDouble())
    val rightOffsetX = moveDistance * Math.cos(rotationRad).toFloat()
    val rightOffsetY = moveDistance * Math.sin(rotationRad).toFloat()

    // После reorderCards targetRotation - финальный угол карты
    val finalRotation = targetRotation

    val animatedRotation by animateFloatAsState(
        targetValue = when {
            animationState.animationStep == 3 -> finalRotation
            animationState.isAnimating -> targetRotation
            else -> targetRotation
        },
        animationSpec = tween(
            durationMillis = if (animationState.animationStep == 3) 300 else 800
        ),
        finishedListener = {
            if (isSwappingCard && animationState.animationStep == 3 && animationState.isAnimating) {
                onAnimationStepComplete?.invoke(3)
            }
        },
        label = "rotation"
    )

    // Обновите анимации перемещения для обработки Шага 2.
    // Шаги 1 и 2 завершаем по оси X.
    // Y анимируется параллельно, но не должен дублировать callback.
    val animatedTranslationX by animateFloatAsState(
        targetValue = when {
            isSwappingCard && animationState.isAnimating && animationState.animationStep == 1 -> rightOffsetX
            isSwappingCard && animationState.isAnimating && animationState.animationStep == 2 -> 0f // Движение в центр
            else -> 0f
        },
        animationSpec = tween(durationMillis = 300),
        finishedListener = {
            if (isSwappingCard && animationState.isAnimating) {
                when (animationState.animationStep) {
                    1 -> onAnimationStepComplete?.invoke(1)
                    2 -> onAnimationStepComplete?.invoke(2)
                }
            }
        },
        label = "translationX"
    )

    // Шаг 2: возврат в центр и приподнимаемся.
    // Шаг 3: опускаемся обратно в стек.
    val animatedTranslationY by animateFloatAsState(
        targetValue = when {
            isSwappingCard && animationState.isAnimating && animationState.animationStep == 1 -> rightOffsetY
            isSwappingCard && animationState.isAnimating && animationState.animationStep == 2 -> 0f // Движение в центр
            else -> 0f
        },
        animationSpec = tween(durationMillis = 300),
        label = "translationY"
    )

    // Добавьте zIndex для поднятия карты наверх
    val shouldBringToFront = isSwappingCard &&
        animationState.isAnimating &&
        animationState.animationStep >= 2

    Card(
        modifier = Modifier
            .size(width = 100.dp, height = 160.dp)
            // TODO: [Задание 5] Добавьте анимацию карты при свайпе вправо или влево
            .graphicsLayer {
                translationX = animatedTranslationX
                translationY = animatedTranslationY
                rotationZ = animatedRotation
                transformOrigin = TransformOrigin(0.5f, 1.0f)
            }.let { modifier ->
                if (shouldBringToFront) {
                    modifier.zIndex(1000f)
                } else {
                    modifier
                }
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = (4 + cardIndex).dp
        )
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(cardData.imageResId),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}