package com.example.taskpagination.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.taskpagination.BuildConfig
import com.example.taskpagination.R
import com.example.taskpagination.databinding.FragmentSignInBinding
import com.example.taskpagination.util.InAppMessageClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.common.base.Objects
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.setConsent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.inappmessaging.FirebaseInAppMessaging
import com.google.firebase.inappmessaging.ktx.inAppMessaging
import com.google.firebase.inappmessaging.model.Action
import com.google.firebase.inappmessaging.model.Button
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.android.synthetic.main.fragment_sign_in.*


class SignInFragment : Fragment() {
    lateinit var binding: FragmentSignInBinding
    var packageInfo:PackageInfo?= null
     lateinit var firebaseRemoteConfig:FirebaseRemoteConfig
    private lateinit var firebaseIam: FirebaseInAppMessaging
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater,container,false)

        //cGhTB-StQq2wBHsueWAh7E
        // eRNXVx-6RuGxquUjtNRELw



        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        // start firebase In- app message event
        firebaseIam = FirebaseInAppMessaging.getInstance()
        firebaseIam.isAutomaticDataCollectionEnabled = true
        firebaseIam.setMessagesSuppressed(true)
        binding.btnSignIn.setOnClickListener { view ->
            //firebaseAnalytics.logEvent("engagement_party", Bundle())
            firebaseIam.triggerEvent("engagement_party")


        }


        // Get and display/log the Instance ID
        FirebaseInstallations.getInstance().id.addOnSuccessListener {
           // instanceIdText.text = getString(R.string.instance_id_fmt, it)
            Log.d("TAG", "InstanceId: $it")

        }

        addClickListener()
        suppressMessages()
        enableDataCollection()

        // end firebase In- app message event


        binding.btnAll.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.btnSignUpGo.setOnClickListener {
           // findNavController().navigate(R.id.addFragment)
            findNavController().navigate(R.id.imageFragment)
        }

//        binding.btnSignIn.setOnClickListener {
//             if (checkValidation()){
//                 signInProcess()
//             }
//        }


        // start firebase remote config
        // firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseRemoteConfig =Firebase.remoteConfig

        val value:HashMap<String,String> = HashMap()
       // value["new_version_code"] = getVersionCode().toString()
      val configSettings  =  FirebaseRemoteConfigSettings.Builder()
          .setFetchTimeoutInSeconds(10)
          .build()

        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener {
              if (it.isSuccessful){
                  
                 val newVersionCode = firebaseRemoteConfig.getString("new_version_code")
                  Log.e("tagVersionCode", newVersionCode)
                  if (newVersionCode.toInt() > getVersionCode()){
                     // showDialog(newVersionCode)
                  }
              }else{
                  Log.e("tag","fail")

              }
          }


        // end firebase remote config


        return binding.root
    }


    private fun addClickListener() {
        val listener = InAppMessageClickListener()
        firebaseIam.addClickListener(listener)
    }
    private fun suppressMessages() {
        firebaseIam.setMessagesSuppressed(true)
    }
    private fun enableDataCollection() {
        // Only needed if firebase_inapp_messaging_auto_data_collection_enabled is set to
        // false in AndroidManifest.xml
        firebaseIam.isAutomaticDataCollectionEnabled = true
    }

    private fun showDialog(newVersionCode: String) {

        MaterialAlertDialogBuilder(requireContext())
      .setTitle("New update available")
      .setMessage("previous version :${BuildConfig.VERSION_CODE} \nLatest version is $newVersionCode")
      .setPositiveButton("Update Now"){dialog,_->

          try {
              startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("")))
          }catch (e:android.content.ActivityNotFoundException){
              e.printStackTrace()
          }

      }
      .setNegativeButton("No thanks"){dialog,_->
          dialog.dismiss()
      }
      .setCancelable(false)
      .show()

    }

    fun  getVersionCode(): Int {


//        try {
//             packageInfo = requireActivity().packageManager.getPackageInfo(requireActivity().packageName,0)
//
//        }catch (e:PackageManager.NameNotFoundException){
//            Log.e("tag","Name not found ${e.message}")
//        }
       return BuildConfig.VERSION_CODE
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