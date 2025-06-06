package com.dream.game.handler.chat;

import com.dream.game.handler.IChatHandler;
import com.dream.game.model.actor.instance.L2PcInstance;
import com.dream.game.network.SystemChatChannelId;
import com.dream.game.network.serverpackets.CreatureSay;

public class ChatAlliance implements IChatHandler
{
	private final SystemChatChannelId[] _chatTypes =
	{
		SystemChatChannelId.Chat_Alliance
	};

	@Override
	public SystemChatChannelId[] getChatTypes()
	{
		return _chatTypes;
	}

	@Override
	public void useChatHandler(L2PcInstance activeChar, String target, SystemChatChannelId chatType, String text)
	{
		if (activeChar.getClan() == null)
			return;

		activeChar.getClan().broadcastToOnlineAllyMembers(new CreatureSay(activeChar.getObjectId(), chatType, activeChar.getName(), text));
		activeChar.getClan().broadcastSnoopToOnlineAllyMembers(chatType.getId(), activeChar.getName(), text);
	}
}