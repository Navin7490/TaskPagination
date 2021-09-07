package com.example.taskpagination.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskpagination.R
import com.example.taskpagination.`interface`.Deleted
import com.example.taskpagination.`interface`.RecyclerViewPagination
import com.example.taskpagination.adapter.AdapterPagination
import com.example.taskpagination.databinding.FragmentHomeBinding
import com.example.taskpagination.model.CollectionUsers
import com.example.taskpagination.model.ModelPagination
import com.example.taskpagination.model.NetworkPaginationModel
import com.example.taskpagination.model.ViewModelEdit
import com.example.taskpagination.util.UserType
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment(), Deleted<ModelPagination> {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentHomeBinding
    lateinit var adapterPagination: AdapterPagination
    private val pageSize: Long = 10
    private var lastDocument: DocumentSnapshot? = null
    private var listenerRegistration:ArrayList<ListenerRegistration> = arrayListOf()
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

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.setHasFixedSize(true)
        adapterPagination = AdapterPagination(arrayListOf(), this)
        binding.recyclerView.adapter = adapterPagination
        reachedEnd = false


        realTimeUpdate()
       // dataPagination()

        binding.btnAdd.setOnClickListener {
            if (checkValidation()) {
                addNumber()
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

    private fun dataPagination() {
        binding.progressbar.visibility = View.VISIBLE

        if (!reachedEnd) {

            var query = Firebase.firestore.collection(CollectionUsers.name)
                .whereEqualTo(CollectionUsers.kUserType, UserType.CUSTOMER.value)
                .orderBy(CollectionUsers.kCreatedAt, Query.Direction.ASCENDING)

            if (lastDocument != null) {
                query = query.startAfter(lastDocument!!)
            }
            query = query.limit(pageSize)
            Tasks.whenAllComplete(query.get())

                .addOnCompleteListener { response ->

                    if (response.isSuccessful) {

                       val  arrayList = ArrayList<ModelPagination>()
                        val resultTasks = response.result

                        var docsCount = 0

                        if (resultTasks != null) {

                            for (resultTask in resultTasks) {

                                val querySnapshot: QuerySnapshot =
                                    resultTask.result as QuerySnapshot

                                docsCount += querySnapshot.documents.size

                                lastDocument = querySnapshot.documents.lastOrNull()

                                for (documentSnapShot in querySnapshot.documents) {
                                    val networkPaginationModel =
                                        documentSnapShot.toObject(NetworkPaginationModel::class.java)
                                    if (networkPaginationModel != null) {
                                        val modelPagination =
                                            ModelPagination.create(networkPaginationModel)
                                        arrayList.add(modelPagination)

                                    }

                                }
                            }
                            reachedEnd = docsCount < pageSize.toInt()

                            adapterPagination.updateDataSource(arrayList)
                            Log.e("tag", "updated")

                            binding.progressbar.visibility = View.GONE
                        }

                    } else {
                        binding.progressbar.visibility = View.GONE

                    }

                }

        } else {
            binding.progressbar.visibility = View.GONE

        }


    }


    private fun realTimeUpdate() {
        binding.progressbar.visibility = View.VISIBLE

        if (!reachedEnd){
            val query =if (lastDocument == null){
                Firebase.firestore.collection(CollectionUsers.name)
                    .whereEqualTo(CollectionUsers.kUserType, UserType.CUSTOMER.value)
                    .orderBy(CollectionUsers.kCreatedAt,Query.Direction.ASCENDING)
                    .limit(pageSize)

            } else{
                Firebase.firestore.collection(CollectionUsers.name)
                    .whereEqualTo(CollectionUsers.kUserType, UserType.CUSTOMER.value)
                    .orderBy(CollectionUsers.kCreatedAt,Query.Direction.ASCENDING)
                    .startAfter(lastDocument!!)
                    .limit(pageSize)
            }

          val listener=  query.addSnapshotListener { querySnapShot, error ->

                if (error != null) {
                    Log.e("error", "$error")
                    binding.progressbar.visibility = View.GONE

                } else {
                    if (querySnapShot != null) {
                        if (!querySnapShot.isEmpty) {
                            binding.progressbar.visibility = View.GONE

                            reachedEnd = querySnapShot.documents.size < pageSize.toInt()
                            lastDocument = querySnapShot.documents.last()

                                for (dc in querySnapShot.documentChanges) {

                                    when (dc.type) {

                                        DocumentChange.Type.ADDED -> {
                                            Log.e("tag","add change type")

                                            val networkPaginationModel= dc.document.toObject(NetworkPaginationModel::class.java)
                                            val modelPagination = ModelPagination.create(networkPaginationModel)

                                            adapterPagination.insertNewData(modelPagination)

                                        }
                                        DocumentChange.Type.MODIFIED -> {
                                            Log.e("tag","modified change type")

                                            val networkPaginationModel= dc.document.toObject(NetworkPaginationModel::class.java)
                                            val modelPagination = ModelPagination.create(networkPaginationModel)
                                            adapterPagination.updateData(modelPagination)

                                        }

                                        DocumentChange.Type.REMOVED -> {
                                            Log.e("tag","remove change type")

                                            adapterPagination.removeData(dc.document.id)

                                        }
                                        else -> {

                                        }
                                    }
                                }


                               // reachedEnd = true


                        }else{
                           // reachedEnd = true
                            binding.progressbar.visibility = View.GONE

                        }
                    }else{
                       // reachedEnd = true
                        binding.progressbar.visibility = View.GONE

                    }

                }
            }

            // add listener
            listenerRegistration.add(listener)

        }else{
            binding.progressbar.visibility = View.GONE

        }



    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_top, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> {
            }
            R.id.update -> {

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

    override fun onDeletedItem(modelPagination: ModelPagination) {
        binding.progressbar.visibility = View.VISIBLE
        Firebase.firestore.collection(CollectionUsers.name).document(modelPagination.id)
            .delete()
            .addOnCompleteListener {
                binding.progressbar.visibility = View.GONE

                if (it.isSuccessful) {
                    adapterPagination.removeData(modelPagination.id)
                    Toast.makeText(requireContext(), "item removed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun didScrollEnd(position: Int) {
        Log.e("tag", "$position")
       // dataPagination()
        realTimeUpdate()
    }

}