package com.hiskytech.selfmademarket.Fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytech.selfmademarket.Adapter.AdaterCommint
import com.hiskytech.selfmademarket.ApiInterface.CommentsInterface
import com.hiskytech.selfmademarket.Model.CommintsBuilder
import com.hiskytech.selfmademarket.Model.ModelCommint
import com.hiskytech.selfmademarket.R
import com.hiskytech.selfmademarket.databinding.FragmentCommunityBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentCommunity : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCommunity.layoutManager = LinearLayoutManager(context)
        fetchComments()
    }

    private fun fetchComments() {
        showAnimation()
        val apiInterface = CommintsBuilder.getInstance().create(CommentsInterface::class.java)
        val call = apiInterface.getCommints()

        call.enqueue(object : Callback<ModelCommint> {

            override fun onResponse(p0: Call<ModelCommint>, p1: Response<ModelCommint>) {
                closeAnimation()
                if (p1.isSuccessful) {
                    val commentsList = p1.body()?.comments ?: emptyList()
                    Log.d("FetchSuccess", "Fetched ${commentsList.size} comments")
                    binding.rvCommunity.adapter = AdaterCommint(requireContext(), commentsList)
                } else {
                    Log.e("FetchError", "Response code: ${p1.code()}")
                    Log.e("FetchError", "Response message: ${p1.message()}")
                    Log.e("FetchError", "Error body: ${p1.errorBody()?.string()}")
                }
            }

            override fun onFailure(p0: Call<ModelCommint>, p1: Throwable) {
                closeAnimation()
                Log.e("FetchError", "API call failed: ${p1.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
}
