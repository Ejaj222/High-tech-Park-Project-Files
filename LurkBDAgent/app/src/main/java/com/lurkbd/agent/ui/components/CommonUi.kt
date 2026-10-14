package com.lurkbd.agent.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.lurkbd.agent.ui.theme.GoldAccent
import com.lurkbd.agent.ui.theme.InkMuted
import com.lurkbd.agent.ui.theme.SandSurface
import com.lurkbd.agent.ui.theme.TealDark
import com.lurkbd.agent.ui.theme.TealPrimary

@Composable
fun ScreenScaffold(
    title: String,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 96.dp)
    ) {
        Text(title, style = MaterialTheme.typography.headlineLarge, color = TealPrimary)
        if (subtitle != null) {
            Spacer(Modifier.height(6.dp))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(Modifier.height(20.dp))
        content()
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = TealDark,
        modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
    )
}

@Composable
fun ListRow(
    title: String,
    subtitle: String,
    trailing: String? = null,
    highlight: Boolean = false
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
        }
        if (trailing != null) {
            Text(
                trailing,
                style = MaterialTheme.typography.titleMedium,
                color = if (highlight) GoldAccent else TealPrimary
            )
        }
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
            contentColor = SandSurface
        ),
        contentPadding = PaddingValues(vertical = 14.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = SandSurface)
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
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TealPrimary,
            unfocusedBorderColor = InkMuted.copy(alpha = 0.35f),
            focusedLabelColor = TealPrimary
        )
    )
}

@Composable
fun VoiceFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = TealPrimary,
        contentColor = SandSurface,
        shape = CircleShape
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(TealPrimary, TealDark)))
        ) {
            Icon(Icons.Default.Mic, contentDescription = "ভয়েস")
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(GoldAccent)
            )
        }
    }
}

@Composable
fun HeroBand(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.verticalGradient(listOf(TealPrimary, TealDark)))
            .padding(24.dp),
        content = content
    )
}
