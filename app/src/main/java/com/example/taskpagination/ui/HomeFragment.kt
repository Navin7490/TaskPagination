package com.example.taskpagination.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskpagination.R
import com.example.taskpagination.`interface`.Deleted
import com.example.taskpagination.adapter.AdapterPagination
import com.example.taskpagination.databinding.FragmentHomeBinding
import com.example.taskpagination.model.*
import com.example.taskpagination.model.ModelPagination.Companion.create
import com.example.taskpagination.util.UserType
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment(), Deleted<UserModel> {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentHomeBinding
    lateinit var adapterPagination: AdapterPagination
    private val pageSize: Long = 10
    private var lastDocument: DocumentSnapshot? = null
    private var listenerRegistration: ArrayList<ListenerRegistration> = arrayListOf()
    var reachedEnd = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // data binding



        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.setHasFixedSize(true)
        adapterPagination = AdapterPagination(arrayListOf(), this)
        binding.recyclerView.adapter = adapterPagination
        reachedEnd = false


        //realTimeUpdate()
        // dataPagination()
        getRealTimeDatabase()

        binding.btnAdd.setOnClickListener {
            if (checkValidation()) {
                //addNumber()
                addToRealtimeDatabase()
            }
        }
        return binding.root
    }

    override fun onDestroy() {
        lastDocument = null

        listenerRegistration.forEach {
            it.remove()
        }
        listenerRegistration.clear()
        super.onDestroy()

    }


    private fun checkValidation(): Boolean {
        var flag = true
        val text = binding.etAdd.text.toString().trim()
        if (text.isEmpty()) {
            flag = false
            Toast.makeText(requireContext(), "Enter number", Toast.LENGTH_SHORT).show()
        }

        if (text.isNotEmpty()) {
            flag = true
        }

        return flag

    }

    // add real time database

    private  fun addToRealtimeDatabase() {
        binding.progressbar.visibility = View.VISIBLE

        val text = binding.etAdd.text.toString().trim()
        val id = Firebase.database.getReference("Users").push().key.toString()
        val addMap = hashMapOf<String,Any>(
            "phone" to text
        )

        Firebase.database.getReference("Users").child(id)
            .setValue(addMap)
            .addOnCompleteListener {
                binding.progressbar.visibility = View.GONE

                if (it.isSuccessful){

                    binding.etAdd.text = null
                    Toast.makeText(requireContext(),"data added..",Toast.LENGTH_SHORT).show()
                }
            }
    }
  // add cloud fire store
    private fun addNumber() {
        binding.progressbar.visibility = View.VISIBLE
        val add = hashMapOf<String, Any>(
            CollectionUsers.kCreatedAt to Timestamp.now(),
            CollectionUsers.kNumber to binding.etAdd.text.toString().trim(),
            CollectionUsers.kUserType to UserType.CUSTOMER.value
        )
        Firebase.firestore.collection(CollectionUsers.name).document()
            .set(add)
            .addOnCompleteListener {
                binding.progressbar.visibility = View.GONE

                if (it.isSuccessful) {
                    binding.etAdd.text = null
                    adapterPagination.updateDataSource(listOf())
                    Toast.makeText(requireContext(), "Added item ", Toast.LENGTH_SHORT).show()
                }
            }
    }


    // get data in real time database
    private fun getRealTimeDatabase() {
        binding.progressbar.visibility = View.VISIBLE

        Firebase.database.getReference("Users")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val arrayList = ArrayList<UserModel>()

                    for (data in snapshot.children){
                        val usersRealModel = data.getValue(UsersRealModel::class.java)

                        if (usersRealModel != null) {
                            usersRealModel.id = data.key.toString()
                            val userModel = UserModel.create(usersRealModel)
                            Log.e("tagUser", "$userModel")
                            arrayList.add(userModel)

                        }

                    }
                    binding.progressbar.visibility = View.GONE

                    adapterPagination.updateDatReal(arrayList)
                }

                override fun onCancelled(error: DatabaseError) {
                    binding.progressbar.visibility = View.GONE

                    Log.e("tag", "${error.message}")
                }

            })

    }

    // update data
    private fun updateRealTimeDatbase() {
        val db = Firebase.database.reference
        //  db.child("Users").child()
    }



//    private fun realTimeUpdate() {
//        binding.progressbar.visibility = View.VISIBLE
//
//
////        val settiing= FirebaseFirestoreSettings.Builder()
////            .setPersistenceEnabled(true)
////            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).build()
////        db.firestoreSettings = settiing
//        if (!reachedEnd) {
//
//
//            val query = if (lastDocument == null) {
//                Firebase.firestore.collection(CollectionUsers.name)
//                    .whereEqualTo(CollectionUsers.kUserType, UserType.CUSTOMER.value)
//                    .orderBy(CollectionUsers.kCreatedAt, Query.Direction.ASCENDING)
//                    .limit(pageSize)
//
//            } else {
//                Firebase.firestore.collection(CollectionUsers.name)
//                    .whereEqualTo(CollectionUsers.kUserType, UserType.CUSTOMER.value)
//                    .orderBy(CollectionUsers.kCreatedAt, Query.Direction.ASCENDING)
//                    .startAfter(lastDocument!!)
//                    .limit(pageSize)
//
//            }
//
//
//            val listener = query.addSnapshotListener { querySnapShot, error ->
//
//                if (error != null) {
//                    Log.e("error", "$error")
//                    binding.progressbar.visibility = View.GONE
//
//                } else {
//                    if (querySnapShot != null) {
//                        if (!querySnapShot.isEmpty) {
//                            binding.progressbar.visibility = View.GONE
//
//                            reachedEnd = querySnapShot.documents.size < pageSize.toInt()
//                            lastDocument = querySnapShot.documents.last()
//
//                            for (dc in querySnapShot.documentChanges) {
//
//                                when (dc.type) {
//
//                                    DocumentChange.Type.ADDED -> {
//                                        Log.e("tag", "add change type")
//
//                                        val data =
//                                            if (dc.document.metadata.hasPendingWrites()) {
//                                                "local"
//                                            } else {
//                                                "server"
//                                            }
//                                        Log.e("tag", "$data")
//
//
//                                        val networkPaginationModel = dc.document.toObject(
//                                            NetworkPaginationModel::class.java
//                                        )
//                                        val modelPagination = ModelPagination.create(
//                                            networkPaginationModel
//                                        )
//
//                                        adapterPagination.insertNewData(modelPagination)
//
//                                    }
//                                    DocumentChange.Type.MODIFIED -> {
//                                        Log.e("tag", "modified change type")
//
//                                        val networkPaginationModel = dc.document.toObject(
//                                            NetworkPaginationModel::class.java
//                                        )
//                                        val modelPagination = ModelPagination.create(
//                                            networkPaginationModel
//                                        )
//                                        adapterPagination.updateData(modelPagination)
//
//                                    }
//
//                                    DocumentChange.Type.REMOVED -> {
//                                        Log.e("tag", "remove change type")
//
//                                        adapterPagination.removeData(dc.document.id)
//
//                                    }
//                                    else -> {
//
//                                    }
//                                }
//                            }
//
//
//                            // reachedEnd = true
//
//
//                        } else {
//                            // reachedEnd = true
//                            binding.progressbar.visibility = View.GONE
//
//                        }
//                    } else {
//                        // reachedEnd = true
//                        binding.progressbar.visibility = View.GONE
//
//                    }
//
//                }
//            }
//
//            // add listener
//            listenerRegistration.add(listener)
//
//        } else {
//            binding.progressbar.visibility = View.GONE
//
//        }
//
//
//    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_top, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                findNavController().navigate(R.id.addFragment)
            }

        }

        return super.onOptionsItemSelected(item)

    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDeletedItem(modelPagination: UserModel) {
        binding.progressbar.visibility = View.VISIBLE
        val updateData = hashMapOf<String,Any>(
            "name"  to  "vijay"
        )

        Firebase.database.getReference("Users").child(modelPagination.id)
            .updateChildren(updateData)
            .addOnCompleteListener {
                binding.progressbar.visibility = View.GONE

                if (it.isSuccessful) {
                   // adapterPagination.removeData(modelPagination.id)
                      adapterPagination.updateData(modelPagination)
                    Toast.makeText(requireContext(), "item removed", Toast.LENGTH_SHORT).show()
                }
            }
//        Firebase.firestore.collection(CollectionUsers.name).document(modelPagination.id)
//            .delete()
//            .addOnCompleteListener {
//                binding.progressbar.visibility = View.GONE
//
//                if (it.isSuccessful) {
//                    adapterPagination.removeData(modelPagination.id)
//                    Toast.makeText(requireContext(), "item removed", Toast.LENGTH_SHORT).show()
//                }
//            }
    }

    override fun didScrollEnd(position: Int) {
        Log.e("tag", "$position")
        //realTimeUpdate()
    }

}



//private fun dataPagination() {
//    binding.progressbar.visibility = View.VISIBLE
//
//    if (!reachedEnd) {
//
//        var query = Firebase.firestore.collection(CollectionUsers.name)
//            .whereEqualTo(CollectionUsers.kUserType, UserType.CUSTOMER.value)
//            .orderBy(CollectionUsers.kCreatedAt, Query.Direction.ASCENDING)
//
//        if (lastDocument != null) {
//            query = query.startAfter(lastDocument!!)
//        }
//        query = query.limit(pageSize)
//        Tasks.whenAllComplete(query.get())
//
//            .addOnCompleteListener { response ->
//
//                if (response.isSuccessful) {
//
//                    val arrayList = ArrayList<ModelPagination>()
//                    val resultTasks = response.result
//
//                    var docsCount = 0
//
//                    if (resultTasks != null) {
//
//                        for (resultTask in resultTasks) {
//
//                            val querySnapshot: QuerySnapshot =
//                                resultTask.result as QuerySnapshot
//
//                            docsCount += querySnapshot.documents.size
//
//                            lastDocument = querySnapshot.documents.lastOrNull()
//
//                            for (documentSnapShot in querySnapshot.documents) {
//                                val networkPaginationModel =
//                                    documentSnapShot.toObject(NetworkPaginationModel::class.java)
//                                if (networkPaginationModel != null) {
//                                    val modelPagination =
//                                        ModelPagination.create(networkPaginationModel)
//                                    arrayList.add(modelPagination)
//
//                                }
//
//                            }
//                        }
//                        reachedEnd = docsCount < pageSize.toInt()
//
//                        adapterPagination.updateDataSource(arrayList)
//                        Log.e("tag", "updated")
//
//                        binding.progressbar.visibility = View.GONE
//                    }
//
//                } else {
//                    binding.progressbar.visibility = View.GONE
//
//                }
//
//            }
//
//    } else {
//        binding.progressbar.visibility = View.GONE
//
//    }
//
//
//}
