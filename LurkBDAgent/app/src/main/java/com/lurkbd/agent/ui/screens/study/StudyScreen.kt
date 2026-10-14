package com.lurkbd.agent.ui.screens.study

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.lurkbd.agent.ui.LurkViewModel
import com.lurkbd.agent.ui.components.ListRowRich
import com.lurkbd.agent.ui.components.LurkField
import com.lurkbd.agent.ui.components.PrimaryButton
import com.lurkbd.agent.ui.components.ScreenBody
import com.lurkbd.agent.ui.components.SectionTitle
import com.lurkbd.agent.ui.components.SoftCard
import com.lurkbd.agent.ui.components.TealAppHeader
import com.lurkbd.agent.ui.theme.GoldAccent
import com.lurkbd.agent.ui.theme.SandSurface
import com.lurkbd.agent.ui.theme.TealPrimary
import com.lurkbd.agent.util.dayNameBn
import com.lurkbd.agent.util.formatMinutes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StudyScreen(vm: LurkViewModel) {
    val classes by vm.classPeriods.collectAsState()
    val blocks by vm.studyBlocks.collectAsState()
    val notes by vm.studyNotes.collectAsState()
    val results by vm.results.collectAsState()
    val assignments by vm.assignments.collectAsState()
    var tab by remember { mutableIntStateOf(0) }
    var subject by remember { mutableStateOf("গণিত") }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var score by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("20") }
    var timeText by remember { mutableStateOf("9:00") }

    Column(Modifier.fillMaxSize().background(SandSurface)) {
        TealAppHeader(title = "পড়াশোনা")
        ScreenBody(Modifier.verticalScroll(rememberScrollState())) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("রুটিন", "নোট", "রেজাল্ট", "অ্যাসাইন").forEachIndexed { index, label ->
                    FilterChip(
                        selected = tab == index,
                        onClick = { tab = index },
                        label = { Text(label) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            when (tab) {
                0 -> {
                    SectionTitle("আজকের ক্লাস", icon = Icons.Outlined.Schedule)
                    SoftCard {
                        classes.forEach { item ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    ListRowRich(
                                        leading = {
                                            Box(
                                                Modifier.size(42.dp).clip(CircleShape)
                                                    .background(GoldAccent.copy(alpha = 0.2f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Outlined.MenuBook, null, tint = GoldAccent)
                                            }
                                        },
                                        title = item.subjectName,
                                        subtitle = "${dayNameBn(item.dayOfWeek)} · ${formatMinutes(item.startMinutes)}–${formatMinutes(item.endMinutes)}",
                                        trailing = if (item.alarmEnabled) "অ্যালার্ম" else "বন্ধ",
                                        trailingColor = GoldAccent
                                    )
                                }
                                Switch(
                                    checked = item.alarmEnabled,
                                    onCheckedChange = { vm.toggleClassAlarm(item, it) },
                                    colors = SwitchDefaults.colors(checkedTrackColor = GoldAccent)
                                )
                            }
                        }
                    }
                    SectionTitle("পড়ার রুটিন")
                    SoftCard {
                        if (blocks.isEmpty()) {
                            Text("এখনো নিজস্ব পড়ার রুটিন নেই", color = TealPrimary.copy(alpha = 0.6f))
                        } else {
                            blocks.forEach { b ->
                                ListRowRich(
                                    leading = {
                                        Box(
                                            Modifier.size(42.dp).clip(CircleShape)
                                                .background(TealPrimary.copy(alpha = 0.12f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Outlined.Schedule, null, tint = TealPrimary)
                                        }
                                    },
                                    title = b.subjectName,
                                    subtitle = "${dayNameBn(b.dayOfWeek)} · ${formatMinutes(b.startMinutes)}",
                                    trailing = "রুটিন",
                                    trailingColor = TealPrimary
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    LurkField(subject, { subject = it }, "সাবজেক্ট")
                    Spacer(Modifier.height(8.dp))
                    LurkField(timeText, { timeText = it }, "সময় (HH:MM)")
                    Spacer(Modifier.height(8.dp))
                    PrimaryButton("ক্লাস যোগ") {
                        val parts = timeText.split(":", ".", limit = 2)
                        val minutes = (parts.getOrNull(0)?.toIntOrNull() ?: 9) * 60 +
                            (parts.getOrNull(1)?.toIntOrNull() ?: 0)
                        vm.addClass(subject.ifBlank { "ক্লাস" }, minutes)
                    }
                    Spacer(Modifier.height(8.dp))
                    PrimaryButton("পড়ার রুটিন যোগ") {
                        val parts = timeText.split(":", ".", limit = 2)
                        val minutes = (parts.getOrNull(0)?.toIntOrNull() ?: 21) * 60 +
                            (parts.getOrNull(1)?.toIntOrNull() ?: 0)
                        vm.addStudyBlock(subject.ifBlank { "পড়া" }, minutes)
                    }
                }
                1 -> {
                    SectionTitle("সাবজেক্ট নোট", icon = Icons.Outlined.MenuBook)
                    SoftCard {
                        notes.forEach { n ->
                            ListRowRich(
                                leading = {
                                    Box(
                                        Modifier.size(42.dp).clip(CircleShape)
                                            .background(GoldAccent.copy(alpha = 0.18f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Outlined.MenuBook, null, tint = GoldAccent)
                                    }
                                },
                                title = "${n.subjectName} — ${n.title}",
                                subtitle = n.body.take(80),
                                trailing = "নোট"
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    LurkField(subject, { subject = it }, "সাবজেক্ট")
                    Spacer(Modifier.height(8.dp))
                    LurkField(title, { title = it }, "শিরোনাম")
                    Spacer(Modifier.height(8.dp))
                    LurkField(body, { body = it }, "নোট")
                    Spacer(Modifier.height(8.dp))
                    PrimaryButton("নোট সেভ") {
                        if (title.isBlank()) return@PrimaryButton
                        vm.addNote(subject.ifBlank { "সাধারণ" }, title, body)
                        title = ""
                        body = ""
                    }
                }
                2 -> {
                    SectionTitle("রেজাল্ট")
                    SoftCard {
                        results.forEach { r ->
                            ListRowRich(
                                leading = {
                                    Box(
                                        Modifier.size(42.dp).clip(CircleShape)
                                            .background(TealPrimary.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${r.score.toInt()}", color = TealPrimary)
                                    }
                                },
                                title = "${r.subjectName} · ${r.examType}",
                                subtitle = SimpleDateFormat("dd MMM", Locale.US).format(Date(r.date)),
                                trailing = "${r.score.toInt()}/${r.total.toInt()}",
                                trailingColor = GoldAccent
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    LurkField(subject, { subject = it }, "সাবজেক্ট")
                    Spacer(Modifier.height(8.dp))
                    LurkField(score, { score = it.filter { ch -> ch.isDigit() || ch == '.' } }, "নম্বর")
                    Spacer(Modifier.height(8.dp))
                    LurkField(total, { total = it.filter { ch -> ch.isDigit() || ch == '.' } }, "পূর্ণমান")
                    Spacer(Modifier.height(8.dp))
                    PrimaryButton("রেজাল্ট সেভ") {
                        val s = score.toDoubleOrNull() ?: return@PrimaryButton
                        val t = total.toDoubleOrNull() ?: 20.0
                        vm.addResult(subject.ifBlank { "সাবজেক্ট" }, "ক্লাস টেস্ট", s, t)
                        score = ""
                    }
                }
                else -> {
                    SectionTitle("অ্যাসাইনমেন্ট")
                    SoftCard {
                        assignments.forEach { a ->
                            ListRowRich(
                                leading = {
                                    Box(
                                        Modifier.size(42.dp).clip(CircleShape)
                                            .background(GoldAccent.copy(alpha = 0.18f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Outlined.Schedule, null, tint = GoldAccent)
                                    }
                                },
                                title = a.title,
                                subtitle = "${a.subjectName} · ${SimpleDateFormat("dd MMM", Locale.US).format(Date(a.dueAt))}",
                                trailing = if (a.done) "শেষ" else "বাকি",
                                trailingColor = GoldAccent,
                                highlight = !a.done
                            )
                        }
                        if (assignments.isEmpty()) {
                            Text("কোনো অ্যাসাইনমেন্ট নেই", color = TealPrimary.copy(alpha = 0.6f))
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    LurkField(subject, { subject = it }, "সাবজেক্ট")
                    Spacer(Modifier.height(8.dp))
                    LurkField(title, { title = it }, "অ্যাসাইনমেন্ট শিরোনাম")
                    Spacer(Modifier.height(8.dp))
                    PrimaryButton("অ্যাসাইনমেন্ট যোগ") {
                        if (title.isBlank()) return@PrimaryButton
                        vm.addAssignment(subject.ifBlank { "সাবজেক্ট" }, title)
                        title = ""
                    }
                }
            }
        }
    }
}
