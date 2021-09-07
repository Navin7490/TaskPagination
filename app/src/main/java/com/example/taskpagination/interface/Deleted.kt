package com.example.taskpagination.`interface`

import com.example.taskpagination.model.ModelPagination

interface Deleted<T>:RecyclerViewPagination<ModelPagination> {

    fun  onDeletedItem(modelPagination: ModelPagination)

}