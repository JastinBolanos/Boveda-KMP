package com.jastin.boveda.presentation.screens.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import boveda_kmp.composeapp.generated.resources.Res
import boveda_kmp.composeapp.generated.resources.splash_background
import boveda_kmp.composeapp.generated.resources.splash_card_bg
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jastin.boveda.presentation.screens.main.MainScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

class WelcomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val coroutineScope = rememberCoroutineScope()

        // --- ESTADOS DE ANIMACIÓN ---
        var isLoading by remember { mutableStateOf(false) }

        val buttonPurple = Color(0xFF6D28D9)

        // --- DEGRADADO EXACTO PARA "VAULT" ---
        val vaultGradient = Brush.linearGradient(
            colorStops = arrayOf(
                0.00f to Color(0xFF899CFF),
                0.23f to Color(0xFF924FFF),
                0.68f to Color(0xFFF442B5),
                1.00f to Color(0xFFD61672)
            )
        )

        // --- ACCIÓN DE ENTRADA A LA BÓVEDA ---
        val onEnterVault = {
            if (!isLoading) {
                isLoading = true
                coroutineScope.launch {
                    // Mantenemos la animación veloz y el brillo por 1.2 segundos para que el usuario lo disfrute
                    delay(1200)
                    navigator.push(MainScreen())
                    // Reseteamos el estado por si el usuario presiona el botón de "Atrás" después
                    isLoading = false
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {

            // --- 1. FONDO PRINCIPAL ---
            Image(
                painter = painterResource(Res.drawable.splash_background),
                contentDescription = "Welcome Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 48.dp),
            ) {

                // Espaciador superior
                Spacer(modifier = Modifier.weight(0.6f))

                // --- 2. SECCIÓN SUPERIOR (TEXTOS) ---
                Column {
                    Text(
                        text = buildAnnotatedString {
                            append("Enterprise banking\non ")
                            withStyle(style = SpanStyle(brush = vaultGradient)) {
                                append("Vault")
                            }
                        },
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        lineHeight = 48.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "No borders, no downtime, no compromises.\nYour offline-first financial engine.",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 24.sp
                    )
                }

                Spacer(modifier = Modifier.weight(0.4f))

                // --- 3. TARJETA CORPORATIVA ---
                Image(
                    painter = painterResource(Res.drawable.splash_card_bg),
                    contentDescription = "Card Background",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.weight(1.2f))

                // --- 4. BOTONES DE ACCIÓN ---
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = onEnterVault,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonPurple),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading // Desactiva el clic múltiple
                    ) {
                        if (isLoading) {
                            // Animación de puntos veloces
                            PremiumLoadingDots()
                        } else {
                            Text("Open Corporate Account", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Log in to your Vault",
                        color = if (isLoading) Color.White.copy(alpha = 0.5f) else Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable(enabled = !isLoading) { onEnterVault() }
                            .padding(vertical = 12.dp, horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // --- 5. LUZ QUE BRILLA (EFECTO BÓVEDA ABRIÉNDOSE) ---
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(animationSpec = tween(600)),
                exit = fadeOut(animationSpec = tween(400))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF8B5CF6).copy(alpha = 0.3f),
                                    Color.Transparent
                                ),
                                radius = 1500f
                            )
                        )
                )
            }
        }
    }

    // --- COMPONENTE: TRES PUNTITOS PODEROSOS Y VELOCES ---
    @Composable
    private fun PremiumLoadingDots() {
        val infiniteTransition = rememberInfiniteTransition(label = "loading_dots")

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 3) {
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.4f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 300,
                            delayMillis = i * 100,
                            easing = FastOutSlowInEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot_scale_$i"
                )

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
    }
}