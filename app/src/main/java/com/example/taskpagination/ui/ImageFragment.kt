package com.example.taskpagination.ui

import android.content.Intent
import android.graphics.Bitmap
import android.media.FaceDetector
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.taskpagination.databinding.FragmentImageBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceLandmark
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

import java.io.ByteArrayOutputStream
import java.io.IOException


class ImageFragment : Fragment() {
   lateinit var binding: FragmentImageBinding
    lateinit var bitmap:Bitmap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // binding
        binding = FragmentImageBinding.inflate(inflater, container, false)

        binding.image.setOnClickListener {
            openGallery()
        }

        binding.btnDetected.setOnClickListener {



        }
        return binding.root



    }

    private fun openGallery(){
      val intent=  Intent(Intent.ACTION_GET_CONTENT)
        intent.type="image/*"
        startActivityForResult(intent, 0)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode ==0 && data != null  ){
            val uri= data.data

            bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            binding.image.setImageBitmap(bitmap)


            var image = InputImage.fromBitmap(bitmap, 0)

            try {
                val options=ImageLabelerOptions.Builder()
                    .setConfidenceThreshold(1.0f)
                    .build()
            // val labeler= FaceDetection.getClient().process(image)
               val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS).process(image)
                labeler.addOnSuccessListener { labele->

                    var spp= StringBuffer()
                    // imageLabel
                   for (label in labele) {
                        val text = label.text
                        val confidence = label.confidence
                        val index = label.index


                        spp.append("$text")
                        spp.append(",")
                        binding.tvResult.text="$spp"
                    }
                    // face detection

                    /*for (face in labele) {
                        val bounds = face.boundingBox
                        val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
                        val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

                        // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                        // nose available):
                        val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
                        leftEar?.let {
                            val leftEarPos = leftEar.position
                        }

                        // If contour detection was enabled:
                        val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
                        val upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points

                        // If classification was enabled:
                        if (face.smilingProbability != null) {
                            val smileProb = face.smilingProbability
                        }
                        if (face.rightEyeOpenProbability != null) {
                            val rightEyeOpenProb = face.rightEyeOpenProbability
                        }

                        // If face tracking was enabled:
                        if (face.trackingId != null) {
                            val id = face.trackingId
                        }
                    }*/

                    }
                    .addOnFailureListener{

                    }


            } catch (e: IOException) {
                e.printStackTrace()
            }


        }
    }


    private fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        var resizedWidth = maxDimension
        var resizedHeight = maxDimension
        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension
            resizedWidth =
                (resizedHeight * originalWidth.toFloat() / originalHeight.toFloat()).toInt()
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension
            resizedHeight =
                (resizedWidth * originalHeight.toFloat() / originalWidth.toFloat()).toInt()
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension
            resizedWidth = maxDimension
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false)
    }


    private fun bitmapToString(bitmap: Bitmap):String{
        // Convert bitmap to base64 encoded string
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
        val base64encoded = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
        return base64encoded

    }

}