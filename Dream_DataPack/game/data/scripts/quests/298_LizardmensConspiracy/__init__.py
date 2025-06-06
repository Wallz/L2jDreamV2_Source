# Made by disKret
# Rate fix by Gnat
import sys
from com.dream import Config
from com.dream.game.model.quest import State
from com.dream.game.model.quest import QuestState
from com.dream.game.network.serverpackets import SocialAction
from com.dream.game.model.quest.jython import QuestJython as JQuest

qn = "298_LizardmensConspiracy"

PATROLS_REPORT = 7182
SHINING_GEM = 7183
SHINING_RED_GEM = 7184
DROP_CHANCE = 60

class Quest (JQuest) :

 def __init__(self,id,name,descr): 
     JQuest.__init__(self,id,name,descr)
     self.questItemIds = [SHINING_RED_GEM, SHINING_GEM]

 def onEvent (self,event,st) :
   htmltext = event
   if event == "30333-1a.htm" :
     st.set("cond","1")
     st.giveItems(PATROLS_REPORT,1)
     st.setState(State.STARTED)
     st.set("awaitGem","1")
     st.set("awaitRedGem","1")
     st.playSound("ItemSound.quest_accept")
   if event == "30344-1.htm" :
     st.takeItems(PATROLS_REPORT,1)
     st.set("cond","2")
   if event == "30344-3.htm" :
     if st.getQuestItemsCount(SHINING_RED_GEM) == st.getQuestItemsCount(SHINING_GEM) == 50 :
       st.takeItems(SHINING_GEM,-1)
       st.takeItems(SHINING_RED_GEM,-1)
       st.addExpAndSp(90000,42000)
       ObjectId=st.getPlayer().getObjectId()
       st.getPlayer().broadcastPacket(SocialAction(ObjectId,3))
       st.playSound("ItemSound.quest_finish")
       st.exitQuest(1)
     else :
       htmltext = "You don't have required items"
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if id == State.CREATED :
     st.set("cond","0")
   cond = st.getInt("cond")
   if npcId == 30333 and cond == 0  :
     if player.getLevel() >= 25 :
       htmltext = "30333-0a.htm"
     else:
       st.exitQuest(1)
   elif npcId == 30344 and id == State.STARTED:
     if cond == 1 :
       htmltext = "30344-0.htm"
     elif cond == 3 :
       htmltext = "30344-2.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   npcId = npc.getNpcId()
   if npcId in [20926,20927] :
     partyMember = self.getRandomPartyMember(player,"awaitRedGem","1")
     if not partyMember : return
     st = partyMember.getQuestState(qn)
     count = st.getQuestItemsCount(SHINING_RED_GEM)
     numItems, chance = divmod(DROP_CHANCE*Config.RATE_DROP_QUEST,100)
     if st.getRandom(100) < chance : 
         numItems += 1
     if count+numItems > 50 :
         numItems = 50 - count
     st.giveItems(SHINING_RED_GEM,int(numItems))
     if st.getQuestItemsCount(SHINING_GEM) == 50 and st.getQuestItemsCount(SHINING_RED_GEM) == 50 :
         st.playSound("ItemSound.quest_middle")
         st.unset("awaitRedGem")
         st.set("cond","3")
     else :
         st.playSound("ItemSound.quest_itemget")
   if npcId in [20922,20923,20924] :
     partyMember = self.getRandomPartyMember(player,"awaitGem","1")
     if not partyMember : return
     st = partyMember.getQuestState(qn)
     count = st.getQuestItemsCount(SHINING_GEM)
     numItems, chance = divmod(DROP_CHANCE*Config.RATE_DROP_QUEST,100)
     if st.getRandom(100) < chance : 
         numItems += 1
     if count+numItems > 50 :
         numItems = 50 - count
     st.giveItems(SHINING_GEM,int(numItems))
     if st.getQuestItemsCount(SHINING_GEM) == 50 and st.getQuestItemsCount(SHINING_RED_GEM) == 50 :
         st.playSound("ItemSound.quest_middle")
         st.unset("awaitRedGem")
         st.set("cond","3")
     else :
         st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(298,qn,"Lizardmen's Conspiracy")

QUEST.addStartNpc(30333)

QUEST.addTalkId(30333)
QUEST.addTalkId(30344)

for i in range(20922,20928) :
    QUEST.addKillId(i)