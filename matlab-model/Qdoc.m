function  Qdoc(q)
if nargin == 0
    if exist('QWords.mat','file') ~=0
        display('Loading ...');
        load QWords.mat;
    else
        display('Generating Database. This will not happen again');
        Qreader;
    end
end

words_cnt = 77878;
last_p = -1; 
simitem_id = 1;

fid=fopen('qsimdoc.xml','w');
fprintf(fid,'<?xml version="1.0" encoding="utf-8"?>\n');
fprintf(fid,'<qdoc version="0.01">\n');
fprintf(fid,'<!-- This is an auto-generated file. -->\n');
fprintf(fid,'<!-- Check: https://code.google.com/p/quranquiz/source/browse/trunk/matlab-model -->\n');

for i=1:words_cnt-2
    simlength = q.simn(i).cnt;
    if simlength >0
        fprintf(fid,'   <simitem simitem_id="%d" simitem_len="%d" simitem_txt="%s">\n',simitem_id,simlength,char(strjoin(q.txt_sym(i:i-1+simlength))));
        for occ = [i, q.simn(i).idx]
            fprintf(fid,'     <simoccur sura_id="%d" aya_id="%d">%s</simoccur>\n',idx2sura(occ),idx2aya(occ,q),char(strjoin(q.txt_sym(occ+simlength: occ+simlength+8))));
            q.simn(occ).cnt = 0;
        end
        fprintf(fid,'   </simitem>\n');
        simitem_id = simitem_id+1;
    end
    
    if(round(i/words_cnt*100)>last_p)
        display(num2str(round(i/words_cnt*100)));
        last_p = round(i/words_cnt*100);
    end
end
fprintf(fid,'</qdoc>\n');
fclose(fid);
end

function a = idx2aya(i,q)
while isempty(str2num(cell2mat(q.aya(i))))
    i=i+1;
end
a = str2double(cell2mat(q.aya(i)));
end

function a = idx2sura(i)
ss=[30,6150,9635,13386,16194,19248,22572,23809,26307,28144,30065,31846,32703,...
    33537,34196,36044,37604,39187,40152,41491,42664,43942,44996,46316,47213,...
    48535,49690,51124,52104,52925,53475,53851,55142,56029,56808,57537,58402,...
    59139,60315,61538,62336,63200,64034,64384,64876,65523,66066,66630,66981,...
    67358,67722,68038,68402,68748,69103,69486,70064,70540,70989,71341,71566,...
    71745,71929,72174,72465,72718,73055,73359,73621,73842,74072,74361,74564,...
    74823,74991,75238,75423,75600,75783,75920,76028,76112,76285,76396,76509,...
    76574,76650,76746,76887,76973,77031,77106,77150,77181,77219,77295,77329,...
    77427,77467,77511,77551,77583,77601,77638,77665,77686,77715,77729,77759,...
    77782,77809,77828,77855,77878];
s=1;
while i >= ss(s)
    s=s+1;
end
a=s;
end

