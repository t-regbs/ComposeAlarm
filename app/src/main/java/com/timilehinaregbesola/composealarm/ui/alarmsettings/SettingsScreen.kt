package com.timilehinaregbesola.composealarm.ui.alarmsettings

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timilehinaregbesola.composealarm.database.Alarm
import com.timilehinaregbesola.composealarm.ui.ComposeAlarmTheme
import com.timilehinaregbesola.composealarm.ui.alarmlist.TimeLeftSnack
import com.timilehinaregbesola.composealarm.utils.cancelAlarm
import com.timilehinaregbesola.composealarm.utils.getFormatTime
import com.timilehinaregbesola.composealarm.utils.getTimeLeftMessage
import com.timilehinaregbesola.composealarm.utils.scheduleAlarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val days = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
val fullDays = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

@ExperimentalMaterialApi
@Composable
fun SettingsScreen(alarm: Alarm, viewModel: AlarmSettingsViewModel, isFromAdd: Boolean?) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { state -> TimeLeftSnack(state) },
        topBar = {
            TopAppBar(
                title = { Text("Alarm Settings") },
                actions = {
                    val scope = rememberCoroutineScope()
                    IconButton(
                        onClick = {
                            if (alarm.isOn) alarm.cancelAlarm(context)
                            viewModel.onDeleteAlarm(alarm)
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete")
                    }
                    IconButton(
                        onClick = {
                            if (isFromAdd!!) {
                                scheduleAndSnack(alarm, context, scope, scaffoldState)
                                viewModel.onUpdate(alarm)
                                viewModel.pop()
                            } else {
                                if (alarm.isOn) {
                                    alarm.cancelAlarm(context)
                                }
                                scheduleAndSnack(alarm, context, scope, scaffoldState)
                                viewModel.onUpdate(alarm)
                                viewModel.pop()
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Done, contentDescription = "Done")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                modifier = Modifier.padding(top = 15.dp, bottom = 5.dp),
                text = "SET ALARM TIME"
            )
            Divider(color = Color.Gray, thickness = 1.dp)
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                text = alarm.getFormatTime().toString(),
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.padding(top = 15.dp, bottom = 5.dp),
                text = "REPEAT",
            )
            Divider(color = Color.Gray, thickness = 1.dp)
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
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .align(Alignment.CenterVertically),
                    checked = checkedState.value,
                    onCheckedChange = { checkedState.value = it }
                )
            }
            Text(
                modifier = Modifier.padding(top = 15.dp, bottom = 5.dp),
                text = "ALARM SETTINGS",
            )
            Divider(color = Color.Gray, thickness = 1.dp)
            RowWithDropdown("Math Difficulty")
            RowWithDropdown("Alarm Tone")
            SnoozeRow()
            Row(modifier = Modifier.padding(bottom = 20.dp)) {
                Text(modifier = Modifier.weight(3f), text = "Vibrate", fontSize = 15.sp)
                val checkedState = remember { mutableStateOf(false) }
                Switch(
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .align(Alignment.CenterVertically),
                    checked = checkedState.value,
                    onCheckedChange = { checkedState.value = it }
                )
            }
//            ImageButton(modifier = Modifier.padding(bottom = 10.dp, top = 10.dp)
//                    .align(Alignment.CenterHorizontally))
            Button(
                onClick = {},
                modifier = Modifier
                    .padding(bottom = 10.dp, top = 10.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Test Alarm")
            }
        }
    }
}

@ExperimentalMaterialApi
private fun scheduleAndSnack(
    alarm: Alarm,
    context: Context,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    alarm.isOn = alarm.scheduleAlarm(context)
    if (alarm.isOn) {
        scope.launch {
            when (
                scaffoldState.snackbarHostState.showSnackbar(
                    message = alarm.getTimeLeftMessage(context)!!,
                    duration = SnackbarDuration.Short
                )
            ) {
                SnackbarResult.Dismissed ->
                    Log.d("Track", "Dismissed")
                SnackbarResult.ActionPerformed ->
                    Log.d("Track", "Action!")
            }
        }
    }
}

@Composable
private fun SnoozeRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp, top = 10.dp)
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(3f),
            text = "Snooze (0 = OFF)",
            fontSize = 15.sp
        )
//        NumberTextInputComponent(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f),
            text = "minute(s)"
        )
    }
}

@Composable
private fun RowWithDropdown(title: String) {
    Row(modifier = Modifier.padding(bottom = 10.dp, top = 10.dp)) {
        Text(modifier = Modifier.weight(3f), text = title, fontSize = 15.sp)
        val expanded = remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)) {
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                onClick = { expanded.value = true }
            ) {
                Text("Press me")
            }
            DropdownMenu(
                modifier = Modifier.padding(end = 4.dp),
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
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
}

//@Composable
//fun NumberTextInputComponent(modifier: Modifier = Modifier) {
//    val textValue = remember { mutableStateOf(TextFieldValue()) }
//    TextField(
//        value = textValue.value,
//        onValueChange = {value: TextFieldValue ->  textValue.value = value },
//        modifier = modifier.padding(end = 4.dp),
//        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
//        singleLine = true,
//        backgroundColor = Color.Transparent
//    )
//}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    ComposeAlarmTheme {
//        SettingsScreen(Alarm(), alarmSettingsViewModel)
//        NumberTextInputComponent()
//        ImageButton()
    }
}
