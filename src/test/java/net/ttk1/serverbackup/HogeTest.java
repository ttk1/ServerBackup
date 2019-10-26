package net.ttk1.serverbackup;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

public class HogeTest {
    @Test
    public void hogeTest() {
        assertThat(1, is(1));
    }

    @Test
    public void piyoTest() {
        assertThat(1, not(0));
    }

    @Test
    public void fugaTest() {
        assertThat(1, is(0));
    }
}
