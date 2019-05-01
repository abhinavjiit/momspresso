package com.mycity4kids.ui.campaign

data class PaymentModeListModal(
        val available: List<PaymentModesModal>? = null,
        val default: DefaultData? = null
)
