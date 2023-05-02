package com.doubtless.doubtless.screens.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.doubtless.doubtless.databinding.FragmentCreatePostBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

class CreatePostFragment : Fragment() {
    private var _binding: FragmentCreatePostBinding? = null

    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.postButton.setOnClickListener {
            createPost(
                binding.postDoubt.text.toString(),
                binding.postHeading.text.toString(),
                binding.postDescription.text.toString()
            )
        }
    }

    private fun createPost(doubt: String, heading: String, description: String) {
        val post = hashMapOf(
            "doubt" to doubt,
            "heading" to heading,
            "description" to description,
            "date" to Date(),
            "username" to "Aditya",
            "uid" to "EDF90KLJFLKAUKLF"
        )
        db = Firebase.firestore
        db.collection("AllPosts").add(post).addOnSuccessListener {
            Toast.makeText(context, "Post Created", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Post Failed ${it.message}", Toast.LENGTH_SHORT).show()

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}