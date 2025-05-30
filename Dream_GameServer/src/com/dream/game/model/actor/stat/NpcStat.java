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
package com.dream.game.model.actor.stat;

import com.dream.Config;
import com.dream.game.model.actor.L2Npc;
import com.dream.game.skills.Stats;

public class NpcStat extends CharStat
{
	public NpcStat(L2Npc activeChar)
	{
		super(activeChar);

		setLevel(getActiveChar().getTemplate().getLevel());
	}

	@Override
	public L2Npc getActiveChar()
	{
		return (L2Npc) _activeChar;
	}

	@Override
	public int getMaxHp()
	{
		return (int) calcStat(Stats.MAX_HP, getActiveChar().getTemplate().getBaseHpMax() * (getActiveChar().isChampion() ? Config.CHAMPION_HP : 1), null, null);
	}
}