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
package com.dream.game.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.dream.Config;
import com.dream.L2DatabaseFactory;
import com.dream.game.Announcements;
import com.dream.game.datatables.sql.NpcTable;
import com.dream.game.datatables.sql.SpawnTable;
import com.dream.game.idfactory.IdFactory;
import com.dream.game.model.L2Spawn;
import com.dream.game.model.actor.L2Npc;
import com.dream.game.model.entity.Town;
import com.dream.game.model.world.Location;
import com.dream.game.network.ThreadPoolManager;
import com.dream.game.templates.chars.L2NpcTemplate;
import com.dream.tools.random.Rnd;

public class AutoSpawnManager
{
	private class AutoDespawner implements Runnable
	{
		private final int _objectId;

		public AutoDespawner(int objectId)
		{
			_objectId = objectId;
		}

		@Override
		public void run()
		{
			try
			{
				AutoSpawnInstance spawnInst = _registeredSpawns.get(_objectId);

				for (L2Npc npcInst : spawnInst.getNPCInstanceList())
				{
					npcInst.deleteMe();

					L2Spawn spawn = npcInst.getSpawn();
					if (spawn != null)
					{
						spawn.stopRespawn();
						SpawnTable.getInstance().deleteSpawn(spawn, true);
					}
					spawnInst.removeNpcInstance(npcInst);
				}
			}
			catch (Exception e)
			{
				_log.warn("AutoSpawnHandler: An error occurred while despawning spawn (Object ID = " + _objectId + "): " + e);
			}
		}
	}

	private class AutoSpawner implements Runnable
	{
		private final int _objectId;

		public AutoSpawner(int objectId)
		{
			_objectId = objectId;
		}

		@Override
		public void run()
		{
			try
			{
				AutoSpawnInstance spawnInst = _registeredSpawns.get(_objectId);

				if (!spawnInst.isSpawnActive())
					return;

				Location[] locationList = spawnInst.getLocationList();

				if (locationList.length == 0)
				{
					_log.info("AutoSpawnHandler: No location co-ords specified for spawn instance (Object ID = " + _objectId + ").");
					return;
				}

				int locationCount = locationList.length;
				int locationIndex = Rnd.nextInt(locationCount);

				if (!spawnInst.isRandomSpawn())
				{
					locationIndex = spawnInst._lastLocIndex + 1;

					if (locationIndex == locationCount)
					{
						locationIndex = 0;
					}

					spawnInst._lastLocIndex = locationIndex;
				}

				final int x = locationList[locationIndex].getX();
				final int y = locationList[locationIndex].getY();
				final int z = locationList[locationIndex].getZ();
				final int heading = locationList[locationIndex].getHeading();

				L2NpcTemplate npcTemp = NpcTable.getInstance().getTemplate(spawnInst.getNpcId());
				if (npcTemp == null)
				{
					_log.warn("Couldnt find NPC id" + spawnInst.getNpcId() + " Try to update your DP");
					return;
				}
				L2Spawn newSpawn = new L2Spawn(npcTemp);

				newSpawn.setLocx(x);
				newSpawn.setLocy(y);
				newSpawn.setLocz(z);
				if (heading != -1)
				{
					newSpawn.setHeading(heading);
				}
				newSpawn.setAmount(spawnInst.getSpawnCount());
				if (spawnInst._desDelay == 0)
				{
					newSpawn.setRespawnDelay(spawnInst._resDelay);
				}

				SpawnTable.getInstance().addNewSpawn(newSpawn, false);
				L2Npc npcInst = null;

				if (spawnInst._spawnCount == 1)
				{
					npcInst = newSpawn.doSpawn();
					npcInst.getPosition().setXYZ(npcInst.getX(), npcInst.getY(), npcInst.getZ());
					spawnInst.addNpcInstance(npcInst);
				}
				else
				{
					for (int i = 0; i < spawnInst._spawnCount; i++)
					{
						npcInst = newSpawn.doSpawn();

						npcInst.getPosition().setXYZ(npcInst.getX() + Rnd.nextInt(50), npcInst.getY() + Rnd.nextInt(50), npcInst.getZ());

						spawnInst.addNpcInstance(npcInst);
					}
				}

				Town town = TownManager.getInstance().getClosestTown(npcInst);
				String nearestTown = "";
				if (town != null)
				{
					nearestTown = TownManager.getInstance().getTownName(town.getTownId());
				}
				else
				{
					nearestTown = "None";
				}

				if (spawnInst.isBroadcasting() && npcInst != null)
				{
					Announcements.getInstance().announceToAll(npcInst.getName() + " appeared in " + nearestTown + "!");
				}

				if (spawnInst.getDespawnDelay() > 0)
				{
					AutoDespawner rd = new AutoDespawner(_objectId);
					ThreadPoolManager.getInstance().scheduleAi(rd, spawnInst.getDespawnDelay() - 1000);
				}
			}
			catch (Exception e)
			{
				_log.warn("AutoSpawnHandler: An error occurred while initializing spawn instance (Object ID = " + _objectId + "): ", e);
			}
		}
	}

	public class AutoSpawnInstance
	{
		protected int _objectId;
		protected int _spawnIndex;

		protected int _npcId;
		protected int _initDelay;
		protected int _resDelay;
		protected int _desDelay;
		protected int _spawnCount = 1;
		protected int _lastLocIndex = -1;

		private final List<L2Npc> _npcList = new ArrayList<>();

		private final List<Location> _locList = new ArrayList<>();

		private boolean _spawnActive;
		private boolean _randomSpawn = false;
		private boolean _broadcastAnnouncement = false;

		protected AutoSpawnInstance(int npcId, int initDelay, int respawnDelay, int despawnDelay)
		{
			_npcId = npcId;
			_initDelay = initDelay;
			_resDelay = respawnDelay;
			_desDelay = despawnDelay;
		}

		protected boolean addNpcInstance(L2Npc npcInst)
		{
			return _npcList.add(npcInst);
		}

		public boolean addSpawnLocation(int x, int y, int z, int heading)
		{
			return _locList.add(new Location(x, y, z, heading));
		}

		public boolean addSpawnLocation(int[] spawnLoc)
		{
			if (spawnLoc.length != 3)
				return false;

			return addSpawnLocation(spawnLoc[0], spawnLoc[1], spawnLoc[2], -1);
		}

		public int getDespawnDelay()
		{
			return _desDelay;
		}

		public int getInitialDelay()
		{
			return _initDelay;
		}

		public Location[] getLocationList()
		{
			return _locList.toArray(new Location[_locList.size()]);
		}

		public int getNpcId()
		{
			return _npcId;
		}

		public L2Npc[] getNPCInstanceList()
		{
			L2Npc[] ret;
			synchronized (_npcList)
			{
				ret = new L2Npc[_npcList.size()];
				_npcList.toArray(ret);
			}

			return ret;
		}

		public int getObjectId()
		{
			return _objectId;
		}

		public int getRespawnDelay()
		{
			return _resDelay;
		}

		public int getSpawnCount()
		{
			return _spawnCount;
		}

		public L2Spawn[] getSpawns()
		{
			List<L2Spawn> npcSpawns = new ArrayList<>();

			for (L2Npc npcInst : _npcList)
			{
				npcSpawns.add(npcInst.getSpawn());
			}

			return npcSpawns.toArray(new L2Spawn[npcSpawns.size()]);
		}

		public boolean isBroadcasting()
		{
			return _broadcastAnnouncement;
		}

		public boolean isRandomSpawn()
		{
			return _randomSpawn;
		}

		public boolean isSpawnActive()
		{
			return _spawnActive;
		}

		protected boolean removeNpcInstance(L2Npc npcInst)
		{
			return _npcList.remove(npcInst);
		}

		public Location removeSpawnLocation(int locIndex)
		{
			try
			{
				return _locList.remove(locIndex);
			}
			catch (IndexOutOfBoundsException e)
			{
				return null;
			}
		}

		public void setBroadcast(boolean broadcastValue)
		{
			_broadcastAnnouncement = broadcastValue;
		}

		public void setRandomSpawn(boolean randValue)
		{
			_randomSpawn = randValue;
		}

		protected void setSpawnActive(boolean activeValue)
		{
			_spawnActive = activeValue;
		}

		public void setSpawnCount(int spawnCount)
		{
			_spawnCount = spawnCount;
		}
	}

	private static class SingletonHolder
	{
		protected static final AutoSpawnManager _instance = new AutoSpawnManager();
	}

	protected static Logger _log = Logger.getLogger(AutoSpawnManager.class.getName());

	private static final int DEFAULT_INITIAL_SPAWN = 30000;
	private static final int DEFAULT_RESPAWN = 3600000;

	private static final int DEFAULT_DESPAWN = 3600000;

	public static AutoSpawnManager getInstance()
	{
		return SingletonHolder._instance;
	}

	protected Map<Integer, AutoSpawnInstance> _registeredSpawns;

	protected Map<Integer, ScheduledFuture<?>> _runningSpawns;

	protected boolean _activeState = true;

	protected AutoSpawnManager()
	{
		_registeredSpawns = new ConcurrentHashMap<>();
		_runningSpawns = new ConcurrentHashMap<>();

		restoreSpawnData();
	}

	public final AutoSpawnInstance getAutoSpawnInstance(int id, boolean isObjectId)
	{
		if (isObjectId)
		{
			if (isSpawnRegistered(id))
				return _registeredSpawns.get(id);
		}
		else
		{
			for (AutoSpawnInstance spawnInst : _registeredSpawns.values())
				if (spawnInst.getNpcId() == id)
					return spawnInst;
		}

		return null;
	}

	public Map<Integer, AutoSpawnInstance> getAutoSpawnInstances(int npcId)
	{
		Map<Integer, AutoSpawnInstance> spawnInstList = new HashMap<>();

		for (AutoSpawnInstance spawnInst : _registeredSpawns.values())
			if (spawnInst.getNpcId() == npcId)
			{
				spawnInstList.put(spawnInst.getObjectId(), spawnInst);
			}

		return spawnInstList;
	}

	public final long getTimeToNextSpawn(AutoSpawnInstance spawnInst)
	{
		int objectId = spawnInst.getObjectId();

		if (!isSpawnRegistered(objectId))
			return -1;

		return _runningSpawns.get(objectId).getDelay(TimeUnit.MILLISECONDS);
	}

	public final boolean isSpawnRegistered(AutoSpawnInstance spawnInst)
	{
		return _registeredSpawns.containsValue(spawnInst);
	}

	public final boolean isSpawnRegistered(int objectId)
	{
		return _registeredSpawns.containsKey(objectId);
	}

	public AutoSpawnInstance registerSpawn(int npcId, int initialDelay, int respawnDelay, int despawnDelay)
	{
		return registerSpawn(npcId, null, initialDelay, respawnDelay, despawnDelay);
	}

	public AutoSpawnInstance registerSpawn(int npcId, int[][] spawnPoints, int initialDelay, int respawnDelay, int despawnDelay)
	{
		if (initialDelay < 0)
		{
			initialDelay = DEFAULT_INITIAL_SPAWN;
		}

		if (respawnDelay < 0)
		{
			respawnDelay = DEFAULT_RESPAWN;
		}

		if (despawnDelay < 0)
		{
			despawnDelay = DEFAULT_DESPAWN;
		}

		AutoSpawnInstance newSpawn = new AutoSpawnInstance(npcId, initialDelay, respawnDelay, despawnDelay);

		if (spawnPoints != null)
		{
			for (int[] spawnPoint : spawnPoints)
			{
				newSpawn.addSpawnLocation(spawnPoint);
			}
		}

		int newId = IdFactory.getInstance().getNextId();
		newSpawn._objectId = newId;
		_registeredSpawns.put(newId, newSpawn);

		setSpawnActive(newSpawn, true);

		if (_log.isDebugEnabled())
		{
			_log.debug("AutoSpawnHandler: Registered auto spawn for NPC ID " + npcId + " (Object ID = " + newId + ").");
		}

		return newSpawn;
	}

	public void reload()
	{
		for (ScheduledFuture<?> sf : _runningSpawns.values())
			if (sf != null)
			{
				sf.cancel(true);
			}

		for (AutoSpawnInstance asi : _registeredSpawns.values())
			if (asi != null)
			{
				this.removeSpawn(asi);
			}

		_registeredSpawns = new HashMap<>();
		_runningSpawns = new HashMap<>();

		restoreSpawnData();
	}

	public boolean removeSpawn(AutoSpawnInstance spawnInst)
	{
		if (!isSpawnRegistered(spawnInst))
			return false;

		try
		{
			_registeredSpawns.remove(spawnInst.getNpcId());

			ScheduledFuture<?> respawnTask = _runningSpawns.remove(spawnInst._objectId);
			respawnTask.cancel(false);

			if (_log.isDebugEnabled())
			{
				_log.debug("AutoSpawnHandler: Removed auto spawn for NPC ID " + spawnInst._npcId + " (Object ID = " + spawnInst._objectId + ").");
			}
		}
		catch (Exception e)
		{
			_log.warn("AutoSpawnHandler: Could not auto spawn for NPC ID " + spawnInst._npcId + " (Object ID = " + spawnInst._objectId + "): " + e);
			return false;
		}

		return true;
	}

	public void removeSpawn(int objectId)
	{
		removeSpawn(_registeredSpawns.get(objectId));
	}

	private void restoreSpawnData()
	{
		int numLoaded = 0;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement2 = null;
			ResultSet rs2 = null;

			// Restore spawn group data, then the location data.
			PreparedStatement statement = con.prepareStatement("SELECT * FROM random_spawn ORDER BY groupId ASC");
			ResultSet rs = statement.executeQuery();

			while (rs.next())
			{
				// Register random spawn group, set various options on the
				// created spawn instance.
				AutoSpawnInstance spawnInst = registerSpawn(rs.getInt("npcId"), rs.getInt("initialDelay"), rs.getInt("respawnDelay"), rs.getInt("despawnDelay"));

				spawnInst.setSpawnCount(rs.getInt("count"));
				spawnInst.setBroadcast(rs.getBoolean("broadcastSpawn"));
				spawnInst.setRandomSpawn(rs.getBoolean("randomSpawn"));
				numLoaded++;

				// Restore the spawn locations for this spawn group/instance.
				statement2 = con.prepareStatement("SELECT * FROM random_spawn_loc WHERE groupId=?");
				statement2.setInt(1, rs.getInt("groupId"));
				rs2 = statement2.executeQuery();

				while (rs2.next())
				{
					// Add each location to the spawn group/instance.
					spawnInst.addSpawnLocation(rs2.getInt("x"), rs2.getInt("y"), rs2.getInt("z"), rs2.getInt("heading"));
				}

				statement2.close();
			}

			statement.close();

			if (Config.DEBUG)
			{
				_log.warn("AutoSpawnHandler: Loaded " + numLoaded + " spawn group(s) from the database.");
			}
		}
		catch (Exception e)
		{
			_log.warn("AutoSpawnHandler: Could not restore spawn data: " + e);
		}
	}

	public void setAllActive(boolean isActive)
	{
		if (_activeState == isActive)
			return;

		for (AutoSpawnInstance spawnInst : _registeredSpawns.values())
		{
			setSpawnActive(spawnInst, isActive);
		}

		_activeState = isActive;
	}

	public void setSpawnActive(AutoSpawnInstance spawnInst, boolean isActive)
	{
		if (spawnInst == null)
			return;

		int objectId = spawnInst._objectId;

		if (isSpawnRegistered(objectId))
		{
			ScheduledFuture<?> spawnTask = null;

			if (isActive)
			{
				AutoSpawner rs = new AutoSpawner(objectId);

				if (spawnInst._desDelay > 0)
				{
					spawnTask = ThreadPoolManager.getInstance().scheduleEffectAtFixedRate(rs, spawnInst._initDelay, spawnInst._resDelay);
				}
				else
				{
					spawnTask = ThreadPoolManager.getInstance().scheduleEffect(rs, spawnInst._initDelay);
				}
				_runningSpawns.put(objectId, spawnTask);
			}
			else
			{
				AutoDespawner rd = new AutoDespawner(objectId);
				spawnTask = _runningSpawns.remove(objectId);

				if (spawnTask != null)
				{
					spawnTask.cancel(false);
				}

				ThreadPoolManager.getInstance().scheduleEffect(rd, 0);
			}

			spawnInst.setSpawnActive(isActive);
		}
	}

	public final int size()
	{
		return _registeredSpawns.size();
	}
}