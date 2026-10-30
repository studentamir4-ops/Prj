package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.ClientEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.ReceiptEntity
import com.example.data.model.WorkdayEntity
import com.example.util.PersianDateUtils
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class AppRepository(private val database: AppDatabase) {

    private val clientDao = database.clientDao()
    private val projectDao = database.projectDao()
    private val receiptDao = database.receiptDao()
    private val workdayDao = database.workdayDao()

    // Clients
    val allClients: Flow<List<ClientEntity>> = clientDao.getAllClients()

    suspend fun insertClient(client: ClientEntity): Long = clientDao.insertClient(client)

    suspend fun updateClient(client: ClientEntity) = clientDao.updateClient(client)

    suspend fun deleteClient(client: ClientEntity) = clientDao.deleteClient(client)

    // Projects
    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    suspend fun insertProject(project: ProjectEntity): Long {
        val share = ProjectEntity.calculateShare(
            totalAmount = project.totalAmount,
            shareType = project.myShareType,
            customAmount = project.customAmount,
            percentage = project.mySharePercentage
        )
        return projectDao.insertProject(project.copy(myShareCalculated = share))
    }

    suspend fun updateProject(project: ProjectEntity) {
        val share = ProjectEntity.calculateShare(
            totalAmount = project.totalAmount,
            shareType = project.myShareType,
            customAmount = project.customAmount,
            percentage = project.mySharePercentage
        )
        projectDao.updateProject(project.copy(myShareCalculated = share))
    }

    suspend fun deleteProject(project: ProjectEntity) = projectDao.deleteProject(project)

    // Receipts (Independent)
    val allReceipts: Flow<List<ReceiptEntity>> = receiptDao.getAllReceipts()

    suspend fun insertReceipt(receipt: ReceiptEntity): Long = receiptDao.insertReceipt(receipt)

    suspend fun updateReceipt(receipt: ReceiptEntity) = receiptDao.updateReceipt(receipt)

    suspend fun deleteReceipt(receipt: ReceiptEntity) = receiptDao.deleteReceipt(receipt)

    // Workdays
    val allWorkdays: Flow<List<WorkdayEntity>> = workdayDao.getAllWorkdays()

    suspend fun toggleWorkday(solarDate: String, year: Int, month: Int, day: Int, note: String = "") {
        val existing = workdayDao.getWorkdayByDate(solarDate)
        if (existing != null) {
            workdayDao.deleteWorkday(existing)
        } else {
            workdayDao.insertWorkday(
                WorkdayEntity(
                    solarDate = solarDate,
                    year = year,
                    month = month,
                    day = day,
                    note = note
                )
            )
        }
    }

    suspend fun deleteWorkday(workday: WorkdayEntity) = workdayDao.deleteWorkday(workday)

    // Initial Data Seed (Only if totally empty)
    suspend fun seedInitialDataIfNeeded() {
        val existingClients = clientDao.getAllClientsList()
        if (existingClients.isEmpty()) {
            val client1Id = clientDao.insertClient(
                ClientEntity(
                    name = "شرکت فناوران نوین",
                    phone = "09121234567",
                    address = "تهران، خیابان ولیعصر، برج سپهر"
                )
            )
            val client2Id = clientDao.insertClient(
                ClientEntity(
                    name = "استودیو طراحی افق",
                    phone = "09359876543",
                    address = "اصفهان، میدان آزادی"
                )
            )
            val client3Id = clientDao.insertClient(
                ClientEntity(
                    name = "دکتر جلالی",
                    phone = "09131112233",
                    address = "شیراز، بلوار ارم"
                )
            )

            val today = PersianDateUtils.getTodayJalali()
            val todayStr = today.formatToString()

            // Projects: default 15% share or custom
            val proj1Id = projectDao.insertProject(
                ProjectEntity(
                    title = "طراحی و توسعه پلتفرم تجارت الکترونیک",
                    clientId = client1Id,
                    totalAmount = 120_000_000L,
                    myShareType = ProjectEntity.SHARE_TYPE_PERCENTAGE,
                    mySharePercentage = 15.0,
                    myShareCalculated = 18_000_000L,
                    description = "توسعه فرانت‌اند و بک‌اند با رعایت آخرین استانداردها",
                    status = ProjectEntity.STATUS_IN_PROGRESS,
                    startDate = "1405/07/01",
                    endDate = "1405/09/30"
                )
            )

            val proj2Id = projectDao.insertProject(
                ProjectEntity(
                    title = "بازطراحی اپلیکیشن موبایل سازمانی",
                    clientId = client2Id,
                    totalAmount = 80_000_000L,
                    myShareType = ProjectEntity.SHARE_TYPE_CUSTOM,
                    customAmount = 25_000_000L,
                    myShareCalculated = 25_000_000L,
                    description = "طراحی رابط کاربری Glassmorphism در فلاتر و اندروید",
                    status = ProjectEntity.STATUS_COMPLETED,
                    startDate = "1405/06/15",
                    endDate = "1405/08/01"
                )
            )

            projectDao.insertProject(
                ProjectEntity(
                    title = "سامانه نوبت‌دهی آنلاین کلینیک",
                    clientId = client3Id,
                    totalAmount = 50_000_000L,
                    myShareType = ProjectEntity.SHARE_TYPE_PERCENTAGE,
                    mySharePercentage = 15.0,
                    myShareCalculated = 7_500_000L,
                    description = "طراحی سایت وردپرسی سفارشی و پنل پیامک",
                    status = ProjectEntity.STATUS_IN_PROGRESS,
                    startDate = "1405/08/01",
                    endDate = "1405/08/25"
                )
            )

            // Independent Receipts
            receiptDao.insertReceipt(
                ReceiptEntity(
                    amount = 15_000_000L,
                    date = todayStr,
                    paymentMethod = "پایا",
                    description = "پیش‌پرداخت قسط اول پلتفرم تجارت الکترونیک",
                    projectId = proj1Id
                )
            )

            receiptDao.insertReceipt(
                ReceiptEntity(
                    amount = 25_000_000L,
                    date = "1405/07/20",
                    paymentMethod = "کارت به کارت",
                    description = "تسویه کامل پروژه بازطراحی اپلیکیشن",
                    projectId = proj2Id
                )
            )

            receiptDao.insertReceipt(
                ReceiptEntity(
                    amount = 5_000_000L,
                    date = todayStr,
                    paymentMethod = "انتقال ساتنا",
                    description = "مشاوره فنی خارج از پروژه",
                    projectId = null
                )
            )

            // Seed a few workdays in the current month
            val y = today.year
            val m = today.month
            val sampleDays = listOf(1, 3, 4, 8, 10, 12, 15, 18, 21, 24)
            for (d in sampleDays) {
                if (d <= PersianDateUtils.getDaysInMonth(y, m)) {
                    val dateStr = String.format("%04d/%02d/%02d", y, m, d)
                    workdayDao.insertWorkday(
                        WorkdayEntity(
                            solarDate = dateStr,
                            year = y,
                            month = m,
                            day = d,
                            note = "فعالیت کاری"
                        )
                    )
                }
            }
        }
    }

    // Export entire database to JSON backup string
    suspend fun createBackupJson(): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("timestamp", System.currentTimeMillis())

        // Clients
        val clients = clientDao.getAllClientsList()
        val clientsArray = JSONArray()
        for (c in clients) {
            val obj = JSONObject()
            obj.put("id", c.id)
            obj.put("name", c.name)
            obj.put("phone", c.phone)
            obj.put("address", c.address)
            obj.put("createdAt", c.createdAt)
            clientsArray.put(obj)
        }
        root.put("clients", clientsArray)

        // Projects
        val projects = projectDao.getAllProjectsList()
        val projectsArray = JSONArray()
        for (p in projects) {
            val obj = JSONObject()
            obj.put("id", p.id)
            obj.put("title", p.title)
            obj.put("clientId", p.clientId)
            obj.put("totalAmount", p.totalAmount)
            obj.put("myShareType", p.myShareType)
            obj.put("mySharePercentage", p.mySharePercentage)
            obj.put("customAmount", p.customAmount)
            obj.put("myShareCalculated", p.myShareCalculated)
            obj.put("description", p.description)
            obj.put("status", p.status)
            obj.put("startDate", p.startDate)
            obj.put("endDate", p.endDate)
            obj.put("createdAt", p.createdAt)
            projectsArray.put(obj)
        }
        root.put("projects", projectsArray)

        // Receipts
        val receipts = receiptDao.getAllReceiptsList()
        val receiptsArray = JSONArray()
        for (r in receipts) {
            val obj = JSONObject()
            obj.put("id", r.id)
            obj.put("amount", r.amount)
            obj.put("date", r.date)
            obj.put("paymentMethod", r.paymentMethod)
            obj.put("description", r.description)
            if (r.projectId != null) {
                obj.put("projectId", r.projectId)
            } else {
                obj.put("projectId", JSONObject.NULL)
            }
            obj.put("createdAt", r.createdAt)
            receiptsArray.put(obj)
        }
        root.put("receipts", receiptsArray)

        // Workdays
        val workdays = workdayDao.getAllWorkdaysList()
        val workdaysArray = JSONArray()
        for (w in workdays) {
            val obj = JSONObject()
            obj.put("id", w.id)
            obj.put("solarDate", w.solarDate)
            obj.put("year", w.year)
            obj.put("month", w.month)
            obj.put("day", w.day)
            obj.put("note", w.note)
            obj.put("createdAt", w.createdAt)
            workdaysArray.put(obj)
        }
        root.put("workdays", workdaysArray)

        return root.toString(2)
    }

    // Restore database from JSON backup safely
    suspend fun restoreFromJson(jsonString: String): Result<String> {
        return try {
            val root = JSONObject(jsonString)
            if (!root.has("clients") && !root.has("projects")) {
                return Result.failure(IllegalArgumentException("فرمت فایل پشتیبان نامعتبر است"))
            }

            // Restore clients
            val clientsArray = root.optJSONArray("clients")
            if (clientsArray != null) {
                for (i in 0 until clientsArray.length()) {
                    val obj = clientsArray.getJSONObject(i)
                    val client = ClientEntity(
                        name = obj.getString("name"),
                        phone = obj.optString("phone", ""),
                        address = obj.optString("address", ""),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                    clientDao.insertClient(client)
                }
            }

            // Restore projects
            val projectsArray = root.optJSONArray("projects")
            if (projectsArray != null) {
                for (i in 0 until projectsArray.length()) {
                    val obj = projectsArray.getJSONObject(i)
                    val project = ProjectEntity(
                        title = obj.getString("title"),
                        clientId = obj.optLong("clientId", 0L),
                        totalAmount = obj.optLong("totalAmount", 0L),
                        myShareType = obj.optString("myShareType", ProjectEntity.SHARE_TYPE_PERCENTAGE),
                        mySharePercentage = obj.optDouble("mySharePercentage", 15.0),
                        customAmount = obj.optLong("customAmount", 0L),
                        myShareCalculated = obj.optLong("myShareCalculated", 0L),
                        description = obj.optString("description", ""),
                        status = obj.optString("status", ProjectEntity.STATUS_IN_PROGRESS),
                        startDate = obj.optString("startDate", ""),
                        endDate = obj.optString("endDate", ""),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                    projectDao.insertProject(project)
                }
            }

            // Restore receipts
            val receiptsArray = root.optJSONArray("receipts")
            if (receiptsArray != null) {
                for (i in 0 until receiptsArray.length()) {
                    val obj = receiptsArray.getJSONObject(i)
                    val pId = if (obj.isNull("projectId")) null else obj.optLong("projectId")
                    val receipt = ReceiptEntity(
                        amount = obj.getLong("amount"),
                        date = obj.getString("date"),
                        paymentMethod = obj.optString("paymentMethod", "کارت به کارت"),
                        description = obj.optString("description", ""),
                        projectId = pId,
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                    receiptDao.insertReceipt(receipt)
                }
            }

            // Restore workdays
            val workdaysArray = root.optJSONArray("workdays")
            if (workdaysArray != null) {
                for (i in 0 until workdaysArray.length()) {
                    val obj = workdaysArray.getJSONObject(i)
                    val solarDate = obj.getString("solarDate")
                    val existing = workdayDao.getWorkdayByDate(solarDate)
                    if (existing == null) {
                        val workday = WorkdayEntity(
                            solarDate = solarDate,
                            year = obj.getInt("year"),
                            month = obj.getInt("month"),
                            day = obj.getInt("day"),
                            note = obj.optString("note", ""),
                            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                        )
                        workdayDao.insertWorkday(workday)
                    }
                }
            }

            Result.success("اطلاعات با موفقیت بازیابی شد")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
