package com.example.taskpagination.`interface`

import com.example.taskpagination.model.ModelPagination
import com.example.taskpagination.model.UserModel

interface Deleted<T>:RecyclerViewPagination<UserModel> {

    fun  onDeletedItem(modelPagination: UserModel)

}