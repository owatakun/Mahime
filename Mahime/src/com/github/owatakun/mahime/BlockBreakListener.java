package com.github.owatakun.mahime;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		// EditMode以外なら抜ける
		if (!RandomChest.instance.isRcEditMode()) {
			return;
		}
		// 置かれたブロックと置いたプレイヤーを取得
		Block block = event.getBlock();
		Player player = event.getPlayer();
		// Edit指示ブロックでなければ抜ける
		if (block.getTypeId() != RandomChest.instance.getRcEditBlock()) {
			return;
		}
		if (RandomChest.instance.removeRCPoint(player, block.getLocation())) {
			return;
		} else {
			event.setCancelled(true);
		}
	}
}
