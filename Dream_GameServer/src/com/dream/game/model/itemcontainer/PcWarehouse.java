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
package com.dream.game.model.itemcontainer;

import com.dream.game.model.actor.instance.L2ItemInstance.ItemLocation;
import com.dream.game.model.actor.instance.L2PcInstance;

public class PcWarehouse extends Warehouse
{
	private final L2PcInstance _owner;

	public PcWarehouse(L2PcInstance owner)
	{
		_owner = owner;
	}

	@Override
	public ItemLocation getBaseLocation()
	{
		return ItemLocation.WAREHOUSE;
	}

	public String getLocationId()
	{
		return "0";
	}

	public int getLocationId(boolean dummy)
	{
		return 0;
	}

	@Override
	public L2PcInstance getOwner()
	{
		return _owner;
	}

	public void setLocationId(L2PcInstance dummy)
	{
	}

	@Override
	public boolean validateCapacity(int slots)
	{
		return _items.size() + slots <= _owner.getWareHouseLimit();
	}
}