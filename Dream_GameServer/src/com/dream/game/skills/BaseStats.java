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
package com.dream.game.skills;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.dream.Config;
import com.dream.game.model.actor.L2Character;

public enum BaseStats
{
	@SuppressWarnings("synthetic-access") STR(new STR()),
	@SuppressWarnings("synthetic-access") INT(new INT()),
	@SuppressWarnings("synthetic-access") DEX(new DEX()),
	@SuppressWarnings("synthetic-access") WIT(new WIT()),
	@SuppressWarnings("synthetic-access") CON(new CON()),
	@SuppressWarnings("synthetic-access") MEN(new MEN()),
	@SuppressWarnings("synthetic-access") NULL(new NULL());

	private interface BaseStat
	{
		public double calcBonus(L2Character actor);
	}

	private static final class CON implements BaseStat
	{
		@Override
		public final double calcBonus(L2Character actor)
		{
			return CONbonus[actor.getCON()];
		}
	}

	private static final class DEX implements BaseStat
	{
		@Override
		public final double calcBonus(L2Character actor)
		{
			return DEXbonus[actor.getDEX()];
		}
	}

	private static final class INT implements BaseStat
	{
		@Override
		public final double calcBonus(L2Character actor)
		{
			return INTbonus[actor.getINT()];
		}
	}

	private static final class MEN implements BaseStat
	{
		@Override
		public final double calcBonus(L2Character actor)
		{
			return MENbonus[actor.getMEN()];
		}
	}

	private static final class NULL implements BaseStat
	{
		@Override
		public final double calcBonus(L2Character actor)
		{
			return 1f;
		}
	}

	private static final class STR implements BaseStat
	{
		@Override
		public final double calcBonus(L2Character actor)
		{
			return STRbonus[actor.getSTR()];
		}
	}

	private static final class WIT implements BaseStat
	{
		@Override
		public final double calcBonus(L2Character actor)
		{
			return WITbonus[actor.getWIT()];
		}
	}

	protected static final Logger _log = Logger.getLogger(BaseStats.class.getName());

	public static final int MAX_STAT_VALUE = 100;

	public static final double[] STRbonus = new double[MAX_STAT_VALUE];

	public static final double[] INTbonus = new double[MAX_STAT_VALUE];

	public static final double[] DEXbonus = new double[MAX_STAT_VALUE];

	public static final double[] WITbonus = new double[MAX_STAT_VALUE];

	public static final double[] CONbonus = new double[MAX_STAT_VALUE];

	public static final double[] MENbonus = new double[MAX_STAT_VALUE];

	static
	{
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setIgnoringComments(true);
		final File file = new File(Config.DATAPACK_ROOT, "data/xml/player/statBonus.xml");
		Document doc = null;

		if (file.exists())
		{
			try
			{
				doc = factory.newDocumentBuilder().parse(file);
			}
			catch (Exception e)
			{
				_log.log(Level.WARNING, "[BaseStats] Could not parse file: " + e.getMessage(), e);
			}

			String statName;
			int val;
			double bonus;
			NamedNodeMap attrs;
			for (@SuppressWarnings("null")
			Node list = doc.getFirstChild(); list != null; list = list.getNextSibling())
				if ("list".equalsIgnoreCase(list.getNodeName()))
				{
					for (Node stat = list.getFirstChild(); stat != null; stat = stat.getNextSibling())
					{
						statName = stat.getNodeName();
						for (Node value = stat.getFirstChild(); value != null; value = value.getNextSibling())
							if ("stat".equalsIgnoreCase(value.getNodeName()))
							{
								attrs = value.getAttributes();
								try
								{
									val = Integer.parseInt(attrs.getNamedItem("value").getNodeValue());
									bonus = Double.parseDouble(attrs.getNamedItem("bonus").getNodeValue());
								}
								catch (Exception e)
								{
									_log.severe("[BaseStats] Invalid stats value: " + value.getNodeValue() + ", skipping");
									continue;
								}

								if ("STR".equalsIgnoreCase(statName))
								{
									STRbonus[val] = bonus;
								}
								else if ("INT".equalsIgnoreCase(statName))
								{
									INTbonus[val] = bonus;
								}
								else if ("DEX".equalsIgnoreCase(statName))
								{
									DEXbonus[val] = bonus;
								}
								else if ("WIT".equalsIgnoreCase(statName))
								{
									WITbonus[val] = bonus;
								}
								else if ("CON".equalsIgnoreCase(statName))
								{
									CONbonus[val] = bonus;
								}
								else if ("MEN".equalsIgnoreCase(statName))
								{
									MENbonus[val] = bonus;
								}
								else
								{
									_log.severe("[BaseStats] Invalid stats name: " + statName + ", skipping");
								}
							}
					}
				}
		}
		else
			throw new Error("[BaseStats] File not found: " + file.getName());
	}

	public static final BaseStats valueOfXml(String name)
	{
		name = name.intern();
		for (BaseStats s : values())
			if (s.getValue().equalsIgnoreCase(name))
				return s;

		throw new NoSuchElementException("Unknown name '" + name + "' for enum BaseStats");
	}

	private final BaseStat _stat;

	private BaseStats(BaseStat s)
	{
		_stat = s;
	}

	public final double calcBonus(L2Character actor)
	{
		if (actor != null)
			return _stat.calcBonus(actor);

		return 1;
	}

	public final String getValue()
	{
		return _stat.getClass().getSimpleName();
	}
}