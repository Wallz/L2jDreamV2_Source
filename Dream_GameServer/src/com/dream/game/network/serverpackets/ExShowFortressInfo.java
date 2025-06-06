/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dream.game.network.serverpackets;

import java.util.List;

import com.dream.game.manager.FortManager;
import com.dream.game.model.L2Clan;
import com.dream.game.model.entity.siege.Fort;

public class ExShowFortressInfo extends L2GameServerPacket
{
	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x15);
		List<Fort> forts = FortManager.getInstance().getForts();
		writeD(forts.size());
		for (Fort fort : forts)
		{
			L2Clan clan = fort.getOwnerClan();
			writeD(fort.getFortId());
			if (clan != null)
			{
				writeS(clan.getName());
			}
			else
			{
				writeS("");
			}

			if (fort.getSiege().getIsInProgress())
			{
				writeD(1);
			}
			else
			{
				writeD(0);
			}

			writeD(fort.getOwnedTime());
		}
	}

}