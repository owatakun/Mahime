package com.github.owatakun.mahime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class Kits {

	private FileConfiguration kitsConf;

	public Kits(Plugin plugin, YamlConfiguration kitsConf) {
		this.kitsConf = kitsConf;
	}

	/**
	 * 装備配布
	 * @param player 対象Player名
	 * @param kitName 対象Kit名
	 * @return 正常に配布できたか(現在はすべてtrue)
	 */
	public boolean giveKit(Player player, String kitName) {
		PlayerInventory getInv = player.getInventory();
		// インベントリ初期化
		getInv.setHelmet(null);
		getInv.setChestplate(null);
		getInv.setLeggings(null);
		getInv.setBoots(null);
		getInv.clear();
		// 装備変換
		ItemStack helmet = getItem(kitsConf.getString("Kits." + kitName + ".armor.helmet"));
		ItemStack chestplate = getItem(kitsConf.getString("Kits." + kitName + ".armor.chestplate"));
		ItemStack leggins = getItem(kitsConf.getString("Kits." + kitName + ".armor.leggins"));
		ItemStack boots = getItem(kitsConf.getString("Kits." + kitName + ".armor.boots"));
		// 装備配布
		getInv.setHelmet(helmet);
		getInv.setChestplate(chestplate);
		getInv.setLeggings(leggins);
		getInv.setBoots(boots);

		// アイテム配布
		for (int slot = 0; slot <= 35; slot++) {
			ItemStack item = getItem(kitsConf.getString("Kits." + kitName + ".items." + slot));
			getInv.setItem(slot, item);
		}
		// メッセージ送信
		if (kitsConf.contains("Kits." + kitName +".message")) {
			String message = kitsConf.getString("Kits." + kitName + ".message");
			if (message != null && !message.equalsIgnoreCase("null")) {
				player.sendMessage(Util.repSec(message));
			}
		}
		return true;
	}

	/**
	 * アイテム指定の文字列をItemStackに変換する
	 * @param source アイテム指定文字列
	 * @return ItemStackに変換された物、失敗時はnull
	 */
	private ItemStack getItem(String source) {
		/**
		 * アイテム指定形式が正しいかどうかの正規表現チェック
		 * グループ(未該当時はnull): ItemID, amount, DamageValue, EnchantData, ItemName
		 */
		if (source == null) {
			return null;
		}
		Pattern pattern = Pattern.compile("([0-9]+)(?::([0-9]+))?(?:@([0-9]+))?(?:\\^([0-9]+:[0-9]+(?:\\ [0-9]+:[0-9])*))?(?:\\$(.+))?");
		Matcher matcher = pattern.matcher(source);
		if (matcher.matches()) {
			// type, amount, damageの読み出し
			if (matcher.group(1) != null) {
				int type = 0, amount = 1;
				short damage = 0;
				type = Integer.parseInt(matcher.group(1));
				if (matcher.group(2) != null) {
					amount = Integer.parseInt(matcher.group(2));
				}
				if (matcher.group(3) != null) {
					damage = Short.parseShort(matcher.group(3));
				}
				// itemが0であればnullを返す
				if (type == 0) {
					return null;
				}
				// 正しいIDか確認し、存在しなければnullを返す
				Material meterial = Material.getMaterial(type);
				if (meterial == null) {
					return null;
				}
				// ItemStack生成
				ItemStack item;
				if (damage != 0) {
					item = new ItemStack(type, amount, damage);
				} else {
					item = new ItemStack(type, amount);
				}
				// エンチャント
				if (matcher.group(4) != null) {
					String input = matcher.group(4);
					for (String enchStr: input.split(" ")) {
						Pattern enchpattern = Pattern.compile("([0-9]+):([0-9]+)");
						Matcher enchmatcher = enchpattern.matcher(enchStr);
						if (enchmatcher.matches()) {
							Enchantment enchant = new EnchantmentWrapper(Integer.parseInt(enchmatcher.group(1)));
							item.addUnsafeEnchantment(enchant, Integer.parseInt(enchmatcher.group(2)));
						}
					}
				}
				// 表示アイテム名変更
				if (matcher.group(5) != null) {
					String name = Util.repSec(matcher.group(5));
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(name);
					item.setItemMeta(meta);
				}
				return item;
			}
		}
		return null;
	}
}
