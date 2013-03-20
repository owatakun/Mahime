package com.github.owatakun.mahime;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class Mahime extends JavaPlugin{

	private FileConfiguration config;

	/**
	 * 有効
	 */
	public void onEnable(){
		config = getConfig();
		// kits.yml, config.yml, rc.ymlがなければコピーor作成
		File kitsFile = new File(this.getDataFolder() + File.separator + "kits.yml");
		if (!kitsFile.exists()) {
			saveResource("kits.yml", false);
		}
		File rcFile = new File(getDataFolder(), "rc.yml");
		if (!rcFile.exists()) {
			saveResource("rc.yml", false);
		}
		saveDefaultConfig();
		// Command
		getCommand("mahime").setExecutor(new MahimeCommandExecutor(config, this));
		// 起動メッセージ
		getLogger().info("Mahime v" + getDescription().getVersion() + " has been enabled!");
		// イベント購読
		getServer().getPluginManager().registerEvents(new BlockPlaceListener(), this);
		getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
	}

	/**
	 * 無効
	 */
	public void onDisable(){
		// EditModeを終了しておく
		if (RandomChest.isRcEditMode()) {
			getServer().broadcast(Util.repSec("&7&o") + "[Mahime] Disable EditMode...", "mahime.admin");
			RandomChest.instance.disableEditMode((getServer().getConsoleSender()));
		}
		// 終了メッセージ
		getLogger().info("Mahime v" + getDescription().getVersion() + " has been disabled!");
	}
}
