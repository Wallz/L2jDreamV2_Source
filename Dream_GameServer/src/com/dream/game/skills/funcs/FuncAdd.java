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
package com.dream.game.skills.funcs;

import com.dream.game.skills.Env;
import com.dream.game.skills.Stats;
import com.dream.game.skills.conditions.Condition;

public final class FuncAdd extends FuncLambda
{
	public FuncAdd(Stats pStat, int pOrder, FuncOwner pFuncOwner, double value, Condition pCondition)
	{
		super(pStat, pOrder, pFuncOwner, String.valueOf(value), pCondition);
	}

	public FuncAdd(Stats pStat, int pOrder, FuncOwner pFuncOwner, String value, Condition pCondition)
	{
		super(pStat, pOrder, pFuncOwner, value, pCondition);
	}

	@Override
	protected void calc(Env env)
	{
		env.value += _lambda;
	}
}