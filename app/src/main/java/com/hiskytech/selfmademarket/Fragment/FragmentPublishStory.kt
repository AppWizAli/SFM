package com.hiskytech.selfmademarket.Fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import okhttp3.MediaType.Companion.toMediaType
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hiskytech.selfmademarket.Model.NotificationBuilder
import com.hiskytech.selfmademarket.Model.ModelNotification
import com.hiskytech.selfmademarket.Model.ModelStoryResponse
import com.hiskytech.selfmademarket.Model.StoryBuilder.apiInterface
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.databinding.FragmentPublishStoryBinding
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentPublishStory : Fragment() {

    private var _binding: FragmentPublishStoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPublishStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNotification.setOnClickListener {
            fetchNotificationsAndShowDialog()
        }

        binding.btnSubmit.setOnClickListener {
            submitStory()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchNotificationsAndShowDialog() {
        val notificationInterface = NotificationBuilder.getNotificationInterface()
        val call = notificationInterface.getNotification()

        call.enqueue(object : Callback<ModelNotification> {
            override fun onResponse(
                call: Call<ModelNotification>,
                response: Response<ModelNotification>
            ) {
                if (response.isSuccessful) {
                    val notifications = response.body()
                    if (!notifications.isNullOrEmpty()) {
                        // Show the first notification in the dialog
                        val firstNotification = notifications[0]
                        showDialog(firstNotification.title, firstNotification.message)
                    } else {
                        Toast.makeText(requireContext(), "No notifications available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to get notifications", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ModelNotification>, t: Throwable) {
                Log.e("API_ERROR", "Failed to fetch notifications: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDialog(title: String, message: String) {
        val dialog = Dialog(requireContext())
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.notification_dialog, null)

        dialog.setContentView(dialogView)

        // Set the dialog to show at the top
        dialog.window?.setGravity(Gravity.TOP)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes = dialog.window?.attributes?.apply {
            y = 100 // Adjust the vertical offset if needed
        }

        // Find and set up views in the custom dialog layout
        val dialogTitle = dialogView.findViewById<TextView>(R.id.notificationTitle)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.notificationDescription)

        dialogTitle.text = title
        dialogMessage.text = message

        dialog.show()
    }

    private fun submitStory() {
        val username = binding.etUsername.text.toString()
        val description = binding.etDescription.text.toString()

        // Check if the input fields are not empty
        if (username.isNotEmpty() && description.isNotEmpty()) {
            val usernamePart = RequestBody.create("text/plain".toMediaType(), username)
            val descriptionPart = RequestBody.create("text/plain".toMediaType(), description)

//            // Assuming you have a placeholder image to send; replace with actual image part if needed
//            val imageFile = File("path/to/your/image") // Replace with the actual image path
//            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageFile.asRequestBody())

            apiInterface.publishStory(usernamePart, descriptionPart)
                .enqueue(object : Callback<ModelStoryResponse> {
                    override fun onResponse(
                        call: Call<ModelStoryResponse>,
                        response: Response<ModelStoryResponse>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(requireContext(), "Story published successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Failed to publish story", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ModelStoryResponse>, t: Throwable) {
                        Log.e("API_ERROR", "Failed to publish story: ${t.message}")
                        Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }
}
