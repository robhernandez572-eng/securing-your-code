package com.osint.crawler.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.osint.crawler.ui.HistoryFragment
import com.osint.crawler.ui.NewScanFragment

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> NewScanFragment()
        else -> HistoryFragment()
    }
}
