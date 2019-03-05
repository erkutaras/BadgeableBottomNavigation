package com.erkutaras.badgeablebottomnavigation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Created by erkutaras on 30.12.2018.
 */
class BadgeableBottomNavigation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    BottomNavigationView(context, attrs, defStyleAttr),
    BottomNavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemReselectedListener {

    private var badgeType: BadgeType = BadgeType.NONE
    private var tabList: MutableList<Int> = mutableListOf()
    private var badgeList: MutableList<View> = mutableListOf()

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.BadgeableBottomNavigation,
            0, 0
        ).apply {

            try {
                badgeType = BadgeType.values()[getInteger(
                    R.styleable.BadgeableBottomNavigation_badgeType,
                    BadgeType.NONE.ordinal
                )]
            } finally {
                recycle()
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val bottomNavigationView = getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until bottomNavigationView.childCount) {
            val tabItem = bottomNavigationView.getChildAt(i) as BottomNavigationItemView
            tabList.add(tabItem.id)

            val badgeView: View = when (badgeType) {
                BadgeType.DOT -> provideDotView(bottomNavigationView)
                BadgeType.NUMBER -> provideNumberView(bottomNavigationView)
                    .apply { findViewById<TextView>(R.id.textViewBadgeableNumber).text = i.toString() }
                BadgeType.CIRCLE -> provideCircleView(bottomNavigationView)
                BadgeType.NONE -> TODO()
            }
            badgeList.add(badgeView)
            tabItem.addView(badgeView)
        }

        badgeList[0].visibility = View.GONE
        setOnNavigationItemSelectedListener(this)
        setOnNavigationItemReselectedListener(this)
    }

    private fun provideDotView(bottomNavigationView: BottomNavigationMenuView): View =
        LayoutInflater.from(context).inflate(R.layout.layout_badgeable_dot, bottomNavigationView, false)

    private fun provideNumberView(bottomNavigationView: BottomNavigationMenuView): View =
        LayoutInflater.from(context).inflate(R.layout.layout_badgeable_number, bottomNavigationView, false)

    private fun provideCircleView(bottomNavigationView: BottomNavigationMenuView): View =
        LayoutInflater.from(context).inflate(R.layout.layout_badgeable_circle, bottomNavigationView, false)

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        updateBadgeVisibility(menuItem)
        return true
    }

    override fun onNavigationItemReselected(menuItem: MenuItem) = updateBadgeVisibility(menuItem)

    private fun updateBadgeVisibility(menuItem: MenuItem) {
        badgeList.forEach { view -> view.visibility = View.VISIBLE }
        badgeList[tabList.indexOf(menuItem.itemId)].visibility = View.GONE
    }

    enum class BadgeType {
        DOT, NUMBER, CIRCLE, NONE
    }
}