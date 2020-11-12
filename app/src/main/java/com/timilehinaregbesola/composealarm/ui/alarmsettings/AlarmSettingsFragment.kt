package com.timilehinaregbesola.composealarm.ui.alarmsettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.timilehinaregbesola.composealarm.R
import com.timilehinaregbesola.composealarm.ui.ComposeAlarmTheme
import com.timilehinaregbesola.composealarm.utils.Screen
import com.timilehinaregbesola.composealarm.utils.navigate
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlarmSettingsFragment : Fragment() {
    private val alarmSettingsViewModel by viewModel<AlarmSettingsViewModel>()
    private val args: AlarmSettingsFragmentArgs by navArgs()
    private var key: Long? = null
    private var isFromAdd: Boolean? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        isFromAdd = args.add
        key = args.alarmKey
        alarmSettingsViewModel.navigateToAlarmMath.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { id ->
                navigate(Screen.AlarmSettings, Screen.AlarmList, id, null)
            }
        }

        return ComposeView(requireContext()).apply {
            // In order for savedState to work, the same ID needs to be used for all instances.
            id = R.id.alarmFragment

            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )
            setContent {
                ComposeAlarmTheme {
                    alarmSettingsViewModel.alarm.observeAsState().value?.let { alarm ->
                        SettingsScreen(alarm)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alarmSettingsViewModel.getAlarm(key!!)
    }
}