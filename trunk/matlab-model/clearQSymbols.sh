#!/bin/bash
#
# This scripts removes all unneeded arabic tashkeel from a quranic text
# Check Unicode list: http://jrgraphix.net/r/Unicode/0600-06FF
#
# Copy Quran only, without footer: 
head -6236 quran-uthmani-min.txt | sed 's/ /\n/g' > quran-uthmani-min.sym.txt

# Define: Fat7a-Damma-Kasra either single or double,
#         shadda and sokoon 
HARAKAT=$(python -c 'print u"\u064B\u064C\u064D\u064E\u064F\u0650\u0651\u0652\u06E6".encode("utf8")')

#Define: Optional Hamza, as in: al2akhera, anbia2. The first causes char disconnection
#HAMZA=$(python -c 'print u"\u0621".encode("utf8")')
#SMALLALEF=$(python -c 'print u"\u0670".encode("utf8")')
sed 's/['"$HARAKAT"'|'"$SMALLALEF"']//g' < quran-uthmani-min.sym.txt > quran-uthmani-min.t2.txt

#Replace hamza with a LamAlef with LamAlef-mad
LAMHAMZAALEF=$(python -c 'print u"\u0644\u0621\u0627".encode("utf8")')
LAMALEFMAD=$(python -c 'print u"\u0644\u0622".encode("utf8")')
sed 's/'"$LAMHAMZAALEF"'/'"$LAMALEFMAD"'/g' < quran-uthmani-min.t2.txt > quran-uthmani-min.t3.txt

#File cleanup
mv  quran-uthmani-min.t3.txt quran-uthmani-min.nosym.txt 
rm -f quran-uthmani-min.t*.txt

echo "Run matlab to parse the quran words!" 

