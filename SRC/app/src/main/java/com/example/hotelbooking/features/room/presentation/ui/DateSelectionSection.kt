package com.example.hotelbooking.features.room.presentation.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelectionSection(
    checkInMillis: Long, checkOutMillis: Long, onDateConfirm: (Long, Long) -> Unit
) {
    var showCheckInDialog by remember { mutableStateOf(false) }
    var showCheckOutDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DateInputItem(
            label = "Check-in",
            dateStr = checkInMillis.toDisplayString(),
            onClick = { showCheckInDialog = true })

        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Black)

        DateInputItem(
            label = "Check-out",
            dateStr = checkOutMillis.toDisplayString(),
            onClick = { showCheckOutDialog = true })
    }

    if (showCheckInDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = checkInMillis, selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= System.currentTimeMillis() - 86400000
                }
            })

        DatePickerDialog(onDismissRequest = { showCheckInDialog = false }, confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { newStart ->
                    showCheckInDialog = false
                    showCheckOutDialog = true

                    val newEnd =
                        if (checkOutMillis <= newStart) newStart + 86400000 else checkOutMillis
                    onDateConfirm(newStart, newEnd)
                }
            }) { Text("Next") }
        }, dismissButton = {
            TextButton(onClick = {
                showCheckInDialog = false
            }) { Text("Cancel") }
        }) {
            DatePicker(state = datePickerState, title = {
                Text(
                    "Select Check-in Date", fontSize = 18.sp, modifier = Modifier.padding(16.dp)
                )
            })
        }
    }

    if (showCheckOutDialog) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = checkOutMillis, selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis > checkInMillis
                }
            })

        DatePickerDialog(onDismissRequest = { showCheckOutDialog = false }, confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { newEnd ->
                    showCheckOutDialog = false
                    onDateConfirm(checkInMillis, newEnd)
                }
            }) { Text("Done") }
        }, dismissButton = {
            TextButton(onClick = {
                showCheckOutDialog = false
            }) { Text("Cancel") }
        }) {
            DatePicker(state = datePickerState, title = {
                Text(
                    "Select Check-out Date", fontSize = 18.sp, modifier = Modifier.padding(16.dp)
                )
            })
        }
    }
}

@Composable
fun DateInputItem(label: String, dateStr: String, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable { onClick() }) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(
            text = dateStr,
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}