package com.timilehinaregbesola.composealarm.utils

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.timilehinaregbesola.composealarm.R
import com.timilehinaregbesola.composealarm.ui.alarmlist.AlarmListFragmentDirections
import java.security.InvalidParameterException

enum class Screen { AlarmList, AlarmSettings, AlarmMath }

fun Fragment.navigate(to: Screen, from: Screen, alarmId: Long? = null, isAdd: Boolean?) {
    if (to == from) {
        throw InvalidParameterException("Can't navigate to $to")
    }
    when (to) {
        Screen.AlarmList -> {
            findNavController().navigate(R.id.alarmFragment)
        }
        Screen.AlarmSettings -> {
            findNavController().navigate(AlarmListFragmentDirections
                .actionAlarmFragmentToAlarmSettingsFragment(alarmId!!, isAdd!!))
        }
        Screen.AlarmMath -> {
//            findNavController().navigate(R.id.sign_in_fragment)
        }
    }
}