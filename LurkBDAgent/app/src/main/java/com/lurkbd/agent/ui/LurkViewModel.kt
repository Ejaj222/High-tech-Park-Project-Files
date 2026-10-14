package com.lurkbd.agent.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.lurkbd.agent.data.model.AssignmentEntity
import com.lurkbd.agent.data.model.ClassPeriodEntity
import com.lurkbd.agent.data.model.CustomerEntity
import com.lurkbd.agent.data.model.LoanEntity
import com.lurkbd.agent.data.model.MedicineEntity
import com.lurkbd.agent.data.model.ProductEntity
import com.lurkbd.agent.data.model.ResultEntity
import com.lurkbd.agent.data.model.StudyBlockEntity
import com.lurkbd.agent.data.model.StudyNoteEntity
import com.lurkbd.agent.data.model.TransactionEntity
import com.lurkbd.agent.data.repository.LurkRepository
import com.lurkbd.agent.voice.VoiceIntent
import com.lurkbd.agent.voice.VoiceIntentParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class HisabSummary(
    val income: Double = 0.0,
    val expense: Double = 0.0,
    val transactions: List<TransactionEntity> = emptyList(),
    val loans: List<LoanEntity> = emptyList()
)

data class VoiceUiState(
    val open: Boolean = false,
    val transcript: String = "",
    val preview: String = "",
    val intent: VoiceIntent? = null,
    val message: String = ""
)

class LurkViewModel(private val repo: LurkRepository) : ViewModel() {
    val hisab: StateFlow<HisabSummary> = combine(
        repo.todayTransactions(),
        repo.loans()
    ) { txs, loans ->
        HisabSummary(
            income = txs.filter { it.type == "INCOME" }.sumOf { it.amount },
            expense = txs.filter { it.type == "EXPENSE" }.sumOf { it.amount },
            transactions = txs,
            loans = loans
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HisabSummary())

    val allTransactions: StateFlow<List<TransactionEntity>> =
        repo.transactions().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val customers: StateFlow<List<CustomerEntity>> =
        repo.customers().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val products: StateFlow<List<ProductEntity>> =
        repo.products().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val classPeriods: StateFlow<List<ClassPeriodEntity>> =
        repo.classPeriods().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val studyBlocks: StateFlow<List<StudyBlockEntity>> =
        repo.studyBlocks().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val studyNotes: StateFlow<List<StudyNoteEntity>> =
        repo.studyNotes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val assignments: StateFlow<List<AssignmentEntity>> =
        repo.assignments().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val results: StateFlow<List<ResultEntity>> =
        repo.results().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val medicines: StateFlow<List<MedicineEntity>> =
        repo.medicines().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _voice = MutableStateFlow(VoiceUiState())
    val voice: StateFlow<VoiceUiState> = _voice

    val prayerAlarms = MutableStateFlow(
        listOf(
            "ফজর" to "05:05",
            "যোহর" to "12:10",
            "আসর" to "15:45",
            "মাগরিব" to "17:55",
            "এশা" to "19:20"
        )
    )
    val ramadanAlarms = MutableStateFlow("সেহেরি 04:20 · ইফতার 17:55" to true)

    fun addExpense(amount: Double, category: String, channel: String) = viewModelScope.launch {
        repo.addTransaction("EXPENSE", amount, category, channel)
    }

    fun addIncome(amount: Double, category: String, channel: String) = viewModelScope.launch {
        repo.addTransaction("INCOME", amount, category, channel)
    }

    fun addCustomerCredit(name: String, amount: Double) = viewModelScope.launch {
        repo.addCustomerCredit(name, amount)
    }

    fun setStock(name: String, qty: Double) = viewModelScope.launch {
        repo.setStock(name, qty)
    }

    fun addClass(subject: String, startMinutes: Int) = viewModelScope.launch {
        val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        repo.addClass(subject, day, startMinutes, startMinutes + 60)
    }

    fun addStudyBlock(subject: String, startMinutes: Int) = viewModelScope.launch {
        val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        repo.addStudyBlock(subject, day, startMinutes, startMinutes + 60)
    }

    fun addNote(subject: String, title: String, body: String) = viewModelScope.launch {
        repo.addNote(subject, title, body)
    }

    fun addResult(subject: String, examType: String, score: Double, total: Double) =
        viewModelScope.launch {
            repo.addResult(subject, examType, score, total)
        }

    fun addAssignment(subject: String, title: String) = viewModelScope.launch {
        repo.addAssignment(subject, title, System.currentTimeMillis() + 86_400_000 * 3)
    }

    fun addMedicine(name: String, timeMinutes: Int) = viewModelScope.launch {
        repo.addMedicine(name, timeMinutes)
    }

    fun toggleMedicine(item: MedicineEntity, active: Boolean) = viewModelScope.launch {
        repo.setMedicineActive(item, active)
    }

    fun toggleClassAlarm(item: ClassPeriodEntity, enabled: Boolean) = viewModelScope.launch {
        repo.setClassAlarm(item, enabled)
    }

    fun openVoice() {
        _voice.value = VoiceUiState(open = true, message = "শুনছি… বাংলায় বলুন")
    }

    fun closeVoice() {
        _voice.value = VoiceUiState()
    }

    fun onTranscript(text: String) {
        val intent = VoiceIntentParser.parse(text)
        _voice.value = _voice.value.copy(
            transcript = text,
            intent = intent,
            preview = VoiceIntentParser.describe(intent),
            message = if (intent is VoiceIntent.Unknown) intent.reason else "নিশ্চিত করুন"
        )
    }

    fun confirmVoice() = viewModelScope.launch {
        when (val intent = _voice.value.intent) {
            is VoiceIntent.Expense -> repo.addTransaction(
                "EXPENSE",
                intent.amount,
                intent.category,
                intent.channel
            )
            is VoiceIntent.Income -> repo.addTransaction(
                "INCOME",
                intent.amount,
                intent.category,
                intent.channel
            )
            is VoiceIntent.CreditAdd -> repo.addCustomerCredit(intent.name, intent.amount)
            is VoiceIntent.StockSet -> repo.setStock(intent.name, intent.qty)
            is VoiceIntent.MedicineAlarm -> repo.addMedicine(intent.name, intent.timeMinutes)
            is VoiceIntent.ClassAdd -> repo.addClass(
                intent.subject,
                intent.dayOfWeek,
                intent.startMinutes,
                intent.startMinutes + 60
            )
            is VoiceIntent.StudyRoutine -> {
                val day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                repo.addStudyBlock(intent.subject, day, intent.timeMinutes, intent.timeMinutes + 60)
            }
            is VoiceIntent.ResultAdd -> repo.addResult(
                intent.subject,
                "ক্লাস টেস্ট",
                intent.score,
                intent.total
            )
            else -> Unit
        }
        _voice.value = VoiceUiState(message = "সেভ হয়েছে")
        closeVoice()
    }

    companion object {
        fun factory(repo: LurkRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LurkViewModel(repo) as T
                }
            }
    }
}
