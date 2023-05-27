package com.doubtless.doubtless.screens.doubt

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.analytics.AnalyticsTracker
import com.doubtless.doubtless.databinding.FragmentCreateDoubtBinding
import com.doubtless.doubtless.screens.auth.User
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import java.util.Date
import java.util.UUID
import kotlin.properties.Delegates

class CreateDoubtFragment : Fragment() {
    private var _binding: FragmentCreateDoubtBinding? = null

    private val binding get() = _binding!!
    private var isButtonClicked = false

    private lateinit var db: FirebaseFirestore
    private lateinit var userManager: UserManager
    private lateinit var analyticsTracker: AnalyticsTracker

    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var configSettings: FirebaseRemoteConfigSettings
    private var maxHeadingCharLimit by Delegates.notNull<Int>()
    private var maxDescriptionCharLimit by Delegates.notNull<Int>()
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Firebase.firestore
        userManager = DoubtlessApp.getInstance().getAppCompRoot().getUserManager()
        analyticsTracker = DoubtlessApp.getInstance().getAppCompRoot().getAnalyticsTracker()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateDoubtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        remoteConfig = FirebaseRemoteConfig.getInstance()
        configSettings =
            FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(10).build()

        binding.doubtHeading.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
        binding.doubtHeading.requestFocus()
        val mgr = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.showSoftInput(binding.doubtHeading, InputMethodManager.SHOW_FORCED)

        getMaxCharacterLimit()

        sharedPreferences = requireContext().getSharedPreferences("post_data", Context.MODE_PRIVATE)

        binding.postButton.setOnClickListener {
            if (!isButtonClicked) {
                checkText()
            }
        }
        val (savedHeadingText, savedDescriptionText) = getSharedPreferencesData()

        savedHeadingText?.let {
            if (it.isNotEmpty()) {
                binding.doubtHeading.setText(savedHeadingText)
            }
        }

        savedDescriptionText?.let {
            if (it.isNotEmpty()) {
                binding.doubtDescription.setText(savedDescriptionText)
            }
        }


    }

    override fun onPause() {
        super.onPause()
        saveTextToSharedPreferences(
            binding.doubtHeading.text.toString(), binding.doubtDescription.text.toString()
        )
    }

    private fun getSharedPreferencesData(): Pair<String?, String?> {
        val headingText = sharedPreferences.getString("headingText", null)
        val descriptionText = sharedPreferences.getString("descriptionText", null)
        return Pair(headingText, descriptionText)
    }

    private fun saveTextToSharedPreferences(headingText: String, descriptionText: String) {
        val editor = sharedPreferences.edit()
        editor.putString("headingText", headingText)
        editor.putString("descriptionText", descriptionText)
        editor.apply()
    }


    private fun checkText() {
        if (binding.doubtHeading.text.toString().isEmpty()) {
            Toast.makeText(context, "Heading Required", Toast.LENGTH_SHORT).show()
        } else {

            analyticsTracker.trackPostDoubtButtonClicked()

            isButtonClicked = true
            binding.progress.isVisible = !binding.progress.isVisible
            binding.postButton.isClickable = false
            binding.postButton.alpha = 0.8f

            showConfirmationDialog()

        }
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmation").setMessage("Are you sure you want to post?")
            .setPositiveButton("Post") { dialogInterface: DialogInterface, _: Int ->
                createDoubt(
                    binding.doubtHeading.text.toString(),
                    binding.doubtDescription.text.toString(),
                    userManager.getCachedUserData()!!
                )
                dialogInterface.dismiss()
            }.setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                isButtonClicked = false
                binding.postButton.alpha = 1f
                dialogInterface.dismiss()
            }.show()
    }

    private fun getMaxCharacterLimit() {
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                val maxCharLimit = remoteConfig.getString("max_character_limit")
                try {
                    val jsonObject = Gson().fromJson(maxCharLimit, Map::class.java) as Map<*, *>
                    maxHeadingCharLimit = (jsonObject["max_heading_char_limit"] as Double).toInt()
                    maxDescriptionCharLimit =
                        (jsonObject["max_description_char_limit"] as Double).toInt()
                    setMaxCharacterLimit(maxHeadingCharLimit, maxDescriptionCharLimit)

                } catch (e: Exception) {
//                    Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Try Again Later", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setMaxCharacterLimit(maxHeadingCharLimit: Int, maxDescriptionCharLimit: Int) {
        binding.doubtHeading.filters =
            arrayOf<InputFilter>(InputFilter.LengthFilter(maxHeadingCharLimit))
        binding.doubtDescription.filters =
            arrayOf<InputFilter>(InputFilter.LengthFilter(maxDescriptionCharLimit))
    }

    private fun createDoubt(heading: String, description: String, user: User) {
        val doubt = DoubtData(
            id = UUID.randomUUID().toString(),
            userName = user.name,
            userId = user.id,
            userPhotoUrl = user.photoUrl,
            date = Date().toString(),
            heading = heading.trim(),
            description = description.trim(),
            upVotes = 0,
            downVotes = 0,
            score = (0..100).random().toLong(), // fixme : for testing
            timeMillis = System.currentTimeMillis()
        )

        db.collection("AllDoubts").add(doubt).addOnSuccessListener {
            isButtonClicked = false
            binding.postButton.alpha = 1f
            binding.doubtHeading.setText("")
            binding.doubtDescription.setText("")

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