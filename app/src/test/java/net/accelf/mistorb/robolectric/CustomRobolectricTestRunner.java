package net.accelf.mistorb.robolectric;

import net.accelf.mistorb.viewhelper.DrawerHelper;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.internal.bytecode.InstrumentationConfiguration;
import org.robolectric.internal.bytecode.ShadowMap;

public class CustomRobolectricTestRunner extends RobolectricTestRunner {

    /**
     * Creates a runner to run {@code testClass}. Use the {@link Config} annotation to configure.
     *
     * @param testClass the test class to be run
     * @throws InitializationError if junit says so
     */
    public CustomRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected ShadowMap createShadowMap() {
        return super.createShadowMap().newBuilder()
                .addShadowClasses(ShadowDrawerHelper.class)
                .build();
    }

    public InstrumentationConfiguration createClassLoaderConfig() {
        return InstrumentationConfiguration.newBuilder()
                .addInstrumentedClass(DrawerHelper.class.getName())
                .build();
    }
}
