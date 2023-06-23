package com.doubtless.doubtless.screens.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.widget.AutoCompleteTextView
import android.widget.ArrayAdapter
import android.os.Bundle
import android.util.Log
import com.doubtless.doubtless.R
import android.widget.ListView
import android.widget.Toast
import androidx.core.view.children
import com.doubtless.doubtless.DoubtlessApp
import com.doubtless.doubtless.navigation.Router
import com.doubtless.doubtless.screens.auth.UserAttributes
import com.doubtless.doubtless.screens.onboarding.usecases.AddOnBoardingDataUseCase
import com.doubtless.doubtless.screens.onboarding.usecases.FetchOnBoardingDataUseCase
import com.doubtless.doubtless.theming.buttons.SecondaryButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OnBoardingActivity : AppCompatActivity() {

    private var college: Int = -1
    private var purpose: Int = -1
    private var year: Int = -1
    private var department: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val userManager = DoubtlessApp.getInstance().getAppCompRoot().getUserManager()
        val fetchOnBoardingDataUseCase =
            DoubtlessApp.getInstance().getAppCompRoot()
                .getFetchOnBoardingDataUseCase(userManager.getCachedUserData()!!)
        val addOnBoardingDataUseCase =
            DoubtlessApp.getInstance().getAppCompRoot()
                .getAddOnBoardingDataUseCase(userManager.getCachedUserData()!!)


        CoroutineScope(Dispatchers.Main).launch {

            val result = fetchOnBoardingDataUseCase.getData()

            if (result is FetchOnBoardingDataUseCase.Result.Error) {
                Toast.makeText(this@OnBoardingActivity, result.error, Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }

            if (this@OnBoardingActivity.isDestroyed) return@launch

            val onBoardingAttributes = (result as FetchOnBoardingDataUseCase.Result.Success).data

            val autoCompleteTxtYear: AutoCompleteTextView =
                findViewById(R.id.auto_complete_txt_year)
            val adapterItemsYear = ArrayAdapter(
                this@OnBoardingActivity,
                R.layout.list_item,
                onBoardingAttributes.years!!.toMutableList()
            )
            autoCompleteTxtYear.setAdapter(adapterItemsYear)
            autoCompleteTxtYear.setOnItemClickListener { parent, view, position, id ->
                year = position
            }

            val autoCompleteTxtDept: AutoCompleteTextView =
                findViewById(R.id.auto_complete_txt_dept)
            val adapterItemsDept = ArrayAdapter(
                this@OnBoardingActivity,
                R.layout.list_item,
                onBoardingAttributes.departments!!.toMutableList()
            )
            autoCompleteTxtDept.setAdapter(adapterItemsDept)
            autoCompleteTxtDept.setOnItemClickListener { parent, view, position, id ->
                department = position
            }

            val autoCompleteTxtPurpose: AutoCompleteTextView =
                findViewById(R.id.auto_complete_txt_purpose)
            val adapterItemsPurpose = ArrayAdapter(
                this@OnBoardingActivity,
                R.layout.list_item,
                onBoardingAttributes.purposes!!.toMutableList()
            )
            autoCompleteTxtPurpose.setAdapter(adapterItemsPurpose)
            autoCompleteTxtPurpose.setOnItemClickListener { parent, view, position, id ->
                purpose = position
            }

            val autoCompleteCollege =
                findViewById<AutoCompleteTextView>(R.id.auto_complete_txt_college)
            val collegeAdapter: ArrayAdapter<String> = ArrayAdapter(
                this@OnBoardingActivity,
                R.layout.list_item,
                onBoardingAttributes.colleges!!.toMutableList()
            )
            autoCompleteCollege.setAdapter(collegeAdapter)
            autoCompleteCollege.setOnItemClickListener { parent, view, position, id ->
                college = position
            }

            val chipGroupHobbies = findViewById<ChipGroup>(R.id.chipGroupHobbies)

            repeat(onBoardingAttributes.hobbies!!.size) {
                val chip = Chip(this@OnBoardingActivity).apply {
                    text = onBoardingAttributes.hobbies!!.get(it)
                    isCheckable = true
                }
                chipGroupHobbies.addView(chip)
            }

            val chipGroupTags = findViewById<ChipGroup>(R.id.chipGroupTag)

            repeat(onBoardingAttributes.tags!!.size) {
                val chip = Chip(this@OnBoardingActivity).apply {
                    text = onBoardingAttributes.tags!!.get(it)
                    isCheckable = true
                }
                chipGroupTags.addView(chip)
            }

            findViewById<SecondaryButton>(R.id.btn_done).setOnClickListener {
                var checkedCount = 0

                // ----- Hobbies

                chipGroupHobbies.children.forEach {
                    if ((it as Chip).isChecked)
                        checkedCount += 1
                }

                if (checkedCount > 3) {
                    Toast.makeText(
                        this@OnBoardingActivity,
                        "Select Only 3 Hobbies",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // ----- TAGS

                checkedCount = 0

                chipGroupTags.children.forEach {
                    if ((it as Chip).isChecked)
                        checkedCount += 1
                }

                if (checkedCount > 3) {
                    Toast.makeText(
                        this@OnBoardingActivity,
                        getString(R.string.select_at_max_tags),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else if (checkedCount == 0) {
                    Toast.makeText(
                        this@OnBoardingActivity,
                        "Please select atleast one tag",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                // ----- AutoCompleteViews

                if (college == ListView.INVALID_POSITION) {
                    showError("Please select college!")
                    return@setOnClickListener
                }

                if (department == ListView.INVALID_POSITION) {
                    showError("Please select department!")
                    return@setOnClickListener
                }

                if (purpose == ListView.INVALID_POSITION) {
                    showError("Please select purpose!")
                    return@setOnClickListener
                }

                if (year == ListView.INVALID_POSITION) {
                    showError("Please select year!")
                    return@setOnClickListener
                }

                val checkedHobbies = mutableListOf<String>()

                chipGroupHobbies.checkedChipIds.forEach {
                    checkedHobbies.add(chipGroupHobbies.findViewById<Chip>(it).text.toString())
                }

                val checkedTags = mutableListOf<String>()

                chipGroupTags.checkedChipIds.forEach {
                    checkedTags.add(chipGroupTags.findViewById<Chip>(it).text.toString())
                }

                val userAttributes = UserAttributes(
                    tags = checkedTags,
                    hobbies = checkedHobbies,
                    year = adapterItemsYear.getItem(year),
                    department = adapterItemsDept.getItem(department),
                    college = collegeAdapter.getItem(college),
                    purpose = adapterItemsPurpose.getItem(purpose)
                )

                CoroutineScope(Dispatchers.Main).launch {

                    val result = addOnBoardingDataUseCase.add(userAttributes)

                    if (isDestroyed) return@launch

                    if (result is AddOnBoardingDataUseCase.Result.Error) {
                        showError(result.message)
                        return@launch
                    }

                    // update cached user-data
                    val user = userManager.getCachedUserData()!!.copy(local_user_attr = userAttributes)
                    userManager.setNewCachedUserData(user)
                    userManager.storeCachedUserData()

                    DoubtlessApp.getInstance()
                        .getAppCompRoot().router.moveToMainActivity(this@OnBoardingActivity)
                    finish()
                }
            }
        }

    }

    private fun showError(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}