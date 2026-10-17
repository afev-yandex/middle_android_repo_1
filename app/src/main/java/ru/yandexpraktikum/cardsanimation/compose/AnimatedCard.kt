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

    val rotationState by animateFloatAsState(targetValue = targetRotation, animationSpec = tween(durationMillis = 300))
    val density = LocalDensity.current

    val animatedTranslationX by animateFloatAsState(
        targetValue = if (cardIndex == 0 && animationState.isAnimating && animationState.animationStep == 1) {
            val moveDistance = with(density) { 50.dp.toPx() }
            val rotationRad = Math.toRadians(targetRotation.toDouble())
            moveDistance * Math.cos(rotationRad).toFloat()
        } else 0f,
        animationSpec = tween(durationMillis = 300),
        finishedListener = { if (cardIndex == 0 && animationState.isAnimating && animationState.animationStep == 1) onAnimationStepComplete?.invoke(1) },
        label = "translationX"
    )

    val animatedTranslationY by animateFloatAsState(
        targetValue = if (cardIndex == 0 && animationState.isAnimating && animationState.animationStep == 1) {
            val moveDistance = with(density) { 50.dp.toPx() }
            val rotationRad = Math.toRadians(targetRotation.toDouble())
            moveDistance * Math.sin(rotationRad).toFloat()
        } else 0f,
        animationSpec = tween(durationMillis = 300),
        finishedListener = { if (cardIndex == 0 && animationState.isAnimating && animationState.animationStep == 1) onAnimationStepComplete?.invoke(1) },
        label = "translationY"
    )

    Card(
        modifier = Modifier
            .size(width = 100.dp, height = 160.dp)
            // TODO: [Задание 5] Добавьте анимацию карты при свайпе вправо или влево
            .graphicsLayer {
                translationX = if (cardIndex == 0 && animationState.isAnimating) animatedTranslationX else 0f
                translationY = if (cardIndex == 0 && animationState.isAnimating) animatedTranslationY else 0f
                rotationZ = rotationState
                transformOrigin = TransformOrigin(0.5f, 1.0f)
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