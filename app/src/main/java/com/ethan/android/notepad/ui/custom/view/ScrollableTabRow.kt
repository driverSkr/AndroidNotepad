package com.ethan.android.notepad.ui.custom.view

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFold
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastForEachIndexed
import com.ethan.android.notepad.ui.custom.view.TabRowDefaults.myTabIndicatorOffset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ScrollableTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = TabRowDefaults.primaryContainerColor,
    contentColor: Color = TabRowDefaults.primaryContentColor,
    edgePadding: Dp = TabRowDefaults.ScrollableTabRowEdgeStartPadding,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit =
        @Composable { tabPositions ->
            TabRowDefaults.SecondaryIndicator(Modifier.myTabIndicatorOffset(tabPositions[selectedTabIndex]))
        },
    divider: @Composable () -> Unit = @Composable { HorizontalDivider() },
    tabs: @Composable () -> Unit,
) {
    ScrollableTabRowWithSubComposeImpl(
        selectedTabIndex = selectedTabIndex,
        indicator = indicator,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        edgePadding = edgePadding,
        divider = divider,
        tabs = tabs,
        scrollState = rememberScrollState()
    )
}

@Composable
private fun ScrollableTabRowWithSubComposeImpl(
    selectedTabIndex: Int,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = TabRowDefaults.primaryContainerColor,
    contentColor: Color = TabRowDefaults.primaryContentColor,
    edgePadding: Dp = TabRowDefaults.ScrollableTabRowEdgeStartPadding,
    divider: @Composable () -> Unit = @Composable { HorizontalDivider() },
    tabs: @Composable () -> Unit,
    scrollState: ScrollState,
) {
    Surface(modifier = modifier, color = containerColor, contentColor = contentColor) {
        val coroutineScope = rememberCoroutineScope()
        val scrollableTabData =
            remember(scrollState, coroutineScope) {
                ScrollableTabData(scrollState = scrollState, coroutineScope = coroutineScope)
            }
        SubcomposeLayout(
            Modifier
                .fillMaxWidth()
                .wrapContentSize(align = Alignment.CenterStart)
                .horizontalScroll(scrollState)
                .selectableGroup()
                .clipToBounds()
        ) { constraints ->
            val minTabWidth = ScrollableTabRowMinimumTabWidth.roundToPx()
            val padding = edgePadding.roundToPx()

            val tabMeasurables = subcompose(TabSlots.Tabs, tabs)

            val layoutHeight =
                tabMeasurables.fastFold(initial = 0) { curr, measurable ->
                    maxOf(curr, measurable.maxIntrinsicHeight(Constraints.Infinity))
                }

            val tabConstraints =
                constraints.copy(
                    minWidth = minTabWidth,
                    minHeight = layoutHeight,
                    maxHeight = layoutHeight,
                )

            val tabPlaceables = mutableListOf<Placeable>()
            val tabContentWidths = mutableListOf<Dp>()
            tabMeasurables.fastForEach {
                val placeable = it.measure(tabConstraints)
                var contentWidth = minOf(it.maxIntrinsicWidth(placeable.height), placeable.width).toDp()
                contentWidth -= HorizontalTextPadding * 2
                tabPlaceables.add(placeable)
                tabContentWidths.add(contentWidth)
            }

            val layoutWidth =
                tabPlaceables.fastFold(initial = padding * 2) { curr, measurable ->
                    curr + measurable.width
                }

            // Position the children.
            layout(layoutWidth, layoutHeight) {
                // Place the tabs
                val tabPositions = mutableListOf<TabPosition>()
                var left = padding
                tabPlaceables.fastForEachIndexed { index, placeable ->
                    placeable.placeRelative(left, 0)
                    tabPositions.add(
                        TabPosition(
                            left = left.toDp(),
                            width = placeable.width.toDp(),
                            contentWidth = tabContentWidths[index]
                        )
                    )
                    left += placeable.width
                }

                subcompose(TabSlots.Divider, divider).fastForEach {
                    val placeable =
                        it.measure(
                            constraints.copy(
                                minHeight = 0,
                                minWidth = layoutWidth,
                                maxWidth = layoutWidth
                            )
                        )
                    placeable.placeRelative(0, layoutHeight - placeable.height)
                }

                subcompose(TabSlots.Indicator) { indicator(tabPositions) }
                    .fastForEach {
                        it.measure(Constraints.fixed(layoutWidth, layoutHeight)).placeRelative(0, 0)
                    }

                scrollableTabData.onLaidOut(
                    density = this@SubcomposeLayout,
                    edgeOffset = padding,
                    tabPositions = tabPositions,
                    selectedTab = selectedTabIndex
                )
            }
        }
    }
}

@Immutable
class TabPosition internal constructor(val left: Dp, val width: Dp, val contentWidth: Dp) {

    val right: Dp
        get() = left + width

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TabPosition) return false

        if (left != other.left) return false
        if (width != other.width) return false
        if (contentWidth != other.contentWidth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + width.hashCode()
        result = 31 * result + contentWidth.hashCode()
        return result
    }

    override fun toString(): String {
        return "TabPosition(left=$left, right=$right, width=$width, contentWidth=$contentWidth)"
    }
}

private class ScrollableTabData(
    private val scrollState: ScrollState,
    private val coroutineScope: CoroutineScope,
) {
    private var selectedTab: Int? = null

    fun onLaidOut(
        density: Density,
        edgeOffset: Int,
        tabPositions: List<TabPosition>,
        selectedTab: Int,
    ) {
        if (this.selectedTab != selectedTab) {
            this.selectedTab = selectedTab
            tabPositions.getOrNull(selectedTab)?.let {
                val calculatedOffset = it.calculateTabOffset(density, edgeOffset, tabPositions)
                if (scrollState.value != calculatedOffset) {
                    coroutineScope.launch {
                        scrollState.animateScrollTo(
                            calculatedOffset,
                            animationSpec = ScrollableTabRowScrollSpec
                        )
                    }
                }
            }
        }
    }

    private fun TabPosition.calculateTabOffset(
        density: Density,
        edgeOffset: Int,
        tabPositions: List<TabPosition>,
    ): Int = with(density) {
        val totalTabRowWidth = tabPositions.last().right.roundToPx() + edgeOffset
        val visibleWidth = totalTabRowWidth - scrollState.maxValue
        val tabOffset = left.roundToPx()
        val scrollerCenter = visibleWidth / 2
        val tabWidth = width.roundToPx()
        val centeredTabOffset = tabOffset - (scrollerCenter - tabWidth / 2)
        val availableSpace = (totalTabRowWidth - visibleWidth).coerceAtLeast(0)
        return centeredTabOffset.coerceIn(0, availableSpace)
    }
}

internal val HorizontalTextPadding = 0.dp

private val ScrollableTabRowMinimumTabWidth = 0.dp

private val TabRowIndicatorSpec: AnimationSpec<Dp> = tween(durationMillis = 250, easing = FastOutSlowInEasing)

private val ScrollableTabRowScrollSpec: AnimationSpec<Float> = tween(durationMillis = 250, easing = FastOutSlowInEasing)

private enum class TabSlots {
    Tabs,
    Divider,
    Indicator
}

object TabRowDefaults {

    fun Modifier.myTabIndicatorOffset(currentTabPosition: TabPosition): Modifier =
        composed(
            inspectorInfo =
            debugInspectorInfo {
                name = "tabIndicatorOffset"
                value = currentTabPosition
            }
        ) {
            val currentTabWidth by
            animateDpAsState(
                targetValue = currentTabPosition.width,
                animationSpec = TabRowIndicatorSpec
            )
            val indicatorOffset by
            animateDpAsState(
                targetValue = currentTabPosition.left,
                animationSpec = TabRowIndicatorSpec
            )
            fillMaxWidth()
                .wrapContentSize(Alignment.BottomStart)
                .offset { IntOffset(x = indicatorOffset.roundToPx(), y = 0) }
                .width(currentTabWidth)
        }
}