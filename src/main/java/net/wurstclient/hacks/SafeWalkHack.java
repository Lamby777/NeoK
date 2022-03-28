/*
 * Copyright (c) 2014-2022 Wurst-Imperium and contributors.
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
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;

@SearchTags({
	"safe walk", "scaffoldlegit", "speedbridge", "bridge"
})
public final class SafeWalkHack extends Hack {
	private final CheckboxSetting sneak = new CheckboxSetting("Sneak at edges", "Visibly sneak at edges.", false);

	private final SliderSetting maxDistance = new SliderSetting("Sneak distance",
			"I have officially lost my marbles. Just log into a world and tinker with this setting, I couldn't care less.",
			45, 30, 200, 1, ValueDisplay.INTEGER);

	private boolean sneaking;

	public SafeWalkHack() {
		super("SafeWalk");
		setCategory(Category.MOVEMENT);
		addSetting(sneak);
		addSetting(maxDistance);
	}

	@Override
	protected void onEnable() {
		WURST.getHax().parkourHack.setEnabled(false);
		sneaking = false;
	}

	@Override
	protected void onDisable() {
		if (sneaking) setSneaking(false);
	}

	public void onClipAtLedge(boolean clipping) {
		if (!isEnabled() || !sneak.isChecked() || !MC.player.isOnGround()) {
			if (sneaking) setSneaking(false);

			return;
		}

		ClientPlayerEntity player = MC.player;
		Box bb = player.getBoundingBox();
		float stepHeight = player.stepHeight;
		float dMaxDistance = (maxDistance.getValueF() / 100);

		for (double x = -dMaxDistance; x <= dMaxDistance; x += dMaxDistance)
			for (double z = -dMaxDistance; z <= dMaxDistance; z += dMaxDistance)
				if (MC.world.isSpaceEmpty(player, bb.offset(x, -stepHeight, z))) clipping = true;

		setSneaking(clipping);
	}

	private void setSneaking(boolean sneaking) {
		IKeyBinding sneakKey = (IKeyBinding) MC.options.sneakKey;

		if (sneaking) ((KeyBinding) sneakKey).setPressed(true);
		else
			((KeyBinding) sneakKey).setPressed(sneakKey.isActallyPressed());

		this.sneaking = sneaking;
	}
}
