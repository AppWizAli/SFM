package com.hiskytech.selfmademarket.Fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.hiskytech.selfmademarket.ActivityInvoices
import com.hiskytech.selfmademarket.Model.NotificationBuilder
import com.hiskytech.selfmademarket.Model.ModelNotification
import com.hiskytech.selfmademarket.Model.ModelStoryResponse
import com.hiskytech.selfmademarket.Model.StoryBuilder.apiInterface
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.Repo.MySharedPref
import com.hiskytech.selfmademarket.databinding.FragmentPublishStoryBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class FragmentPublishStory : Fragment() {
    private lateinit var mySharedPref: MySharedPref
    private var _binding: FragmentPublishStoryBinding? = null
    private var screenshotUri: Uri? = null
    private val IMAGE_PICK_CODE = 1000
    private val binding get() = _binding!!
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPublishStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mySharedPref = MySharedPref(requireContext())

        binding.btnNotification.setOnClickListener {
            fetchNotificationsAndShowDialog()
        }

        val fullUrl = "https://en.selfmademarket.net/planemanger/uploads/${mySharedPref.getUserModel()?.user_image}"
        Glide.with(requireContext()).load(fullUrl).into(binding.img)
        binding.courseName.text = mySharedPref.getUserModel()?.name
        Glide.with(requireContext()).load(fullUrl).into(binding.profileImage)
        binding.username.text = mySharedPref.getUserModel()?.name

        binding.layImage.setOnClickListener {
            pickImageFromGallery()
        }

        binding.btnSubmit.setOnClickListener {
            showAnimation()
            submitStory()
        }

        binding.img.setOnClickListener {
            val intent = Intent(requireContext(), ActivityInvoices::class.java)
            startActivity(intent)
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

    private fun uriToFile(uri: Uri): File {
        val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
        val file = File(requireContext().cacheDir, getFileName(uri))
        val outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
        }
        return file
    }

    private fun getFileName(uri: Uri): String {
        var name = ""
        val returnCursor = requireContext().contentResolver.query(uri, null, null, null, null)
        if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            name = returnCursor.getString(nameIndex)
            returnCursor.close()
        }
        return name
    }

    private fun submitStory() {
        val username = mySharedPref.getUserModel()?.name ?: ""
        val description = binding.etDescription.text.toString()

        if (username.isNotEmpty() && description.isNotEmpty()) {
            val usernamePart = RequestBody.create("text/plain".toMediaType(), username)
            val descriptionPart = RequestBody.create("text/plain".toMediaType(), description)
            val user_image = RequestBody.create("text/plain".toMediaType(), mySharedPref.getUserModel()?.user_image!!)

            screenshotUri?.let { uri ->
                val screenShot = uriToFile(uri)
                val requestFile = RequestBody.create("image/jpeg".toMediaType(), screenShot)
                val screenShotImage = MultipartBody.Part.createFormData("image", screenShot.name, requestFile)

                apiInterface.publishStory(usernamePart, descriptionPart, user_image,screenShotImage)
                    .enqueue(object : Callback<ModelStoryResponse> {
                        override fun onResponse(
                            call: Call<ModelStoryResponse>,
                            response: Response<ModelStoryResponse>
                        ) {
                            closeAnimation()
                            if (response.isSuccessful) {
                                binding.etDescription.setText("")
                                Toast.makeText(requireContext(), "Story published successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "Failed to publish story", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<ModelStoryResponse>, t: Throwable) {
                            closeAnimation()
                            Log.e("API_ERROR", "Failed to publish story: ${t.message}")
                            Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            } ?: run {
                closeAnimation()
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAnimation() {
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.loadingdialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun closeAnimation() {
        dialog.dismiss()
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val imageUri = data?.data
            imageUri?.let {

                binding.imgAdd.visibility=View.VISIBLE
                binding.layImage.visibility=View.GONE
                screenshotUri = it
                Glide.with(requireContext()).load(screenshotUri).centerCrop().into(binding.imgAdd)
            }
        }
    }

    private fun compressImage(uri: Uri): ByteArray {
        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
        val outputStream = ByteArrayOutputStream()
        // Compress bitmap to JPEG format with 75% quality
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
        return outputStream.toByteArray()
    }

    private fun createPartFromUri(context: Context, uri: Uri?, partName: String): MultipartBody.Part? {
        return uri?.let {
            try {
                // Use the compressImage function
                val byteArray = compressImage(uri)
                val file = File(context.cacheDir, getFileName(uri))
                file.writeBytes(byteArray)

                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
                MultipartBody.Part.createFormData(partName, file.name, requestFile)
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error creating file from URI: ${e.message}")
                null
            }
        }
    }
}
