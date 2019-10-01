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
            if (this.s3Service != null) {
                this.commandSender.sendMessage("S3へアップロードを開始します。");
                this.s3Service.upload(backupFile);
                this.commandSender.sendMessage("アップロードが完了しました。");
            } else {
                this.commandSender.sendMessage("バックアップが完了しました。");
            }
        } catch (BackupException | IOException e) {
            this.commandSender.sendMessage("バックアップに失敗しました。");
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
