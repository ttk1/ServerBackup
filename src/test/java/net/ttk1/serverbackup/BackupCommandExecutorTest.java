package net.ttk1.serverbackup;

import org.bukkit.command.CommandSender;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class BackupCommandExecutorTest {
    @Test
    public void onCommandTest() {
        BackupTaskRunner backupTaskRunner = mock(BackupTaskRunner.class);
        CommandSender sender = mock(CommandSender.class);
        when(sender.hasPermission("serverbackup.backup")).thenReturn(true);
        BackupCommandExecutor executor = new BackupCommandExecutor(backupTaskRunner);
        executor.onCommand(sender, null, null, null);
        verify(backupTaskRunner).runBackupTask(sender);
    }

    @Test
    public void onCommandWithoutPermissionTest() {
        BackupTaskRunner backupTaskRunner = mock(BackupTaskRunner.class);
        CommandSender sender = mock(CommandSender.class);
        when(sender.hasPermission("serverbackup.backup")).thenReturn(false);
        BackupCommandExecutor executor = new BackupCommandExecutor(backupTaskRunner);
        executor.onCommand(sender, null, null, null);
        verify(backupTaskRunner, never()).runBackupTask(any());
        verify(sender).sendMessage("コマンドを実行するための権限がありません。");
    }
}
