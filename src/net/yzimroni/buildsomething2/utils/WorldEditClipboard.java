package net.yzimroni.buildsomething2.utils;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;

public class WorldEditClipboard {
	CuboidClipboard clipBoard;
	EditSession editSession;

	public WorldEditClipboard(CuboidClipboard clipboard2, EditSession editSession) {
		super();
		this.clipBoard = clipboard2;
		this.editSession = editSession;
	}

	public CuboidClipboard getClipBoard() {
		return clipBoard;
	}

	public void setClipBoard(CuboidClipboard clipBoard) {
		this.clipBoard = clipBoard;
	}

	public EditSession getEditSession() {
		return editSession;
	}

	public void setEditSession(EditSession editSession) {
		this.editSession = editSession;
	}

}
