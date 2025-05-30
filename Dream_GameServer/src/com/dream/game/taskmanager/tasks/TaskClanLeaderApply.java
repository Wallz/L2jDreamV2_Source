/*
 * Copyright (C) 2004-2013 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dream.game.taskmanager.tasks;

import java.util.Calendar;

import com.dream.Config;
import com.dream.game.datatables.sql.ClanTable;
import com.dream.game.manager.TaskManager;
import com.dream.game.manager.TaskManager.ExecutedTask;
import com.dream.game.model.L2Clan;
import com.dream.game.model.L2ClanMember;
import com.dream.game.taskmanager.Task;
import com.dream.game.taskmanager.TaskTypes;

/**
 * @author UnAfraid
 */
public class TaskClanLeaderApply extends Task
{
	private static final String NAME = "clanleaderapply";

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public void initializate()
	{
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", Config.ALT_CLAN_LEADER_HOUR_CHANGE, "");
	}

	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		Calendar cal = Calendar.getInstance();
		if (cal.get(Calendar.DAY_OF_WEEK) == Config.ALT_CLAN_LEADER_DATE_CHANGE)
		{
			for (L2Clan clan : ClanTable.getInstance().getClans())
			{
				if (clan.getNewLeaderId() != 0)
				{
					final L2ClanMember member = clan.getClanMember(clan.getNewLeaderId());
					if (member == null)
					{
						continue;
					}

					clan.setNewLeader(member);
				}
			}
		}
	}
}
