package net.ttk1.serverbackup;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;

public class BackupCommand implements CommandExecutor {
    private final ServerBackup plugin;
    private final File dataFolder;
    private final File serverFolder;

    BackupCommand(ServerBackup plugin, File dataFolder, File serverFolder) {
        this.plugin = plugin;
        this.dataFolder = dataFolder;
        this.serverFolder = serverFolder;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("serverbackup.backup")) {
            BackupTask backupTask = new BackupTask(dataFolder, serverFolder, commandSender);
            backupTask.runTask(this.plugin);
        } else {
            commandSender.sendMessage("コマンドを実行するための権限がありません。");
        }
        return true;
    }
}
