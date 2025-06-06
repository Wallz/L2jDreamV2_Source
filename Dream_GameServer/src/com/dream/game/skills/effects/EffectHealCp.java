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
package com.dream.game.skills.effects;

import com.dream.game.model.L2Effect;
import com.dream.game.skills.Env;
import com.dream.game.templates.skills.L2EffectType;

public final class EffectHealCp extends L2Effect
{

	public EffectHealCp(Env env, EffectTemplate template)
	{
		super(env, template);
	}

	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.BUFF;
	}

	@Override
	public boolean onActionTime()
	{
		return false;
	}

	@Override
	public void onExit()
	{
	}

	@Override
	public boolean onStart()
	{
		if ((int) calc() != 0)
		{
			getEffected().getStatus().setCurrentCp(getEffected().getCurrentCp() + (int) calc());
		}
		else
		{
			getEffected().getStatus().setCurrentCp(getEffected().getMaxCp());
		}
		return true;
	}
}
