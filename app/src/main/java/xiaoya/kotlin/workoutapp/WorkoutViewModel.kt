package xiaoya.kotlin.workoutapp

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import xiaoya.kotlin.workoutapp.data.workout.Workout
import xiaoya.kotlin.workoutapp.data.workout.WorkoutDao

class WorkoutViewModel(private val workoutDao: WorkoutDao) : ViewModel() {

    val allWorkouts: LiveData<List<Workout>> = workoutDao.getWorkouts().asLiveData()

    init {
        val dummyWorkouts = DummyWorkoutData.getDummyWorkouts()
        for (workout in dummyWorkouts) {
            insertItem(workout)
        }
    }

    object DummyWorkoutData {

        fun getDummyWorkouts(): List<Workout> {
            val dummyWorkouts = mutableListOf<Workout>()

            // Add dummy workouts
            dummyWorkouts.add(
                Workout(
                    workoutActivity = "Weight Lifting",
                    duration = "30 minutes",
                    calories = "350 Cal",
                    imageResId = "workout2"
                )
            )

            dummyWorkouts.add(
                Workout(
                    workoutActivity = "Boxing",
                    duration = "30 minutes",
                    calories = "300 Cal",
                    imageResId = "workout3"
                )
            )

            dummyWorkouts.add(
                Workout(
                    workoutActivity = "Strength",
                    duration = "30 minutes",
                    calories = "300 Cal",
                    imageResId = "workout4"
                )
            )

            dummyWorkouts.add(
                Workout(
                    workoutActivity = "At-Home Yoga",
                    duration = "30 minutes",
                    calories = "200 Cal",
                    imageResId = "workout1"
                )
            )

            return dummyWorkouts
        }
    }


    private fun insertItem(workout: Workout) {
        viewModelScope.launch {
            workoutDao.insert(workout)
        }
    }

    private fun getNewItemEntry(workoutActivity: String, duration: String, calories: String, imageResId: String): Workout {
        return Workout(
            workoutActivity = workoutActivity,
            duration = duration,
            calories = calories,
            imageResId = imageResId
        )
    }

//    private fun getUpdatedItemEntry(
//        workoutId: Int,
//        workoutActivity: String,
//        duration: String,
//        calories: String
//    ): Workout {
//        return Workout(
//            id = workoutId,
//            workoutActivity = workoutActivity,
//            duration = duration,
//            calories = calories
//        )
//    }

//    fun addNewWorkout(workoutActivity: String, duration: String, calories: String) {
//        val newWorkout = getNewItemEntry(workoutActivity, duration, calories)
//        insertItem(newWorkout)
//    }

    fun isEntryValid(workoutActivity: String, duration: String, calories: String): Boolean {
        if (workoutActivity.isBlank() || duration.isBlank() || calories.isBlank()) {
            return false
        }
        return true
    }

    fun retrieveWorkout(id: Int): LiveData<Workout> {
        return workoutDao.getWorkout(id).asLiveData()
    }

    private fun updateWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.update(workout)
        }
    }

//    fun updateWorkout(
//        itemId: Int,
//        activity: String,
//        date: String,
//        time: String
//    ) {
//        val updatedWorkout = getUpdatedItemEntry(itemId, activity, date, time)
//        updateWorkout(updatedWorkout)
//    }

    fun deleteWorkout(workout: Workout) {
        viewModelScope.launch {
            workoutDao.delete(workout)
        }
    }
}

class WorkoutViewModelFactory(private val workoutDao: WorkoutDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutViewModel(workoutDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}