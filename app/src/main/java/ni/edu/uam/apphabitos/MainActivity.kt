package ni.edu.uam.apphabitos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.apphabitos.ui.theme.AppHabitosTheme

// ─── Paleta de colores ───────────────────────────────────────────────────────
val BackgroundColor  = Color(0xFFF5F7FA)
val CardColor        = Color(0xFFFFFFFF)
val PrimaryGreen     = Color(0xFF2ECC71)
val DarkGreen        = Color(0xFF27AE60)
val AccentPurple     = Color(0xFF8B5CF6)
val AccentBlue       = Color(0xFF3B82F6)
val AccentOrange     = Color(0xFFF59E0B)
val AccentRed        = Color(0xFFEF4444)
val TextPrimary      = Color(0xFF1A1A2E)
val TextSecondary    = Color(0xFF6B7280)
val GrayLight        = Color(0xFFE5E7EB)
val GrayDark         = Color(0xFF9CA3AF)
val CompletedBg      = Color(0xFFECFDF5)

// ─── Modelos de datos ────────────────────────────────────────────────────────
data class Habito(
    val id: Int,
    val nombre: String,
    val meta: String,
    val icono: ImageVector,
    val categoria: String,
    val categoriaColor: Color,
    var completado: Boolean = false
)

data class DiaSemana(val letra: String, val completado: Boolean)

// ─── Datos de muestra ────────────────────────────────────────────────────────
val habitosIniciales = listOf(
    Habito(1, "Beber agua",      "2 L",      Icons.Default.WaterDrop,    "Salud",   PrimaryGreen, completado = true),
    Habito(2, "Ejercicio",       "30 min",   Icons.Default.FitnessCenter,"Salud",   AccentBlue),
    Habito(3, "Leer",            "20 págs",  Icons.Default.MenuBook,     "Estudio", AccentPurple, completado = true),
    Habito(4, "Meditar",         "10 min",   Icons.Default.SelfImprovement,"Bienestar", AccentOrange),
    Habito(5, "Revisar tareas",  "9:00 AM",  Icons.Default.CheckCircle,  "Trabajo", AccentRed, completado = true),
)

val diasSemana = listOf(
    DiaSemana("L", true),
    DiaSemana("M", true),
    DiaSemana("X", true),
    DiaSemana("J", false),
    DiaSemana("V", false),
    DiaSemana("S", false),
    DiaSemana("D", false),
)

// ─── Activity ────────────────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppHabitosTheme {
                HabitosDashboard()
            }
        }
    }
}

// ─── Pantalla principal ───────────────────────────────────────────────────────
@Composable
fun HabitosDashboard() {
    var habitos by remember { mutableStateOf(habitosIniciales) }

    val totalHabitos   = habitos.size
    val completados    = habitos.count { it.completado }
    val progreso       = if (totalHabitos > 0) completados.toFloat() / totalHabitos else 0f
    val porcentaje     = (progreso * 100).toInt()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 56.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── Header ──────────────────────────────────────────────────────
            item { HeaderSection() }

            // ── Tarjeta de progreso ─────────────────────────────────────────
            item { TarjetaProgreso(progreso, porcentaje, completados, totalHabitos) }

            // ── Resumen semanal ─────────────────────────────────────────────
            item { ResumenSemanal() }

            // ── Lista de hábitos ────────────────────────────────────────────
            item {
                Text(
                    "Hábitos de hoy",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            items(habitos, key = { it.id }) { habito ->
                HabitoCard(habito) { id ->
                    habitos = habitos.map {
                        if (it.id == id) it.copy(completado = !it.completado) else it
                    }
                }
            }
        }

        // ── Botón flotante "+" ───────────────────────────────────────────────
        BotonFlotante(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )
    }
}

// ─── Sección Header ──────────────────────────────────────────────────────────
@Composable
fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                "Hola, Oscar 👋",
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Text(
                "¡Sigues en racha! 🔥 3 días",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(CardColor)
                .shadow(4.dp, CircleShape)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones",
                tint = AccentPurple, modifier = Modifier.size(22.dp))
            // Badge
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(AccentRed)
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 4.dp)
            )
        }
    }
}

// ─── Tarjeta de progreso ──────────────────────────────────────────────────────
@Composable
fun TarjetaProgreso(progreso: Float, porcentaje: Int, completados: Int, total: Int) {
    val animatedProgress by animateFloatAsState(
        targetValue = progreso,
        animationSpec = tween(durationMillis = 800),
        label = "progreso"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(listOf(DarkGreen, PrimaryGreen))
            )
            .padding(24.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Progreso de hoy", fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold, color = Color.White.copy(alpha = 0.9f))
                Text(
                    "$porcentaje%",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }

            // Barra de progreso
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                )
            }

            Text(
                "$completados de $total hábitos completados",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

// ─── Resumen semanal ──────────────────────────────────────────────────────────
@Composable
fun ResumenSemanal() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardColor)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Semana actual", fontSize = 16.sp,
            fontWeight = FontWeight.Bold, color = TextPrimary)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            diasSemana.forEach { dia ->
                CirculoDia(dia)
            }
        }
    }
}

@Composable
fun CirculoDia(dia: DiaSemana) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (dia.completado) PrimaryGreen else GrayLight),
            contentAlignment = Alignment.Center
        ) {
            if (dia.completado) {
                Icon(Icons.Default.Check, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
        Text(dia.letra, fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (dia.completado) DarkGreen else GrayDark)
    }
}

// ─── Tarjeta de hábito ────────────────────────────────────────────────────────
@Composable
fun HabitoCard(habito: Habito, onToggle: (Int) -> Unit) {
    val animColor by animateColorAsState(
        targetValue = if (habito.completado) CompletedBg else CardColor,
        animationSpec = tween(400),
        label = "bgColor"
    )
    val checkColor by animateColorAsState(
        targetValue = if (habito.completado) PrimaryGreen else GrayLight,
        animationSpec = tween(400),
        label = "checkColor"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(animColor)
            .border(
                width = 1.dp,
                color = if (habito.completado) PrimaryGreen.copy(alpha = 0.3f) else GrayLight,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onToggle(habito.id) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icono de categoría
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(habito.categoriaColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(habito.icono, contentDescription = null,
                tint = habito.categoriaColor, modifier = Modifier.size(22.dp))
        }

        // Nombre y categoría
        Column(modifier = Modifier.weight(1f)) {
            Text(
                habito.nombre,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (habito.completado) TextSecondary else TextPrimary,
                textDecoration = if (habito.completado) TextDecoration.LineThrough else TextDecoration.None
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(habito.categoriaColor.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(habito.categoria, fontSize = 10.sp,
                        fontWeight = FontWeight.Bold, color = habito.categoriaColor)
                }
                Text("·", color = GrayDark, fontSize = 12.sp)
                Text(habito.meta, fontSize = 12.sp, color = TextSecondary)
            }
        }

        // Checkbox
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(checkColor),
            contentAlignment = Alignment.Center
        ) {
            if (habito.completado) {
                Icon(Icons.Default.Check, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// ─── Botón flotante ───────────────────────────────────────────────────────────
@Composable
fun BotonFlotante(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(listOf(AccentPurple, AccentBlue))
            )
            .shadow(12.dp, CircleShape)
            .clickable { /* Agregar nuevo hábito */ },
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Add, contentDescription = "Agregar hábito",
            tint = Color.White, modifier = Modifier.size(28.dp))
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview() {
    AppHabitosTheme {
        HabitosDashboard()
    }
}