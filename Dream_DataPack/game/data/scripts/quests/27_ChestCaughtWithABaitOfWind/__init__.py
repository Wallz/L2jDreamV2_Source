# Made by DooMIta (ethernaly@email.it) and DrLecter.
# Visit http://www.l2jdp.com/trac if you find a bug and wish to report it.
# Visit http://forum.l2jdp.com for more details about our community and the project.
import sys
from com.dream.game.model.quest import State
from com.dream.game.model.quest import QuestState
from com.dream.game.model.quest.jython import QuestJython as JQuest

qn = "27_ChestCaughtWithABaitOfWind"

#NPC
LANOSCO = 31570
SHALING = 31434

#QUEST ITEMs and REWARD
BLUE_TREASURE_BOX    = 6500
STRANGE_BLUESPRINT   = 7625
BLACK_PEARL_RING     = 880

class Quest (JQuest) :

  def __init__(self,id,name,descr): JQuest.__init__(self,id,name,descr)
  
  def onEvent(self, event, st):
    htmltext = event
    if event== "31570-03.htm" :
      st.set("cond","1")
      st.setState(State.STARTED)
      st.playSound("ItemSound.quest_accept")
    elif event == "31570-05.htm" and st.getQuestItemsCount(BLUE_TREASURE_BOX) :
      htmltext="31570-06.htm"
      st.playSound("ItemSound.quest_middle")
      st.giveItems(STRANGE_BLUESPRINT,1)
      st.takeItems(BLUE_TREASURE_BOX,-1)
      st.set("cond","2")
    elif event == "31434-02.htm" and st.getQuestItemsCount(BLACK_PEARL_RING) :
      htmltext = "31434-01.htm"
      st.playSound("ItemSound.quest_finish")
      st.giveItems(BLACK_PEARL_RING,1)
      st.takeItems(STRANGE_BLUESPRINT,-1)
      st.unset("cond")
      st.exitQuest(False)
    return htmltext

  def onTalk(self, npc, player):
    htmltext="<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
    st = player.getQuestState(qn)
    if not st : return htmltext
    npcId=npc.getNpcId()
    id = st.getState()
    if id == State.CREATED :
      req = player.getQuestState("50_LanoscosSpecialBait")
      if req : reqst = req.getState()
      if player.getLevel() >= 27 and req and reqst == State.COMPLETED :
        htmltext = "31570-01.htm"
      else :
        st.exitQuest(1)
        htmltext = "31570-02.htm"
    elif id == State.STARTED :
      cond = st.getInt("cond")
      if npcId == LANOSCO :
        if cond == 1 :
          if st.getQuestItemsCount(BLUE_TREASURE_BOX) :
            htmltext = "31570-03.htm"
          else :
            htmltext = "31570-04.htm"
        else :
          htmltext = "31570-05.htm"
      else :
        htmltext = "31434-00.htm"
    elif id == State.COMPLETED :
      htmltext = "<html><body>This quest has already been completed.</body></html>"
    return htmltext

QUEST=Quest(27,qn,"Chest Caught With A Bait Of Wind")

QUEST.addStartNpc(LANOSCO)

QUEST.addTalkId(LANOSCO)
QUEST.addTalkId(SHALING)