package com.timilehinaregbesola.composealarm.ui.alarmsettings

import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.CoreTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import com.timilehinaregbesola.composealarm.database.Alarm
import com.timilehinaregbesola.composealarm.ui.ComposeAlarmTheme
import com.timilehinaregbesola.composealarm.utils.getFormatTime

val days = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

@Composable
fun SettingsScreen(alarm: Alarm) {
    Scaffold(
            topBar = {
                TopAppBar(
                        title = { Text("Alarm Settings") }
                )
            }
    ) {
        ScrollableColumn(modifier = Modifier.fillMaxHeight().fillMaxWidth().padding(8.dp)) {
            Text(
                    modifier = Modifier.padding(top = 15.dp, bottom = 5.dp),
                    text = "SET ALARM TIME",
            )
            Text(
                    modifier = Modifier.fillMaxWidth().padding(30.dp),
                    text = alarm.getFormatTime().toString(),
                    fontSize = TextUnit.Sp(50),
                    textAlign = TextAlign.Center
            )
            Text(
                    modifier = Modifier.padding(top = 15.dp, bottom = 5.dp),
                    text = "REPEAT",
            )
            Row(
                    modifier = Modifier
                            .padding(start = 5.dp, end = 5.dp, top = 20.dp, bottom = 20.dp)
                            .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (day in days) {
                    val checkedState = remember { mutableStateOf(false) }
                    IconToggleButton(
                            checked = checkedState.value,
                            onCheckedChange = { checkedState.value = it },
                            modifier = Modifier
                                    .toggleable(
                                            value = checkedState.value,
                                            onValueChange = { checkedState.value = it }
                                    )
                    ) {
                        Text(day)
                    }
                }
            }
            Row(modifier = Modifier.padding(bottom = 20.dp)) {
                Text(modifier = Modifier.weight(3f), text = "Repeat Weekly", fontSize = 15.sp)
                val checkedState = remember { mutableStateOf(false) }
                Switch(
                        modifier = Modifier.weight(1f).padding(4.dp).align(Alignment.CenterVertically),
                        checked = checkedState.value,
                        onCheckedChange = { checkedState.value = it }
                )
            }
            Text(
                    modifier = Modifier.padding(top = 15.dp, bottom = 5.dp),
                    text = "ALARM SETTINGS",
            )
            RowWithDropdown("Math Difficulty")
            RowWithDropdown("Alarm Tone")
            SnoozeRow()
            Row(modifier = Modifier.padding(bottom = 20.dp)) {
                Text(modifier = Modifier.weight(3f), text = "Vibrate", fontSize = 15.sp)
                val checkedState = remember { mutableStateOf(false) }
                Switch(
                        modifier = Modifier.weight(1f).padding(4.dp).align(Alignment.CenterVertically),
                        checked = checkedState.value,
                        onCheckedChange = { checkedState.value = it }
                )
            }
            Button(onClick = {}, modifier = Modifier
                            .padding(bottom = 10.dp, top = 10.dp)
                            .align(Alignment.CenterHorizontally)
            ) {
                Text("Test Alarm")
            }
        }
    }
}

@Composable
private fun SnoozeRow() {
    Row(modifier = Modifier.padding(bottom = 10.dp, top = 10.dp)) {
        Text(
            modifier = Modifier.weight(3f),
            text = "Snooze (0 = OFF)",
            fontSize = 15.sp
        )
        NumberTextInputComponent(modifier = Modifier.align(Alignment.CenterVertically).weight(1f))
        Text(modifier = Modifier.weight(1f), text = "minute(s)")
    }
}

@Composable
private fun RowWithDropdown(title: String) {
    Row(modifier = Modifier.padding(bottom = 10.dp, top = 10.dp)) {
        Text(modifier = Modifier.weight(3f), text = title, fontSize = 15.sp)
        val expanded = remember { mutableStateOf(false) }
        val iconButton = @Composable {
            Button(backgroundColor = Color.White, onClick = { expanded.value = true }) {
                Text("Press me")
            }
        }
        DropdownMenu(
                toggleModifier = Modifier.padding(end = 4.dp),
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
                toggle = iconButton
        ) {
            DropdownMenuItem(onClick = { }) {
                Text("Refresh")
            }
            DropdownMenuItem(onClick = { /* Handle settings! */ }) {
                Text("Settings")
            }
            Divider()
            DropdownMenuItem(onClick = { /* Handle send feedback! */ }) {
                Text("Send Feedback")
            }
        }
    }
}

@Composable
fun NumberTextInputComponent(modifier: Modifier = Modifier) {
    var textValue by remember { mutableStateOf(TextFieldValue("")) }
    TextField(value = textValue,
            modifier = modifier.padding(end = 4.dp),
            keyboardType = KeyboardType.Number,
            onValueChange = {
                textValue = it
            }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    ComposeAlarmTheme {
        SettingsPreview()
    }
}