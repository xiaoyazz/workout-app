package xiaoya.kotlin.workoutapp

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import xiaoya.kotlin.workoutapp.databinding.CustomWorkoutStepBinding

class WorkoutDetailsDialogFragment : DialogFragment() {

    private var _binding: CustomWorkoutStepBinding? = null
    private val binding get() = _binding!!
    private val AUTO_SCROLL_DELAY: Long = 3000
    private var currentPage = 0
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var countdownTimer: CountDownTimer
    private val initialTimeInMillis = 30 * 60 * 1000
    private var timeRemainingInMillis = initialTimeInMillis

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CustomWorkoutStepBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up ViewPager2 with images
        val images = listOf(R.drawable.yoga1, R.drawable.yoga2, R.drawable.yoga3) // Replace with your image resources
        val viewPager: ViewPager2 = binding.viewPager
        val adapter = ImagesPagerAdapter(images)
        viewPager.adapter = adapter

        // Set up timer TextView
        val timerTextView: TextView = binding.timerTextView

        // Set up close button
        binding.closeButton.setOnClickListener {
            dismiss()
        }
        startAutoScroll(viewPager)
        startCountdownTimer(timerTextView)
    }

    private fun startAutoScroll(viewPager: ViewPager2) {
        val runnable = object : Runnable {
            override fun run() {
                if (currentPage == viewPager.adapter?.itemCount) {
                    currentPage = 0
                }
                viewPager.setCurrentItem(currentPage++, true)
                handler.postDelayed(this, AUTO_SCROLL_DELAY)
            }
        }
        handler.postDelayed(runnable, AUTO_SCROLL_DELAY)
    }

    private fun startCountdownTimer(timerTextView: TextView) {
        countdownTimer = object : CountDownTimer(timeRemainingInMillis.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemainingInMillis = millisUntilFinished.toInt()
                updateCountdownUI(timerTextView)
            }

            override fun onFinish() {
                // Handle actions after countdown finishes (if needed)
            }
        }
        countdownTimer.start()
    }

    private fun updateCountdownUI(timerTextView: TextView) {
        val minutes = (timeRemainingInMillis / 1000) / 60
        val seconds = (timeRemainingInMillis / 1000) % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        timerTextView.text = timeFormatted
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownTimer.cancel()
        _binding = null
    }
}