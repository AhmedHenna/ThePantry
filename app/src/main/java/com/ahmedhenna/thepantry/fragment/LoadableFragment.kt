package com.ahmedhenna.thepantry.fragment

import androidx.fragment.app.Fragment
import com.techiness.progressdialoglibrary.ProgressDialog

open class LoadableFragment: Fragment() {

    private var progressDialog: ProgressDialog? = null

    fun showLoading(){
        progressDialog = ProgressDialog(requireContext())
        progressDialog!!.show()
    }

    fun hideLoading(){
        progressDialog?.dismiss()
    }
}