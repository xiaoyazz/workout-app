package xiaoya.kotlin.workoutapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import xiaoya.kotlin.workoutapp.databinding.FragmentWorkoutListBinding

/**
 * A simple [Fragment] subclass.
 * Use the [WorkoutListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WorkoutListFragment : Fragment() {

    private var _binding: FragmentWorkoutListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkoutViewModel by activityViewModels {
        WorkoutViewModelFactory(
            (activity?.application as WorkoutApplication).workoutDB.workoutDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWorkoutListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = WorkoutListAdapter {
            val action = WorkoutListFragmentDirections.actionWorkoutListFragmentToWorkoutDetailFragment(it.id)
            this.findNavController().navigate(action)
        }
        viewModel.allWorkouts.observe(this.viewLifecycleOwner) { workouts ->
            workouts.let {
                adapter.submitList(it)
            }
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
//        binding.floatingActionButton.setOnClickListener {
//            val action = WorkoutListFragmentDirections.actionWorkoutListFragmentToItemListFragment(
//            )
//            this.findNavController().navigate(action)
//        }
    }
}
