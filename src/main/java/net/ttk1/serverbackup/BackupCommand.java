package net.ttk1.serverbackup;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;

public class BackupCommand implements CommandExecutor {
    private final ServerBackup plugin;
    private final S3Service s3Service;

    BackupCommand(ServerBackup plugin, S3Service s3Service) {
        this.plugin = plugin;
        this.s3Service = s3Service;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("serverbackup.backup")) {
            File severFolder = plugin.getServer().getWorldContainer();
            File dataFolder = plugin.getDataFolder();
            BackupTask backupTask = new BackupTask(severFolder, dataFolder, s3Service, commandSender);
            backupTask.runTaskAsynchronously(this.plugin);
        } else {
            commandSender.sendMessage("コマンドを実行するための権限がありません。");
        }
        return true;
    }
}
