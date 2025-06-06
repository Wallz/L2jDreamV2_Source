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

import com.dream.game.model.entity.sevensigns.SevenSigns;

public class SSQInfo extends L2GameServerPacket
{
	private int _state = 0;

	public SSQInfo()
	{
		int compWinner = SevenSigns.getInstance().getCabalHighestScore();

		if (SevenSigns.getInstance().isSealValidationPeriod())
			if (compWinner == SevenSigns.CABAL_DAWN)
			{
				_state = 2;
			}
			else if (compWinner == SevenSigns.CABAL_DUSK)
			{
				_state = 1;
			}
	}

	public SSQInfo(int state)
	{
		_state = state;
	}

	@Override
	protected final void writeImpl()
	{
		writeC(0xF8);

		if (_state == 2)
		{
			writeH(258);
		}
		else if (_state == 1)
		{
			writeH(257);
		}
		else
		{
			writeH(256);
		}
	}

}