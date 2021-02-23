package com.timilehinaregbesola.composealarm.ui.alarmsettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timilehinaregbesola.composealarm.database.Alarm
import com.timilehinaregbesola.composealarm.utils.Event
import com.timilehinaregbesola.mathalarm.database.AlarmRepository
import kotlinx.coroutines.launch

class AlarmSettingsViewModel(private val repository: AlarmRepository) : ViewModel() {
    var alarm = MutableLiveData<Alarm?>()
    var popFrag = MutableLiveData<Boolean?>()

    private val _navigateToAlarmMath = MutableLiveData<Event<Long>>()
    val navigateToAlarmMath: LiveData<Event<Long>>
        get() = _navigateToAlarmMath

    private var _latestAlarm = MutableLiveData<Alarm?>()
    val latestAlarm: LiveData<Alarm?>
        get() = _latestAlarm

    init {
//        getAlarm(alarmKey)
        initializeCurrentAlarm()
    }

    fun onUpdate(alarm: Alarm) {
        viewModelScope.launch {
            repository.update(alarm)
            _latestAlarm.value = repository.getLatestAlarmFromDatabase()
        }
    }

    fun onDeleteFromId(alarmId: Long?) {
        viewModelScope.launch {
            val alarm = repository.findAlarm(alarmId!!)
            repository.delete(alarm)
            _latestAlarm.value = repository.getLatestAlarmFromDatabase()
        }
    }

    fun onDeleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.delete(alarm)
            _latestAlarm.value = repository.getLatestAlarmFromDatabase()
            popFrag.value = true
        }
    }

    fun getAlarm(key: Long) = viewModelScope.launch {
        val alarmFound = repository.findAlarm(key)
        alarm.postValue(alarmFound)
    }

    fun pop() {
        popFrag.value = true
    }

    private fun initializeCurrentAlarm() {
        viewModelScope.launch {
            _latestAlarm.value = repository.getLatestAlarmFromDatabase()
        }
    }

    // Called when add menu is pressed
    fun onAdd(newAlarm: Alarm) {
        viewModelScope.launch {
            val id = repository.add(newAlarm)
            _navigateToAlarmMath.value = Event(id)
            _latestAlarm.value = repository.getLatestAlarmFromDatabase()
        }
    }

    fun onAlarmMathNavigated() {
        _navigateToAlarmMath.value = null
    }
}
