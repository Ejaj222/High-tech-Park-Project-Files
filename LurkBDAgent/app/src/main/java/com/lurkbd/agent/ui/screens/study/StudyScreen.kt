package com.lurkbd.agent.ui.screens.study

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.DocumentScanner
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lurkbd.agent.scan.rememberDocumentScanLauncher
import com.lurkbd.agent.ui.LurkViewModel
import com.lurkbd.agent.ui.components.GoldOutlineButton
import com.lurkbd.agent.ui.components.IconBadge
import com.lurkbd.agent.ui.components.LurkField
import com.lurkbd.agent.ui.components.MockCard
import com.lurkbd.agent.ui.components.PrimaryButton
import com.lurkbd.agent.ui.components.ScreenPad
import com.lurkbd.agent.ui.components.SectionHead
import com.lurkbd.agent.ui.components.WavyTealHeader
import com.lurkbd.agent.ui.theme.GoldAccent
import com.lurkbd.agent.ui.theme.Ink
import com.lurkbd.agent.ui.theme.InkMuted
import com.lurkbd.agent.ui.theme.SandSurface
import com.lurkbd.agent.ui.theme.TealPrimary
import com.lurkbd.agent.util.formatMinutes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StudyScreen(vm: LurkViewModel) {
    val classes by vm.classPeriods.collectAsState()
    val notes by vm.studyNotes.collectAsState()
    val results by vm.results.collectAsState()
    val assignments by vm.assignments.collectAsState()
    val documents by vm.studyDocuments.collectAsState()
    var tab by remember { mutableIntStateOf(0) }
    var subject by remember { mutableStateOf("গণিত") }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var score by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("20") }
    var timeText by remember { mutableStateOf("9:00") }
    val launchScan = rememberDocumentScanLauncher(subject = subject.ifBlank { "পড়াশোনা" }) { uri, mime, scanTitle ->
        vm.saveScannedDocument(subject.ifBlank { "পড়াশোনা" }, scanTitle, uri, mime)
    }

    Column(Modifier.fillMaxSize().background(SandSurface)) {
        WavyTealHeader(title = "পড়াশোনা", titleColor = Color.White, wavy = true)
        ScreenPad {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                StudySubNav(tab) { tab = it }
                Spacer(Modifier.height(12.dp))

                when (tab) {
                    0 -> {
                        SectionHead("আজকের ক্লাস রুটিন", action = "সব দেখুন >", leadingIcon = Icons.Outlined.CalendarMonth)
                        MockCard {
                            if (classes.isEmpty()) {
                                Text("আজ কোনো ক্লাস নেই", color = InkMuted)
                            } else {
                                classes.forEachIndexed { i, item ->
                                    Row(
                                        Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            formatMinutes(item.startMinutes),
                                            color = TealPrimary,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.width(56.dp)
                                        )
                                        Text("—", color = InkMuted)
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            item.subjectName,
                                            color = Ink,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Switch(
                                            checked = item.alarmEnabled,
                                            onCheckedChange = { vm.toggleClassAlarm(item, it) },
                                            colors = SwitchDefaults.colors(
                                                checkedTrackColor = TealPrimary,
                                                checkedThumbColor = Color.White
                                            )
                                        )
                                    }
                                    if (i != classes.lastIndex) HorizontalDivider(color = Color(0xFFE8DFD0))
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        LurkField(subject, { subject = it }, "সাবজেক্ট")
                        Spacer(Modifier.height(6.dp))
                        LurkField(timeText, { timeText = it }, "সময় (HH:MM)")
                        Spacer(Modifier.height(8.dp))
                        PrimaryButton("ক্লাস যোগ") {
                            val parts = timeText.split(":", ".", limit = 2)
                            val minutes = (parts.getOrNull(0)?.toIntOrNull() ?: 9) * 60 +
                                (parts.getOrNull(1)?.toIntOrNull() ?: 0)
                            vm.addClass(subject.ifBlank { "ক্লাস" }, minutes)
                        }
                        Spacer(Modifier.height(12.dp))
                        SectionHead("নোট", action = "সব নোট >", leadingIcon = Icons.Outlined.Description)
                        notes.take(3).forEach { n ->
                            Spacer(Modifier.height(8.dp))
                            MockCard {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconBadge(Icons.AutoMirrored.Outlined.MenuBook, Color(0xFFE8D5A3), GoldAccent)
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text("${n.subjectName} — ${n.title}", color = Ink, fontWeight = FontWeight.SemiBold)
                                        Text("সর্বশেষ সম্পাদনা: আজ", color = InkMuted, fontSize = 12.sp)
                                    }
                                    Icon(Icons.Outlined.MoreVert, null, tint = InkMuted)
                                }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        GoldOutlineButton("স্ক্যান করে রাখুন", Icons.Outlined.DocumentScanner) { launchScan() }
                    }
                    1 -> {
                        SectionHead("সাবজেক্ট নোট", leadingIcon = Icons.Outlined.Description)
                        notes.forEach { n ->
                            Spacer(Modifier.height(8.dp))
                            MockCard {
                                Text("${n.subjectName} — ${n.title}", fontWeight = FontWeight.SemiBold, color = TealPrimary)
                                Text(n.body, color = InkMuted, fontSize = 13.sp)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        LurkField(subject, { subject = it }, "সাবজেক্ট")
                        Spacer(Modifier.height(6.dp))
                        LurkField(title, { title = it }, "শিরোনাম")
                        Spacer(Modifier.height(6.dp))
                        LurkField(body, { body = it }, "নোট")
                        Spacer(Modifier.height(8.dp))
                        PrimaryButton("নোট সেভ") {
                            if (title.isBlank()) return@PrimaryButton
                            vm.addNote(subject.ifBlank { "সাধারণ" }, title, body)
                            title = ""; body = ""
                        }
                    }
                    2 -> {
                        SectionHead("ডকুমেন্ট / অ্যাসাইনমেন্ট", leadingIcon = Icons.Outlined.Folder)
                        GoldOutlineButton("স্ক্যান করে রাখুন", Icons.Outlined.DocumentScanner) { launchScan() }
                        Spacer(Modifier.height(10.dp))
                        if (documents.isNotEmpty()) {
                            Text("স্ক্যান করা ডকুমেন্ট", color = TealPrimary, fontWeight = FontWeight.SemiBold)
                            documents.take(8).forEach { doc ->
                                Spacer(Modifier.height(8.dp))
                                MockCard {
                                    Text(doc.title, fontWeight = FontWeight.SemiBold, color = TealPrimary)
                                    Text(
                                        "${doc.subjectName} · ${doc.mimeType}",
                                        color = InkMuted,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                        }
                        assignments.forEach { a ->
                            Spacer(Modifier.height(8.dp))
                            MockCard(highlight = !a.done) {
                                Text(a.title, fontWeight = FontWeight.SemiBold, color = TealPrimary)
                                Text(
                                    "${a.subjectName} · ${SimpleDateFormat("dd MMM", Locale.US).format(Date(a.dueAt))}" +
                                        if (a.documentUri.isNotBlank()) " · স্ক্যান আছে" else "",
                                    color = InkMuted,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        LurkField(subject, { subject = it }, "সাবজেক্ট")
                        Spacer(Modifier.height(6.dp))
                        LurkField(title, { title = it }, "অ্যাসাইনমেন্ট")
                        Spacer(Modifier.height(8.dp))
                        PrimaryButton("অ্যাসাইনমেন্ট যোগ") {
                            if (title.isBlank()) return@PrimaryButton
                            vm.addAssignment(subject.ifBlank { "সাবজেক্ট" }, title)
                            title = ""
                        }
                    }
                    else -> {
                        SectionHead("রেজাল্ট", leadingIcon = Icons.Outlined.BarChart)
                        results.forEach { r ->
                            Spacer(Modifier.height(8.dp))
                            MockCard {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        Modifier.size(42.dp).clip(CircleShape).background(TealPrimary.copy(0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("${r.score.toInt()}", color = TealPrimary, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text("${r.subjectName} · ${r.examType}", fontWeight = FontWeight.SemiBold, color = Ink)
                                        Text(SimpleDateFormat("dd MMM", Locale.US).format(Date(r.date)), color = InkMuted, fontSize = 12.sp)
                                    }
                                    Text("${r.score.toInt()}/${r.total.toInt()}", color = GoldAccent, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        LurkField(subject, { subject = it }, "সাবজেক্ট")
                        Spacer(Modifier.height(6.dp))
                        LurkField(score, { score = it.filter { ch -> ch.isDigit() || ch == '.' } }, "নম্বর")
                        Spacer(Modifier.height(6.dp))
                        LurkField(total, { total = it.filter { ch -> ch.isDigit() || ch == '.' } }, "পূর্ণমান")
                        Spacer(Modifier.height(8.dp))
                        PrimaryButton("রেজাল্ট সেভ") {
                            val s = score.toDoubleOrNull() ?: return@PrimaryButton
                            val t = total.toDoubleOrNull() ?: 20.0
                            vm.addResult(subject.ifBlank { "সাবজেক্ট" }, "ক্লাস টেস্ট", s, t)
                            score = ""
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudySubNav(selected: Int, onSelect: (Int) -> Unit) {
    val items = listOf(
        Triple("রুটিন", Icons.Outlined.CalendarMonth, 0),
        Triple("নোট", Icons.Outlined.Description, 1),
        Triple("ডকুমেন্ট", Icons.Outlined.Folder, 2),
        Triple("রেজাল্ট", Icons.Outlined.BarChart, 3)
    )
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE8DFD0), RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { (label, icon, index) ->
            val on = selected == index
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable { onSelect(index) }
                    .padding(horizontal = 6.dp)
            ) {
                Icon(icon, null, tint = if (on) GoldAccent else TealPrimary, modifier = Modifier.size(20.dp))
                Text(label, color = if (on) GoldAccent else TealPrimary, fontSize = 11.sp, fontWeight = if (on) FontWeight.Bold else FontWeight.Normal)
                if (on) {
                    Spacer(Modifier.height(4.dp))
                    Box(Modifier.width(28.dp).height(2.dp).background(GoldAccent, RoundedCornerShape(2.dp)))
                }
            }
        }
    }
}
