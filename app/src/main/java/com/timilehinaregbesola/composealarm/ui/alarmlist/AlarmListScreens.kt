package com.timilehinaregbesola.composealarm.ui.alarmlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun EmptyScreen(viewModel: AlarmListViewModel) {
    val openDialog = remember { mutableStateOf(false) }
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Math Alarm") },
                    actions = {
                        IconButton(onClick = {  openDialog.value = true }) {
                            Icon(imageVector = Icons.Filled.List)
                        }
                        IconButton(onClick = { viewModel.onAdd() }) {
                            Icon(imageVector = Icons.Filled.Add)
                        }
                    }
                )
            }
        ) {
            if (openDialog.value) {
                ClearDialog(openDialog)
            }

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
fun ListDisplayScreen(list: List<Alarm>, viewModel: AlarmListViewModel) {
    val openDialog = remember { mutableStateOf(false) }
    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
                topBar = {
                    TopAppBar(
                            title = { Text("Math Alarm") },
                            actions = {
                                IconButton(onClick = {  openDialog.value = true }) {
                                    Icon(imageVector = Icons.Filled.List)
                                }
                                IconButton(onClick = { viewModel.onAdd() }) {
                                    Icon(imageVector = Icons.Filled.Add)
                                }
                            }
                    )
                }
        ) {
            LazyColumnFor(items = list, modifier = Modifier.fillMaxHeight().background(color = Color.Black)) { alarm ->
                // TODO Replace this with an index callback once its available.
                val index = list.indexOf(alarm)
                AlarmItem(modifier = Modifier.fillParentMaxWidth(), alarm)
            }
        }
    }
}

@Composable
fun AlarmItem(modifier: Modifier = Modifier, alarm: Alarm) {
    Card(modifier = modifier.fillMaxWidth().padding(4.dp),
            backgroundColor = if (alarm.hour < 12) colorSkyBlue else colorNightBlue
    ){
        Column {
            Row {
                val timeColor = if (alarm.repeat) goldColor else Color.White
                Text(
                        modifier = Modifier.padding(4.dp).weight(3f),
                        text = alarm.getFormatTime().toString(),
                        fontSize = 40.sp,
                        color = timeColor
                )
                val checkedState = remember { mutableStateOf(false) }
//                Switch(
//                        modifier = Modifier.weight(1f).padding(4.dp).align(Alignment.CenterVertically),
//                        checked = alarm.isOn,
//                        onCheckedChange = {
//                            alarm.isOn = !alarm.isOn
////                            alarm.isOn = it
//                            if (alarm.isOn) {
//                                if (alarm.scheduleAlarm(ContextAmbient)) {
//                                    Toast.makeText(
//                                            alarm.context, alarm.getTimeLeftMessage(alarm.context),
//                                            Toast.LENGTH_SHORT
//                                    ).show()
//                                } else {
//                                    alarm.isOn = false
////                                    binding.alarmSwitchButton.isChecked = false
//                                }
//                            } else {
//                                alarm.cancelAlarm(alarm.context)
//                            }
//                            viewModel.onUpdate(item)
//                        }
//                )
            }
            Row(modifier = Modifier.padding(bottom = 4.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (day in days.indices) {
                    val textColor = if(alarm.repeatDays[day] == 'T') goldColor else Color.White
                    Text(text = days[day], color = textColor)
                }
            }
        }
    }
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