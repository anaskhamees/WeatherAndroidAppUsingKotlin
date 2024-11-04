package com.example.weatherforecast.Features.Alert.view

import SwipeToDeleteCallback
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherforecast.R
import com.example.weatherforecast.Data.DataBase.local.alert.AlertDataSourceImpl
import com.example.weatherforecast.Data.DataBase.room.AppDatabase
import com.example.weatherforecast.Data.Model.AlarmEntity
import com.example.weatherforecast.databinding.ActivityAlertBinding
import com.example.weatherforecast.Features.Alert.viewModel.AlertViewModel
import com.example.weatherforecast.Features.Alert.viewModel.AlertViewModelFactory
import java.util.Calendar

/**
 * AlertActivity is responsible for managing the user interface for setting and viewing alarms.
 * It allows users to add new alarms, view existing alarms, and delete alarms using a swipe gesture.
 */
class AlertActivity : AppCompatActivity() {

    // Binding object to access UI elements
    private lateinit var binding: ActivityAlertBinding

    // Variable to store the selected alarm time in milliseconds
    private var selectedTimeInMillis: Long = 0

    // ViewModel instance for managing alarm data
    private lateinit var alarmViewModel: AlertViewModel

    // Adapter for displaying alarms in a RecyclerView
    private lateinit var alarmAdapter: AlertAdapter

    /**
     * Called when the activity is first created. Initializes the UI and sets up necessary components.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using view binding
        binding = ActivityAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the ViewModel with the AlertDataSource implementation
        alarmViewModel = ViewModelProvider(
            this,
            AlertViewModelFactory(
                AlertDataSourceImpl(
                    this,
                    AppDatabase.getDatabase(this).alarmDao(),
                    getSystemService(ALARM_SERVICE) as AlarmManager
                )
            )
        )[AlertViewModel::class.java]

        // Set up the RecyclerView for displaying alarms
        setUpRecyclerView()

        // Load alarms when the activity starts
        alarmViewModel.loadAlarms()

        // Set up click listener for the back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Set up click listener for the add alert button
        binding.btnAddAlert.setOnClickListener {
            showDatePicker()
        }
    }

    /**
     * Configures the RecyclerView for displaying alarms.
     */
    private fun setUpRecyclerView() {
        // Initialize the adapter
        alarmAdapter = AlertAdapter()
        binding.rvAlerts.layoutManager = LinearLayoutManager(this)
        binding.rvAlerts.adapter = alarmAdapter

        // Observe the alarms LiveData and update the UI accordingly
        alarmViewModel.alarms.observe(this) { alarmList ->
            if (alarmList.isEmpty()) {
                // Show a message if there are no alarms
                binding.tvNoItems.visibility = View.VISIBLE
                binding.imcNoSaved.visibility = View.VISIBLE
                binding.rvAlerts.visibility = View.GONE
            } else {
                // Update the UI to display alarms
                binding.tvNoItems.visibility = View.GONE
                binding.imcNoSaved.visibility = View.GONE
                binding.rvAlerts.visibility = View.VISIBLE
                alarmAdapter.submitList(alarmList)
            }
        }

        // Set up swipe-to-delete functionality
        val swipeToDeleteCallback = SwipeToDeleteCallback(
            onSwipedAction = { position ->
                // Get the alarm at the swiped position
                val alarm = alarmAdapter.currentList[position]
                // Show a confirmation dialog for deletion
                showDeleteConfirmationDialog(alarm, position)
            },
            iconResId = R.drawable.ic_delete // Use the delete icon
        )

        // Attach the swipe-to-delete functionality to the RecyclerView
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvAlerts)
    }

    /**
     * Displays a confirmation dialog for deleting an alarm.
     * @param alarm The alarm to be deleted.
     * @param position The position of the alarm in the RecyclerView.
     */
    private fun showDeleteConfirmationDialog(alarm: AlarmEntity, position: Int) {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_alert))
            .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_alert))
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                // Delete the alarm and dismiss the dialog
                alarmViewModel.deleteAlarm(alarm)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                // Revert swipe if the user cancels the deletion
                alarmAdapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .create()

        // Customize button colors in the dialog
        dialog.setOnShowListener {
            val buttonOk = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val buttonCancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            buttonOk.setTextColor(resources.getColor(R.color.delete_color, null))
            buttonCancel.setTextColor(resources.getColor(R.color.buttons_, null))
        }
        dialog.show()
    }

    /**
     * Displays a date picker dialog for selecting an alarm date.
     */
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this, R.style.CustomDatePickerDialog,
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                // After selecting a date, show the time picker
                showTimePicker(calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        // Set the minimum date to the current date
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    /**
     * Displays a time picker dialog for selecting an alarm time.
     * @param calendar The calendar object with the selected date.
     */
    private fun showTimePicker(calendar: Calendar) {
        val now = Calendar.getInstance()
        // Check if the selected date is today
        val isToday = now.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)

        // Set initial hour and minute based on whether it’s today
        val hour = if (isToday) now.get(Calendar.HOUR_OF_DAY) else 0
        val minute = if (isToday) now.get(Calendar.MINUTE) else 0

        val timePickerDialog = TimePickerDialog(
            this, R.style.CustomTimePickerDialog,
            { _, hourOfDay, minute ->
                // Validate the selected time to ensure it's not in the past
                if (isToday && (hourOfDay < now.get(Calendar.HOUR_OF_DAY) ||
                            (hourOfDay == now.get(Calendar.HOUR_OF_DAY) && minute < now.get(Calendar.MINUTE)))
                ) {
                    Toast.makeText(
                        this,
                        "This time has already passed. Please select a future time.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Set the calendar with the selected time and proceed to check permissions
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    selectedTimeInMillis = calendar.timeInMillis
                    checkOverlayPermission() // Proceed to set the alarm
                }
            },
            hour,
            minute,
            false
        )

        timePickerDialog.show()
    }

    /**
     * Checks for permission to set exact alarms on devices running Android S (API level 31) and above.
     */
    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            // Check if the app can schedule exact alarms
            if (!alarmManager.canScheduleExactAlarms()) {
                // Request permission from the user
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivityForResult(intent, 2000)
            } else {
                setAlarm(selectedTimeInMillis) // Set the alarm if permission is granted
            }
        } else {
            setAlarm(selectedTimeInMillis) // For older versions, set the alarm directly
        }
    }

    /**
     * Handles the result of permission requests for overlay and exact alarm permissions.
     */
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1000 -> {
                // Handle overlay permission result
                if (Settings.canDrawOverlays(this)) {
                    checkExactAlarmPermission()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.overlay_permission_is_required_to_display_the_alarm_screen),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            2000 -> {
                // Handle exact alarm permission result
                if (resultCode == RESULT_OK) {
                    setAlarm(selectedTimeInMillis)
                }
            }
        }
    }

    /**
     * Checks if the application has permission to display overlays.
     * If not, it requests the user to grant overlay permission.
     */
    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                checkExactAlarmPermission() // Check for exact alarm permission
            } else {
                // Request overlay permission from the user
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                startActivityForResult(intent, 1000)
            }
        } else {
            checkExactAlarmPermission() // For older versions, check for exact alarm permission directly
        }
    }

    /**
     * Sets an alarm at the specified time.
     * @param timeInMillis The time in milliseconds when the alarm should go off.
     */
    private fun setAlarm(timeInMillis: Long) {
        alarmViewModel.setAlarm(timeInMillis) // Set the alarm using the ViewModel
        Toast.makeText(this, "Alarm set for ${Calendar.getInstance().time}", Toast.LENGTH_SHORT).show()
    }
}
