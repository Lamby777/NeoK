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
import java.util.Arrays;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.wurstclient.DontBlock;
import net.wurstclient.command.CmdError;
import net.wurstclient.command.CmdException;
import net.wurstclient.command.CmdSyntaxError;
import net.wurstclient.command.Command;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.util.json.JsonException;
import net.wurstclient.util.json.JsonUtils;
import net.wurstclient.util.json.WsonObject;

@DontBlock
public final class OnActionCmd extends Command {
	private final Path bindsFile =
		WURST.getWurstFolder().resolve("event-binds.json");
	
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
				listEvents();
				break;
			
			case "list-binds":
				listBinds();
				break;
			
			case "wipe":
				wipeBinds();
				break;
			
			default:
				throw new CmdSyntaxError();
		}
	}

	public void bindAction(String[] args) throws CmdException {
		if(args.length < 4)
			throw new CmdSyntaxError();
		
		String bindId = args[1];
		String event = args[2];
		String action = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
		
		WsonObject parsed = null;
		boolean skipFinally = false;
		try {
			parsed = JsonUtils.parseFileToObject(bindsFile);
		} catch (NoSuchFileException e) {
			// File doesn't exist, so make it
			ChatUtils.warning("Binds file does not exist (yet). Making one now...\n" +
							"(This is normal if you haven't made binds yet)");
			try {
				JsonUtils.toJson(new JsonObject(), bindsFile);
				parsed = JsonUtils.parseFileToObject(bindsFile);
			} catch (JsonException | IOException newFileFail) {
				ChatUtils.error("Failed to create new file");
				newFileFail.printStackTrace();
				skipFinally = true;
			}
		} catch (JsonException | IOException e) {
			// File failed to parse
			ChatUtils.error("There was a problem reading your binds JSON file. " + 
							"If you want to delete your list and start fresh, type .binds wipe");
			e.printStackTrace();
			skipFinally = true;
		} finally {
			// Skip this block if invalid JSON or disk IO error
			if (!skipFinally) {
				try {
					JsonArray newElement = new JsonArray();
					
					// Add ID, then event, then action
					newElement.add(event);
					newElement.add(action);
					
					parsed.json.add(bindId, newElement);
					JsonUtils.toJson(parsed.toJsonObject(), bindsFile);
					
					ChatUtils.message("Bound action \""+ bindId +"\" to event \""+ event +"!\"");
				} catch (IOException | JsonException e) {
					e.printStackTrace();
					throw new CmdError("Couldn't save action bind: " + e.getMessage());
				}
			}
		}
	}
	
	public void unbindAction(String[] args) {}
	public void wipeBinds() {
		try {
			JsonUtils.toJson(new JsonObject(), bindsFile);
			ChatUtils.message("Wiped event action binds!");
		} catch (JsonException | IOException e) {
			ChatUtils.error("File does not exist");
			e.printStackTrace();
		}
	}
	
	private void listEvents() {}
	
	private void listBinds() {
		WsonObject parsed = null;
		try {
			parsed = JsonUtils.parseFileToObject(bindsFile);
		    ChatUtils.message("Binds list:");
			for (Entry<String, JsonElement> entry : parsed.json.entrySet()) {
			    String key = entry.getKey();
			    JsonElement value = entry.getValue();
			    
			    JsonArray arr = value.getAsJsonArray();
			    
			    ChatUtils.message("ID " + key + " on event " + arr.get(0) + " runs action " + arr.get(1));
			}
		} catch (JsonException | IOException e) {
			// File failed to parse
			ChatUtils.error("There was a problem reading your binds JSON file. Does it even exist? " +
							"If you want to delete your list and start fresh, type .binds wipe");
			e.printStackTrace();
		}
	}
}
