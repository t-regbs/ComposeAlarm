package com.timilehinaregbesola.composealarm.ui.alarmlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.timilehinaregbesola.composealarm.R
import com.timilehinaregbesola.composealarm.ui.ComposeAlarmTheme
import com.timilehinaregbesola.composealarm.utils.Screen
import com.timilehinaregbesola.composealarm.utils.navigate
import org.koin.androidx.viewmodel.ext.android.viewModel

class AlarmListFragment : Fragment() {
    private val alarmListViewModel by viewModel<AlarmListViewModel>()
    private var add: Boolean? = null

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

        alarmListViewModel.navigateToAlarmSettings.observe(viewLifecycleOwner) { navigateToEvent ->
            navigateToEvent.getContentIfNotHandled()?.let { id ->
                navigate(Screen.AlarmSettings, Screen.AlarmList, id, add!!)
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
                    alarmListViewModel.alarms.observeAsState().value?.let { alarms ->
                        if (alarms.isEmpty()) {
                            EmptyScreen(alarmListViewModel)
                        } else {
                            ListDisplayScreen(alarms, alarmListViewModel)
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
