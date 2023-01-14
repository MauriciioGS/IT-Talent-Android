package mx.mauriciogs.ittalent.ui.authentication.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class VpWelcomeAdapter(frag: Fragment): FragmentStateAdapter(frag) {

    private val fragList: MutableList<Fragment> = ArrayList()

    fun addFragment(fragment: Fragment) {
        fragList.add(fragment)
    }

    override fun getItemCount(): Int = fragList.size

    override fun createFragment(position: Int): Fragment = fragList[position]

}