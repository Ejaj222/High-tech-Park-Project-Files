package com.lurkbd.agent.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lurkbd.agent.ui.theme.AlertWash
import com.lurkbd.agent.ui.theme.Cream
import com.lurkbd.agent.ui.theme.GoldAccent
import com.lurkbd.agent.ui.theme.GoldRing
import com.lurkbd.agent.ui.theme.GoldSoft
import com.lurkbd.agent.ui.theme.Ink
import com.lurkbd.agent.ui.theme.InkMuted
import com.lurkbd.agent.ui.theme.SandCard
import com.lurkbd.agent.ui.theme.SandSurface
import com.lurkbd.agent.ui.theme.TealDark
import com.lurkbd.agent.ui.theme.TealHeader
import com.lurkbd.agent.ui.theme.TealPrimary

@Composable
fun TealAppHeader(
    title: String,
    onMenu: (() -> Unit)? = null,
    onNotify: (() -> Unit)? = null
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(TealHeader, TealDark)),
                RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            )
            .padding(horizontal = 8.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { onMenu?.invoke() }) {
            Icon(Icons.Outlined.Menu, contentDescription = "মেনু", tint = Color.White)
        }
        Text(
            title,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = GoldAccent,
                fontSize = 26.sp
            )
        )
        IconButton(onClick = { onNotify?.invoke() }) {
            Box {
                Icon(Icons.Outlined.Notifications, contentDescription = "নোটিফ", tint = Color.White)
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-6).dp, y = 6.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(GoldAccent)
                )
            }
        }
    }
}

@Composable
fun SoftCard(
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(18.dp), clip = false)
            .clip(RoundedCornerShape(18.dp))
            .background(if (highlight) AlertWash else SandCard)
            .border(
                width = 1.dp,
                color = if (highlight) GoldAccent.copy(alpha = 0.45f) else Color.White.copy(alpha = 0.7f),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(16.dp),
        content = content
    )
}

@Composable
fun SectionTitle(
    text: String,
    action: String? = null,
    onAction: (() -> Unit)? = null,
    icon: ImageVector? = null
) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(GoldAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TealPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        if (action != null) {
            Text(
                action,
                color = GoldAccent,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onAction?.invoke() }
            )
        }
    }
}

@Composable
fun AvatarCircle(letter: String, tint: Color = TealSoftMuted()) {
    Box(
        Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(tint),
        contentAlignment = Alignment.Center
    ) {
        Text(letter, color = TealPrimary, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TealSoftMuted() = Color(0xFFD7EBE7)

@Composable
fun ListRowRich(
    leading: @Composable () -> Unit,
    title: String,
    subtitle: String,
    trailing: String,
    trailingColor: Color = TealPrimary,
    tag: String? = null,
    highlight: Boolean = false
) {
    Row(
        Modifier
            .fillMaxWidth()
            .then(
                if (highlight) Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(AlertWash)
                    .padding(10.dp)
                else Modifier.padding(vertical = 10.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        leading()
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Ink)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = InkMuted)
            if (tag != null) {
                Text(tag, fontSize = 12.sp, color = GoldAccent, fontWeight = FontWeight.Medium)
            }
        }
        Text(
            trailing,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = trailingColor
        )
    }
}

@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TealPrimary,
            contentColor = Cream
        ),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, color = Cream)
    }
}

@Composable
fun GoldOutlinePill(text: String, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .border(1.5.dp, GoldAccent, RoundedCornerShape(999.dp))
            .background(Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Cream, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun LurkField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TealPrimary,
            unfocusedBorderColor = InkMuted.copy(alpha = 0.3f),
            focusedLabelColor = TealPrimary,
            focusedContainerColor = SandCard,
            unfocusedContainerColor = SandCard
        )
    )
}

@Composable
fun VoiceFab(onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(68.dp)
                .clip(CircleShape)
                .border(3.dp, Color.White, CircleShape)
                .background(GoldAccent)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Mic, contentDescription = "ভয়েস", tint = Color.White, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun GoldMicButton(onClick: () -> Unit, large: Boolean = true) {
    val size = if (large) 96.dp else 72.dp
    Box(contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(size + 28.dp)
                .clip(CircleShape)
                .border(2.dp, GoldSoft.copy(alpha = 0.5f), CircleShape)
        )
        Box(
            Modifier
                .size(size + 14.dp)
                .clip(CircleShape)
                .border(2.dp, GoldAccent.copy(alpha = 0.8f), CircleShape)
        )
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(size),
            shape = CircleShape,
            containerColor = TealPrimary,
            contentColor = GoldAccent
        ) {
            Icon(Icons.Default.Mic, null, modifier = Modifier.size(if (large) 40.dp else 30.dp))
        }
    }
}

@Composable
fun ScreenBody(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier
            .fillMaxWidth()
            .background(SandSurface)
            .padding(horizontal = 18.dp)
            .padding(top = 16.dp, bottom = 100.dp),
        content = content
    )
}
