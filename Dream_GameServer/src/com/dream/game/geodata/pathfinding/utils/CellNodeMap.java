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
package com.dream.game.geodata.pathfinding.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dream.game.geodata.pathfinding.Node;

public class CellNodeMap
{
	private final Map<Integer, List<Node>> _cellIndex;

	public CellNodeMap()
	{
		_cellIndex = new HashMap<>();
	}

	public void add(Node n)
	{
		if (_cellIndex.containsKey(n.getLoc().getY()))
		{
			_cellIndex.get(n.getLoc().getY()).add(n);
		}
		else
		{
			List<Node> array = new ArrayList<>(5);
			array.add(n);
			_cellIndex.put(n.getLoc().getY(), array);
		}
	}

	public boolean contains(Node n)
	{
		List<Node> array = _cellIndex.get(n.getLoc().getY());
		if (array == null)
			return false;

		for (Node node : array)
			if (node.equals(n))
				return true;
		return false;
	}
}