package com.mycity4kids.models

import com.google.gson.annotations.SerializedName

data class LanguageSelectionData(
    @SerializedName("languageName")
    var languageName: String = "",
    @SerializedName("regionalLanguageName")
    var regionalLanguageName: String = "",
    @SerializedName("enabledImage")
    var enabledImage: Int? = null,
    @SerializedName("disabledImage")
    var disabledImage: Int? = null
)
