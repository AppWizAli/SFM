package com.hiskytech.selfmademarket.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytech.selfmademarket.Adapter.AdapterForex
import com.hiskytech.selfmademarket.ApiInterface.ForexInterface
import com.hiskytech.selfmademarket.Model.DataXX
import com.hiskytech.selfmademarket.Model.ModelForex
import com.hiskytech.selfmademarket.Model.RetrofitBuilder
import com.hiskytech.selfmademarket.databinding.FragmentForexBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentForex : Fragment() {

    private var _binding: FragmentForexBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForexBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        binding.rvForex.layoutManager = LinearLayoutManager(context)

        fetchForex()
    }

    private fun fetchForex() {
        val apiInterface =  RetrofitBuilder.getInstance().create(ForexInterface::class.java)
        val call = apiInterface.getForex()

        call.enqueue(object : Callback<ModelForex> {
            override fun onResponse(call: Call<ModelForex>, response: Response<ModelForex>) {
                if (response.isSuccessful) {
                    val forexList = response.body()?.data ?: emptyList()
                    binding.rvForex.adapter = AdapterForex(requireContext(), forexList)
                } else {
                    Log.e("FetchError", "Response code: ${response.code()}")
                    Log.e("FetchError", "Response message: ${response.message()}")
                    Log.e("FetchError", "Error body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ModelForex>, t: Throwable) {
                Log.e("FetchError", "API call failed: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
