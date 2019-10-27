package net.ttk1.serverbackup;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerBackup extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("プラグインの初期化開始");
        if (init()) {
            getLogger().info("プラグインの初期化完了");
        } else {
            getLogger().info("プラグインの初期化に失敗しました。");
        }
    }

    private boolean init() {
        try {
            saveDefaultConfig();
            PluginCommand command = getCommand("backup");
            if (command == null) {
                return false;
            }
            command.setExecutor(new BackupCommandExecutor(new BackupTaskRunner(this)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("bye!");
    }
}
