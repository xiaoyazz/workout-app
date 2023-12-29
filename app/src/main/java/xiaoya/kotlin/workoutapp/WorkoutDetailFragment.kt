package xiaoya.kotlin.workoutapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import xiaoya.kotlin.workoutapp.data.workout.Workout
import xiaoya.kotlin.workoutapp.databinding.FragmentWorkoutDetailBinding

class WorkoutDetailFragment : Fragment() {
    lateinit var workout: Workout
    private val navigationArgs: WorkoutDetailFragmentArgs by navArgs()

    private var _binding: FragmentWorkoutDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkoutViewModel by activityViewModels {
            WorkoutViewModelFactory(
            (activity?.application as WorkoutApplication).workoutDB.workoutDao()
        )
    }


    private fun bind(workout: Workout) {
        binding.apply {
            workoutImage.setImageResource(getImageResource(requireContext(), workout.imageResId))
            workoutActivity.text = workout.workoutActivity
            duration.text = workout.duration
            calories.text = workout.calories
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkoutDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.workoutId

        viewModel.retrieveWorkout(id).observe(this.viewLifecycleOwner) { selectedItem ->
            workout = selectedItem
            bind(workout)
        }

        // Set an onClickListener for the btnStart button
        binding.btnStart.setOnClickListener {
            // Create an instance of WorkoutDetailsDialogFragment
            val workoutDetailsDialog = WorkoutDetailsDialogFragment()

            // Display the dialog fragment
            workoutDetailsDialog.show(childFragmentManager, "WorkoutDetailsDialogFragment")
        }
    }


    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}