package com.hiskytech.selfmademarket.Fragment

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
import com.hiskytech.selfmademarket.Model.ModelComments
import com.hiskytech.selfmademarket.Model.ModelCommint
import com.hiskytech.selfmademarket.databinding.FragmentCommunityBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentCommunity : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

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
        val apiInterface = CommintsBuilder.getInstance().create(CommentsInterface::class.java)
        val call = apiInterface.getCommints()

        call.enqueue(object : Callback<ModelCommint> {

            override fun onResponse(call: Call<ModelCommint>, response: Response<ModelCommint>) {
                if (response.isSuccessful) {
                    val commentsList = response.body()?.comments ?: emptyList()
                    Log.d("FetchSuccess", "Fetched ${commentsList.size} comments")
                    binding.rvCommunity.adapter = AdaterCommint(requireContext(), commentsList)
                } else {
                    Log.e("FetchError", "Response code: ${response.code()}")
                    Log.e("FetchError", "Response message: ${response.message()}")
                    Log.e("FetchError", "Error body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ModelCommint>, t: Throwable) {
                Log.e("FragmentCommunity", "Error fetching courses", t)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
