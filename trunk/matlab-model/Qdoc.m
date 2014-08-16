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

docNode = com.mathworks.xml.XMLUtils.createDocument('qdoc');
toc = docNode.getDocumentElement;
toc.setAttribute('version','0.01');
for i=1:words_cnt-2
    simlength = q.simn(i).cnt;
    if simlength >0
        simitem = docNode.createElement('simitem');
        simitem.setAttribute('simitem_id',num2str(simitem_id));
        simitem.setAttribute('simitem_len',num2str(simlength));
        simitem.appendChild(docNode.createTextNode(strjoin(q.txt_sym(i:i+simlength))));
        toc.appendChild(simitem);
        
        %simitem.appendChild(docNode.createComment(' Functions '));
        for occ = [i, q.simn(i).idx]
            curr_node = docNode.createElement('simoccur');
            curr_node.setAttribute('wrd_id',num2str(occ));
            curr_node.appendChild(docNode.createTextNode(strjoin(q.txt_sym(occ+simlength+1: occ+simlength+8))));
            simitem.appendChild(curr_node);
        end
        simitem_id = simitem_id+1;
        q.simn(occ).cnt = 0;
    end
    
    if(round(i/words_cnt*100)>last_p)
        display(num2str(round(i/words_cnt*100)));
        last_p = round(i/words_cnt*100);
    end
end
xmlwrite('info.xml',docNode);
