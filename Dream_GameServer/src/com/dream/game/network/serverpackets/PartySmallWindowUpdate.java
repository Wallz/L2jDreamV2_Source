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

import com.dream.game.model.actor.instance.L2PcInstance;

public class PartySmallWindowUpdate extends L2GameServerPacket
{
	private final L2PcInstance _member;

	public PartySmallWindowUpdate(L2PcInstance member)
	{
		_member = member;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x52);
		writeD(_member.getObjectId());
		writeS(_member.getName());

		writeD((int) _member.getStatus().getCurrentCp());
		writeD(_member.getMaxCp());

		writeD((int) _member.getStatus().getCurrentHp());
		writeD(_member.getMaxHp());
		writeD((int) _member.getStatus().getCurrentMp());
		writeD(_member.getMaxMp());
		writeD(_member.getLevel());
		writeD(_member.getClassId().getId());
	}

}