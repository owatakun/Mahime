package com.github.owatakun.mahime;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

public class Mahime extends JavaPlugin{

	/**
	 * 有効
	 */
	public void onEnable(){
		// kits.yml, config.ymlがなければコピー
		File kitsFile = new File(this.getDataFolder() + File.separator + "kits.yml");
		if (!kitsFile.exists()) {
			saveResource("kits.yml", false);
		}
		saveDefaultConfig();
		// Command
		getCommand("mahime").setExecutor(new MahimeCommandExecutor(this));
		// 起動メッセージ
		getLogger().info("Mahime v" + getDescription().getVersion() + " has been enabled!");
	}

	/**
	 * 無効
	 */
	public void onDisable(){
		// 終了メッセージ
		getLogger().info("Mahime v" + getDescription().getVersion() + " has been disabled!");
	}
}
