package com.jimmy.mlkit.ui.views

import android.net.Uri
import android.support.annotation.LayoutRes
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.view.View
import kotlinx.android.synthetic.main.google_lens_fragment.*


abstract class BaseFragment : Fragment() {

    lateinit var sheetBehavior: BottomSheetBehavior<*>

    fun setupBottomSheet(@LayoutRes id : Int){
        //Using a ViewStub since changing the layout of an <include> tag dynamically wasn't possible
        stubView.layoutResource = id
        val inflatedView = stubView.inflate()
        //Set layout parameters for the inflated bottomsheet
        val lparam = inflatedView.layoutParams as CoordinatorLayout.LayoutParams
        lparam.behavior = BottomSheetBehavior<View>()
        inflatedView.layoutParams = lparam
        sheetBehavior = BottomSheetBehavior.from(inflatedView)
        sheetBehavior.peekHeight = 120

        //Hide the fab as bottomSheet is expanded
        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                fab_take_photo.animate().scaleX(1 - slideOffset)
                    .scaleY(1 - slideOffset)
                    .setDuration(0).start()
            }
        })
    }
}
