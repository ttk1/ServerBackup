package net.ttk1.serverbackup;

import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class ServerBackup extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getLogger().info("プラグインの初期化開始");

        File serverFolder = this.getServer().getWorldContainer();
        File dataFolder = this.getDataFolder();

        // データフォルダ作成
        if (!dataFolder.exists()) {
            this.getLogger().info("データフォルダを作成します: " + dataFolder.getPath());
            if (!dataFolder.mkdir()) {
                this.getLogger().info("データフォルダの作成に失敗しました。");
                return; // 機能停止
            } else {
                this.getLogger().info("データフォルダが作成されました。");
            }
        }

        // 設定ファイル読み込み
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();

        // コマンド登録
        PluginCommand command = this.getCommand("backup");
        if (command == null) {
            this.getLogger().info("コマンドの作成に失敗しました。");
            return; // 機能停止
        }
        command.setExecutor(new BackupCommand(this, dataFolder, serverFolder));

        this.getLogger().info("プラグインの初期化完了");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("bye!");
    }
}
