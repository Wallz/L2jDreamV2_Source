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
package com.dream.game.skills.conditions;

import com.dream.game.model.actor.instance.L2PcInstance;
import com.dream.game.skills.Env;

final class ConditionPlayerGrade extends Condition
{
	public final static int COND_NO_GRADE = 0x0001;
	public final static int COND_D_GRADE = 0x0002;
	public final static int COND_C_GRADE = 0x0004;
	public final static int COND_B_GRADE = 0x0008;
	public final static int COND_A_GRADE = 0x0010;
	public final static int COND_S_GRADE = 0x0020;

	private final int _value;

	public ConditionPlayerGrade(int value)
	{
		_value = value;
	}

	@Override
	public boolean testImpl(Env env)
	{
		if (env.player instanceof L2PcInstance)
		{
			byte expIndex = (byte) ((L2PcInstance) env.player).getExpertiseIndex();

			return _value == expIndex;
		}
		return false;
	}
}