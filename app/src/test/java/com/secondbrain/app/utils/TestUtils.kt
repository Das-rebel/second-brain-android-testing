package com.secondbrain.app.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * A JUnit Test Rule that swaps the Main dispatcher for testing coroutines.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}

/**
 * Helper function to test coroutines.
 * Runs the test block in a test coroutine scope.
 */
@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> runTest(testBlock: suspend () -> T) {
    kotlinx.coroutines.test.runTest {
        testBlock()
    }
}

/**
 * Helper function to test LiveData.
 * Collects the values emitted by the LiveData and returns them as a list.
 */
/*
suspend fun <T> LiveData<T>.collectValues(
    count: Int = 1,
    timeout: Long = 1000L
): List<T> = coroutineScope {
    val result = mutableListOf<T>()
    val job = launch(Dispatchers.Main) {
        this@collectValues.observeForever { value ->
            if (value != null) {
                result.add(value)
                if (result.size >= count) {
                    cancel()
                }
            }
        }
    }
    
    withTimeout(timeout) {
        job.join()
    }
    
    result
}
*/

/**
 * Helper function to create a test Hilt component for testing.
 */
/*
@OptIn(ExperimentalCoroutinesApi::class)
fun hiltTestRule() = HiltAndroidRule(this).apply {
    inject()
    moveToState(Lifecycle.State.RESUMED)
}
*/

/**
 * Helper function to create a test Compose rule with Hilt.
 */
/*
@OptIn(ExperimentalMaterial3Api::class)
fun createComposeRuleWithHilt(
    hiltTestRule: HiltAndroidRule
): ComposeTestRule {
    return createAndroidComposeRule<ComponentActivity>().apply {
        hiltTestRule.inject()
    }
}
*/
