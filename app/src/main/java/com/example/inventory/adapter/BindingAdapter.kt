package com.example.inventory.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.inventory.data.Item
import com.example.inventory.data.getFormattedPrice

@BindingAdapter("dataList")
fun bindRecyclerViewData(recyclerView: RecyclerView,dataList: LiveData<List<Item>?>){
    (recyclerView.adapter as ItemAdapter).submitList(dataList.value)
}

@BindingAdapter("item")
fun bindCurrencyData(textView: TextView,item: Item?){
    textView.text = item?.getFormattedPrice()
}