package tech.alexib.yaba.android.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowLeft
import androidx.compose.material.icons.outlined.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import tech.alexib.yaba.android.ui.AddSpace
import tech.alexib.yaba.android.ui.theme.MoneyGreen
import tech.alexib.yaba.android.ui.theme.green700
import tech.alexib.yaba.android.ui.theme.lightColorsForChart
import tech.alexib.yaba.android.util.asMoneyString
import tech.alexib.yaba.model.AllCategoriesSpend
import tech.alexib.yaba.model.RangeOption
import java.text.DecimalFormat
import kotlin.math.absoluteValue
import kotlin.math.min


@Composable
fun SpendingWidget(spending: AllCategoriesSpend, openMenu: () -> Unit) {
    var selectedIdx by remember { mutableStateOf(0) }

    val floats = spending.spend.map { it.percentage }

    val sortedSpend = spending.spend

    val selected = sortedSpend[selectedIdx]

    fun previous() {
        selectedIdx = if (selectedIdx == 0) sortedSpend.lastIndex else selectedIdx - 1
    }

    fun next() {
        selectedIdx =
            if (selectedIdx == sortedSpend.lastIndex) 0 else selectedIdx + 1
    }

    YabaCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 4.dp)
                .pointerInput("spend") {
                    detectHorizontalDragGestures { change, _ ->
                        val changeX = change.positionChange().x
                        if (changeX.absoluteValue > 50) {
                            if (changeX > 0) {
                                next()
                            } else previous()
                        }
                    }
                },
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .wrapContentWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = {
                    previous()
                }) {
                    Icon(
                        Icons.Outlined.ArrowLeft, contentDescription = "previous",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colors.onSurface,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .wrapContentWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(bottom = 4.dp)
                        .clickable { openMenu() },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                        Text(
                            text = "${selected.rangeOption.value} spending",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.offset(x = 20.dp)

                        )
                        IconButton(
                            onClick = { openMenu() },
                            modifier = Modifier.offset(x = 10.dp)
                        ) {
                            Icon(
                                Icons.Outlined.ArrowDropDown,
                                contentDescription = "change date range"
                            )
                        }
                    }
                }

                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = spending.total.asMoneyString(),
                        textAlign = TextAlign.Center
                    )
                }

                SpendingPieChart(floats, selectedIdx, true)

                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(text = selected.category)
                    AddSpace(4.dp)
                    Text(
                        text = selected.total.asMoneyString(),
                        color = MoneyGreen,
                        textAlign = TextAlign.Center
                    )
                    AddSpace(4.dp)
                    Text(
                        text = "${
                            DecimalFormat("##0.00")
                                .format(selected.percentage.toDouble() * 100)
                        }%",
                        textAlign = TextAlign.Center
                    )
                    AddSpace(4.dp)
                }
            }
            Column(
                modifier = Modifier
                    .wrapContentWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                IconButton(
                    onClick = {
                        next()
                    },
                ) {
                    Icon(
                        Icons.Outlined.ArrowRight,
                        contentDescription = "next",
                        tint = MaterialTheme.colors.onSurface,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

//thanks to https://github.com/Gurupreet/ComposeCookBook
@Composable
fun SpendingPieChart(pieValues: List<Float>, selectedValue: Int, shouldAnimate: Boolean = true) {
    val idx = remember { Animatable(0f) }
    val targetIndex = (pieValues.size - 1).toFloat()

    LaunchedEffect(Unit) {
        idx.animateTo(
            targetValue = targetIndex,
            animationSpec = tween(
                durationMillis = if (shouldAnimate) 300 else 0,
                easing = FastOutSlowInEasing
            ),
        )
    }
    Canvas(
        modifier = Modifier
            .size(180.dp)
            .padding(16.dp)
    ) {
        val totalPieValue = pieValues.sum()
        var startAngle = 0f

        (0..min(pieValues.size - 1, idx.value.toInt())).forEach { index ->
            val sliceAngle: Float = 360f * pieValues[index] / totalPieValue

            drawPieSlice(
                color = if (index != selectedValue) lightColorsForChart[index].copy(alpha = 0.32f) else green700,
                size = size,
                startAngle = startAngle,
                sweepAngle = sliceAngle,
            )

            startAngle += sliceAngle
        }
    }
}


private fun DrawScope.drawPieSlice(color: Color, size: Size, startAngle: Float, sweepAngle: Float) {
    drawArc(
        color = color,
        size = size,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = true
    )
}


private val spendingStub by lazy {
    AllCategoriesSpend.from(
        RangeOption.September, listOf<Pair<String, Double>>(
            "Food and Drink" to 974.1,
            "Interest" to 111.47,
            "Payment" to 450.0,
            "Recreation" to 284.35,
            "Service" to 1974.45,
            "Shops" to 13285.65,
            "Transfer" to -17090.26,
            "Travel" to 3079.83
        )
    )

}

@Preview
@Composable
private fun SpendingPreview() {
    SpendingWidget(spendingStub) {}
}
