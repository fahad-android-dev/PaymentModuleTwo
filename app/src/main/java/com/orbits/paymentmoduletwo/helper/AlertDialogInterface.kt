package com.orbits.paymentmoduletwo.helper

import android.view.View

interface AlertDialogInterface {
    fun onYesClick() {}
    fun onNoClick() {}
    fun onCloseDialog() {}
    fun onSubmitPasswordClick(emailID: String, view: View) {}
}