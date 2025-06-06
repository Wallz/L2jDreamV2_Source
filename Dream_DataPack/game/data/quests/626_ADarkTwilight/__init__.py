# Made by disKret
# Rate Fix by Gnat
import sys
from com.dream import Config
from com.dream.game.model.quest import State
from com.dream.game.model.quest import QuestState
from com.dream.game.model.quest.jython import QuestJython as JQuest

qn = "626_ADarkTwilight"

#NPC
HIERARCH = 31517

#ITEMS
BLOOD_OF_SAINT = 7169

#Drop rate
DROP_CHANCE = 100

#REWARDS
ADENA = 57

class Quest (JQuest) :

 def __init__(self,id,name,descr): 
     JQuest.__init__(self,id,name,descr)
     self.questItemIds = [BLOOD_OF_SAINT]

 def onEvent (self,event,st) :
   htmltext = event
   count = st.getQuestItemsCount(BLOOD_OF_SAINT)
   if event == "31517-1.htm" :
     st.set("cond","1")
     st.setState(State.STARTED)
     st.playSound("ItemSound.quest_accept")
   elif event == "31517-3.htm" :
     if count < 300 :
        htmltext = "31517-3a.htm"
   elif event == "31517-4.htm" :
     if count < 300 :
        htmltext = "31517-3a.htm"
     else :
        st.addExpAndSp(162773,12500)
        st.takeItems(BLOOD_OF_SAINT,-1)
        st.playSound("ItemSound.quest_finish")
        st.exitQuest(1)
   elif event == "31517-5.htm" :
     if count < 300 :
        htmltext = "31517-3a.htm"
     else :
        st.rewardItems(ADENA,100000)
        st.takeItems(BLOOD_OF_SAINT,-1)
        st.playSound("ItemSound.quest_finish")
        st.exitQuest(1)
   return htmltext

 def onTalk (self,npc,player):
   htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
   st = player.getQuestState(qn)
   if st :
       npcId = npc.getNpcId()
       id = st.getState()
       cond = st.getInt("cond")
       if cond == 0 :
         if player.getLevel() >= 60 : # and player.getLevel() <= 71
           htmltext = "31517-0.htm"
         else:
           htmltext = "31517-0a.htm"
           st.exitQuest(1)
       elif id == State.STARTED :
           if st.getQuestItemsCount(BLOOD_OF_SAINT) == 300 :
             htmltext = "31517-2.htm"
           else :
             htmltext = "31517-2a.htm"
   return htmltext

 def onKill(self,npc,player,isPet):
   partyMember = self.getRandomPartyMember(player,"1")
   if not partyMember : return
   st = partyMember.getQuestState(qn)
   if st :
      if st.getState() == State.STARTED :
         count = st.getQuestItemsCount(BLOOD_OF_SAINT)
         if st.getInt("cond") == 1 and count < 300 :
            numItems, chance = divmod(DROP_CHANCE*Config.RATE_DROP_QUEST,100)
            if st.getRandom(100) < chance :
                numItems += 1
            if count + numItems >= 300 :
                numItems = 300 - count
            if numItems :
               st.giveItems(BLOOD_OF_SAINT,int(numItems))
               if st.getQuestItemsCount(BLOOD_OF_SAINT) == 300 :
                   st.set("cond","2")
                   st.playSound("ItemSound.quest_middle")
               else :
                   st.playSound("ItemSound.quest_itemget")
   return

QUEST       = Quest(626,qn,"A Dark Twilight")

QUEST.addStartNpc(31517)

QUEST.addTalkId(31517)

for mobs in range(21520,21541):
  QUEST.addKillId(mobs)