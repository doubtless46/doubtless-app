package com.doubtless.doubtless.screens.answers

sealed class AnswerItem {

    data class WriteAnswerItem(var id: String? = null ) : AnswerItem()


    data class ShowAnswerItem(
        var id: String? = null,
        var doubtId: String? = null,
        var authorId: String? = null,
        var authorName: String? = null,
        var authorPhotoUrl: String? = null,
        var description: String? = null,
    ) : AnswerItem()

    data class ShowQuestionItem(
        var id: String? = null,
        var doubtId: String? = null,
        var authorId: String? = null,
        var authorName: String? = null,
        var authorPhotoUrl: String? = null,
        var description: String? = null,
    ) : AnswerItem()
}
