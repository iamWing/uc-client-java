package uk.co.alphaowl.uc;

import org.junit.After;
import org.junit.Before;

public class UCClientTest {

    private UCClient instance;

    @Before
    protected void setUp() {
        instance = UCClient.init();
    }

    @After
    protected void tearDown() {
        instance.destroy();
    }

}