package xiaoya.kotlin.workoutapp

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import xiaoya.kotlin.workoutapp.databinding.FragmentRecipeBinding
import xiaoya.kotlin.workoutapp.network.FoodCategory
import java.net.HttpURLConnection
import java.net.URL

class RecipeFragment : Fragment() {

    private val viewModel: RecipeViewModel by viewModels()
    private var currentCategoryIndex = 0 // Variable to keep track of the current category index

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRecipeBinding.inflate(inflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.currentCategoryIndex = currentCategoryIndex

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            if (categories.isNotEmpty()) {
//                displayImage(categories[currentCategoryIndex].strCategoryThumb, binding.imageView)
                displayImageAndDescription(categories[currentCategoryIndex], binding)

                binding.btnNext.setOnClickListener {
                    if (currentCategoryIndex < categories.size - 1) {
                        currentCategoryIndex++
                    } else {
                        currentCategoryIndex = 0
                    }
//                    displayImage(categories[currentCategoryIndex].strCategoryThumb, binding.imageView)
                    displayImageAndDescription(categories[currentCategoryIndex], binding)
                }

                binding.btnBack.setOnClickListener {
                    if (currentCategoryIndex > 0) {
                        currentCategoryIndex--
                    } else {
                        currentCategoryIndex = categories.size - 1
                    }
//                    displayImage(categories[currentCategoryIndex].strCategoryThumb, binding.imageView)
                    displayImageAndDescription(categories[currentCategoryIndex], binding)
                }
            }
        }

        return binding.root
    }

    private fun displayImageAndDescription(category: FoodCategory, binding: FragmentRecipeBinding) {
        // Display image
        displayImage(category.strCategoryThumb, binding.imageView)

        // Display category description
        binding.textViewCategoryDescription.text = category.strCategoryDescription
    }

    private fun displayImage(imageUrl: String, imageView: ImageView) {
        val thread = Thread {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)

                activity?.runOnUiThread {
                    imageView.setImageBitmap(bitmap)
                }

                inputStream.close()
                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }
}