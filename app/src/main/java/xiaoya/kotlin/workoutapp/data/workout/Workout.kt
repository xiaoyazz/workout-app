package xiaoya.kotlin.workoutapp.data.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout")
data class Workout (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "workoutActivity")
    val workoutActivity: String,
    @ColumnInfo(name = "duration")
    val duration: String,
    @ColumnInfo(name = "calories")
    val calories: String,
    @ColumnInfo(name = "imageResId")
    val imageResId: String

)