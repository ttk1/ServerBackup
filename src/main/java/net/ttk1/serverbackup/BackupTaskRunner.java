package net.ttk1.serverbackup;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class BackupTaskRunner {
    private final ServerBackup plugin;
    private final File serverFolder;
    private final File dataFolder;
    private final S3Service s3Service;

    BackupTaskRunner(ServerBackup plugin) {
        this.plugin = plugin;
        this.serverFolder = this.plugin.getServer().getWorldContainer();
        this.dataFolder = this.plugin.getDataFolder();
        FileConfiguration config = this.plugin.getConfig();
        if (config.getBoolean("use_s3", false)) {
            this.s3Service = new S3Service(
                    config.getString("s3.region", "ap-northeast-1"),
                    config.getBoolean("s3.overwrite", true),
                    config.getString("s3.bucket_name"),
                    config.getString("s3.prefix"),
                    config.getString("s3.access_key"),
                    config.getString("s3.access_token")
            );
        } else {
            this.s3Service = null;
        }
    }

    void runBackupTask(CommandSender sender) {
        BackupTask backupTask = new BackupTask(
                this.serverFolder,
                this.dataFolder,
                this.s3Service,
                sender
        );
        backupTask.runTaskAsynchronously(this.plugin);
    }
}
