package com.timilehinaregbesola.composealarm.ui.alarmlist

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.loadImageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timilehinaregbesola.composealarm.R
import com.timilehinaregbesola.composealarm.database.Alarm
import com.timilehinaregbesola.composealarm.ui.alarmsettings.days
import com.timilehinaregbesola.composealarm.ui.colorNightBlue
import com.timilehinaregbesola.composealarm.ui.colorSkyBlue
import com.timilehinaregbesola.composealarm.ui.goldColor
import com.timilehinaregbesola.composealarm.utils.*
import kotlinx.coroutines.launch

@Composable
fun EmptyScreen(viewModel: AlarmListViewModel) {
    val openDialog = remember { mutableStateOf(false) }
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                ListTopAppBar(openDialog, viewModel)
            }
        ) {
            if (openDialog.value) ClearDialog(openDialog)

            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight().background(color = Color.Black),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                val androidImage = loadImageResource(id = R.drawable.ic_android)
                val bubble = loadImageResource(id = R.drawable.ic_bubble)
                val imgMessage = loadImageResource(id = R.drawable.ic_message)

                imgMessage.resource.resource?.let {
                    Image(
                        bitmap = it,
                        modifier = Modifier.width(200.dp).height(200.dp).align(Alignment.End)
                    )
                }
                bubble.resource.resource?.let {
                    Image(
                        bitmap = it,
                        modifier = Modifier.width(200.dp).height(200.dp)
                    )
                }
                androidImage.resource.resource?.let {
                    Image(
                        bitmap = it,
                        modifier = Modifier.width(200.dp).height(200.dp).align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
private fun ListTopAppBar(
    openDialog: MutableState<Boolean>,
    viewModel: AlarmListViewModel
) {
    TopAppBar(
        title = { Text("Math Alarm") },
        actions = {
            IconButton(onClick = { openDialog.value = true }) {
                Icon(imageVector = Icons.Filled.List)
            }
            IconButton(onClick = { viewModel.onAdd() }) {
                Icon(imageVector = Icons.Filled.Add)
            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
fun ListDisplayScreen(list: List<Alarm>, viewModel: AlarmListViewModel) {
    val openDialog = remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                ListTopAppBar(openDialog = openDialog, viewModel = viewModel)
            },
            snackbarHost = { state -> TimeLeftSnack(state) }
        ) {
            if (openDialog.value) ClearDialog(openDialog)
            LazyColumn(modifier = Modifier.fillMaxHeight().background(color = Color.Black)) {
                items(list) { alarm ->
                    AlarmItem(
                        alarm = alarm,
                        onClick = { viewModel.onAlarmClicked(alarm.alarmId) },
                        onUpdateAlarm = viewModel::onUpdate,
                        scaffoldState = scaffoldState
                    )
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun AlarmItem(
    alarm: Alarm,
    onClick: () -> Unit,
    onUpdateAlarm: (Alarm) -> Unit,
    scaffoldState: ScaffoldState
) {
    val context = AmbientContext.current
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier.fillMaxWidth().padding(4.dp).clickable(onClick = onClick),
        backgroundColor = if (alarm.hour < 12) colorSkyBlue else colorNightBlue
    ) {
        Column {
            Row {
                val timeColor = if (alarm.repeat) goldColor else Color.White
                Text(
                    modifier = Modifier
                        .padding(start = 24.dp, top = 8.dp, bottom = 8.dp)
                        .weight(3f),
                    text = alarm.getFormatTime().toString(),
                    fontSize = 40.sp,
                    color = timeColor
                )
                val checkedState = remember { mutableStateOf(alarm.isOn) }
                Switch(
                    modifier = Modifier.weight(1f).padding(4.dp).align(Alignment.CenterVertically),
                    checked = checkedState.value,
                    onCheckedChange = {
                        checkedState.value = it
                        alarm.isOn = it
                        if (alarm.isOn) {
                            if (alarm.scheduleAlarm(context)) {
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
                            } else {
                                alarm.isOn = false
                                checkedState.value = false
                            }
                        } else {
                            alarm.cancelAlarm(context)
                        }
                        onUpdateAlarm(alarm)
                    }
                )
            }
            Row(
                modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (day in days.indices) {
                    val textColor = if (alarm.repeatDays[day] == 'T') goldColor else Color.White
                    Text(text = days[day], color = textColor)
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun TimeLeftSnack(state: SnackbarHostState) {
    SnackbarHost(
        hostState = state,
        snackbar = { data -> Snackbar(data) }
    )
}

@Composable
private fun ClearDialog(openDialog: MutableState<Boolean>) {
    AlertDialog(
        onDismissRequest = {
            openDialog.value = false
        },
        title = {
            Text("Clear Alarms")
        },
        text = {
            Text("Are you sure you want to clear the alarms?")
        },
        confirmButton = {
            Button(onClick = { openDialog.value = false }) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = { openDialog.value = false }) {
                Text("No")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EmptyPreview() {
//    EmptyScreen()
}
