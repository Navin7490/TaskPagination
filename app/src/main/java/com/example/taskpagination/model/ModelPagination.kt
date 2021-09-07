package com.example.taskpagination.model
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.io.Serializable


data class NetworkPaginationModel(
     @DocumentId
     val id:String="",
     @ServerTimestamp
     val created_at:Timestamp?=null,
     val number:String?=null,
     val user_type:String?=null
)

@Parcelize
data class ModelPagination(
     val id: String,
     val createdAt:Timestamp,
     val number: String,
     val user_type: String
):Parcelable{
   companion object{

        fun  create(input:NetworkPaginationModel):ModelPagination{

             return ModelPagination(
                  id = input.id,
                  createdAt = input.created_at ?:throw NullPointerException("null created at"),
                  number = input.number ?:throw NullPointerException("null number"),
                  user_type = input.user_type ?: throw NullPointerException("null user type")
             )
        }

   }
}