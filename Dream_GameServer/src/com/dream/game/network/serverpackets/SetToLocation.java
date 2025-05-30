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

import com.dream.game.model.actor.L2Character;

public class SetToLocation extends L2GameServerPacket
{
	private final int _charObjId;
	private final int _x, _y, _z, _heading;

	public SetToLocation(L2Character character)
	{
		_charObjId = character.getObjectId();
		_x = character.getX();
		_y = character.getY();
		_z = character.getZ();
		_heading = character.getHeading();
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0x76);

		writeD(_charObjId);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(_heading);
	}

}