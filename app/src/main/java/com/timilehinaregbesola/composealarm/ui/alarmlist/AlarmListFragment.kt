package com.timilehinaregbesola.composealarm.ui.alarmlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.timilehinaregbesola.composealarm.R
import com.timilehinaregbesola.composealarm.ui.ComposeAlarmTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlarmListFragment : Fragment() {
    private val alarmListViewModel by viewModel<AlarmListViewModel>()
    private var add: Boolean? = false

    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        alarmListViewModel.addClicked.observe(
            viewLifecycleOwner,
            {
                if (it != null) {
                    add = it
                }
            }
        )

        return ComposeView(requireContext()).apply {
            // In order for savedState to work, the same ID needs to be used for all instances.
            id = R.id.alarmFragment

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                var alarmId: Long? = null
                val alarm = alarmListViewModel.alarm.value
                ComposeAlarmTheme {
                    alarmListViewModel.alarms.observeAsState().value?.let { alarms ->
                        alarmListViewModel.navigateToAlarmSettings.observeAsState().value.let { id ->
                            if (id != null) {
                                alarmId = id
                                alarmListViewModel.getAlarm(alarmId!!)
                            }
//                                navigate(Screen.AlarmSettings, Screen.AlarmList, id, add!!)

                            if (alarms.isEmpty()) {
                                EmptyScreen(alarmListViewModel)
                            } else {
                                ListDisplayScreen(alarms, alarmListViewModel, alarmId, add!!, alarm)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alarmListViewModel.getAlarms()
    }
}
