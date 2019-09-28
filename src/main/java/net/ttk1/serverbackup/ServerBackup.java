package net.ttk1.serverbackup;

import org.bukkit.plugin.java.JavaPlugin;

public class ServerBackup extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getLogger().info("hello!");
    }

    @Override
    public void onDisable() {
        this.getLogger().info("bye!");
    }
}
