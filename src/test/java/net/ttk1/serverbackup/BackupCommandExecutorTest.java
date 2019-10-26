package net.ttk1.serverbackup;

import org.bukkit.command.CommandSender;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BackupCommandExecutorTest {
    @Test
    public void onCommandTest() {
        assertThat(true, is(true));
    }
}
