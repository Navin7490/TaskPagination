package com.example.taskpagination.util

import com.google.firebase.firestore.model.Values
import com.google.firestore.v1.Value
import java.lang.NullPointerException

class Constant {
}

enum class UserType (val value:String){

      CUSTOMER("customer"),
      SELLER("seller"),
      EMPLOYEE("employee") ;

    companion object{
        private val type= values().associateBy { it.value }

        fun findValue(value: String) = type[value]?: NullPointerException("No exist value")
    }

}