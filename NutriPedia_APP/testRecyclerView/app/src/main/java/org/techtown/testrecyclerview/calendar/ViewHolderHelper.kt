package org.techtown.testrecyclerview.calendar

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer



open class ViewHolderHelper(override val containerView: View) : RecyclerView.ViewHolder(containerView),
    LayoutContainer {
    //val tv_date = findViewById<TextView>(R.id.tv_date)

}