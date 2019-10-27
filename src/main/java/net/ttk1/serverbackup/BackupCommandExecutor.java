package net.ttk1.serverbackup;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BackupCommandExecutor implements CommandExecutor {
    private final BackupTaskRunner backupTaskRunner;

    BackupCommandExecutor(BackupTaskRunner backupTaskRunner) {
        this.backupTaskRunner = backupTaskRunner;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender.hasPermission("serverbackup.backup")) {
            backupTaskRunner.runBackupTask(sender);
        } else {
            sender.sendMessage("コマンドを実行するための権限がありません。");
        }
        return true;
    }
}
