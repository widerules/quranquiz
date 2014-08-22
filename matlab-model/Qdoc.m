if exist('QWords.mat','file') ~=0
    display('Loading ...');
    load QWords.mat;
else
    display('Generating Database. This will not happen again');
    Qreader;
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
            fprintf(fid,'     <simoccur wrd_id="%d">%s</simoccur>\n',occ,char(strjoin(q.txt_sym(occ+simlength: occ+simlength+8))));
        end
        fprintf(fid,'   </simitem>\n');
        simitem_id = simitem_id+1;
        q.simn(occ).cnt = 0;
    end
    
    if(round(i/words_cnt*100)>last_p)
        display(num2str(round(i/words_cnt*100)));
        last_p = round(i/words_cnt*100);
    end
end
fprintf(fid,'</qdoc>\n');
fclose(fid);

