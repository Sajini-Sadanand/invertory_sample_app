package com.example.inventory.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {
    private var _edited = MutableLiveData(false)
    val edited: LiveData<Boolean> get() = _edited

    private var _itemList = MutableLiveData<List<Item>?>()
    val itemList: LiveData<List<Item>?> get() = _itemList

    private var _itemDetail = MutableLiveData<Item?>()
    val itemDetail: LiveData<Item?> get() = _itemDetail

    fun getItems() {
        viewModelScope.launch {
            itemDao.getItems().collect {
                _itemList.value = it
            }
        }
    }

    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

//    Collect not implemented, because, it causes error without tracking it for single item
    fun retrieveItem(id: Int):LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }

    fun update(item: Item) {
        synchronized(this) {
            viewModelScope.launch {
                Log.e("Update Item", item?.id.toString())
                itemDao.update(item)
            }
        }
    }

    fun sellItem() {
        synchronized(this) {
            if (itemDetail.value?.quantityInStock!! > 0) {
                Log.e("Sell Item", itemDetail.value?.id.toString())
//            Decrease the item by 1
                val newItem =
                    itemDetail.value?.copy(quantityInStock = itemDetail.value?.quantityInStock!! - 1)
                update(newItem!!)
            }
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: String): Item {
        return Item(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }

    fun addNewItem(itemName: String, itemPrice: String, itemCount: String) {
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount)
        insertItem(newItem)
    }

    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()) {
            return false
        }
        return true
    }

    fun deleteCurrentItem() {
        itemDetail.value?.let { deleteItem(it) }
    }

    fun setEdit(isEdit: Boolean) {
        _edited.value = isEdit
    }

    fun setItemDetails(retrieveItem: Item) {
        _itemDetail.value = retrieveItem
    }
}

class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}