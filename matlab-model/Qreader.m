% Parses the Quran Words ..

words_cnt = 77878;
txt_sym=2;
fid=fopen('quran-uthmani-min.nosym.txt');
fid2=fopen('quran-uthmani-min.sym.txt');


display('Loading Quran txt ..');
for i=1:words_cnt
    q.txt(i) = {fgetl(fid)};
    q.txt_sym(i) = {fgetl(fid2)};
end
fclose(fid);
fclose(fid2);

display('Parsing .. (%)');
last_p = -1;
for i=1:words_cnt-2
    tmp = find(strcmp(q.txt(:),q.txt(i))>0);
    q.sim1(i).cnt = length(tmp);
    q.sim1(i).idx = tmp(tmp~=i);

    q.sim2(i).idx = q.sim1(i).idx(strcmp(q.txt(q.sim1(i).idx(q.sim1(i).idx<words_cnt)+1)    ,q.txt(i+1))>0);
    q.sim2(i).cnt = length(q.sim2(i).idx);
    q.sim3(i).idx = q.sim2(i).idx(strcmp(q.txt(q.sim2(i).idx(q.sim2(i).idx<(words_cnt-1))+2),q.txt(i+2))>0);
    q.sim3(i).cnt = length(q.sim3(i).idx);
    
    %Longer patterns are applicable!
    if(round(i/words_cnt*100)>last_p)
        display(num2str(round(i/words_cnt*100)));
        last_p = round(i/words_cnt*100);
    end
end
% tailed loops
    tmp = find(strcmp(q.txt(:),q.txt(words_cnt-1))>0);
    q.sim1(words_cnt-1).cnt = length(tmp);
    q.sim1(words_cnt-1).idx = tmp(tmp~=words_cnt-1);
    q.sim2(words_cnt-1).idx = q.sim1(words_cnt-1).idx(strcmp(q.txt(q.sim1(words_cnt-1).idx(q.sim1(words_cnt-1).idx<words_cnt)+1)    ,q.txt(words_cnt-1+1))>0);
    q.sim2(words_cnt-1).cnt = length(q.sim2(words_cnt-1).idx);
    
    tmp = find(strcmp(q.txt(:),q.txt(words_cnt))>0);
    q.sim1(words_cnt).cnt = length(tmp);
    q.sim1(words_cnt).idx = tmp(tmp~=words_cnt);
    
    
save QWords.mat q;

 fido=fopen('idx-data.csv','w');
 
 if(txt_sym==0)
    for i=1:words_cnt-2
        fprintf(fido,'%d,%s,%d,%d,%d\n', i, char(q.txt(i)),q.sim1(i).cnt,q.sim2(i).cnt,q.sim3(i).cnt);
    end
    fprintf(fido,'%d,%s,%d,%d,0\n', words_cnt-1, char(q.txt(words_cnt-1)),q.sim1(words_cnt-1).cnt,q.sim2(words_cnt-1).cnt);
    fprintf(fido,'%d,%s,%d,0,0\n', words_cnt, char(q.txt(words_cnt)),q.sim1(words_cnt).cnt);
 elseif(txt_sym==1)
    for i=1:words_cnt-2
        fprintf(fido,'%d,%s,%d,%d,%d\n', i, char(q.txt_sym(i)),q.sim1(i).cnt,q.sim2(i).cnt,q.sim3(i).cnt);
    end
    fprintf(fido,'%d,%s,%d,%d,0\n', words_cnt-1, char(q.txt_sym(words_cnt-1)),q.sim1(words_cnt-1).cnt,q.sim2(words_cnt-1).cnt);
    fprintf(fido,'%d,%s,%d,0,0\n', words_cnt, char(q.txt_sym(words_cnt)),q.sim1(words_cnt).cnt);
 else %Print both sym and noSym
    for i=1:words_cnt-2
        fprintf(fido,'%d,%s,%s,%d,%d,%d\n', i, char(q.txt(i)),char(q.txt_sym(i)),q.sim1(i).cnt,q.sim2(i).cnt,q.sim3(i).cnt);
    end
    fprintf(fido,'%d,%s,%s,%d,%d,0\n', words_cnt-1, char(q.txt(words_cnt-1)),char(q.txt_sym(words_cnt-1)),q.sim1(words_cnt-1).cnt,q.sim2(words_cnt-1).cnt);
    fprintf(fido,'%d,%s,%s,%d,0,0\n', words_cnt, char(q.txt(words_cnt)),char(q.txt_sym(words_cnt)),q.sim1(words_cnt).cnt);     
 end
 
 fclose(fido);
display('Done!');
