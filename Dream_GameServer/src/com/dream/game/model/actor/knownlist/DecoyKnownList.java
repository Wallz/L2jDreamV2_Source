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
package com.dream.game.model.actor.knownlist;

import com.dream.game.model.L2Object;
import com.dream.game.model.actor.L2Decoy;

public class DecoyKnownList extends CharKnownList
{
	public DecoyKnownList(L2Decoy activeChar)
	{
		super(activeChar);
	}

	@Override
	public final L2Decoy getActiveChar()
	{
		return (L2Decoy) _activeChar;
	}

	@Override
	public int getDistanceToForgetObject(L2Object object)
	{
		if (object == getActiveChar().getOwner() || object == getActiveChar().getTarget())
			return 6000;
		return 3000;
	}

	@Override
	public int getDistanceToWatchObject(L2Object object)
	{
		return 1500;
	}
}