package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ClientEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ReceiptEntity
import com.example.data.model.WorkdayEntity
import com.example.data.repository.AppRepository
import com.example.util.PersianDateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class DashboardFilter(val title: String) {
    TODAY("روزانه"),
    THIS_WEEK("هفتگی"),
    THIS_MONTH("ماهانه"),
    THIS_YEAR("سالانه"),
    ALL("کل")
}

data class DashboardStats(
    val myShareTotal: Long = 0L,
    val totalReceived: Long = 0L,
    val outstandingReceivables: Long = 0L,
    val totalProjectsCount: Int = 0,
    val inProgressProjectsCount: Int = 0,
    val completedProjectsCount: Int = 0,
    val workdaysCount: Int = 0
)

data class ProjectShareItem(
    val projectTitle: String,
    val clientName: String,
    val totalAmount: Long,
    val myShare: Long,
    val status: String
)

data class ReceiptDetailItem(
    val amount: Long,
    val date: String,
    val paymentMethod: String,
    val description: String,
    val projectTitle: String?,
    val clientName: String?
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = AppRepository(db)
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    val clients: StateFlow<List<ClientEntity>> = repository.allClients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val receipts: StateFlow<List<ReceiptEntity>> = repository.allReceipts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workdays: StateFlow<List<WorkdayEntity>> = repository.allWorkdays
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedFilter = MutableStateFlow(DashboardFilter.ALL)
    val selectedFilter: StateFlow<DashboardFilter> = _selectedFilter.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val todayJalali = PersianDateUtils.getTodayJalali()
    private val _calendarYear = MutableStateFlow(todayJalali.year)
    val calendarYear: StateFlow<Int> = _calendarYear.asStateFlow()

    private val _calendarMonth = MutableStateFlow(todayJalali.month)
    val calendarMonth: StateFlow<Int> = _calendarMonth.asStateFlow()

    // Modals visibility state
    private val _showMyShareModal = MutableStateFlow(false)
    val showMyShareModal: StateFlow<Boolean> = _showMyShareModal.asStateFlow()

    private val _showTotalReceivedModal = MutableStateFlow(false)
    val showTotalReceivedModal: StateFlow<Boolean> = _showTotalReceivedModal.asStateFlow()

    // Message feedback
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun dismissUserMessage() {
        _userMessage.value = null
    }

    fun setFilter(filter: DashboardFilter) {
        _selectedFilter.value = filter
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setCalendarMonth(year: Int, month: Int) {
        _calendarYear.value = year
        _calendarMonth.value = month
    }

    fun openMyShareModal() {
        _showMyShareModal.value = true
    }

    fun closeMyShareModal() {
        _showMyShareModal.value = false
    }

    fun openTotalReceivedModal() {
        _showTotalReceivedModal.value = true
    }

    fun closeTotalReceivedModal() {
        _showTotalReceivedModal.value = false
    }

    // Filtered items & Dashboard Stats
    val dashboardStats: StateFlow<DashboardStats> = combine(
        projects,
        receipts,
        workdays,
        _selectedFilter
    ) { pList, rList, wList, filter ->
        val filterKey = when (filter) {
            DashboardFilter.TODAY -> "TODAY"
            DashboardFilter.THIS_WEEK -> "THIS_WEEK"
            DashboardFilter.THIS_MONTH -> "THIS_MONTH"
            DashboardFilter.THIS_YEAR -> "THIS_YEAR"
            DashboardFilter.ALL -> "ALL"
        }

        val filteredProjects = if (filterKey == "ALL") pList else {
            pList.filter { PersianDateUtils.matchesFilter(it.startDate, filterKey) }
        }

        val filteredReceipts = if (filterKey == "ALL") rList else {
            rList.filter { PersianDateUtils.matchesFilter(it.date, filterKey) }
        }

        val filteredWorkdays = if (filterKey == "ALL") wList else {
            wList.filter { PersianDateUtils.matchesFilter(it.solarDate, filterKey) }
        }

        val myShare = filteredProjects.sumOf { it.myShareCalculated }
        val received = filteredReceipts.sumOf { it.amount }
        val receivables = myShare - received

        val totalProj = filteredProjects.size
        val inProgress = filteredProjects.count { it.status == ProjectEntity.STATUS_IN_PROGRESS }
        val completed = filteredProjects.count { it.status == ProjectEntity.STATUS_COMPLETED }
        val workdaysCount = filteredWorkdays.size

        DashboardStats(
            myShareTotal = myShare,
            totalReceived = received,
            outstandingReceivables = receivables,
            totalProjectsCount = totalProj,
            inProgressProjectsCount = inProgress,
            completedProjectsCount = completed,
            workdaysCount = workdaysCount
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    // Detailed Project Share List for Center Modal
    val projectShareItems: StateFlow<List<ProjectShareItem>> = combine(
        projects,
        clients,
        _selectedFilter
    ) { pList, cList, filter ->
        val clientMap = cList.associateBy { it.id }
        val filterKey = when (filter) {
            DashboardFilter.TODAY -> "TODAY"
            DashboardFilter.THIS_WEEK -> "THIS_WEEK"
            DashboardFilter.THIS_MONTH -> "THIS_MONTH"
            DashboardFilter.THIS_YEAR -> "THIS_YEAR"
            DashboardFilter.ALL -> "ALL"
        }
        val filtered = if (filterKey == "ALL") pList else {
            pList.filter { PersianDateUtils.matchesFilter(it.startDate, filterKey) }
        }

        filtered.map { p ->
            ProjectShareItem(
                projectTitle = p.title,
                clientName = clientMap[p.clientId]?.name ?: "کارفرمای نامشخص",
                totalAmount = p.totalAmount,
                myShare = p.myShareCalculated,
                status = p.status
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Detailed Receipts List for Center Modal
    val receiptDetailItems: StateFlow<List<ReceiptDetailItem>> = combine(
        receipts,
        projects,
        clients,
        _selectedFilter
    ) { rList, pList, cList, filter ->
        val projectMap = pList.associateBy { it.id }
        val clientMap = cList.associateBy { it.id }

        val filterKey = when (filter) {
            DashboardFilter.TODAY -> "TODAY"
            DashboardFilter.THIS_WEEK -> "THIS_WEEK"
            DashboardFilter.THIS_MONTH -> "THIS_MONTH"
            DashboardFilter.THIS_YEAR -> "THIS_YEAR"
            DashboardFilter.ALL -> "ALL"
        }
        val filtered = if (filterKey == "ALL") rList else {
            rList.filter { PersianDateUtils.matchesFilter(it.date, filterKey) }
        }

        filtered.map { r ->
            val proj = r.projectId?.let { projectMap[it] }
            val client = proj?.clientId?.let { clientMap[it] }

            ReceiptDetailItem(
                amount = r.amount,
                date = r.date,
                paymentMethod = r.paymentMethod,
                description = r.description,
                projectTitle = proj?.title,
                clientName = client?.name
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // CRUD Client
    fun addClient(name: String, phone: String, address: String) {
        viewModelScope.launch {
            repository.insertClient(ClientEntity(name = name.trim(), phone = phone.trim(), address = address.trim()))
            _userMessage.value = "کارفرما با موفقیت افزوده شد"
        }
    }

    fun updateClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.updateClient(client)
            _userMessage.value = "اطلاعات کارفرما بروزرسانی شد"
        }
    }

    fun deleteClient(client: ClientEntity) {
        viewModelScope.launch {
            repository.deleteClient(client)
            _userMessage.value = "کارفرما حذف شد"
        }
    }

    // CRUD Project
    fun addProject(
        title: String,
        clientId: Long,
        totalAmount: Long,
        myShareType: String,
        customAmount: Long,
        percentage: Double = 15.0,
        description: String,
        status: String,
        startDate: String,
        endDate: String
    ) {
        viewModelScope.launch {
            repository.insertProject(
                ProjectEntity(
                    title = title.trim(),
                    clientId = clientId,
                    totalAmount = totalAmount,
                    myShareType = myShareType,
                    mySharePercentage = percentage,
                    customAmount = customAmount,
                    description = description.trim(),
                    status = status,
                    startDate = startDate.ifEmpty { PersianDateUtils.getTodayJalali().formatToString() },
                    endDate = endDate
                )
            )
            _userMessage.value = "پروژه با موفقیت ایجاد شد"
        }
    }

    fun updateProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.updateProject(project)
            _userMessage.value = "اطلاعات پروژه بروزرسانی شد"
        }
    }

    fun deleteProject(project: ProjectEntity) {
        viewModelScope.launch {
            repository.deleteProject(project)
            _userMessage.value = "پروژه با موفقیت حذف شد"
        }
    }

    // CRUD Receipt
    fun addReceipt(
        amount: Long,
        date: String,
        paymentMethod: String,
        description: String,
        projectId: Long?
    ) {
        viewModelScope.launch {
            repository.insertReceipt(
                ReceiptEntity(
                    amount = amount,
                    date = date.ifEmpty { PersianDateUtils.getTodayJalali().formatToString() },
                    paymentMethod = paymentMethod,
                    description = description.trim(),
                    projectId = projectId
                )
            )
            _userMessage.value = "دریافتی با موفقیت ثبت شد"
        }
    }

    fun updateReceipt(receipt: ReceiptEntity) {
        viewModelScope.launch {
            repository.updateReceipt(receipt)
            _userMessage.value = "اطلاعات دریافتی بروزرسانی شد"
        }
    }

    fun deleteReceipt(receipt: ReceiptEntity) {
        viewModelScope.launch {
            repository.deleteReceipt(receipt)
            _userMessage.value = "دریافتی حذف شد"
        }
    }

    // Workday toggle
    fun toggleWorkday(year: Int, month: Int, day: Int) {
        val solarDate = String.format("%04d/%02d/%02d", year, month, day)
        viewModelScope.launch {
            repository.toggleWorkday(solarDate, year, month, day)
        }
    }

    // Backup & Restore
    fun generateBackup(onReady: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val json = repository.createBackupJson()
                onReady(json)
                _userMessage.value = "نسخه پشتیبان آماده شد"
            } catch (e: Exception) {
                _userMessage.value = "خطا در تهیه پشتیبان: ${e.localizedMessage}"
            }
        }
    }

    fun restoreBackup(jsonString: String) {
        viewModelScope.launch {
            val result = repository.restoreFromJson(jsonString)
            result.onSuccess { msg ->
                _userMessage.value = msg
            }.onFailure { err ->
                _userMessage.value = "خطا در بازیابی: ${err.localizedMessage}"
            }
        }
    }
}
