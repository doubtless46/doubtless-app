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
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.databinding.FragmentCreateDoubtBinding
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date
import java.util.UUID

class CreateDoubtFragment : Fragment() {
    private var _binding: FragmentCreateDoubtBinding? = null

    private val binding get() = _binding!!
    private var isButtonClicked = false

    private lateinit var db: FirebaseFirestore
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        userManager = DoubtlessApp.getInstance().getAppCompRoot().getUserManager()
    }

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
                binding.doubtHeading.text.toString(), binding.doubtDescription.text.toString(),
                userManager.getCachedUserData()!!
            )
        }
    }

    private fun createDoubt(heading: String, description: String, user: User) {
        val doubt = DoubtData(
            id = UUID.randomUUID().toString(),
            userName = user.name,
            userId = user.id,
            userPhotoUrl = user.photoUrl,
            date = Date().toString(),
            heading = heading,
            description = description,
            upVotes = 0,
            downVotes = 0,
            score = (0..100).random().toLong(), // fixme : for testing
            timeMillis = System.currentTimeMillis()
        )

        db.collection("AllDoubts").add(doubt).addOnSuccessListener {
            // requireActivity().finish()
            isButtonClicked = false
            binding.postButton.alpha = 1f
            Toast.makeText(context, "Posted Successfully!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            isButtonClicked = false
            binding.postButton.alpha = 1f
            Toast.makeText(context, "Failed to Post ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}