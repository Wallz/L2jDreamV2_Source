package com.dream.game.network.serverpackets;

import com.dream.game.model.TradeList;
import com.dream.game.model.actor.instance.L2PcInstance;

public class TradeOwnAdd extends L2GameServerPacket
{
	private final TradeList.TradeItem _item;

	public TradeOwnAdd(TradeList.TradeItem item)
	{
		_item = item;
	}

	@Override
	protected final void writeImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;

		writeC(0x20);
		writeH(1);
		writeH(_item.getItem().getType1());
		writeD(_item.getObjectId());
		writeD(_item.getItem().getItemDisplayId());
		writeD(_item.getCount());
		writeH(_item.getItem().getType2());
		writeH(0x00);
		writeD(_item.getItem().getBodyPart());
		writeH(_item.getEnchant());
		writeH(0x00);
		writeH(0x00);
	}

}