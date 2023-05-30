package com.doubtless.doubtless.screens.answers

import com.doubtless.doubtless.screens.doubt.DoubtData
import javax.crypto.NullCipher

data class AnswerDoubtEntity(
    val type: Int,
    val doubt: DoubtData? = null,
    val answer: AnswerItem? = null
) {
    companion object {
        const val TYPE_DOUBT = 1
        const val TYPE_ANSWER_ENTER = 2
        const val TYPE_ANSWER = 3

        fun getSearchEntity(): AnswerDoubtEntity {
            return AnswerDoubtEntity(AnswerDoubtEntity.TYPE_ANSWER_ENTER, null, null)
        }
    }


}
