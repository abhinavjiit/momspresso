package com.mycity4kids.models.campaignmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class FaqResponse {
    @SerializedName("faqs")
    @Expose
    var faqs: ArrayList<QuestionAnswerResponse>? = null
}

class QuestionAnswerResponse {
    @SerializedName("answer")
    @Expose
    var answer: String? = null
    @SerializedName("question")
    @Expose
    var question: String? = null

}

