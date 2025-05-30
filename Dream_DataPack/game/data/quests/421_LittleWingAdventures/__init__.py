# Upgrade your Hatchling to Strider version 0.3
# by DrLecter & DraX_
# last modify by Kerberos
import sys
from java.sql import PreparedStatement
from java.sql import ResultSet
from java.sql import SQLException
from com.dream import L2DatabaseFactory
from com.dream.game.datatables.xml import SkillTable
from com.dream.game.model.quest import State
from com.dream.game.model.quest import QuestState
from com.dream.game.model.quest.jython import QuestJython as JQuest
from com.dream.game.network import SystemMessageId
from com.dream.game.network.serverpackets import ItemList
from com.dream.game.network.serverpackets import NpcSay
from com.dream.game.network.serverpackets import SystemMessage
from com.dream.tools.random import Rnd


qn = "421_LittleWingAdventures"

#Quest items
FT_LEAF = 4325

#NPCs
CRONOS = 30610
MIMYU  = 30747
GHOST  = 27189
MOBS = [27189]

#kinda bugged, missing refresh, works only when player relog so far
def EvolvePet(player,item,striderControlItem) :
   con = L2DatabaseFactory.getInstance().getConnection()
   statement = con.prepareStatement("UPDATE items SET item_id =? WHERE object_id=? AND owner_id=?")
   statement.setInt(1, striderControlItem)
   statement.setInt(2, item.getObjectId())
   statement.setInt(3, player.getObjectId())
   statement.execute()
   statement.close()
   con.close()
   sm1 = SystemMessage(SystemMessageId.S2_S1_DISAPPEARED)
   sm1.addItemName(item.getItemId())
   sm1.addNumber(1)
   sm2 = SystemMessage(SystemMessageId.YOU_PICKED_UP_A_S1_S2)
   sm2.addNumber(item.getEnchantLevel())
   sm2.addItemName(striderControlItem)
   player.sendPacket(sm1)
   player.sendPacket(sm2)
   player.sendPacket(ItemList(player, False))
   return

class Quest (JQuest) :

 def __init__(self,id,name,descr):
   JQuest.__init__(self,id,name,descr)
   self.questItemIds = [FT_LEAF]
   self.killedTrees = []

 def onAdvEvent (self,event,npc,player):
   htmltext = event
   st = player.getQuestState(qn)
   if not st: return
   if event == "30610-05.htm" :
      if ((st.getQuestItemsCount(3500) + st.getQuestItemsCount(3501) + st.getQuestItemsCount(3502)) == 1) :
         if st.getQuestItemsCount(3500) == 1 :
            item = player.getInventory().getItemByItemId(3500)
            if item.getEnchantLevel() < 55 :
               st.exitQuest(1)
               htmltext = "30610-06.htm"
            else :
               st.setState(State.STARTED)
               st.set("summonOid",str(item.getObjectId()))
               st.set("cond","1")
               st.set("id","1")
               st.playSound("ItemSound.quest_accept")
         elif st.getQuestItemsCount(3501) == 1 :
            item = player.getInventory().getItemByItemId(3501)
            if item.getEnchantLevel() < 55 :
               st.exitQuest(1)
               htmltext = "30610-06.htm"
            else :
               st.setState(State.STARTED)
               st.set("summonOid",str(item.getObjectId()))
               st.set("cond","1")
               st.set("id","1")
               st.playSound("ItemSound.quest_accept")
         elif st.getQuestItemsCount(3502) == 1 :
            item = player.getInventory().getItemByItemId(3502)
            if item.getEnchantLevel() < 55 :
               st.exitQuest(1)
               htmltext = "30610-06.htm"
            else :
               st.setState(State.STARTED)
               st.set("summonOid",str(item.getObjectId()))
               st.set("cond","1")
               st.set("id","1")
               st.playSound("ItemSound.quest_accept")
      else :
         st.exitQuest(1)
         htmltext = "30610-06.htm"
   elif event == "30747-02.htm" :
      summon = player.getPet()
      if summon :
         if summon.getControlItem().getObjectId() == st.getInt("summonOid"):
            htmltext = "30747-04.htm"
         else :
            htmltext = "30747-03.htm"
   elif event == "30747-05.htm" :
      summon = player.getPet()
      if summon :
         if summon.getControlItem().getObjectId() == st.getInt("summonOid"):
            htmltext = "30747-05.htm"
            st.giveItems(FT_LEAF,4)
            st.set("cond","2")
            st.set("id","0")
            st.playSound("ItemSound.quest_middle")
         else :
            htmltext = "30747-06.htm"
      else :
         htmltext = "30747-06.htm"
   elif event == "Mob_2 Timer 1" :
      st.set("bspawned","0") 
      return	  
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>" 
   st = player.getQuestState(qn)
   if not st: return htmltext
   id = st.getState()
   cond = st.getInt("cond")
   npcId = npc.getNpcId()

   if id == State.CREATED and npcId == CRONOS :
      if player.getLevel() < 45 and (st.getQuestItemsCount(3500) or st.getQuestItemsCount(3501) or st.getQuestItemsCount(3502)) :
         st.exitQuest(1)
         htmltext = "30610-01.htm"
      elif player.getLevel() >= 45 and ((st.getQuestItemsCount(3500) + st.getQuestItemsCount(3501) + st.getQuestItemsCount(3502)) >= 2) :
         st.exitQuest(1)
         htmltext = "30610-02.htm"
      elif player.getLevel() >= 45 and ((st.getQuestItemsCount(3500) + st.getQuestItemsCount(3501) + st.getQuestItemsCount(3502)) == 1) :
         if st.getQuestItemsCount(3500) == 1 :
            item = player.getInventory().getItemByItemId(3500)
            if item.getEnchantLevel() < 55 :
               htmltext = "30610-03.htm"
            else :
               htmltext = "30610-04.htm"
         elif st.getQuestItemsCount(3501) == 1 :
            item = player.getInventory().getItemByItemId(3501)
            if item.getEnchantLevel() < 55 :
               htmltext = "30610-03.htm"
            else :
               htmltext = "30610-04.htm"
         elif st.getQuestItemsCount(3502) == 1 :
            item = player.getInventory().getItemByItemId(3502)
            if item.getEnchantLevel() < 55 :
               htmltext = "30610-03.htm"
            else :
               htmltext = "30610-04.htm"
   elif id == State.STARTED :
      if npcId == CRONOS :
         htmltext = "30610-07.htm"
      if npcId == MIMYU :
         if st.getInt("id") == 1 :
            st.set("id","2")
            htmltext = "30747-01.htm"
         elif st.getInt("id") == 2 :
            summon = player.getPet()
            if summon :
               if summon.getControlItem().getObjectId() == st.getInt("summonOid"):
                  htmltext = "30747-04.htm"
               else :
                  htmltext = "30747-03.htm"
            else :
               htmltext = "30747-02.htm"
         elif st.getInt("id") == 0 :
            htmltext = "30747-07.htm"
         elif st.getInt("id") > 0 and st.getInt("id") < 15 and st.getQuestItemsCount(FT_LEAF) >= 1 :
            htmltext = "30747-11.htm"
         elif st.getInt("id") == 15 and st.getQuestItemsCount(FT_LEAF) == 0 :
            summon = player.getPet()
            if summon :
               if summon.getControlItem().getObjectId() == st.getInt("summonOid"):
                  st.set("id","16")
                  htmltext = "30747-13.htm"
               else :
                  htmltext = "30747-14.htm"
            else :
               htmltext = "30747-12.htm"
         elif st.getInt("id") == 16:
            summon = player.getPet()
            if summon :
               htmltext = "30747-15.htm"
            elif (st.getQuestItemsCount(3500) + st.getQuestItemsCount(3501) + st.getQuestItemsCount(3502)) == 1 :
               if st.getQuestItemsCount(3500) == 1 :
                  item = player.getInventory().getItemByItemId(3500)
                  if item.getObjectId() == st.getInt("summonOid"):
                     #EvolvePet(player,item,4422)
                     st.takeItems(3500,1)
                     st.giveItems(4422,1)
                     htmltext = "30747-16.htm"
                     st.exitQuest(1)
                     st.playSound("ItemSound.quest_finish")
                  else :
                     npc.setTarget(player)
                     skill = SkillTable.getInstance().getInfo(4167,1)
                     if skill != None:
                        skill.getEffects(npc, player)
                     htmltext = "30747-18.htm"
               elif st.getQuestItemsCount(3501) == 1 :
                  item = player.getInventory().getItemByItemId(3501)
                  if item.getObjectId() == st.getInt("summonOid"):
                     #EvolvePet(player,item,4423)
                     st.takeItems(3501,1)
                     st.giveItems(4423,1)
                     htmltext = "30747-16.htm"
                     st.exitQuest(1)
                     st.playSound("ItemSound.quest_finish")
                  else :
                     npc.setTarget(player)
                     skill = SkillTable.getInstance().getInfo(4167,1)
                     if skill != None:
                        skill.getEffects(npc, player)
                     htmltext = "30747-18.htm"
               elif st.getQuestItemsCount(3502) == 1 :
                  item = player.getInventory().getItemByItemId(3502)
                  if item.getObjectId() == st.getInt("summonOid"):
                     #EvolvePet(player,item,4424)
                     st.takeItems(3502,1)
                     st.giveItems(4424,1)
                     htmltext = "30747-16.htm"
                     st.exitQuest(1)
                     st.playSound("ItemSound.quest_finish")
                  else :
                     npc.setTarget(player)
                     skill = SkillTable.getInstance().getInfo(4167,1)
                     if skill != None:
                        skill.getEffects(npc, player)
                     htmltext = "30747-18.htm"
               else :
                  htmltext = "30747-18.htm"
            elif (st.getQuestItemsCount(3500) + st.getQuestItemsCount(3501) + st.getQuestItemsCount(3502)) >= 2 :
               htmltext = "30747-17.htm"
   return htmltext


 def onAttack(self, npc, player, damage, isPet, skill) :
   st = player.getQuestState(qn)
   if not st:
     return
   npcId = npc.getNpcId()
   maxHp = npc.getMaxHp()
   nowHp = npc.getStatus().getCurrentHp()   
   for pc, mobId, in self.killedTrees:
      if pc == player and mobId == npcId:
         return
   if isPet and st.getInt("id") < 16 and st.getInt("bspawned") == 0 and nowHp > maxHp*0.1 and nowHp < maxHp*1 :
      pet = player.getPet()
      for i in range(1) :
          Ghost = st.addSpawn(GHOST,npc.getX() + Rnd.get(-150, 150), npc.getY() + Rnd.get(-150, 150), npc.getZ(), 0,)	  
          GhostId = Ghost.getObjectId()	  
          Ghost.broadcastPacket(NpcSay(Ghost.getObjectId(),0,Ghost.getNpcId(),"Brothers, come down from sacred tree."))
      for i in range(1) :
          Ghost1 = st.addSpawn(GHOST,npc.getX() + Rnd.get(-150, 150), npc.getY() + Rnd.get(-150, 150), npc.getZ(), 0,)	  
          GhostId = Ghost.getObjectId()	  
          Ghost.broadcastPacket(NpcSay(Ghost.getObjectId(),0,Ghost.getNpcId(),"We must protect the fairy tree!"))
      for i in range(1) :
          Ghost2 = st.addSpawn(GHOST,npc.getX() + Rnd.get(-150, 150), npc.getY() + Rnd.get(-150, 150), npc.getZ(), 0,)	  
          GhostId = Ghost.getObjectId()	  
          Ghost.broadcastPacket(NpcSay(Ghost.getObjectId(),0,Ghost.getNpcId(),"We must protect the fairy tree!"))		  
          st.set("bspawned","1")	  		 	   		  
   elif st.getRandom(100) <= 2 and st.getQuestItemsCount(FT_LEAF) >= 0 and st.getInt("bspawned") >= 0 :
      st.takeItems(FT_LEAF,1)
      st.playSound("ItemSound.quest_middle")
      npc.broadcastPacket(NpcSay(npc.getNpcId(),0,npcId,"gives me spirit leaf...!"))
      self.killedTrees.append([player,npcId])	  
      st.set("bspawned","0")		 		 
      st.set("kills","0")		 		 		 		 
      if st.getQuestItemsCount(FT_LEAF) == 0 :
         st.set("id","15")
         st.set("cond","3")		 
   return 

 def onKill(self,npc,player,isPet):
    npcId = npc.getNpcId()
    st = player.getQuestState(qn)
    if st :
        cond = st.getInt("cond")
        spawn = st.getInt("bspawned")
        if cond == 2 and spawn == 1 :
            if npcId == GHOST :
                if st.getInt("kills") < 2 :
                    st.set("kills",str(st.getInt("kills")+1))
                else :
                    st.set("kills","0")				
                    st.startQuestTimer("Mob_2 Timer 1",30000,npc)
    return
							

QUEST       = Quest(421,qn,"Little Wing's Big Adventures")

QUEST.addStartNpc(CRONOS)

QUEST.addTalkId(CRONOS)
QUEST.addTalkId(MIMYU)

for mob in MOBS :
    QUEST.addKillId(mob)

for i in range(27185,27189):
   QUEST.addAttackId(i)