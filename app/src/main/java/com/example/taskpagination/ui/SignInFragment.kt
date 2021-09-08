package com.example.taskpagination.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.taskpagination.R
import com.example.taskpagination.databinding.FragmentSignInBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_sign_in.*


class SignInFragment : Fragment() {
    lateinit var binding: FragmentSignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater,container,false)

        binding.btnAll.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.btnSignUpGo.setOnClickListener {
            findNavController().navigate(R.id.addFragment)
        }

        binding.btnSignIn.setOnClickListener {
             if (checkValidation()){
                 signInProcess()
             }
        }

        return binding.root
    }


    // check validation

    private  fun checkValidation() :Boolean{
        var flag = true
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()){
            flag = false
            Toast.makeText(requireContext(),"Required all field",Toast.LENGTH_SHORT).show()
        } else {
            flag = true
        }

        return  flag

    }

    // sign in user

    private  fun  signInProcess() {
        binding.progressbar.visibility = View.VISIBLE
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        Firebase.auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                binding.progressbar.visibility = View.GONE

                if (it.isSuccessful){
                    findNavController().navigate(R.id.homeFragment)
                }
            }
            .addOnFailureListener {
                binding.progressbar.visibility = View.GONE

                Toast.makeText(requireContext(),"${it.message}",Toast.LENGTH_SHORT).show()
            }
    }


}