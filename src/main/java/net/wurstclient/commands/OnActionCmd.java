/*
 * Copyright (c) 2014-2021 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.commands;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;

import net.wurstclient.DontBlock;
import net.wurstclient.command.CmdError;
import net.wurstclient.command.CmdException;
import net.wurstclient.command.CmdSyntaxError;
import net.wurstclient.command.Command;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.util.MathUtils;
import net.wurstclient.util.json.JsonException;

@DontBlock
public final class OnActionCmd extends Command {
	public OnActionCmd() {
		super("onaction", "Binds a chat message, Minecraft command, or Wurst command to a game event.",
			".onaction bind <bind ID> <event> <chat message>",
			".onaction unbind <bind ID>",
			".onaction list-events",
			".onaction list-binds"
		);
	}
	
	@Override
	public void call(String[] args) throws CmdException {
		if (args.length < 1)
			throw new CmdSyntaxError();
		
		switch (args[0].toLowerCase()) {
			case "bind":
				bindAction(args);
				break;
			
			case "unbind":
				unbindAction(args);
				break;
			
			case "list-events":
				listEvents(args);
				break;
			
			case "list-binds":
				listBinds(args);
				break;
			
			default:
				throw new CmdSyntaxError();
		}
	}

	public void bindAction(String[] args) {}
	public void unbindAction(String[] args) {}
	public void listEvents(String[] args) {}
	public void listBinds(String[] args) {}
}
