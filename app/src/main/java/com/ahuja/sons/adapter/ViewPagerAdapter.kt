package com.ahuja.sons.adapter

import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val fragments: MutableList<Fragment> = ArrayList()
    private val fragmentTitle: MutableList<String> = ArrayList()

    fun add(fragment: Fragment, title: String) {
        fragments.add(fragment)
        fragmentTitle.add(title)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    @Nullable
    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitle[position]
    }



}