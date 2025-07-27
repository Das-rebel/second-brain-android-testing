package com.secondbrain.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.secondbrain.app.design.SparkThreadDesign
import com.secondbrain.app.ui.theme.spark.SparkTheme
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Floating sakura petals with gentle, realistic animations
 */
@Composable
fun FloatingSakuraPetals(
    count: Int = 8,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sakuraPetals")
    
    Box(modifier = modifier.fillMaxSize()) {
        repeat(count) { index ->
            val random = Random(index)
            val startX = random.nextFloat() * 400f
            val startY = random.nextFloat() * 600f
            
            // Vertical floating animation
            val verticalOffset by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 30f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 4000 + (index * 300),
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "sakuraFloat$index"
            )
            
            // Rotation animation
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 15000 + (index * 1000),
                        easing = LinearEasing
                    )
                ),
                label = "sakuraRotation$index"
            )
            
            // Horizontal sway animation
            val horizontalSway by infiniteTransition.animateFloat(
                initialValue = -10f,
                targetValue = 10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 3000 + (index * 400),
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "sakuraSway$index"
            )
            
            Box(
                modifier = Modifier
                    .offset(
                        x = (startX + horizontalSway).dp,
                        y = (startY + verticalOffset).dp
                    )
                    .size((8 + random.nextInt(4)).dp)
                    .rotate(rotation)
            ) {
                val petalColor = SparkTheme.colorScheme.sakura.copy(
                    alpha = 0.3f + random.nextFloat() * 0.4f
                )
                SakuraPetal(
                    color = petalColor
                )
            }
        }
    }
}

/**
 * Traditional sakura petal shape
 */
@Composable
private fun SakuraPetal(
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val petalPath = Path().apply {
            // Create traditional 5-petal sakura shape
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.minDimension / 2 * 0.8f
            
            repeat(5) { i ->
                val angle = (i * 72 - 90) * (Math.PI / 180)
                val x = centerX + cos(angle) * radius
                val y = centerY + sin(angle) * radius
                
                if (i == 0) moveTo(x.toFloat(), y.toFloat())
                else lineTo(x.toFloat(), y.toFloat())
                
                // Add gentle curve for petal shape
                val nextAngle = ((i + 1) * 72 - 90) * (Math.PI / 180)
                val nextX = centerX + cos(nextAngle) * radius * 0.3f
                val nextY = centerY + sin(nextAngle) * radius * 0.3f
                quadraticBezierTo(
                    centerX, centerY,
                    nextX.toFloat(), nextY.toFloat()
                )
            }
            close()
        }
        
        drawPath(
            path = petalPath,
            color = color,
            style = Fill
        )
    }
}

/**
 * Washi paper texture patterns with subtle animations
 */
@Composable
fun WashiPaperTexture(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "washiTexture")
    
    val textureAlpha by infiniteTransition.animateFloat(
        initialValue = 0.02f,
        targetValue = 0.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 8000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "washiAlpha"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        // Create washi paper texture with organic lines
        val random = Random(42) // Fixed seed for consistent pattern
        
        repeat(15) { i ->
            val startX = random.nextFloat() * size.width
            val startY = random.nextFloat() * size.height
            val endX = startX + (random.nextFloat() - 0.5f) * 200f
            val endY = startY + (random.nextFloat() - 0.5f) * 100f
            
            drawLine(
                color = SparkThreadDesign.Colors.Gold.copy(alpha = textureAlpha),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 1f + random.nextFloat() * 2f,
                cap = StrokeCap.Round
            )
        }
        
        // Add subtle fiber patterns
        repeat(25) { i ->
            val x = random.nextFloat() * size.width
            val y = random.nextFloat() * size.height
            val radius = 1f + random.nextFloat() * 3f
            
            drawCircle(
                color = SparkThreadDesign.Colors.Bamboo.copy(alpha = textureAlpha * 0.5f),
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * Ink splash decorative elements with organic shapes
 */
@Composable
fun InkSplashDecorations(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "inkSplash")
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 6000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "inkPulse"
    )
    
    // Extract colors outside of Canvas contexts
    val inkColor = SparkTheme.colorScheme.ink.copy(alpha = 0.08f)
    val sakuraColor = SparkTheme.colorScheme.sakura.copy(alpha = 0.06f)
    val goldColor = SparkTheme.colorScheme.gold.copy(alpha = 0.04f)
    
    Box(modifier = modifier.fillMaxSize()) {
        // Large ink splash - top right
        Canvas(
            modifier = Modifier
                .size(120.dp)
                .offset(x = 250.dp, y = 50.dp)
        ) {
            drawInkSplash(
                center = Offset(size.width * 0.5f, size.height * 0.5f),
                radius = size.minDimension * 0.3f * pulseScale,
                color = inkColor
            )
        }
        
        // Medium ink splash - bottom left
        Canvas(
            modifier = Modifier
                .size(80.dp)
                .offset(x = 30.dp, y = 400.dp)
        ) {
            drawInkSplash(
                center = Offset(size.width * 0.4f, size.height * 0.6f),
                radius = size.minDimension * 0.4f * pulseScale * 0.7f,
                color = sakuraColor
            )
        }
        
        // Small ink splashes scattered
        repeat(3) { index ->
            val random = Random(index + 10)
            Canvas(
                modifier = Modifier
                    .size(40.dp)
                    .offset(
                        x = (50 + random.nextInt(250)).dp,
                        y = (200 + random.nextInt(200)).dp
                    )
            ) {
                drawInkSplash(
                    center = Offset(size.width * 0.5f, size.height * 0.5f),
                    radius = size.minDimension * 0.2f * pulseScale,
                    color = goldColor
                )
            }
        }
    }
}

/**
 * Draw organic ink splash shape
 */
private fun DrawScope.drawInkSplash(
    center: Offset,
    radius: Float,
    color: Color
) {
    val path = Path()
    val random = Random(42)
    
    // Create irregular organic shape
    val points = 8
    val angleStep = 360f / points
    
    repeat(points) { i ->
        val angle = (i * angleStep) * (Math.PI / 180)
        val variance = 0.7f + random.nextFloat() * 0.6f // Random variation
        val distance = radius * variance
        
        val x = center.x + cos(angle) * distance
        val y = center.y + sin(angle) * distance
        
        if (i == 0) {
            path.moveTo(x.toFloat(), y.toFloat())
        } else {
            // Use curves for organic feel
            val controlX = center.x + cos(angle - angleStep * 0.5 * (Math.PI / 180)) * distance * 0.8f
            val controlY = center.y + sin(angle - angleStep * 0.5 * (Math.PI / 180)) * distance * 0.8f
            path.quadraticBezierTo(
                controlX.toFloat(), controlY.toFloat(),
                x.toFloat(), y.toFloat()
            )
        }
    }
    path.close()
    
    drawPath(
        path = path,
        color = color,
        style = Fill
    )
}

/**
 * Traditional Japanese border elements
 */
@Composable
fun TraditionalBorder(
    thickness: Float = 3f,
    modifier: Modifier = Modifier
) {
    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color.Transparent,
            SparkThreadDesign.Colors.Primary,
            SparkThreadDesign.Colors.Gold,
            SparkThreadDesign.Colors.Primary,
            Color.Transparent
        )
    )
    
    Canvas(modifier = modifier.fillMaxWidth().height(thickness.dp)) {
        drawRect(
            brush = gradient,
            size = Size(size.width, thickness)
        )
        
        // Add decorative dots
        val dotSpacing = 60f
        val dotCount = (size.width / dotSpacing).toInt()
        
        repeat(dotCount) { i ->
            val x = i * dotSpacing + dotSpacing / 2
            drawCircle(
                color = SparkThreadDesign.Colors.Sakura.copy(alpha = 0.6f),
                radius = 2f,
                center = Offset(x, thickness / 2)
            )
        }
    }
}

/**
 * Animated traditional stamp/seal element
 */
@Composable
fun TraditionalStamp(
    text: String = "知",
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    val stampScale by animateFloatAsState(
        targetValue = if (isVisible) 1.0f else 0.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "stampScale"
    )
    
    val stampRotation by animateFloatAsState(
        targetValue = if (isVisible) 0f else -45f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "stampRotation"
    )
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000)
        isVisible = true
    }
    
    Box(
        modifier = modifier
            .size(60.dp)
            .rotate(stampRotation)
            .clip(CircleShape)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SparkThreadDesign.Colors.SealRed,
                        SparkThreadDesign.Colors.SealRed.copy(alpha = 0.8f)
                    )
                )
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        // Traditional stamp background texture
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = SparkThreadDesign.PaperEffects.SealImprint.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        )
        
        Text(
            text = text,
            style = SparkThreadDesign.Typography.HeadlineSmall.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            color = Color.White
        )
    }
}