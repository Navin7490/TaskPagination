package com.example.taskpagination.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.taskpagination.R
import com.example.taskpagination.databinding.FragmentAddBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddFragment : Fragment() {
    lateinit var binding: FragmentAddBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        binding = FragmentAddBinding.inflate(inflater, container, false)


        binding.btnAdd.setOnClickListener {

            if (checkValidation()) {
                addRealDatabase()
            }

        }
        return binding.root


    }

    // check validation

    private fun checkValidation(): Boolean {
        var flag = true
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            flag = false
            Toast.makeText(requireContext(), "Required all field", Toast.LENGTH_SHORT).show()
        } else {
            flag = true
        }

        return flag

    }

    private fun addRealDatabase() {
        binding.progressbar.visibility = View.VISIBLE
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        val database = FirebaseDatabase.getInstance()
//        val key: String = database.getReference("Users").push().key.toString()
        val key: String = Firebase.auth.uid.toString()

        val db = Firebase.database.getReference("Users")
        val setData = hashMapOf<String, String>(

            "name" to binding.etName.text.toString(),
            "email" to binding.etEmail.text.toString(),
            "phone" to binding.etPhone.text.toString(),
            "password" to binding.etPassword.text.toString()
        )

        Firebase.auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Firebase.auth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener {
                            if (it.isSuccessful){

                                db.child(Firebase.auth.uid.toString()).setValue(setData)
                                    .addOnSuccessListener {
                                        binding.progressbar.visibility = View.GONE
                                        Toast.makeText(requireContext(),"Data added..",Toast.LENGTH_SHORT).show()
                                        Firebase.auth.signOut()
                                        findNavController().navigate(R.id.signInFragment)


                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(requireContext(),"${it.message}",Toast.LENGTH_SHORT).show()

                                        binding.progressbar.visibility = View.GONE

                                    }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(),"${it.message}",Toast.LENGTH_SHORT).show()

                            binding.progressbar.visibility = View.GONE

                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(),"${it.message}",Toast.LENGTH_SHORT).show()

                binding.progressbar.visibility = View.GONE

            }



    }


}