package com.dream.game.network.clientpackets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.dream.L2DatabaseFactory;
import com.dream.game.cache.CrestCache;
import com.dream.game.idfactory.IdFactory;
import com.dream.game.model.L2Clan;
import com.dream.game.model.actor.instance.L2PcInstance;
import com.dream.game.network.SystemMessageId;

public class RequestSetPledgeCrest extends L2GameClientPacket
{
	public static Logger _log = Logger.getLogger(RequestSetPledgeCrest.class.getName());
	private int _length;
	private byte[] _data;

	@Override
	protected void readImpl()
	{
		_length = readD();
		if (_length < 0 || _length > 256)
			return;

		_data = new byte[_length];
		readB(_data);
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance activeChar = getClient().getActiveChar();

		if (activeChar == null)
			return;

		L2Clan clan = activeChar.getClan();
		if (clan == null)
			return;

		if (clan.getDissolvingExpiryTime() > System.currentTimeMillis())
		{
			activeChar.sendPacket(SystemMessageId.CANNOT_SET_CREST_WHILE_DISSOLUTION_IN_PROGRESS);
			return;
		}
		if (_length < 0)
		{
			activeChar.sendMessage("File transfer error.");
			return;
		}
		if (_length > 256)
		{
			activeChar.sendMessage("The clan crest file size was too big (max 256 bytes).");
			return;
		}
		if (_length == 0 || _data.length == 0)
		{
			CrestCache.getInstance().removePledgeCrest(clan.getCrestId());

			clan.setHasCrest(false);
			activeChar.sendPacket(SystemMessageId.CLAN_CREST_HAS_BEEN_DELETED);

			for (L2PcInstance member : clan.getOnlineMembers(0))
			{
				member.broadcastUserInfo();
			}

			return;
		}
		if ((activeChar.getClanPrivileges() & L2Clan.CP_CL_REGISTER_CREST) == L2Clan.CP_CL_REGISTER_CREST)
		{
			if (clan.getLevel() < 3)
			{
				activeChar.sendPacket(SystemMessageId.CLAN_LVL_3_NEEDED_TO_SET_CREST);
				return;
			}

			CrestCache crestCache = CrestCache.getInstance();

			int newId = IdFactory.getInstance().getNextId();

			if (clan.hasCrest())
			{
				crestCache.removePledgeCrest(newId);
			}

			if (!crestCache.savePledgeCrest(newId, _data))
			{
				_log.info("Error loading crest of clan:" + clan.getName());
				return;
			}

			Connection con = null;

			try
			{
				con = L2DatabaseFactory.getInstance().getConnection(con);
				PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET crest_id = ? WHERE clan_id = ?");
				statement.setInt(1, newId);
				statement.setInt(2, clan.getClanId());
				statement.executeUpdate();
				statement.close();
			}
			catch (SQLException e)
			{
				_log.warn("could not update the crest id:" + e.getMessage());
			}
			finally
			{
				L2DatabaseFactory.close(con);
			}

			clan.setCrestId(newId);
			clan.setHasCrest(true);

			for (L2PcInstance member : clan.getOnlineMembers(0))
			{
				member.broadcastUserInfo();
			}
		}
	}

}