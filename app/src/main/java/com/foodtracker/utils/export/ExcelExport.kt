package com.foodtracker.utils.export

import android.content.Context
import com.foodtracker.data.local.entities.FoodEntryEntity
import com.foodtracker.domain.repository.MonthlySummary
import org.apache.poi.ss.usermodel.*
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
        
        // Create header style
        val headerStyle = workbook.createCellStyle()
        headerStyle.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
        headerStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
        val headerFont = workbook.createFont()
        headerFont.bold = true
        headerStyle.setFont(headerFont)
        
        // Create header row
        val headers = arrayOf("Date", "Day", "Breakfast", "Lunch", "Dinner", "Meals", "Daily Expense", "Remarks")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { index, header ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
            cell.cellStyle = headerStyle
        }
        
        // Add data rows
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
        
        // Auto-size columns
        for (i in 0..7) {
            sheet.autoSizeColumn(i)
        }
        
        // Create summary sheet
        val summarySheet = workbook.createSheet("Summary")
        val summaryHeaderStyle = workbook.createCellStyle()
        summaryHeaderStyle.fillForegroundColor = IndexedColors.LIGHT_GREEN.index
        summaryHeaderStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
        val summaryFont = workbook.createFont()
        summaryFont.bold = true
        summaryHeaderStyle.setFont(summaryFont)
        
        val summaryData = listOf(
            "Month" to yearMonth.toString(),
            "Total Days" to summary.totalDays.toString(),
            "Present Days" to summary.presentDays.toString(),
            "Absent Days" to summary.absentDays.toString(),
            "Total Breakfast" to summary.totalBreakfast.toString(),
            "Total Lunch" to summary.totalLunch.toString(),
            "Total Dinner" to summary.totalDinner.toString(),
            "Total Meals" to summary.totalMeals.toString(),
            "Total Expense" to summary.totalExpense.toString(),
            "Average Daily Expense" to summary.averageDailyExpense.toString(),
            "Monthly Charge" to summary.monthlyCharge.toString(),
            "Paid Amount" to summary.paidAmount.toString(),
            "Balance" to summary.balance.toString(),
            "Days Covered" to summary.daysCovered.toString(),
            "Remaining Days" to summary.remainingDays.toString(),
            "Remaining Meals" to summary.remainingMeals.toString()
        )
        
        summaryData.forEachIndexed { index, (label, value) ->
            val row = summarySheet.createRow(index)
            val labelCell = row.createCell(0)
            labelCell.setCellValue(label)
            labelCell.cellStyle = summaryHeaderStyle
            
            val valueCell = row.createCell(1)
            valueCell.setCellValue(value)
        }
        
        summarySheet.autoSizeColumn(0)
        summarySheet.autoSizeColumn(1)
        
        // Save file
        val fileName = "food_expense_${yearMonth}.xlsx"
        val file = File(context.filesDir, fileName)
        FileOutputStream(file).use { workbook.write(it) }
        workbook.close()
        
        return file
    }
}
