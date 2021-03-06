package com.sukie2.android.currencyconverter.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sukie2.android.currencyconverter.R
import com.sukie2.android.currencyconverter.utility.isOnline
import com.sukie2.android.currencyconverter.view.adapters.BaseValueChangeListener
import com.sukie2.android.currencyconverter.view.adapters.CurrencyAdapter
import com.sukie2.android.currencyconverter.view.adapters.LOAD_RATES
import com.sukie2.android.currencyconverter.view.viewmodel.ConverterViewModel
import kotlinx.android.synthetic.main.converter_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ConverterFragment : Fragment(), BaseValueChangeListener {

    private val viewModel by viewModel<ConverterViewModel>()
    private lateinit var adapter: CurrencyAdapter
    private var shouldUpdate = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    companion object {
        fun newInstance() = ConverterFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.converter_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        startDownloader()

        //Access the RecyclerView Adapter and load the data into it
        viewModel.currencyLiveData.observe(viewLifecycleOwner, Observer { ratesList ->
            if (adapter.itemCount == 0) {
                adapter.itemPositionList.addAll(ratesList.map { it.name })
                for (currency in ratesList) {
                    adapter.itemsMap[currency.name] = currency
                }
                adapter.notifyDataSetChanged()
            } else {
                if(shouldUpdate) {
                    for (currency in ratesList) {
                        adapter.itemsMap[currency.name] = currency
                    }
                    adapter.notifyItemRangeChanged(1, ratesList.lastIndex, LOAD_RATES)
                }
            }
        })

        initView()
    }

    private fun initView() {
        adapter = CurrencyAdapter(this)
        recyclerViewConverter.setHasFixedSize(true)
        recyclerViewConverter.layoutManager = LinearLayoutManager(context)
        recyclerViewConverter.adapter = adapter
    }

    private fun startDownloader(){
        if (isOnline(context)) {
            //Fires a timer with a 1 second time interval
            viewModel.starDownloadingRates()
        }  else {
            Toast.makeText(context, R.string.error_offline, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBaseCurrencyChanged(baseCurrency: String, baseAmount: Float) {
        shouldUpdate = false
        viewModel.baseCurrency = baseCurrency
        viewModel.baseAmount = baseAmount
        coroutineScope.launch {
            //Wait until the row moving animation is done.
            delay(500)
            shouldUpdate = true
        }
    }

}
