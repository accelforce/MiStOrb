package net.accelf.mistorb.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class StatsTest {

    @Test
    public void test_stats() {
        Stats stats = new Stats();

        assertNotNull(stats.sidekiq);
        assertEquals(0, stats.sidekiq.processed);
        //TODO: add tests for redis value
    }

}