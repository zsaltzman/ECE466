clc;clear all;
hold on;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Reading data from a file
%Note that time is in micro seconds and packetsize is in Bytes
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
[packet_no_p, time_p, packetsize_p] = textread('outputSink.txt', '%f %f %f');

%%%%%%%%%%%%%%%%%%%%%%%%%Exercise 1.2%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%The following code will generate Plot 1; You generate Plot2 , Plot3.
%Hint1: For Plot2 and Plot3, you only need to change 'initial_p', the
%       initial time in microseconds, and 'ag_frame', the time period of
%       aggregation
%Hint2: After adding Plot2 and Plot3 to this part, you can use 'subplot(3,1,2);'
%       and 'subplot(3,1,3);' respectively to show 3 plots in the same figure.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
figure(1);
jj=1;
i=1;
initial_p= 0;
ag_time=1000000;
bytes_p=zeros(1,150);
while time_p(jj)<=initial_p
    jj=jj+1;
end
while i<=150
while ((time_p(jj)-initial_p)<=ag_time*i && jj<length(packetsize_p))
bytes_p(i)=bytes_p(i)+packetsize_p(jj);
jj=jj+1;
end
i=i+1;
end


i = 2;
cumulative_arrivals = zeros(1, 125000);
cumulative_arrivals(1) = packetsize_p(1);
time_p(1) = time_p(1)/1000;
while i <= 125000
    if (i <= length(packetsize_p))
        cumulative_arrivals(i) = cumulative_arrivals(i-1) + packetsize_p(i);
        %time_p(i) = time_p(i)/1000;
    else
        cumulative_arrivals(i) = cumulative_arrivals(i-1);
    end
    
    i = i + 1;
end
%%%%%%%%
%corr_func = autocorr(bytes_p);
%plot(corr_func);
subplot(3,1,2);
bar(bytes_p);
%set(gca, 'XTick', x_spacing);
%set(gca, 'XTickLabels', x_spacing);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Note: Run the same MATLAB code for Exercise 1.3 and 1.4 but change the
%second line of the code in order to read the files 'poisson2.data' and
%'poisson3.data' respectively.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%