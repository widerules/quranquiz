#!/bin/sh
cat QuranArabicNoTashkil.txt | sed 's/[0-9]*|[0-9]*|//g' > ayas.txt
cat ayas.txt | sed 's/ /\n/g' > words.txt

