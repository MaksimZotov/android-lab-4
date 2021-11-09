package com.example.myapplication

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class Tests {

    @get:Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testNavFragmentsJustByButtons() = testNavFragments(listOf(
        R.id.bnToSecond to R.id.fragment2 to listOf(
            R.id.fragment1,
            R.id.fragment2
        ),
        R.id.bnToThird to R.id.fragment3 to listOf(
            R.id.fragment1,
            R.id.fragment2,
            R.id.fragment3
        ),
        R.id.bnToSecond to R.id.fragment2 to listOf(
            R.id.fragment1,
            R.id.fragment2
        ),
        R.id.bnToFirst to R.id.fragment1 to listOf(
            R.id.fragment1,
        ),
        R.id.bnToSecond to R.id.fragment2 to listOf(
            R.id.fragment1,
            R.id.fragment2
        ),
        R.id.bnToThird to R.id.fragment3 to listOf(
            R.id.fragment1,
            R.id.fragment2,
            R.id.fragment3
        ),
        R.id.bnToFirst to R.id.fragment1 to listOf(
            R.id.fragment1,
        )
    ))

    @Test
    fun testNavFragmentsWithPressBack() = testNavFragmentsWithBackNavigationNotByButtons {
        pressBack()
    }

    @Test
    fun testNavFragmentsWithNavigateUp() = testNavFragmentsWithBackNavigationNotByButtons {
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
    }

    @Test
    fun testNavToAndFromAboutWithPressBack() = testNavToAndFromAbout {
        pressBack()
    }

    @Test
    fun testNavToAndFromAboutWithNavigateUp() = testNavToAndFromAbout {
        onView(withContentDescription(R.string.nav_app_bar_navigate_up_description))
            .perform(click())
    }

    @Test
    fun testConfigChanges() {
        onView(withId(R.id.bnToSecond))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.title_to_second)))

        rule.scenario.onActivity { activity ->
            activity.recreate()
        }

        onView(withId(R.id.bnToSecond))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.title_to_second)))

        onView(withId(R.id.bnToSecond))
            .perform(click())

        onView(withId(R.id.bnToFirst))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.title_to_first)))
        onView(withId(R.id.bnToThird))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.title_to_third)))

        rule.scenario.onActivity { activity ->
            activity.recreate()
        }

        onView(withId(R.id.bnToFirst))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.title_to_first)))
        onView(withId(R.id.bnToThird))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.title_to_third)))

        onView(withId(R.id.bnToThird))
            .perform(click())

        onView(withId(R.id.bnToFirst))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.title_to_first)))
        onView(withId(R.id.bnToSecond))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.title_to_second)))

        rule.scenario.onActivity { activity ->
            activity.recreate()
        }

        onView(withId(R.id.bnToFirst))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.title_to_first)))
        onView(withId(R.id.bnToSecond))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.title_to_second)))
    }

    private fun testNavFragmentsWithBackNavigationNotByButtons(back: () -> Unit) =
        testNavFragments(
            listOf(
                R.id.bnToSecond to R.id.fragment2 to listOf(
                    R.id.fragment1,
                    R.id.fragment2
                ),
                null to R.id.fragment1 to listOf(
                    R.id.fragment1,
                ),
                R.id.bnToSecond to R.id.fragment2 to listOf(
                    R.id.fragment1,
                    R.id.fragment2
                ),
                R.id.bnToThird to R.id.fragment3 to listOf(
                    R.id.fragment1,
                    R.id.fragment2,
                    R.id.fragment3
                ),
                null to R.id.fragment2 to listOf(
                    R.id.fragment1,
                    R.id.fragment2
                ),
                null to R.id.fragment1 to listOf(
                    R.id.fragment1
                )
            ),
            back
        )

    private fun testNavToAndFromAbout(back: () -> Unit) {
        val fragments = mapOf(
            R.id.fragment1 to R.id.bnToSecond,
            R.id.fragment2 to R.id.bnToThird,
            R.id.fragment3 to null
        )

        fragments.forEach { (frag, bn) ->
            openAbout()
            onView(withId(R.id.activity_about))
                .check(matches(isDisplayed()))
            back()
            onView(withId(frag))
                .check(matches(isDisplayed()))
            bn?.let { bnNotNull ->
                onView(withId(bnNotNull))
                    .perform(click())
            }
        }
    }

    private fun testNavFragments(
        buttonsToFragmentsToExistentDestinations: List<Pair<Pair<Int?, Int>, List<Int>>>,
        back: () -> Unit = { }
    ) {
        val destinations = listOf(
            R.id.fragment1,
            R.id.fragment2,
            R.id.fragment3,
        )

        buttonsToFragmentsToExistentDestinations.forEach { item ->
            val bn = item.first.first
            val frag = item.first.second

            bn?.let { bnNotNull ->
                onView(withId(bnNotNull))
                    .perform(click())
            } ?: run {
                back()
            }

            val existentDestinations = item.second
            val notExistentDestinations = destinations - existentDestinations

            notExistentDestinations.forEach { destination ->
                onView(withId(destination))
                    .check(doesNotExist())
            }

            onView(withId(frag))
                .check(matches(isDisplayed()))
        }
    }
}