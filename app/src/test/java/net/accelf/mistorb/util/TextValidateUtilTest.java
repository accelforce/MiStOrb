package net.accelf.mistorb.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TextValidateUtilTest {

    @Test
    public void test_isHostName(){
        assertFalse(TextValidateUtil.isHostName(null));
        assertFalse(TextValidateUtil.isHostName(""));
        assertFalse(TextValidateUtil.isHostName("not a domain"));
        assertTrue(TextValidateUtil.isHostName("example.com"));
    }

}