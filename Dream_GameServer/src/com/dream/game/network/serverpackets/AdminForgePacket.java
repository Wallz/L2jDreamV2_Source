package com.dream.game.network.serverpackets;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class AdminForgePacket extends L2GameServerPacket
{
	private class Part
	{
		public byte b;
		public String str;

		public Part(byte bb, String string)
		{
			b = bb;
			str = string;
		}
	}

	private final List<Part> _parts = new ArrayList<>();

	public AdminForgePacket()
	{
	}

	public void addPart(byte b, String string)
	{
		_parts.add(new Part(b, string));
	}

	public boolean generate(byte b, String string)
	{
		if (b == 'C' || b == 'c')
		{
			writeC(Integer.decode(string));
			return true;
		}
		else if (b == 'D' || b == 'd')
		{
			writeD(Integer.decode(string));
			return true;
		}
		else if (b == 'H' || b == 'h')
		{
			writeH(Integer.decode(string));
			return true;
		}
		else if (b == 'F' || b == 'f')
		{
			writeF(Double.parseDouble(string));
			return true;
		}
		else if (b == 'S' || b == 's')
		{
			writeS(string);
			return true;
		}
		else if (b == 'B' || b == 'b' || b == 'X' || b == 'x')
		{
			writeB(new BigInteger(string).toByteArray());
			return true;
		}
		return false;
	}

	@Override
	protected void writeImpl()
	{
		for (Part p : _parts)
		{
			generate(p.b, p.str);
		}

	}

}