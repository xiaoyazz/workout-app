package xiaoya.kotlin.workoutapp

import android.app.Application
import xiaoya.kotlin.workoutapp.data.item.ItemRoomDatabase
import xiaoya.kotlin.workoutapp.data.workout.WorkoutRoomDatabase

class WorkoutApplication : Application() {
    val database: ItemRoomDatabase by lazy { ItemRoomDatabase.getDatabase(this) }
    val workoutDB: WorkoutRoomDatabase by lazy { WorkoutRoomDatabase.getDatabase(this) }
}
