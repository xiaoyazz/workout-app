package xiaoya.kotlin.workoutapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import xiaoya.kotlin.workoutapp.data.workout.Workout
import xiaoya.kotlin.workoutapp.databinding.FragmentWorkoutItemBinding


class WorkoutListAdapter(private val onItemClicked: (Workout) -> Unit) :
    ListAdapter<Workout, WorkoutListAdapter.ItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            FragmentWorkoutItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    class ItemViewHolder(private var binding: FragmentWorkoutItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(workout: Workout) {
            binding.apply {
                workoutActivity.text = workout.workoutActivity
                duration.text = workout.duration
                calories.text = workout.calories
                imageView.setImageResource(getImageResource(itemView.context, workout.imageResId))

            }
        }
        // Function to retrieve the drawable resource ID from the name
        private fun getImageResource(context: Context, imageName: String): Int {
            return context.resources.getIdentifier(
                imageName,
                "drawable",
                context.packageName
            )
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Workout>() {
            override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
                return oldItem.workoutActivity == newItem.workoutActivity
            }
        }
    }
}