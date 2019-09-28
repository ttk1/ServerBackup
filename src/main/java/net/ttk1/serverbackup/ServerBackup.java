package net.ttk1.serverbackup;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServerBackup extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getLogger().info("プラグインの初期化開始!");
        File worldContainer = this.getServer().getWorldContainer();

        File dataFolder = this.getDataFolder();

        if (!dataFolder.exists()) {
            this.getLogger().info("データフォルダを作成します: " + dataFolder.getPath());
            if (!dataFolder.mkdir()) {
                this.getLogger().info("データフォルダの作成に失敗しました...orz");
                return; // 機能停止
            } else {
                this.getLogger().info("データフォルダが作成されました.");
            }
        }
        this.getLogger().info("プラグインの初期化完了!");

        // バックアップ対象の抽出テスト用
        List<File> targetFiles = this.getTargetFiles(worldContainer);
        for (File file: targetFiles) {
            this.getLogger().info((file.getAbsolutePath()));
        }
    }

    private List<File> getTargetFiles(File entryPoint) {
        List<File> targetFiles = new ArrayList<>();
        // 何らかの処理
        if (entryPoint.exists()) {
            if (entryPoint.isDirectory()) {
                // フォルダの場合
                File[] listFiles = entryPoint.listFiles();
                for (File file : listFiles) { // TODO: nullチェック
                    targetFiles.addAll(getTargetFiles(file));
                }
            } else {
                // ファイルの場合
                targetFiles.add(entryPoint);
            }
        }
        return targetFiles;
    }

    @Override
    public void onDisable() {
        this.getLogger().info("bye!");
    }
}
