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

public class ExSendManorList extends L2GameServerPacket
{
	private final List<String> _manors;

	public ExSendManorList(List<String> manors)
	{
		_manors = manors;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0xFE);
		writeH(0x1B);
		writeD(_manors.size());
		for (int i = 0; i < _manors.size(); i++)
		{
			int j = i + 1;
			writeD(j);
			writeS(_manors.get(i));
		}
	}

}