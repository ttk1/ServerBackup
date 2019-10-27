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
        this.serverFolder = plugin.getServer().getWorldContainer();
        this.dataFolder = plugin.getDataFolder();
        this.s3Service = getS3Service(plugin.getConfig());
    }

    static S3Service getS3Service(FileConfiguration config) {
        if (config.getBoolean("use_s3", false)) {
            return new S3Service(
                    config.getString("s3.region", "ap-northeast-1"),
                    config.getBoolean("s3.overwrite", true),
                    config.getString("s3.bucket_name"),
                    config.getString("s3.prefix"),
                    config.getString("s3.access_key"),
                    config.getString("s3.access_token")
            );
        } else {
            return null;
        }
    }

    BackupTask getBackupTask(CommandSender sender) {
        return new BackupTask(serverFolder, dataFolder, s3Service, sender);
    }

    void runBackupTask(CommandSender sender) {
        getBackupTask(sender).runTaskAsynchronously(plugin);
    }
}
