# Exploration of Giants Cave, part 1 version 0.1 
# by DrLecter
# Rate fix by Gnat
import sys
from com.dream import Config
from com.dream.game.model.quest import State
from com.dream.game.model.quest import QuestState
from com.dream.game.model.quest.jython import QuestJython as JQuest
#Quest info
QUEST_NUMBER,QUEST_NAME,QUEST_DESCRIPTION = 376,"GiantsExploration1","Exploration of Giants Cave, part 1"
qn = "376_GiantsExploration1"

#Variables
#Ancient parchment drop rate in %
DROP_CHANCE = 15
MAX = 100
#Mysterious Book drop rate in %
DROP_RATE_2 = 5
#By changing this setting you can make a group of recipes harder to get
RP_BALANCE = 50
#Changing this value to non-zero, will turn recipes to 100% instead of 60%
ALT_RP_100 = 0

#Quest items
ANCIENT_PARCHMENT = 5944
DICT1  = 5891
DICT2  = 5892 #Given as a proof for 2nd part
MST_BK = 5890

#Quest items vs rewards (recipes for low sealed armor parts, 60%)
EXCHANGE = [
 #collection items list,     rnd_1, rnd_2
[[5937,5938,5939,5940,5941], 5346, 5354], #medical theory, tallum_tunic,     tallum_hose
[[5932,5933,5934,5935,5936], 5332, 5334], #architecture,   drk_crstl_leather,tallum_leather
[[5922,5923,5924,5925,5926], 5416, 5418], #golem plans,    drk_crstl_breastp,tallum_plate
[[5927,5928,5929,5930,5931], 5424, 5340]  #basics of magic,drk_crstl_gaiters,dark_crystal_legg
]

#Messages
default   = "<html><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>"
error_1   = "<html><body>Head Researcher Sobling:<br>I think it is too early for you to help me. Come back after you have gained some more experience.<br><font color=\"LEVEL\">(Quest for characters level 51 and above.)</font></body></html>"
start     = "<html><body>Head Researcher Sobling:<br>As leader of the research team i'm looking for experienced adventurers to join us. Actually we're searching for relics from the ancient Giants Culture. There are four scripts we can't find yet: <font color=\"LEVEL\">Plans for the construction of Golems, Basis of the Giant's Magic, Construction Technology Handbook and Giant's Medical Theory.</font><br>Given the value of the items we're looking for, there is no need to tell you how generous shall be my rewards.<br><a action=\"bypass -h Quest 376_GiantsExploration1 yes\">I will search for ancient items</a><br><a action=\"bypass -h Quest 376_GiantsExploration1 0\">I won't help you</a></body></html>"
starting  = "Starting.htm"
checkout  = "<html><body>Head Researcher Sobling:<br>Excellent! You came back! Was it difficult to collect ancient pieces?<br>Let me see what you've found thus far...<br><a action=\"bypass -h Quest 376_GiantsExploration1 show\">Show him the books you collected</a><br><a action=\"bypass -h Quest 376_GiantsExploration1 myst\">Show him another items you've found</a></body></html>"
checkout2 = "<html><body>Head Researcher Sobling:<br>Excellent! You came back! Was it difficult to collect ancient pieces?<br>Hum... what is it? You have some ancient scrolls here, but those are of no use to me until you translate its contents, i have no time to do it on my own and that's why i gave you the dictionary. Anyway i can check any other ancient item you may have...<br><a action=\"bypass -h Quest 376_GiantsExploration1 show\">Show him the scrolls you collected</a><br><a action=\"bypass -h Quest 376_GiantsExploration1 myst\">Show him another items you've found</a></body></html>"
no_items  = "<html><body>Head Researcher Sobling:<br>Hum... I see no valuable items here, you should continue your research. I'm pretty sure you can do it better if you put more effort... what you think?<br><a action=\"bypass -h Quest 376_GiantsExploration1 Starting.htm\">I will continue</a><br><a action=\"bypass -h Quest 376_GiantsExploration1 0\">I will quit</a></body></html>"
tnx4items = "<html><body>Head Researcher Sobling:<br>Amazing! These are the sort of items i was looking for... Take this rare recipes as a proof of my gratitude. Anyhow, I'm sure there are more ancient relics guarded by those monsters, would you like to search some more?<br><a action=\"bypass -h Quest 376_GiantsExploration1 Starting.htm\">I will continue</a><br><a action=\"bypass -h Quest 376_GiantsExploration1 0\">I will quit</a></body></html>"
go_part2  = "<html><body>Head Researcher Sobling:<br>Interesting. What could this mysterious book be? I'm afraid we will not be able to figure out it's contents without assistance. But don't worry, i know who can help us, go now and talk with <font color=\"LEVEL\">Warehouse Freightman Cliff in Oren Castle Town</font>, show him the tome and he will probably know what to do with it.</body></html>"
no_part2  = "<html><body>Head Researcher Sobling:<br>I don't find anything useful here... I'm aware already about some discoveries related to giant's living, but there is no archeological value in vane letters or drawings you may find. As i told you, we are in desperate search for <font color=\"LEVEL\">Plans for the construction of Golems, Basis of the Giant's Magic, Construction Technology and Giant's Medical Theory.</font> You must bring any of those books complete. Few we can do only with fragments.</body></html>"
ok_part2  = "<html><body>Warehouse Freightman Cliff:<br>What is that book? Sobling told you to bring it to me? Well... It's written in a very ancient language.. yes.. Humm.. Take this \"Ancient Language Dictionary: Intermediate Level\" and meet <font color=\"LEVEL\">Sobling</font> again. With this he will be able to translate it. Leave now.</body></html>"
gogogo_2  = "<html><body>Head Researcher Sobling:<br>Are you still there with the book? There is no way to read it's contents with our current knowledge. Take the book to <font color=\"LEVEL\">Warehouse Freightman Cliff in Oren Castle Town</font>.</body></html>"
ext_msg   = "Quest aborted"

#NPCs
HR_SOBLING = 31147
WF_CLIFF   = 30182

#Mobs
MOBS = range(20647,20651)

class Quest (JQuest) :

 def __init__(self,id,name,descr): 
     JQuest.__init__(self,id,name,descr)
     self.questItemIds = [DICT1, MST_BK]

 def onEvent (self,event,st) :
    id = st.getState()
    htmltext = event
    if event == "yes" :
       htmltext = starting
       st.setState(State.STARTED)
       st.set("progress","PART1")
       st.set("cond","1")
       st.set("awaitBook","1")
       st.giveItems(DICT1,1)
       st.playSound("ItemSound.quest_accept")
    elif event == "0" :
       htmltext = ext_msg
       st.playSound("ItemSound.quest_finish")
       st.takeItems(DICT1,-1)
       st.takeItems(MST_BK,-1)
       st.exitQuest(1)
    elif event == "show" :
       htmltext = no_items
       for i in range(len(EXCHANGE)) :
           dec=2**len(EXCHANGE[i][0])
           for j in range(len(EXCHANGE[i][0])) :
               if st.getQuestItemsCount(EXCHANGE[i][0][j]) :
                  dec = dec >> 1
           if dec == 1 :
              htmltext = tnx4items
              for k in range(len(EXCHANGE[i][0])) :
                  st.takeItems(EXCHANGE[i][0][k], 1)
              if st.getRandom(100) < RP_BALANCE :
                 item = EXCHANGE[i][1]
              else :
                 item = EXCHANGE[i][2]
              if ALT_RP_100 != 0 : item += 1
              st.giveItems(item,1)
    elif event == "myst" :
       if st.getQuestItemsCount(MST_BK) :
          if id == State.STARTED and st.get("progress") == "PART1" :
             st.set("progress","PART2")
             st.set("cond","2")
             htmltext = go_part2
          elif id == State.STARTED and st.get("progress") == "PART2":
             htmltext = gogogo_2
       else :
           htmltext = no_part2
    return htmltext

 def onTalk (self,npc,player):
   htmltext = default
   st = player.getQuestState(qn)
   if not st : return htmltext

   npcId = npc.getNpcId()
   id = st.getState()
   if npcId == HR_SOBLING:
      if id == State.CREATED :
         st.set("cond","0")
         htmltext = start
         if player.getLevel() < 51 :
            st.exitQuest(1)
            htmltext = error_1
      elif id == State.STARTED :
         if st.getQuestItemsCount(ANCIENT_PARCHMENT) == 0 :
            htmltext = checkout
         else :
            htmltext = checkout2
   elif npcId == WF_CLIFF :
      if id == State.STARTED and st.getQuestItemsCount(MST_BK) and st.get("progress") == "PART2" :
            htmltext = ok_part2
            st.takeItems(MST_BK,1)
            st.giveItems(DICT2,1)
            st.set("cond","3")
            st.playSound("ItemSound.quest_middle")
   return htmltext

 def onKill(self,npc,player,isPet) :
     # a Mysterious Book may drop to any party member that still hasn't gotten it
     partyMember = self.getRandomPartyMember(player,"awaitBook","1")
     if partyMember :
        st = partyMember.getQuestState(qn)
        drop = st.getRandom(100)
        if drop < DROP_RATE_2  and not st.getQuestItemsCount(MST_BK):
           st.giveItems(MST_BK,1)
           st.unset("awaitBook")
           st.playSound("ItemSound.quest_middle")

     # In addition, drops go to one party member among those who are either in
     # progress PART1 or in PART2
     partyMember = self.getRandomPartyMemberState(player, State.STARTED)
     if not partyMember : return
     st = partyMember.getQuestState(qn)
     numItems, chance = divmod(DROP_CHANCE*Config.RATE_DROP_QUEST,100)
     if st.getRandom(100) < chance : 
        numItems += 1
     if numItems :
        st.giveItems(ANCIENT_PARCHMENT,int(numItems))
        st.playSound("ItemSound.quest_itemget")
     return

QUEST       = Quest(QUEST_NUMBER, str(QUEST_NUMBER)+"_"+QUEST_NAME, QUEST_DESCRIPTION)

QUEST.addStartNpc(HR_SOBLING)

QUEST.addTalkId(HR_SOBLING)
QUEST.addTalkId(WF_CLIFF)

for i in MOBS :
  QUEST.addKillId(i)