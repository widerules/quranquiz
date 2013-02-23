clear;clc
max=30;
x=[0:1:1.2*max];
yy=0.5+0.5*tanh(5*x/max-2.5); plot(x,yy);

hold on;

y=[0.001, 0.11, 0.87, 0.98];

xp=[0:.1:1.5*max];
yp=zeros(1,length(xp));
for i=1:length(xp)
    if (xp(i)<0.3*max)
            yp(i) = y(1) + (y(2)-y(1))/(0.3*max-0)*(xp(i)-0);
    elseif (xp(i)<0.7*max)
            yp(i) = y(2) + (y(3)-y(2))/(0.7*max-0.3*max)*(xp(i)-0.3*max);
    elseif (xp(i)<max)
            yp(i) = y(3) + (y(4)-y(3))/(max-0.7*max)*(xp(i)-0.7*max);
    elseif (xp(i)>=max)
            yp(i) = y(4) + 0.005*(xp(i)-max);
    else
            yp(i)=-1;
    end         
end

plot(xp,yp,'Color','red')

hold off;