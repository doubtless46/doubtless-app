package com.doubtless.doubtless.screens.doubt

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.doubtless.doubtless.databinding.FragmentCreateDoubtBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

class CreateDoubtFragment : Fragment() {
    private var _binding: FragmentCreateDoubtBinding? = null

    private val binding get() = _binding!!
    private var isButtonClicked = false
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateDoubtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.doubtHeading.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
        binding.doubtHeading.requestFocus()
        val mgr = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.showSoftInput(binding.doubtHeading, InputMethodManager.SHOW_FORCED)

        binding.postButton.setOnClickListener {
            if (!isButtonClicked) {
                checkText()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            handleBackPress()
        }

        binding.close.setOnClickListener {
            handleBackPress()
        }

    }

    private fun handleBackPress() {
        if (binding.doubtHeading.text.toString()
                .isNotEmpty() || binding.doubtDescription.text.toString().isNotEmpty()
        ) {
            showWarningDialog()
        } else {
            requireActivity().finish()
        }
    }

    private fun showWarningDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Are you sure?")
        builder.setMessage("Do you want to go back?")
        builder.setPositiveButton("Yes") { _, _ ->
            requireActivity().finish()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.show()
    }


    private fun checkText() {
        if (binding.doubtHeading.text.toString().isEmpty()) {
            Toast.makeText(context, "Heading Required", Toast.LENGTH_SHORT).show()
        } else {
            isButtonClicked = true
            binding.progress.isVisible = !binding.progress.isVisible
            binding.postButton.isClickable = false
            binding.postButton.alpha = 0.8f
            createDoubt(
                binding.doubtHeading.text.toString(), binding.doubtDescription.text.toString()
            )
        }
    }

    private fun createDoubt(heading: String, description: String) {
        val doubt = hashMapOf(
            "heading" to heading,
            "description" to description,
            "date" to Date(),
            "uid" to "EDF90KLJFLKAUKLF",
            "upVotes" to listOf<String>(),
            "downVotes" to listOf<String>(),
            "answersIds" to listOf<String>()
        )
        db = Firebase.firestore
        db.collection("AllDoubts").add(doubt).addOnSuccessListener {
            requireActivity().finish()
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to Post ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}