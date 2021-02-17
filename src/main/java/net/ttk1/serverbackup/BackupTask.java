package net.ttk1.serverbackup;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class BackupTask extends BukkitRunnable {
    private final File dataFolder;
    private final File serverFolder;
    private final S3Service s3Service;
    private final CommandSender sender;
    private static final byte[] buf = new byte[1024];

    BackupTask(File serverFolder, File dataFolder, S3Service s3Service, CommandSender sender) {
        this.serverFolder = serverFolder;
        this.dataFolder = dataFolder;
        this.s3Service = s3Service;
        this.sender = sender;
    }

    @Override
    public synchronized void run() {
        try {
            sender.sendMessage("バックアップを開始します。");
            FilenameFilter filter = (dir, name) -> !dir.getPath().endsWith(dataFolder.getPath());
            List<File> targetFiles = this.getTargetFiles(serverFolder, filter);

            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd.HH-mm-ss");
            String backupFileName = f.format(new Date()) + ".tar.gz";
            File backupFile = new File(dataFolder, backupFileName);

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
            if (s3Service != null) {
                sender.sendMessage("S3へアップロードを開始します。");
                s3Service.upload(backupFile);
                sender.sendMessage("アップロードが完了しました。");
            } else {
                sender.sendMessage("バックアップが完了しました。");
            }
        } catch (BackupException | IOException e) {
            sender.sendMessage("バックアップに失敗しました。");
            e.printStackTrace();
        }
    }

    private List<File> getTargetFiles(File entryPoint, FilenameFilter filter) throws BackupException {
        List<File> targetFiles = new ArrayList<>();
        if (entryPoint.exists()) {
            if (entryPoint.isDirectory()) {
                File[] listFiles = entryPoint.listFiles(filter);
                if (listFiles == null) {
                    throw new BackupException("バックアップ対象のリストに失敗しました。");
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
