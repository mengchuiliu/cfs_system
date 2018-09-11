package com.xxjr.cfs_system.ViewsHolder

import android.graphics.Rect
import android.os.Build
import android.widget.PopupWindow
import android.view.View

class Popup7(mMenuView: View, matchParent: Int, matchParent1: Int) : PopupWindow(mMenuView, matchParent, matchParent1) {

    override fun showAsDropDown(anchor: View) {
        if (Build.VERSION.SDK_INT == 24) {
            val rect = Rect()
            anchor.getGlobalVisibleRect(rect)
            val h = anchor.resources.displayMetrics.heightPixels - rect.bottom
            height = h
        }
        super.showAsDropDown(anchor)
    }
}