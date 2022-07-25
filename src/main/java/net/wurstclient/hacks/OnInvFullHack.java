/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hacks;

import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.settings.CheckboxSetting;

@SearchTags({"oninvfull", "inventory", "full", "automatic"})
public final class OnInvFullHack extends Hack implements UpdateListener {
	public static String action = ".t OnInvFull";
	private CheckboxSetting debounce = new CheckboxSetting("Debounce",
			"Disable this to run the action every tick repeatedly until inventory is no longer full.", true);
	private boolean lastCheckFull = false;
	
	public OnInvFullHack() {
		super("OnInvFull");
		
		setCategory(Category.OTHER);
		addSetting(debounce);
	}
	
	@Override
	public void onEnable() {
		lastCheckFull = false;
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	public void onDisable() {
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate() {
		if (MC.player.getInventory().getEmptySlot() == -1) {
			if (debounce.isChecked() && lastCheckFull) return;
			sendMessage(action);
			
			lastCheckFull = true;
		} else {
			lastCheckFull = false;
		}
	}
	
	// Send message to chat
	private void sendMessage(String message) {
		if (message.startsWith(".")) {
			// Message is Wurst command
			WURST.getCmdProcessor().process(message.substring(1));
		} else {
			// Otherwise, send to chat
			MC.player.sendChatMessage(message);
		}
	}
}
