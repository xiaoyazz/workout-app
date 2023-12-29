package xiaoya.kotlin.workoutapp.data.workout

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(workout: Workout)

    @Update
    suspend fun update(workout: Workout)

    @Delete
    suspend fun delete(workout: Workout)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<Workout>)

    @Query("SELECT * from workout WHERE id = :id")
    fun getWorkout(id: Int): Flow<Workout>

    @Query("SELECT * from workout ORDER BY workoutActivity ASC")
    fun getWorkouts(): Flow<List<Workout>>

    @Query("SELECT * FROM workout WHERE workoutActivity = :activityName")
    fun getWorkoutByName(activityName: String): Flow<Workout?>


}