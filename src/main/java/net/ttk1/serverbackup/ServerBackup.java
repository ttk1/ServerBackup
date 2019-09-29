package net.ttk1.serverbackup;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

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

        //////// ここから下は後で消す ////////

        File piyo = new File(dataFolder,"piyo.txt");
        if (!piyo.exists()) {
            try {
                if (!piyo.createNewFile()) {
                    this.getLogger().info("テスト用ファイルの作成失敗");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // バックアップ対象の抽出テスト用
        // とりあえず、データフォルダを除外する
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !dir.getPath().endsWith(dataFolder.getPath());
            }
        };

        List<File> targetFiles = this.getTargetFiles(worldContainer, filter);

        // とりあえずリストの表示
        for (File file: targetFiles) {
            this.getLogger().info((file.getPath()));
        }

        // アーカイブ
        try {
            GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(new File(dataFolder, "archive.tar.gz")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<File> getTargetFiles(File entryPoint, FilenameFilter filter) {
        List<File> targetFiles = new ArrayList<>();
        // 何らかの処理
        if (entryPoint.exists()) {
            if (entryPoint.isDirectory()) {
                // フォルダの場合
                File[] listFiles = entryPoint.listFiles(filter);
                for (File file : listFiles) { // TODO: nullチェック
                    targetFiles.addAll(getTargetFiles(file, filter));
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
