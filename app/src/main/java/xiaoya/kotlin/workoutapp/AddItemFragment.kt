package xiaoya.kotlin.workoutapp

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract.Calendars
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import xiaoya.kotlin.workoutapp.data.item.Item
import java.text.SimpleDateFormat
import java.util.*
import xiaoya.kotlin.workoutapp.databinding.FragmentAddItemBinding as FragmentAddItemBinding1

/**
 * Fragment to add or update an item in the Inventory database.
 */
class AddItemFragment : Fragment() {

    private val viewModel: ItemViewModel by activityViewModels {
        ItemViewModelFactory(
            (activity?.application as WorkoutApplication).database
                .itemDao()
        )
    }
    lateinit var item: Item

    private val navigationArgs: ItemDetailFragmentArgs by navArgs()

    private val calendar = Calendar.getInstance()

    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0
    private var hour: Int = 0
    private var minute: Int = 0

    private val MY_PERMISSIONS_REQUEST_ALARM = 123

    // Binding object instance corresponding to the fragment_add_item.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private var _binding: FragmentAddItemBinding1? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddItemBinding1.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.itemId
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                item = selectedItem
                bind(item)

                // Show date and time pickers when editing an existing item
                binding.btnShowDatePicker.setOnClickListener {
                    showDatePicker()
                }

                binding.btnShowTimePicker.setOnClickListener {
                    showTimePicker()
                }
            }
        } else {
            binding.saveAction.setOnClickListener {
                addNewItem()
            }

            binding.btnShowDatePicker.setOnClickListener {
                showDatePicker()
            }

            binding.btnShowTimePicker.setOnClickListener {
                showTimePicker()
            }
        }
    }

//    private fun showDatePicker(){
//        val datePickerDialog = DatePickerDialog(requireContext(), {DatePicker, year: Int, monthOfYear: Int,
//        dayOfMonth: Int ->
//            val selectedDate = Calendar.getInstance()
//            selectedDate.set(year, monthOfYear, dayOfMonth)
//            val dateFormat = SimpleDateFormat("dd/MM/yyyy",Locale.getDefault())
//            val formattedDate = dateFormat.format(selectedDate.time)
//            binding.txtDate.text = formattedDate
//        },
//
//            calendar.get(Calendar.YEAR),
//            calendar.get(Calendar.MONTH),
//            calendar.get(Calendar.DAY_OF_MONTH)
//            )
//        datePickerDialog.show()
//    }
//
//    private fun showTimePicker() {
//        val timePickerDialog = TimePickerDialog(
//            requireContext(),
//            { timePicker, hourOfDay, minute ->
//                val selectedTime = Calendar.getInstance().apply {
//                    set(Calendar.HOUR_OF_DAY, hourOfDay)
//                    set(Calendar.MINUTE, minute)
//                }
//                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
//                val formattedTime = timeFormat.format(selectedTime.time)
//                binding.txtTime.text = formattedTime
//            },
//            calendar.get(Calendar.HOUR_OF_DAY),
//            calendar.get(Calendar.MINUTE),
//            true // Set true for 24-hour format, false for AM/PM format
//        )
//        timePickerDialog.show()
//    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                year = selectedYear
                month = selectedMonth
                day = selectedDay
                val selectedDate = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                binding.txtDate.text = formattedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
        checkAlarmPermissionAndSet()
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                hour = selectedHour
                minute = selectedMinute
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                }
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val formattedTime = timeFormat.format(selectedTime.time)
                binding.txtTime.text = formattedTime
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // Set true for 24-hour format, false for AM/PM format
        )
        timePickerDialog.show()
        checkAlarmPermissionAndSet()
    }

    private fun checkAlarmPermissionAndSet() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.SCHEDULE_EXACT_ALARM
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted, request it
            requestAlarmPermission()
        } else {
            // Permission granted or SDK < S, set the alarm
            setAlarm()
        }
    }

    private fun requestAlarmPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM),
            MY_PERMISSIONS_REQUEST_ALARM
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_ALARM) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, set the alarm
                setAlarm()
            } else {
                // Permission denied, inform the user or handle accordingly
                // For instance, show a message or prompt the user to grant permission again
            }
        }
    }

    private fun setAlarm() {
        // Convert the selected date and time to milliseconds
        val selectedDateTime = Calendar.getInstance().apply {
            // Set the selected date and time retrieved from your TextViews
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }.timeInMillis

        Log.d("Alarm", "Setting alarm for: $selectedDateTime")

        val readableDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            .format(selectedDateTime)

        Toast.makeText(requireContext(), "Alarm set for $readableDateTime", Toast.LENGTH_LONG).show()

        // Check if the app has permission to set alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            requireContext().checkSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the necessary permission
            requestPermissions(
                arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM),
                MY_PERMISSIONS_REQUEST_ALARM
            )
        } else {
            // Create an Intent for the AlarmReceiver
            val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java)

            // Pass any extra data if needed
            alarmIntent.putExtra("MESSAGE", "Your alarm message goes here")

            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                0,
                alarmIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )

            // Get AlarmManager instance
            val alarmManager =
                requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Set the alarm using AlarmManager if permission is granted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                requireContext().checkSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
                == PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        selectedDateTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        selectedDateTime,
                        pendingIntent
                    )
                }
            } else {
                // Handle the case when permission is not granted
                // You can inform the user that the alarm cannot be set without permission
                // Or prompt them again to grant the permission
            }
        }
    }


//
//    private fun setAlarm() {
//        // Convert the selected date and time to milliseconds
//        val selectedDateTime = Calendar.getInstance().apply {
//            // Set the selected date and time retrieved from your TextViews
//            set(Calendar.YEAR, year)
//            set(Calendar.MONTH, month)
//            set(Calendar.DAY_OF_MONTH, day)
//            set(Calendar.HOUR_OF_DAY, hour)
//            set(Calendar.MINUTE, minute)
//        }.timeInMillis
//
//        // Create an Intent for the AlarmReceiver
//        val alarmIntent = Intent(context, AlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(
//            requireContext(),
//            0,
//            alarmIntent,
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                PendingIntent.FLAG_MUTABLE // or PendingIntent.FLAG_IMMUTABLE
//            } else {
//                PendingIntent.FLAG_UPDATE_CURRENT
//            }
//        )
//
//        // Set the alarm using AlarmManager
//        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        alarmManager.setExact(
//            AlarmManager.RTC_WAKEUP,
//            selectedDateTime,
//            pendingIntent
//        )
//    }


    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.activity.text.toString(),
            binding.txtDate.text.toString(),
            binding.txtTime.text.toString()
        )
    }

    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewItem(
                binding.activity.text.toString(),
                binding.txtDate.text.toString(),
                binding.txtTime.text.toString(),
            )
            setAlarm()
        }
        val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
        findNavController().navigate(action)
    }

    private fun bind(item: Item) {

        binding.apply {
            activity.setText(item.activity, TextView.BufferType.SPANNABLE)
            txtDate.setText(item.date, TextView.BufferType.SPANNABLE)
            txtTime.setText(item.time, TextView.BufferType.SPANNABLE)
            saveAction.setOnClickListener { updateItem() }
        }
    }

    private fun updateItem() {
        if (isEntryValid()) {
            viewModel.updateItem(
                this.navigationArgs.itemId,
                this.binding.activity.text.toString(),
                this.binding.txtDate.text.toString(),
                this.binding.txtTime.text.toString()
            )
            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
            findNavController().navigate(action)
            setAlarm()
        }
    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}
