package xiaoya.kotlin.workoutapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        // Handle the alarm action here
        val message = intent?.getStringExtra("MESSAGE") ?: "Time to move your body!"
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    }
}
