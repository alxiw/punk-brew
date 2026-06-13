package io.github.alxiw.simplesearchview.listeners

import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

internal abstract class SimpleOnTabSelectedListener : OnTabSelectedListener {
    override fun onTabSelected(tab: TabLayout.Tab) = Unit
    override fun onTabUnselected(tab: TabLayout.Tab) = Unit
    override fun onTabReselected(tab: TabLayout.Tab) = Unit
}
