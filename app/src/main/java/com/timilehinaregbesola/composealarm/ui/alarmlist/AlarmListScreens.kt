package com.timilehinaregbesola.composealarm.ui.alarmlist

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timilehinaregbesola.composealarm.R
import com.timilehinaregbesola.composealarm.database.Alarm
import com.timilehinaregbesola.composealarm.ui.alarmsettings.fullDays
import com.timilehinaregbesola.composealarm.utils.*
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalMaterialApi
@Composable
fun EmptyScreen(viewModel: AlarmListViewModel) {
    val openDialog = remember { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    Surface(modifier = Modifier.fillMaxSize()) {
        BottomSheetScaffold(
            sheetContent = {},
            sheetPeekHeight = 0.dp,
            topBar = {
                ListTopAppBar(openDialog)
            }
        ) {
            if (openDialog.value) ClearDialog(openDialog)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(color = Color.White)
            ) {
                val emptyImage = painterResource(id = R.drawable.search_icon)
                val fabImage = painterResource(id = R.drawable.fabb)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .align(Alignment.TopCenter)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 143.dp)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.TopEnd

                    ) {
                        Image(
                            painter = emptyImage,
                            contentDescription = "Empty Alarm List",
                            modifier = Modifier
                                .width(167.dp)
                                .height(228.dp)
                        )
                        Image(
                            painter = emptyImage,
                            contentDescription = "Empty Alarm List",
                            modifier = Modifier
                                .padding(top = 24.dp, end = 40.dp)
                                .width(167.dp)
                                .height(228.dp)
                        )
                    }

                    Text(
                        modifier = Modifier
                            .padding(top = 29.dp)
                            .align(Alignment.CenterHorizontally),
                        text = "Nothing to see here",
                        fontSize = 16.sp
                    )
                }
                AddAlarmFab(
                    viewModel = viewModel,
                    fabImage = fabImage,
                    scaffoldState = scaffoldState
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun AddAlarmFab(
    modifier: Modifier = Modifier,
    viewModel: AlarmListViewModel,
    fabImage: Painter,
    scaffoldState: BottomSheetScaffoldState
) {
    val scope = rememberCoroutineScope()
    FloatingActionButton(
        modifier = modifier,
        onClick = {
//            viewModel.onAdd()
            scope.launch {
                scaffoldState.bottomSheetState.expand()
            }
        },
        backgroundColor = Color(0x482FF7),
    ) {
        Image(
            modifier = Modifier
                .width(72.dp)
                .height(75.dp),
            painter = fabImage,
            contentDescription = null
        )
    }
}

@Composable
private fun ListTopAppBar(
    openDialog: MutableState<Boolean>
) {
    TopAppBar(
        title = {
            Text(
                text = "Alarms",
                fontSize = 16.sp
            )
        },
        backgroundColor = Color.White,
        actions = {
            IconButton(onClick = { openDialog.value = true }) {
                Icon(imageVector = Icons.Filled.List, contentDescription = null)
            }
            IconButton(onClick = { }) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "More")
            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
fun ListDisplayScreen(
    list: List<Alarm>,
    viewModel: AlarmListViewModel,
    alarmId: Long?,
    fromAdd: Boolean,
    activeAlarm: Alarm?
) {
    val openDialog = remember { mutableStateOf(false) }
    val scaffoldState = rememberBottomSheetScaffoldState()

    val scope = rememberCoroutineScope()
    Surface(modifier = Modifier.fillMaxSize()) {
        BottomSheetScaffold(
            sheetContent = {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Card(
                        modifier = Modifier
                            .padding(start = 24.dp, end = 24.dp)
                            .fillMaxWidth()
                            .height(150.dp),
                        backgroundColor = Color(0xF7F8F8),
                        elevation = 0.dp,
                        shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp))
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(30.dp),
                            text = if (fromAdd) activeAlarm?.getFormatTime().toString() else "Dummy AM",
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            sheetPeekHeight = 0.dp,
            scaffoldState = scaffoldState,
            topBar = {
                ListTopAppBar(openDialog = openDialog)
            },
            snackbarHost = { state -> TimeLeftSnack(state) }
        ) {
            if (openDialog.value) ClearDialog(openDialog)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.LightGray.copy(alpha = 0.1f))
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {
                        val enabled = list.any { it.isOn }
                        val now = System.currentTimeMillis()
                        var nearest = getCal(list [0]).timeInMillis
                        var nearestIndex = 0
                        list.forEachIndexed { index, alarm ->
                            val cal = getCal(alarm)
                            val time = cal.timeInMillis
                            if ((time - now) < (nearest - now)) {
                                nearest = time
                                nearestIndex = index
                            }
                        }
                        val nearestAlarmMessage = list[nearestIndex].getTimeLeft(nearest)
                        Text(
                            text = if (enabled) "Next alarm in $nearestAlarmMessage" else "No upcoming alarms",
                            modifier = Modifier.padding(top = 24.dp, start = 24.dp, bottom = 8.dp),
                            fontSize = 16.sp
                        )
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(list) { alarm ->
                                AlarmItem(
                                    alarm = alarm,
                                    onClick = { viewModel.onAlarmClicked(alarm.alarmId) },
                                    onUpdateAlarm = viewModel::onUpdate,
                                    scaffoldState = scaffoldState,
                                    onDeleteAlarm = viewModel::onDelete
                                )
                            }
                        }
                    }
                    val fabImage = painterResource(id = R.drawable.fabb)
                    AddAlarmFab(
                        modifier = Modifier
                            .padding(bottom = 16.dp, end = 32.dp)
                            .align(Alignment.BottomEnd),
                        viewModel,
                        fabImage,
                        scaffoldState
                    )
                }
            }
        }
    }
}

private fun getCal(alarm: Alarm): Calendar {
    val cal = Calendar.getInstance()
    cal[Calendar.HOUR_OF_DAY] = alarm.hour
    cal[Calendar.MINUTE] = alarm.minute
    cal[Calendar.SECOND] = 0
    return cal
}

@ExperimentalMaterialApi
@Composable
fun AlarmItem(
    alarm: Alarm,
    onClick: () -> Unit,
    onUpdateAlarm: (Alarm) -> Unit,
    onDeleteAlarm: (Alarm) -> Unit,
    scaffoldState: BottomSheetScaffoldState
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 16.dp, start = 24.dp, end = 24.dp),
        elevation = 4.dp,
        shape = MaterialTheme.shapes.medium.copy(CornerSize(8.dp))
    ) {
        Column(modifier = Modifier.background(Color(0x99FFFFFF))) {
            val expandItem = remember { mutableStateOf(false) }
            Column(modifier = Modifier.clickable(onClick = onClick)) {
                Row {
                    val time = alarm.getFormatTime().toString()
                    val actualTime = time.substring(0, time.length - 3)
                    val timeOfDay = time.substring(time.length - 2)
                    Row(
                        modifier = Modifier
                            .padding(start = 24.dp, top = 8.dp, bottom = 8.dp)
                            .weight(3f),
                    ) {
                        Text(
                            text = actualTime,
                            fontSize = 40.sp,
                            color = Color.Black,
                            fontWeight = if (alarm.isOn) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(
                            text = timeOfDay,
                            fontSize = 16.sp,
                            color = Color.Gray,
                            fontWeight = if (alarm.isOn) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .align(Alignment.Bottom)
                                .padding(bottom = 8.dp)
                        )
                    }
                    val checkedState = remember { mutableStateOf(alarm.isOn) }
                    Switch(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .align(Alignment.CenterVertically),
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
                    modifier = Modifier
                        .padding(bottom = 16.dp, top = 24.dp, start = 24.dp)
                        .fillMaxWidth()
                ) {
                    val sb = StringBuilder()
                    for (day in fullDays.indices) {
                        if (alarm.repeatDays[day] == 'T') {
                            sb.append("${fullDays[day]},")
                        }
                    }
                    val alarmInfoText = sb.dropLast(1).toString()

                    val moreInfo = if (alarm.hour < 12) {
                        "Good morning"
                    } else if (alarm.hour in 12..17) {
                        "Afternoon"
                    } else {
                        "Good Evening"
                    }

                    Text(
                        text = "$alarmInfoText | $moreInfo",
                        color = Color.Black,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(3f)
                    )
                    if (!expandItem.value) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand",
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                                .clickable(onClick = { expandItem.value = true })
                        )
                    }
                }
            }
            if (expandItem.value) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(
                    thickness = 3.dp,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)) {
                    Row(modifier = Modifier.weight(3f)) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .clickable(onClick = { onDeleteAlarm(alarm) })
                        )
                        Text(
                            text = "Delete",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(end = 24.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .clickable(onClick = onClick)
                        )
                        Text(
                            text = "Edit",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Collapse",
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .clickable(onClick = { expandItem.value = false })
                    )
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

fun Alarm.getTimeLeft(time: Long): String? {
    val message: String
    val cal = getCal(alarm = this)
    val today = getDayOfWeek(cal[Calendar.DAY_OF_WEEK])
    var i: Int
    var lastAlarmDay: Int
    var nextAlarmDay: Int
    if (System.currentTimeMillis() > time) {
        nextAlarmDay = today + 1
        lastAlarmDay = today
        if (nextAlarmDay == 7) {
            nextAlarmDay = 0
        }
    } else {
        nextAlarmDay = today
        lastAlarmDay = today - 1
        if (lastAlarmDay == -1) {
            lastAlarmDay = 6
        }
    }
    i = nextAlarmDay
    while (i != lastAlarmDay) {
        if (i == 7) {
            i = 0
        }
        if (repeatDays[i] == 'T') {
            break
        }
        i++
    }
    if (i < today || i == today && cal.timeInMillis < System.currentTimeMillis()) {
        val daysUntilAlarm: Int = SAT - today + 1 + i
        cal.add(Calendar.DAY_OF_YEAR, daysUntilAlarm)
    } else {
        val daysUntilAlarm = i - today
        cal.add(Calendar.DAY_OF_YEAR, daysUntilAlarm)
    }
    val alarmTime = cal.timeInMillis
    val remainderTime = alarmTime - System.currentTimeMillis()
    val minutes = (remainderTime / (1000 * 60) % 60).toInt()
    val hours = (remainderTime / (1000 * 60 * 60) % 24).toInt()
    val days = (remainderTime / (1000 * 60 * 60 * 24)).toInt()
    val mString: String
    val hString: String
    val dString: String
    mString = if (minutes == 1) {
        "minute"
    } else {
        "minutes"
    }
    hString = if (hours == 1) {
        "hour"
    } else {
        "hours"
    }
    dString = if (days == 1) {
        "day"
    } else {
        "days"
    }
    message = if (days == 0) {
        if (hours == 0) {
            ("$minutes $mString")
        } else {
            ("$hours $hString $minutes $mString")
        }
    } else {
        (
            " " + days + " " + dString + " " + hours + " " + hString + " " + minutes + " " +
                mString + " "
            )
    }
    return message
}
