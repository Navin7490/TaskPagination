package com.example.taskpagination.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize


data class UsersRealModel(
    var id:String="",
    val name: String="",
    val email: String="",
    val phone: String="",
    val password: String=""
)


@Parcelize
data class UserModel(
    var id:String="",
    val name:String="",
    val email:String="",
    val phone:String="",
    val password:String="",
) :Parcelable{
    companion object{
      // @Exclude
        fun create(usersRealModel: UsersRealModel):UserModel {

            return UserModel(
                id = usersRealModel.id?: throw NullPointerException("not empty"),
                name= usersRealModel.name?: throw NullPointerException("not empty"),
                email = usersRealModel.email?: throw NullPointerException("not empty"),
                phone =usersRealModel.phone?: throw NullPointerException("not empty"),
                password = usersRealModel.password?: throw NullPointerException("not empty")

            )
        }
    }
}
