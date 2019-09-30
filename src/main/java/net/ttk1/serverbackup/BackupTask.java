package net.ttk1.serverbackup;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class BackupTask extends BukkitRunnable {
    private final File dataFolder;
    private final File serverFolder;
    private final S3Service s3Service;
    private final CommandSender commandSender;
    private final byte[] buf = new byte[1024];

    BackupTask(File serverFolder, File dataFolder, S3Service s3Service, CommandSender commandSender) {
        this.serverFolder = serverFolder;
        this.dataFolder = dataFolder;
        this.s3Service = s3Service;
        this.commandSender = commandSender;
    }

    @Override
    public void run() {
        try {
            this.commandSender.sendMessage("バックアップを開始します。");
            FilenameFilter filter = (dir, name) -> !dir.getPath().endsWith(dataFolder.getPath());
            List<File> targetFiles = this.getTargetFiles(serverFolder, filter);

            // TODO: バックアップファイル名
            File backupFile = new File(dataFolder, "archive.tar.gz");
            GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(backupFile));
            TarArchiveOutputStream tos = new TarArchiveOutputStream(gos);
            for (File targetFile : targetFiles) {
                ArchiveEntry archiveEntry = tos.createArchiveEntry(targetFile, targetFile.getPath());
                tos.putArchiveEntry(archiveEntry);
                FileInputStream fis = new FileInputStream(targetFile);
                int len;
                while ((len = fis.read(buf)) >= 0) {
                    tos.write(buf, 0, len);
                }
                tos.closeArchiveEntry();
            }
            tos.close();
            if (this.s3Service != null) {
                this.commandSender.sendMessage("S3へアップロードを開始します。");
                this.s3Service.upload(backupFile);
                this.commandSender.sendMessage("アップロードが完了しました。");
            } else {
                this.commandSender.sendMessage("バックアップが完了しました。");
            }
        } catch (Exception e) {
            this.commandSender.sendMessage("バックアップに失敗しました。");
            e.printStackTrace();
        }
    }

    private List<File> getTargetFiles(File entryPoint, FilenameFilter filter) throws Exception {
        List<File> targetFiles = new ArrayList<>();
        if (entryPoint.exists()) {
            if (entryPoint.isDirectory()) {
                File[] listFiles = entryPoint.listFiles(filter);
                if (listFiles == null) {
                    // TODO: 後でキャッチ用の例外作る
                    throw new Exception("エラー");
                }
                for (File file : listFiles) {
                    targetFiles.addAll(getTargetFiles(file, filter));
                }
            } else {
                targetFiles.add(entryPoint);
            }
        }
        return targetFiles;
    }
}
