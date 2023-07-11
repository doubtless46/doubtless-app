package com.doubtless.doubtless.screens.dashboard.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.doubtless.doubtless.R
import com.doubtless.doubtless.screens.auth.usecases.UserManager
import com.doubtless.doubtless.theming.buttons.SecondaryButton
import com.doubtless.doubtless.utils.Utils.flatten

class UserProfileViewHolder(view: View, private val interactionListener: InteractionListener) :
    RecyclerView.ViewHolder(view) {

    interface InteractionListener {
        fun onDeleteAccountClicked()
        fun onSignOutClicked()
        fun onSubmitFeedbackClicked()
    }

    private val userImage: ImageView
    private val userName: TextView
    private val userEmail: TextView
    private val signOutButton: SecondaryButton
    private val submitFeedback: TextView
    private val deleteAccount: TextView
    private val tvBio: TextView


    init {
        userName = view.findViewById(R.id.tv_name)
        userImage = view.findViewById(R.id.iv_user_image)
        userEmail = view.findViewById(R.id.tv_user_email)
        signOutButton = view.findViewById(R.id.btn_signout)
        submitFeedback = view.findViewById(R.id.btnFeedback)
        deleteAccount = view.findViewById(R.id.btn_delete_account)
        tvBio = view.findViewById(R.id.tv_bio)
    }

    fun setData(userManager: UserManager) {

        deleteAccount.setOnClickListener {
            interactionListener.onDeleteAccountClicked()
        }

        signOutButton.setOnClickListener {
            interactionListener.onSignOutClicked()
        }

        submitFeedback.setOnClickListener {
            interactionListener.onSubmitFeedbackClicked()
        }

        userName.text = userManager.getCachedUserData()!!.name
        userEmail.text = userManager.getCachedUserData()!!.email

        var tags = ""

        userManager.getCachedUserData()!!.local_user_attr!!.tags?.forEach {
            tags += "#$it "
        }

        tvBio.text = tags

        Glide.with(userImage).load(userManager.getCachedUserData()!!.photoUrl).circleCrop()
            .into(userImage)
    }
}