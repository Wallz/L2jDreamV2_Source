# Made by Mr. - Version 0.3 by DrLecter
import sys
from com.dream.game.model.quest import State
from com.dream.game.model.quest import QuestState
from com.dream.game.model.quest.jython import QuestJython as JQuest

qn = "295_DreamsOfFlight"

FLOATING_STONE = 1492
RING_OF_FIREFLY = 1509
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): 
     JQuest.__init__(self,id,name,descr)
     self.questItemIds = [FLOATING_STONE]

 def onEvent (self,event,st) :
    htmltext = event
    if event == "30536-03.htm" :
      st.set("cond","1")
      st.setState(State.STARTED)
      st.playSound("ItemSound.quest_accept")
    return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if id == State.CREATED :
     st.set("cond","0")
   if st.getInt("cond")==0 :
     if player.getLevel() >= 11 :
       htmltext = "30536-02.htm"
     else:
       htmltext = "30536-01.htm"
       st.exitQuest(1)
   else:
     if st.getQuestItemsCount(FLOATING_STONE)<50 :
       htmltext = "30536-04.htm"
     else :
       if st.getQuestItemsCount(RING_OF_FIREFLY)==0 :
          htmltext = "30536-05.htm"
          st.giveItems(RING_OF_FIREFLY,1)
       else :
          htmltext = "30536-06.htm"
          st.rewardItems(ADENA,2400)
       st.addExpAndSp(120,60)
       st.playSound("ItemSound.quest_finish")
       st.takeItems(FLOATING_STONE,-1)
       st.exitQuest(1)
   return htmltext

 def onKill(self,npc,player,isPet):
   st = player.getQuestState(qn)
   if not st : return 
   if st.getState() != State.STARTED : return 
   
   count=st.getQuestItemsCount(FLOATING_STONE)
   if count < 50 :
     if st.getRandom(100) < 25 and count < 49 :
       st.giveItems(FLOATING_STONE,2)
     else:
       st.giveItems(FLOATING_STONE,1)
     if count == 49 :
         st.playSound("ItemSound.quest_middle")
         st.set("cond","2")
     else:
         st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(295,qn,"Dreams Of Flight")

QUEST.addStartNpc(30536)

QUEST.addTalkId(30536)

QUEST.addKillId(20153)