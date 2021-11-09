package com.example.myapplication

import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Tests {
    /**
     * <Кнопка> to <Следующий фрагмент> to <Следующий размер backStack>
     */
    private val navListOnlyButtons = listOf(
        R.id.bnToSecond to R.id.fragment2 to 2,
        R.id.bnToThird to R.id.fragment3 to 3,
        R.id.bnToSecond to R.id.fragment2 to 2,
        R.id.bnToFirst to R.id.fragment1 to 1,
        R.id.bnToSecond to R.id.fragment2 to 2,
        R.id.bnToThird to R.id.fragment3 to 3,
        R.id.bnToFirst to R.id.fragment1 to 1
    )

    /**
     * <Кнопка / null - шаг назад> to <Следующий фрагмент> to <Следующий размер backStack>
     */
    private val navListNotOnlyButtons = listOf(
        R.id.bnToSecond to R.id.fragment2 to 2,
        null to R.id.fragment1 to 1,
        R.id.bnToSecond to R.id.fragment2 to 2,
        R.id.bnToThird to R.id.fragment3 to 3,
        null to R.id.fragment2 to 2,
        null to R.id.fragment1 to 1
    )

    /**
     * <Кнопка> to
     * <Следующий фрагмент> to
     * <Размер backStack после того, как мы перешли из <Следующий фрагмент> в About и обратно>
     */
    private val navListAbout = listOf(
        R.id.bnToSecond to R.id.fragment2 to 2,
        R.id.bnToThird to R.id.fragment3 to 3
    )

    @Test
    fun testNavFragmentsWithButtons() = testNavFragments(navListOnlyButtons)

    @Test
    fun testNavFragmentsWithPressBack() = testNavFragments(navListNotOnlyButtons) { pressBack() }

    @Test
    fun testNavFragmentsWithNavigateUp() = testNavFragments(navListNotOnlyButtons) { navigateUp() }

    @Test
    fun testNavToAndFromAboutWithPressBack() = testNavToAndFromAbout(navListAbout) { pressBack() }

    @Test
    fun testNavToAndFromAboutWithNavigateUp() = testNavToAndFromAbout(navListAbout) { navigateUp() }

    private fun testNavFragments(
        buttonsToFragmentsToBackStackSize: List<Pair<Pair<Int?, Int>, Int>>,
        back: () -> Unit = { }
    ) = testNav(buttonsToFragmentsToBackStackSize) { bn, frag ->
        bn?.let { onView(withId(bn)).perform(click()) } ?: run { back() }
        onView(withId(frag)).check(matches(isDisplayed()))
    }

    private fun testNavToAndFromAbout(
        buttonsToFragmentsToBackStackSize: List<Pair<Pair<Int?, Int>, Int>>,
        back: () -> Unit
    ) = testNav(buttonsToFragmentsToBackStackSize) { bn, frag ->
        bn?.let { onView(withId(bn)).perform(click()) }
        onView(withId(frag)).check(matches(isDisplayed()))
        openAbout()
        back()
    }

    private fun testNav(
        buttonsToFragmentsToBackStackSize: List<Pair<Pair<Int?, Int>, Int>>,
        nextNavAction: (bn: Int?, frag: Int) -> Unit
    ) {
        for (i in 1..buttonsToFragmentsToBackStackSize.size) {
            val scenario = launchActivity<MainActivity>()
            val list = buttonsToFragmentsToBackStackSize.subList(0, i)
            var frag = R.id.fragment1

            list.forEach { item ->

                // Проверка поворота экрана
                scenario.recreate()
                onView(withId(frag)).check(matches(isDisplayed()))

                val bn = item.first.first
                frag = item.first.second

                // Навигация и проверка корректности навигации
                nextNavAction(bn, frag)
            }

            // Проверка содержимого backStack
            val backStackSize = list.last().second
            repeat(backStackSize) { pressBackUnconditionally() }
            assertEquals(scenario.state, DESTROYED)
        }
    }

    private fun navigateUp() =
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
}