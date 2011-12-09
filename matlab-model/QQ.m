mtb_level = 2;   % Game Level:1 (no motashabehat),2 or 3 (motashabehat)
mtb_qLen  = 3;   % Length of question, words to display. (0 = a single word)

if exist('QWords.mat','file') ~=0
    display('Loading ...');
    load QWords.mat;
else
    display('Generating Database. This will not happen again');
    Qreader;
end
quit = 0;
Options = zeros(10,5); % Maximum 10 words, 5 options per each

while ~quit %% Loop to generate questions
    
    % Randomly get a starting word
    start=round(rand*77788+1); %TODO: Pseudo random with saved profile seed 
    
    %% Search for a correct neighbor start according to level
    dir=1; % search down = +1
    limitHit=1;
    while limitHit
        start_shadow = start;
        limitHit=0;
        srch_cond = 1;
        while srch_cond,
            start_shadow = start_shadow+dir;
            if start_shadow == 0 || start_shadow == 77788
                limitHit=1;
                dir = -dir;
                break;
            end
            if mtb_level==1, % Get a non-motashabehat near selected index
                % Motashabehat found,continue!
                srch_cond=q.sim2(start_shadow).cnt >1; %TODO: check: Is 0 better? 
                validCount = 1;% \
                mtb_qLen   = 3;% -|-> Default Constants for level-1
            else
                % Search for a motashabehat near selected index
                % Specify # Words to display
                disp2=0;disp3=0;   
                if(q.sim3(start_shadow).cnt <5 ...
                   && q.sim3(start_shadow).cnt >0),
                    disp3=q.sim3(start_shadow).cnt;
                end
                if(q.sim2(start_shadow).cnt <5 ...
                   && q.sim2(start_shadow).cnt >0),
                    disp2=q.sim2(start_shadow).cnt;
                end
                % Motashabehat not found,continue!            
                srch_cond = disp3==0  && disp2==0;
                if ~srch_cond,
                    [validCount,mtb_qLen] = max([disp2,disp3]);
                    validCount = validCount+1; % Count start-position as well
                end
            end 
        end

        start = start_shadow;       
    end

    %% fill Correct Option Words @indx=1 (2,3,..validCount for higher levels)
    Options(1,1)=start+mtb_qLen+1;
    for i=2:10,Options(i,1)=Options(i-1,1)+1;end
    if mtb_level>1,
        if mtb_qLen==1, % A 2-word Question
            Options(1,2:validCount) = q.sim2(start).idx;
        else  % A 3-word Question
            Options(1,2:validCount) = q.sim3(start).idx;       
        end
        for i=2:10,Options(i,2:validCount)=Options(i-1,2:validCount)+1;end
    end
    
    %% Get Valid Options @indx=2:5 (validCount+1:5 for higher levels)
    % Get the next words to similar-to-previous  
    % Get unique options
    for i=1:10
        last_correct = start+mtb_qLen+i-1;
        
        % We want to remove redundant correct choices from the given
        % options, this is made by removing subset sim2 from sim1
        sim1_not2 =setdiff(q.sim1(last_correct).idx,q.sim2(last_correct).idx);
        % '+1' means the next word
        [nill, options_uniq_idx, nill] = unique(q.txt(sim1_not2+1));
        options_uniq = sim1_not2(options_uniq_idx)+1;
        uniq_cnt= length(options_uniq);
        
        if uniq_cnt > 3,
            rnd_idx = randperm(uniq_cnt);
            Options(i,2:5) = options_uniq(rnd_idx(1:4));
        elseif uniq_cnt > 0,
            rnd_idx = randperm(uniq_cnt);
            Options(i,2:uniq_cnt+1) = options_uniq(rnd_idx);
            Options(i,(uniq_cnt+2):5) = round(rand(1,4-uniq_cnt)*77788+1);
        else % uniq_cnt=0, all random options!
            Options(i,2:5) = round(rand(1,4)*77788+1);
        end
        
    end
    
    %% Loop the game!
    correctOption=1;
    option=1;
    while correctOption && option<11
        %Display Question:
        clc; % Clear Screen
        display(q.txt(start:start+mtb_qLen+option-1));
        
        %Scramble options
        scrambled = randperm(5);
        correct_choice = find(scrambled<2); %idx=1
        
        %Display Options:
        display(['  [1] ',q.txt(Options(option,scrambled(1)))]);
        display(['  [2] ',q.txt(Options(option,scrambled(2)))]);
        display(['  [3] ',q.txt(Options(option,scrambled(3)))]);
        display(['  [4] ',q.txt(Options(option,scrambled(4)))]);
        display(['  [5] ',q.txt(Options(option,scrambled(5)))]);
        if mtb_level==2,
            display(['    -- ',num2str(validCount),' correct options left!']); % TODO: Subtract done options
        elseif mtb_level==3 && option==1,
            display('  [-] No more valid Motashabehat!');
        end
        display('[0] Quit!');
        
        %Get User selection
        choice = input('What is your choice? [0]: ');
        if isempty(choice) || choice==0,
            quit =1;
            correctOption=0;
        else
            % Check if wrong choice
            if correct_choice ~= choice
                correctOption = 0;
                %Display Correct answer
                display(q.txt(start:start+mtb_qLen+11));
                input('Press any key to continue .. ');
            end
        end
 
        option=option+1;
        if option == 11
            display('Correct answer, bravo!');
            system('sleep 1');
        end
    end
end    