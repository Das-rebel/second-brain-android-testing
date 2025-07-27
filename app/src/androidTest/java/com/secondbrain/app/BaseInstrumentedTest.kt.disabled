package com.secondbrain.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode
import com.secondbrain.app.utils.MainDispatcherRule

/**
 * Base class for instrumented tests that need Hilt and other common test dependencies.
 */
@RunWith(RobolectricTestRunner::class)
@Config(
    application = HiltTestApplication::class,
    sdk = [33],
    instrumentedPackages = ["androidx.loader.content"]
)
@LooperMode(LooperMode.Mode.PAUSED)
abstract class BaseInstrumentedTest {
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    protected val context: Context = ApplicationProvider.getApplicationContext()
    protected val instrumentation = InstrumentationRegistry.getInstrumentation()
    
    @Before
    open fun setUp() {
        MockitoAnnotations.openMocks(this)
    }
    
    /**
     * Helper function to get a string resource for testing.
     */
    protected fun getString(resId: Int): String {
        return context.getString(resId)
    }
    
    /**
     * Helper function to get a string resource with format arguments for testing.
     */
    protected fun getString(resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}
