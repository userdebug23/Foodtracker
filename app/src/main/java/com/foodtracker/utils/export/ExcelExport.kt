package com.foodtracker.utils.export

import android.content.Context
import com.foodtracker.data.local.entities.FoodEntryEntity
import com.foodtracker.domain.repository.MonthlySummary
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.YearMonth

class ExcelExporter {
    
    fun exportMonthToExcel(
        context: Context,
        entries: List<FoodEntryEntity>,
        yearMonth: YearMonth,
        summary: MonthlySummary
    ): File {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Food Entries")
        
        val headerStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            setFont(workbook.createFont().apply {
                isBold = true
            })
        }
        
        val headers = arrayOf("Date", "Day", "Breakfast", "Lunch", "Dinner", "Meals", "Daily Expense", "Remarks")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }
        
        entries.forEachIndexed { index, entry ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(entry.date.toString())
            row.createCell(1).setCellValue(entry.dayOfWeek)
            row.createCell(2).setCellValue(if (entry.breakfast) "Present" else "Absent")
            row.createCell(3).setCellValue(if (entry.lunch) "Present" else "Absent")
            row.createCell(4).setCellValue(if (entry.dinner) "Present" else "Absent")
            row.createCell(5).setCellValue(entry.mealCount.toDouble())
            row.createCell(6).setCellValue(entry.dailyExpense)
            row.createCell(7).setCellValue(entry.remarks ?: "")
        }
        
        for (i in 0..7) {
            sheet.autoSizeColumn(i)
        }
        
        val fileName = "food_expense_${yearMonth}.xlsx"
        val file = File(context.filesDir, fileName)
        FileOutputStream(file).use { workbook.write(it) }
        workbook.close()
        
        return file
    }
}
