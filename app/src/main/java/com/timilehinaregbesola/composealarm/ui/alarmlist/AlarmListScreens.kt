package com.timilehinaregbesola.composealarm.ui.alarmlist

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.DeferredResource
import androidx.compose.ui.res.loadImageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timilehinaregbesola.composealarm.R
import com.timilehinaregbesola.composealarm.database.Alarm
import com.timilehinaregbesola.composealarm.ui.alarmsettings.fullDays
import com.timilehinaregbesola.composealarm.utils.*
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun EmptyScreen(viewModel: AlarmListViewModel) {
    val openDialog = remember { mutableStateOf(false) }
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
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
                val emptyImage = loadImageResource(id = R.drawable.search_icon)
                val fabImage = loadImageResource(id = R.drawable.fabb)

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
                        emptyImage.resource.resource?.let {
                            Image(
                                bitmap = it,
                                modifier = Modifier
                                    .width(167.dp)
                                    .height(228.dp)
                            )
                        }
                        emptyImage.resource.resource?.let {
                            Image(
                                bitmap = it,
                                modifier = Modifier
                                    .padding(top = 24.dp, end = 40.dp)
                                    .width(167.dp)
                                    .height(228.dp)
                            )
                        }
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
                    modifier = Modifier
                        .padding(bottom = 16.dp, end = 40.dp)
                        .width(72.dp)
                        .height(75.dp)
                        .align(Alignment.BottomEnd),
                    viewModel,
                    fabImage
                )
            }
        }
    }
}

@Composable
private fun AddAlarmFab(
    modifier: Modifier = Modifier,
    viewModel: AlarmListViewModel,
    fabImage: DeferredResource<ImageBitmap>
) {
    FloatingActionButton(
        modifier = modifier,
        onClick = { viewModel.onAdd() },
        backgroundColor = Color(0x482FF7)
    ) {
        fabImage.resource.resource?.let {
            Image(bitmap = it)
        }
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
                Icon(imageVector = Icons.Filled.List)
            }
            IconButton(onClick = { }) {
                Icon(imageVector = Icons.Filled.MoreVert)
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
                                    scaffoldState = scaffoldState
                                )
                            }
                        }
                    }
                    val fabImage = loadImageResource(id = R.drawable.fabb)
                    AddAlarmFab(
                        modifier = Modifier
                            .padding(bottom = 16.dp, end = 40.dp)
                            .width(72.dp)
                            .height(75.dp)
                            .align(Alignment.BottomEnd),
                        viewModel,
                        fabImage
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
    scaffoldState: ScaffoldState
) {
    val context = AmbientContext.current
    val scope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 16.dp, start = 24.dp, end = 24.dp)
            .clickable(onClick = onClick),
        elevation = 4.dp,
        shape = MaterialTheme.shapes.medium.copy(CornerSize(8.dp))
    ) {
        Column(modifier = Modifier.background(Color(0x99FFFFFF))) {
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
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                )
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
