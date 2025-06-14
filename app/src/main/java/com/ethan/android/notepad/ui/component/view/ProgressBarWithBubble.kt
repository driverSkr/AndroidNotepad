package com.ethan.android.notepad.ui.component.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.ethan.android.notepad.theme.NO_PADDING_TEXT_STYLE

/**
 * 进度条和气泡组件
 */
@Composable
fun ProgressBarWithBubble(
    progress: Float,
    currentStatus: String?,
    isVideo: Boolean,
    isVip: Boolean
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = if (!isVideo && !isVip) 55.dp else 0.dp)
        .padding(top = 24.dp)
        .height(40.dp) // 增加高度以容纳气泡
       ) {
        // 进度条背景
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .align(Alignment.BottomCenter)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Gray.copy(alpha = 0.2f))) {
            // 进度条前景 - 使用渐变色
            Box(modifier = Modifier
                .fillMaxWidth(if (currentStatus != null) progress else 0f) // 只要currentStatus不为null就显示进度
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Brush.horizontalGradient(colors = listOf(Color(0xFF3A1BFF), // 蓝色
                    Color(0xFFB518FF)  // 紫色
                                                                    ))))
        }

        // 百分比气泡 - 使用BoxWithConstraints来简化位置计算
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            // 计算气泡位置，使气泡中心对齐进度条当前位置
            val bubbleWidth = 50.dp  // 从35.dp增加到50.dp，以便更好地容纳百分比文本
            val arrowWidth = 10.dp

            // 计算进度条上的位置点，考虑进度条的起始和结束位置
            val progressBarStartX = 0.dp
            val progressBarEndX = maxWidth
            val progressBarWidth = progressBarEndX - progressBarStartX

            // 计算显示的百分比值
            val displayPercentage = if (currentStatus != null) ((progress * 99).toInt().coerceAtMost(99))
            else 0 // 默认显示0%
            
            // 实际进度点位置
            val progressPoint = progressBarStartX + (progress * progressBarWidth)

            // 计算气泡位置，不限制左右边界，完全跟随进度点
            val bubblePosition = progressPoint - (bubbleWidth / 2)

            // 计算箭头的绝对位置（相对于整个进度条）
            // 这是关键修改：确保箭头始终指向实际的进度位置
            val arrowAbsolutePosition = progressPoint - bubblePosition - (arrowWidth / 2)
            
            // 将箭头位置限制在气泡宽度内
            val arrowPosition = arrowAbsolutePosition.coerceIn(0.dp, bubbleWidth - arrowWidth)

            // 气泡容器，包含气泡和箭头
            Box(modifier = Modifier.offset(x = bubblePosition, y = 0.dp // 保持在顶部
                                          )) {
                // 气泡主体
                Box(modifier = Modifier
                    .width(bubbleWidth)
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFC64FFF))) {
                    Text(text = "${displayPercentage}%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center, style = NO_PADDING_TEXT_STYLE,  // 使用无内边距文本样式
                        modifier = Modifier
                            .fillMaxSize()  // 填充整个气泡空间
                            .wrapContentSize(Alignment.Center)  // 确保内容在中心位置
                        )
                }

                // 气泡底部的箭头 - 使用自定义形状，位置根据进度调整
                Box(modifier = Modifier
                    .size(arrowWidth, 8.1.dp)
                    .offset(x = arrowPosition, // 使用计算的箭头位置
                        y = 20.dp // 紧贴气泡底部，从16.dp调整为20.dp
                           )
                    .background(color = Color(0xFFC64FFF), shape = GenericShape { size, _ ->
                        // 创建一个平滑的三角形，底部为圆弧
                        moveTo(0f, 0f) // 左上角起点
                        lineTo(size.width, 0f) // 右上角

                        // 使用二次贝塞尔曲线创建圆弧底部
                        quadraticTo(size.width / 2, size.height * 1.3f, // 控制点
                            0f, 0f // 回到起点
                                   )
                        close()
                    }))
            }
        }
    }
} 