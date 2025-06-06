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

public class PledgeSkillListAdd extends L2GameServerPacket
{
	private final int _id;
	private final int _lvl;

	public PledgeSkillListAdd(int id, int lvl)
	{
		_id = id;
		_lvl = lvl;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xfe);
		writeH(0x3a);

		writeD(_id);
		writeD(_lvl);
	}

}