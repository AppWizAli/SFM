package com.hiskytech.selfmademarket.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hiskytech.selfmademarket.Adapter.AdapterBitcoin
import com.hiskytech.selfmademarket.ApiInterface.BitCoinInterface
import com.hiskytech.selfmademarket.Model.BitCoinBuilder
import com.hiskytech.selfmademarket.Model.DataX
import com.hiskytech.selfmademarket.Model.ModelBitCoin
import com.hiskytech.selfmademarket.databinding.FragmentCryptoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentCrypto : Fragment() {

    private var _binding: FragmentCryptoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCryptoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        binding.rvCoin.layoutManager = LinearLayoutManager(context)

        fetchBitCoins()
    }

    private fun fetchBitCoins() {
        val apiInterface = BitCoinBuilder.getInstance().create(BitCoinInterface::class.java)
        val call = apiInterface.getBitCoin()

        call.enqueue(object : Callback<ModelBitCoin> {
            override fun onResponse(call: Call<ModelBitCoin>, response: Response<ModelBitCoin>) {
                if (response.isSuccessful) {
                    val bitCoinList = response.body()?.data ?: emptyList()
                    binding.rvCoin.adapter = AdapterBitcoin(requireContext(), bitCoinList)
                } else {
                    Log.e("FetchError", "Response code: ${response.code()}")
                    Log.e("FetchError", "Response message: ${response.message()}")
                    Log.e("FetchError", "Error body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ModelBitCoin>, t: Throwable) {
                Log.e("FetchError", "API call failed: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
