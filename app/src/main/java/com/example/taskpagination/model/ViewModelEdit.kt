package com.example.taskpagination.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelEdit: ViewModel() {

     val isEditing =MutableLiveData<Boolean>(false)
    fun  setEditing(flag:Boolean){
        isEditing.value =flag
    }
}