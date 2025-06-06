# Made by Emperorc
import sys
from com.dream.game.model.quest import State
from com.dream.game.model.quest import QuestState
from com.dream.game.model.quest.jython import QuestJython as JQuest

qn = "20_BringUpWithLove"

#NPCs
TUNATUN = 31537

#ITEMS
GEM = 7185

#NOTE: This quest requires the giving of item GEM upon successful growth and taming of a wild beast, so the rewarding of
# the gem is handled by the feedable_beasts ai script.

class Quest (JQuest) :

 def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)

 def onEvent (self,event,st) :
   htmltext = event
   if event == "31537-09.htm" :
     st.set("cond","1")
     st.setState(State.STARTED)
     st.playSound("ItemSound.quest_accept")
   elif event == "31537-12.htm" :
       st.rewardItems(57,68500)
       st.takeItems(GEM,-1)
       st.playSound("ItemSound.quest_finish")
       st.exitQuest(False)
       st.set("onlyone","1")
   return htmltext

 def onTalk (self,npc,player):
   st = player.getQuestState(qn)
   htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
   if not st: return htmltext
   npcId = npc.getNpcId()
   id = st.getState()
   cond = st.getInt("cond")
   onlyone = st.getInt("onlyone")
   GEM_COUNT = st.getQuestItemsCount(GEM)
   if id == State.COMPLETED :
       htmltext = "<html><body>This quest has already been completed.</body></html>"
   elif id == State.CREATED and onlyone == 0 :
     if player.getLevel() >= 65 :
         htmltext = "31537-01.htm"
     else:
         htmltext = "31537-02.htm"
         st.exitQuest(1)
   elif id == State.STARTED :
       if GEM_COUNT < 1 :
           htmltext = "31537-10.htm"
       else :
           htmltext = "31537-11.htm"
   return htmltext

QUEST     = Quest(20,qn,"Bring up with Love")

QUEST.addStartNpc(TUNATUN)

QUEST.addTalkId(TUNATUN)