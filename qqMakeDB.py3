#!/usr/bin/python3
#
# Creates a table holding quran text statistics as columns:
#  0    1     2         3     4     5     6     7
# _id  txt  txt_nosym  sim1  sim2  sim3  aya  [simn]
#
import string
import unicodedata
# Open a file
fi = open("quran-uthmani-min.txt", "r")
fo = open("quran-qq.csv", "w")

# Define: Fat7a-Damma-Kasra either single or double,
#         shadda and sokoon
HARAKAT = "\u064B\u064C\u064D\u064E\u064F\u0650\u0651\u0652\u06E6\u0670"
#Define: Optional Hamza, as in: al2akhera, anbia2. The first causes char disconnection
#HAMZA=$(python -c 'print u"\u0621".encode("utf8")')
SMALLALEF = "\u0670"
LAMHAMZAALEF = "\u0644\u0621\u0627"
LAMALEFMAD = "\u0644\u0622"
QLEN = 77877

C=7
db = [[] for i in range(C)]
"""
for k in range(77878):
  for i in range(C):
    db[i].append(0)
"""

id_=0
cr_sura = 1

with fi as myfile:
  for line in myfile:
    tokens = line.split('|');
    try:
      lst_sura = cr_sura
      cr_sura= int(tokens[0]);
      cr_aya = int(tokens[1]);
      myAya = tokens[2];
      myAya = myAya[:len(myAya)-2]
    except ValueError:
      break;

    for item in myAya.split(' '):
      db[0].append(id_+1)
      db[1].append(item)
      db[2].append(''.join(c for c in item if not c in HARAKAT))
      db[6].append("null")
      id_+=1
    db[6][id_-1] = cr_aya

# Search for sim{1,2,3}
print('Doing the textual analysis, it will take a while:')
for i in range(id_-3):
  sim1=0
  sim2=0
  sim3=0
  for j in range(id_-1):
    if i==j:  continue;
    if db[2][i] == db[2][j]:
      sim1 += 1
      if db[2][i+1] == db[2][j+1]:
        sim2 += 1
        if db[2][i+2] == db[2][j+2]:
          sim3 += 1
  db[3].append(sim1)
  db[4].append(sim2)
  db[5].append(sim3)
  if i%1000 == 0:
    print('.'),

#boundary unrolled loops
sim1=0
sim2=0
sim3=0
i=id_-2
for j in range(id_-3):
  if i==j:  continue;
  if db[2][i] == db[2][j]:
    sim1 += 1
    if db[2][i+1] == db[2][j+1]:
      sim2 += 1
db[3].append(sim1)
db[4].append(sim2)
db[5].append(sim3)

sim1=0
sim2=0
sim3=0
i=id_-1
for j in range(id_-2):
  if i==j:  continue;
  if db[2][i] == db[2][j]:
    sim1 += 1
db[3].append(sim1)
db[4].append(sim2)
db[5].append(sim3)

# Dump into CSV file
for i in range(id_):
  fo.write(str(db[0][i]))
  fo.write(" , ")
  fo.write(db[1][i])
  fo.write(" , ")
  fo.write(db[2][i])
  fo.write(" , ")
  fo.write(str(db[3][i]))
  fo.write(" , ")
  fo.write(str(db[4][i]))
  fo.write(" , ")
  fo.write(str(db[5][i]))
  fo.write(" , ")
  fo.write(db[6][i])
  fo.write("\n")

"""
for i in range(3):
  print(db[0][i]," , ",db[1][i]," , ",db[2][i]," , ",db[3][i]," , ",db[4][i]," , ",db[5][i]," , ",db[6][i])
"""

# Close opened file
fo.close()
fi.close()