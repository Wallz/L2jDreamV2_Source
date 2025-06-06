package com.dream.game.network.serverpackets;

public class ExAskJoinPartyRoom extends L2GameServerPacket
{
	private final String _charName;

	public ExAskJoinPartyRoom(String charName)
	{
		_charName = charName;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x34);
		writeS(_charName);
	}

}