package com.timilehinaregbesola.composealarm.ui.alarmlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timilehinaregbesola.composealarm.database.Alarm
import com.timilehinaregbesola.composealarm.utils.Event
import com.timilehinaregbesola.composealarm.utils.getDayOfWeek
import com.timilehinaregbesola.mathalarm.database.AlarmRepository
import kotlinx.coroutines.launch
import java.util.*

class AlarmListViewModel(private val repository: AlarmRepository) : ViewModel() {
    var addClicked = MutableLiveData<Boolean?>()
    private val _alarms = MutableLiveData<List<Alarm>>()
    val alarms: LiveData<List<Alarm>>
        get() = _alarms

    private val _navigateToAlarmSettings = MutableLiveData<Event<Long>>()
    val navigateToAlarmSettings: LiveData<Event<Long>>
        get() = _navigateToAlarmSettings
//    init {
//        getAlarms()
//    }

    fun onUpdate(alarm: Alarm) {
        viewModelScope.launch {
            repository.update(alarm)
            getAlarms()
        }
    }

    fun getAlarms() {
        viewModelScope.launch {
            val alarmList = repository.getAlarms()
            _alarms.postValue(alarmList)
        }
    }

    // Called when add menu is pressed
    fun onAdd() {
        val new = Alarm()
        val sb = StringBuilder("FFFFFFF")
        val cal = initCalendar(new)
        val dayOfTheWeek =
            getDayOfWeek(cal[Calendar.DAY_OF_WEEK])
        sb.setCharAt(dayOfTheWeek, 'T')
        new.repeatDays = sb.toString()
        viewModelScope.launch {
            val id = repository.add(new)
            addClicked.value = true
            _navigateToAlarmSettings.value = Event(id)
        }
    }

    private fun initCalendar(alarm: Alarm): Calendar {
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = alarm.hour
        cal[Calendar.MINUTE] = alarm.minute
        cal[Calendar.SECOND] = 0
        return cal
    }

    fun onDelete(alarm: Alarm) {
        viewModelScope.launch {
            repository.delete(alarm)
            getAlarms()
        }
    }

    fun onClear() {
        viewModelScope.launch {
            repository.clear()
            getAlarms()
        }
    }

    fun onAlarmClicked(id: Long) {
        addClicked.value = false
        _navigateToAlarmSettings.value = Event(id)
    }

    fun onAlarmSettingsNavigated() {
        _navigateToAlarmSettings.value = null
    }
}
