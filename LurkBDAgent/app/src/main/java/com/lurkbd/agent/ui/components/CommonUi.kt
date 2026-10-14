package com.lurkbd.agent.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lurkbd.agent.ui.theme.AlertWash
import com.lurkbd.agent.ui.theme.CardBorder
import com.lurkbd.agent.ui.theme.Cream
import com.lurkbd.agent.ui.theme.GoldAccent
import com.lurkbd.agent.ui.theme.GoldMuted
import com.lurkbd.agent.ui.theme.GoldSoft
import com.lurkbd.agent.ui.theme.Ink
import com.lurkbd.agent.ui.theme.InkMuted
import com.lurkbd.agent.ui.theme.SandCard
import com.lurkbd.agent.ui.theme.TealDark
import com.lurkbd.agent.ui.theme.TealHeader
import com.lurkbd.agent.ui.theme.TealPrimary

@Composable
fun WavyTealHeader(
    title: String,
    titleColor: Color = GoldAccent,
    wavy: Boolean = true
) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(TealHeader)
    ) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {}) {
                    Icon(Icons.Outlined.Menu, "মেনু", tint = Color.White)
                }
                Text(
                    title,
                    color = titleColor,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = {}) {
                    Box {
                        Icon(Icons.Outlined.Notifications, "নোটিফ", tint = Color.White)
                        Box(
                            Modifier
                                .align(Alignment.TopEnd)
                                .offset((-4).dp, 6.dp)
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(GoldAccent)
                        )
                    }
                }
            }
            if (wavy) {
                Canvas(Modifier.fillMaxWidth().height(22.dp)) {
                    val w = size.width
                    val h = size.height
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(w, 0f)
                        lineTo(w, h * 0.25f)
                        quadraticBezierTo(w * 0.75f, h * 1.2f, w * 0.5f, h * 0.35f)
                        quadraticBezierTo(w * 0.25f, -h * 0.4f, 0f, h * 0.45f)
                        close()
                    }
                    drawPath(path, TealHeader)
                    // cream fill under wave via parent bg — draw sand bite
                    val sand = Path().apply {
                        moveTo(0f, h)
                        lineTo(w, h)
                        lineTo(w, h * 0.25f)
                        quadraticBezierTo(w * 0.75f, h * 1.2f, w * 0.5f, h * 0.35f)
                        quadraticBezierTo(w * 0.25f, -h * 0.4f, 0f, h * 0.45f)
                        close()
                    }
                    drawPath(sand, Color(0xFFF7F3EC))
                }
            }
        }
    }
}

@Composable
fun MockCard(
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp), clip = false)
            .clip(RoundedCornerShape(20.dp))
            .background(if (highlight) AlertWash else SandCard)
            .border(1.dp, if (highlight) GoldAccent.copy(0.4f) else CardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
fun SectionHead(
    title: String,
    action: String? = null,
    leadingIcon: ImageVector? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            Box(
                Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(GoldAccent),
                contentAlignment = Alignment.Center
            ) {
                Icon(leadingIcon, null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(8.dp))
        }
        Text(
            title,
            color = TealPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        if (leadingIcon != null) {
            HorizontalDivider(
                Modifier.weight(0.35f).padding(horizontal = 8.dp),
                color = GoldAccent.copy(0.5f),
                thickness = 1.dp
            )
        }
        if (action != null) {
            Text(
                action,
                color = GoldAccent,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                modifier = Modifier.clickable { onAction?.invoke() }
            )
        }
    }
}

@Composable
fun AvatarBadge(letter: String, bg: Color = Color(0xFFD7EBE7)) {
    Box(
        Modifier.size(42.dp).clip(CircleShape).background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(letter, color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun IconBadge(icon: ImageVector, bg: Color, tint: Color = TealPrimary) {
    Box(
        Modifier.size(42.dp).clip(CircleShape).background(bg),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun ChannelChip(text: String, color: Color = GoldMuted) {
    Box(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun TxRow(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    amount: String,
    amountColor: Color,
    channel: String,
    channelColor: Color = GoldMuted,
    showDivider: Boolean = true
) {
    Column {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(icon, iconBg, GoldMuted)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = TealPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(subtitle, color = InkMuted, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(amount, color = amountColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(Modifier.height(4.dp))
                ChannelChip(channel, channelColor)
            }
        }
        if (showDivider) {
            HorizontalDivider(color = CardBorder.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun GoldMicFab(onClick: () -> Unit, size: Dp = 64.dp) {
    Box(
        Modifier
            .size(size)
            .shadow(10.dp, CircleShape)
            .clip(CircleShape)
            .border(3.dp, Color.White, CircleShape)
            .background(GoldAccent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Mic, "ভয়েস", tint = Color.White, modifier = Modifier.size(size * 0.42f))
    }
}

@Composable
fun TealMicWithRings(onClick: () -> Unit, size: Dp = 96.dp) {
    Box(contentAlignment = Alignment.Center) {
        Box(Modifier.size(size + 28.dp).clip(CircleShape).border(2.dp, GoldSoft.copy(0.45f), CircleShape))
        Box(Modifier.size(size + 14.dp).clip(CircleShape).border(2.5.dp, GoldAccent, CircleShape))
        Box(
            Modifier
                .size(size)
                .clip(CircleShape)
                .background(TealPrimary)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Mic, null, tint = GoldAccent, modifier = Modifier.size(size * 0.4f))
        }
    }
}

@Composable
fun VoicePill(text: String, onClick: () -> Unit) {
    Row(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .border(1.5.dp, GoldAccent, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text, color = Cream, fontWeight = FontWeight.Medium)
        WaveBars(color = GoldAccent, tall = false)
    }
}

@Composable
fun WaveBars(color: Color = GoldAccent, tall: Boolean = true) {
    val heights = if (tall) listOf(8, 14, 22, 12, 18, 10, 16, 24, 14, 20, 12, 8)
    else listOf(6, 10, 7, 12, 8)
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        heights.forEach { h ->
            Box(Modifier.width(2.5.dp).height(h.dp).background(color, RoundedCornerShape(2.dp)))
        }
    }
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary, contentColor = Cream),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun GoldOutlineButton(text: String, icon: ImageVector? = null, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.5.dp, GoldAccent, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(icon, null, tint = GoldAccent, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, color = GoldAccent, fontWeight = FontWeight.SemiBold)
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
            unfocusedBorderColor = InkMuted.copy(0.3f),
            focusedContainerColor = SandCard,
            unfocusedContainerColor = SandCard
        )
    )
}

@Composable
fun ScreenPad(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .padding(top = 8.dp, bottom = 108.dp),
        content = content
    )
}

@Composable
fun RowScope.Grow(content: @Composable () -> Unit) {
    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { content() }
}
