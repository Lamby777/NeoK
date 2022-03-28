/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */

package net.wurstclient.hacks;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.Box;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.hack.Hack;
import net.wurstclient.mixinterface.IKeyBinding;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({ "safe walk", "scaffoldwalk", "scaffold walk", "scaffoldlegit", "legit", "speedbridge", "godbridge",
		"bridge", "bridging", "autobridge" })
public final class ScaffoldLegitHack extends Hack {
	private final SliderSetting maxDistance = new SliderSetting("Maximum Distance to Edge",
			"How much \"safety\" offset is taken into account?\n\n"
					+ "Good for making your speedbridging look legit.\n",
			0.05, 0.01, 1, 0.01, ValueDisplay.PERCENTAGE);
	
	private boolean sneaking;
	
	public ScaffoldLegitHack() {
		super("ScaffoldLegit");
		setCategory(Category.MOVEMENT);
		addSetting(maxDistance);
	}
	
	@Override
	protected void onEnable() {
		WURST.getHax().parkourHack.setEnabled(false);
		WURST.getHax().safeWalkHack.setEnabled(false);
		sneaking = false;
	}
	
	@Override
	protected void onDisable() {
		if (sneaking)
			setSneaking(false);
	}
	
	public void onUpdate(boolean clipping) {
		if (!isEnabled()) {
			if (sneaking)
				setSneaking(false);
			return;
		}
		
		ClientPlayerEntity player = MC.player;
		Box bb = player.getBoundingBox();
		float stepHeight = player.stepHeight;
		double mdVal = maxDistance.getValue();
		
		for (double x = -mdVal; x <= mdVal; x += mdVal)
			for (double z = -mdVal; z <= mdVal; z += mdVal)
				if (MC.world.isSpaceEmpty(player, bb.offset(x, -stepHeight, z)))
					clipping = true;
		
		//Vec3d pos = MC.player.getPos();
		
		//if (pos.x > (pos.x - (int)pos.x)) {}
				
		setSneaking(clipping);
	}
	
	private void setSneaking(boolean sneaking) {
		IKeyBinding sneakKey = (IKeyBinding) MC.options.sneakKey;
		
		if (sneaking)
			((KeyBinding) sneakKey).setPressed(true);
		else
			((KeyBinding) sneakKey).setPressed(sneakKey.isActallyPressed());
		
		this.sneaking = sneaking;
	}
}
