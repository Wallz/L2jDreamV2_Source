#Seeker of the holy grail by TheOne
# Rate fix by Gnat
import sys
from com.dream import Config
from com.dream.game.model.quest import State
from com.dream.game.model.quest import QuestState
from com.dream.game.model.quest.jython import QuestJython as JQuest

qn = "638_SeekersOfTheHolyGrail"

DROP_CHANCE = 30

#NPC
INNOCENTIN = 31328

#MOBS
MOBS = range(22138,22175)

#ITEM
TOTEM = 8068

class Quest (JQuest) :

 def __init__(self,id,name,descr):
     JQuest.__init__(self,id,name,descr)
     self.questItemIds = [TOTEM]

 def onEvent (self, event, st) :
    htmltext = event
    if event == "31328-02.htm" :
      st.set("cond","1")
      st.setState(State.STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event == "31328-06.htm" :
      st.playSound("ItemSound.quest_finish")
      st.exitQuest(1)
    return htmltext

 def onTalk (self, npc, player) :
    htmltext = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
    st = player.getQuestState(qn)
    if not st : return htmltext

    level = player.getLevel()
    id = st.getState()
    if level >= 73 :
      if id == State.CREATED :
        htmltext = "31328-01.htm"
      elif id == State.STARTED and st.getQuestItemsCount(TOTEM) >= 2000 :
        rr = st.getRandom(3)
        if rr == 0 :
          st.takeItems(TOTEM,2000)
          st.giveItems(959,st.getRandom(4)+3)
          st.playSound("ItemSound.quest_middle")
        if rr == 1 :
          st.takeItems(TOTEM,2000)
          st.rewardItems(57,3576000)
          st.playSound("ItemSound.quest_middle")
        if rr == 2 :
          st.takeItems(TOTEM,2000)
          st.giveItems(960,st.getRandom(4)+3)
          st.playSound("ItemSound.quest_middle")
        htmltext = "31328-03.htm"
      else :
        htmltext = "31328-04.htm"
    else :
      htmltext = "31328-00.htm"
      st.exitQuest(1)
    return htmltext

 def onKill(self, npc, player, isPet) :
    partyMember = self.getRandomPartyMember(player,"1")
    if not partyMember: return
    st = partyMember.getQuestState(qn)
    if st :
      count = st.getQuestItemsCount(TOTEM)
      if st.getInt("cond") == 1 :
        numItems, chance = divmod(DROP_CHANCE*Config.RATE_DROP_QUEST,100)
        if st.getRandom(100) < chance : 
           numItems += 1
        if numItems :
           st.playSound("ItemSound.quest_itemget")   
           st.giveItems(TOTEM,int(numItems))       
    return

QUEST       = Quest(638,qn,"Seekers of the Holy Grail")

QUEST.addStartNpc(INNOCENTIN)

QUEST.addTalkId(INNOCENTIN)

for i in MOBS :
    QUEST.addKillId(i)