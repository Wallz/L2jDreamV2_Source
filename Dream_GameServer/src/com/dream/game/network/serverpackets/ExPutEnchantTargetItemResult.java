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

public class ExPutEnchantTargetItemResult extends L2GameServerPacket
{
	private final int _result;
	private final int _crystal;
	private final int _count;

	public ExPutEnchantTargetItemResult(int result, int crystal, int count)
	{
		_result = result;
		_crystal = crystal;
		_count = count;
	}

	@Override
	protected void writeImpl()
	{
		writeC(0x81);
		writeD(_result);
		writeD(_crystal);
		writeD(_count);
	}

}