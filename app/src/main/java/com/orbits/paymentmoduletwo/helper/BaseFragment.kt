package com.orbits.paymentmoduletwo.helper

import android.app.Dialog
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.orbits.paymentmoduletwo.databinding.LayoutToolbarBinding
import com.orbits.paymentmoduletwo.interfaces.CommonInterfaceClickEvent
import com.orbits.paymentmoduletwo.mvvm.main.view.MainActivity

open class BaseFragment : Fragment() {
    private var progressDialog: Dialog? = null
    var onRequestPermissionsResult: OnRequestPermissionsResult? = null
    var hasInitializedRootView = false
    private var layoutToolbarBinding: LayoutToolbarBinding? = null
    private lateinit var mActivity: MainActivity

    private var rootView: View? = null

    fun getPersistentView(
        view: View?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (rootView == null) {
            // Inflate the layout for this fragment
            hasInitializedRootView = true
            rootView = view
        } else {
            // Do not inflate the layout again.
            // The returned View of onCreateView will be added into the fragment.
            // However it is not allowed to be added twice even if the parent is same.
            // So we must remove rootView from the existing parent view group
            // (it will be added back).
            if (rootView?.parent != null) {
                (rootView?.parent as? ViewGroup)?.removeView(rootView)
            }
        }
        return rootView
    }


    override fun onStop() {
        super.onStop()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity
    }

    fun setUpToolbar(
        binding: LayoutToolbarBinding,
        title: String = "",
        iconTwo: Int = 0,
        navController: NavController,
        isBackArrow: Boolean = false,
        toolbarClickListener: CommonInterfaceClickEvent? = null
    ) {
        layoutToolbarBinding = binding

        if (isBackArrow)  layoutToolbarBinding?.conIconOne?.isVisible = false

        layoutToolbarBinding?.conIconTwo?.isVisible = iconTwo != 0
        if (layoutToolbarBinding?.conIconTwo?.isVisible == true)
            layoutToolbarBinding?.ivIconTwo?.setImageResource(iconTwo)

        layoutToolbarBinding?.linBackArrow?.isVisible = isBackArrow
        layoutToolbarBinding?.linBackArrow?.setOnClickListener {
            navController.popBackStack()

        }
        layoutToolbarBinding?.conIconOne?.setOnClickListener {
            toolbarClickListener?.onToolBarListener(Constants.TOOLBAR_ICON_ONE)
        }

        layoutToolbarBinding?.conIconTwo?.setOnClickListener {
            toolbarClickListener?.onToolBarListener(Constants.TOOLBAR_ICON_TWO)
        }
        layoutToolbarBinding?.txtToolbarHeader?.text = title
    }


}
