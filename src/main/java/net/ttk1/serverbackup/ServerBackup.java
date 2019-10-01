package net.ttk1.serverbackup;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerBackup extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getLogger().info("プラグインの初期化開始");
        if (this.init()) {
            this.getLogger().info("プラグインの初期化完了");
        } else {
            this.getLogger().info("プラグインの初期化に失敗しました。");
        }
    }

    private boolean init() {
        try {
            this.saveDefaultConfig();
            FileConfiguration config = this.getConfig();
            BackupCommand backupCommand;
            if (config.getBoolean("use_s3", false)) {
                backupCommand = new BackupCommand(this, new S3Service(
                        config.getString("s3.region", "ap-northeast-1"),
                        config.getBoolean("s3.overwrite", false),
                        config.getString("s3.bucket_name"),
                        config.getString("s3.prefix"),
                        config.getString("s3.access_key"),
                        config.getString("s3.access_token")
                ));
            } else {
                backupCommand = new BackupCommand(this, null);
            }
            PluginCommand command = this.getCommand("backup");
            if (command == null) {
                return false;
            }
            command.setExecutor(backupCommand);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().info("bye!");
    }
}
