package com.github.owatakun.mahime;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class RandomChest {

	private boolean rcEditMode;
	private Plugin plugin;
	private YamlConfiguration rcConf;
	private ArrayList<Point> rcPointList;
	private World enableWorld;
	private File rcFile;
	protected static RandomChest instance;

	public RandomChest(Plugin plugin, FileConfiguration config, YamlConfiguration rcConf) {
		instance = this;
		rcEditMode = false;
		this.plugin = plugin;
		this.rcFile = new File(plugin.getDataFolder(), "rc.yml");
		this.rcConf = rcConf;
		this.rcPointList = new ArrayList<Point>();
	}
	/**
	 * rcEditModeを有効にして初期処理を行う
	 */
	protected void enableEditMode(World world, CommandSender sender) {
		rcEditMode = true;
		enableWorld = world;
		// rc.ymlにデータが存在すればリストに読み込む
		if (rcConf.contains("rcPointList")) {
			List<String> tempList = rcConf.getStringList("rcPointList");
			rcPointList.clear();
			for (String temp: tempList) {
				Point pt = Point.deserialize(temp);
				if (pt != null) {
					rcPointList.add(pt);
				}
			}
		}
		// リストのデータに従って指示ブロックを設置する
		ArrayList<Point> errList = new ArrayList<Point>();
		for (int i = 0; i < rcPointList.size();) {
			Point pt = rcPointList.get(i);
			Block block = world.getBlockAt(pt.getX(), pt.getY(), pt.getZ());
			if (block.getTypeId() == 0) {
				block.setTypeId(41);
				i++;
			} else {
				sender.sendMessage(block.getType().toString());
				errList.add(pt);
				rcPointList.remove(i);
			}
		}
		sender.sendMessage("EditModeを開始しました");
		if (errList.size() != 0) {
			sender.sendMessage("Error: 以下の箇所に他のブロックが存在したため、これらの指定を削除しました");
			for (Point errPt: errList) {
				sender.sendMessage(errPt.serialize());
			}
		}
	}

	/**
	 * EditModeを終了して保存処理を行う
	 */
	protected void disableEditMode(CommandSender sender) {
		if (rcEditMode) {
			ArrayList<String> saveList = new ArrayList<String>();
			for (Point pt: rcPointList) {
				saveList.add(pt.serialize());
				Block block = enableWorld.getBlockAt(pt.getX(), pt.getY(), pt.getZ());
				block.setTypeId(0);
			}
			rcConf.set("rcPointList", saveList);
			try {
				rcConf.save(rcFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			rcEditMode = false;
			sender.sendMessage("EditModeを終了しました");
		}
	}

	/**
	 * rcPointListに追加
	 * @return 追加結果(現在は全てtrue)
	 */
	protected boolean addRCPoint(Player player, Location loc) {
		int x, y, z;
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
		Point pt = new Point(x, y, z);
		rcPointList.add(pt);
		plugin.getServer().broadcast(Util.repSec("&7") + "Add: " + x + "," + y + "," + z , "mahime.admin");
		return true;
	}
	/**
	 * rcPointListから消去
	 * @return 消去結果
	 */
	protected boolean removeRCPoint(Player player, Location loc) {
		boolean deleted = false;
		int x, y, z;
		x = loc.getBlockX();
		y = loc.getBlockY();
		z = loc.getBlockZ();
		for (int i = 0; i < rcPointList.size();) {
			Point pt = rcPointList.get(i);
			if (pt.getX() == x && pt.getY() == y && pt.getZ() == z) {
				rcPointList.remove(i);
				deleted = true;
				break;
			}
			i++;
		}
		if (deleted) {
			plugin.getServer().broadcast(Util.repSec("&7") + "Remove: " + x + "," + y + "," + z , "mahime.admin");
			return true;
		} else {
			player.sendMessage("Error: リストに存在しません");
			return false;
		}
	}

	/**
	 * rcPointListのコピー取得
	 * @return rcPointListのコピー
	 */
	protected ArrayList<Point> getrcPointListCopy() {
		return new ArrayList<Point>(rcPointList);
	}

	/**
	 * rcEditModeのgetter
	 * @return rcEditMode
	 */
	public boolean isRcEditMode() {
		return rcEditMode;
	}
}
