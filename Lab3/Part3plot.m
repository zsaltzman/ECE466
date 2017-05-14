clc;clear all;
hold on;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Reading data from a file
%Note that time is in micro seconds and packetsize is in Bytes
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
[time_p, packetsize_p, packet_type, backlog_1, backlog_2, backlog_3, pdrop_1, pdrop_2, pdrop_3, delay_1, delay_2, delay_3] = textread('3_2PS.txt', '%f %f %f %f %f %f %f %f %f %f %f %f');

%%%%%%%%%%%%%%%%%%%%%%%%%Exercise 1.2%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%The following code will generate Plot 1; You generate Plot2 , Plot3.
%Hint1: For Plot2 and Plot3, you only need to change 'initial_p', the
%       initial time in microseconds, and 'ag_frame', the time period of
%       aggregation
%Hint2: After adding Plot2 and Plot3 to this part, you can use 'subplot(3,1,2);'
%       and 'subplot(3,1,3);' respectively to show 3 plots in the same figure.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


figure(1);
subplot(3,1,3);
jj=1;
i=1;
initial_p= 0;
ag_time=10000;
data_points = 100000000/ag_time;
bytes_p=zeros(1,data_points);

bound = 1896252;
i = 2;
cumulative_arrivals = zeros(1,bound);
cumulative_arrivals(1) = packetsize_p(1);
cumulative_time = zeros(1,bound);
cumulative_time(1) = 0;

transmissions_1 = zeros(1, bound);
transmissions_2 = zeros(1, bound);
transmissions_3 = zeros(1, bound);
while i <= bound
    cumulative_arrivals(i) = cumulative_arrivals(i-1) + packetsize_p(i);
    cumulative_time(i) = cumulative_time(i-1) + time_p(i);
    if packet_type(i) == 1
        transmissions_1(i) = packetsize_p(i);
    end
    if packet_type(i) == 2
        transmissions_2(i) = packetsize_p(i);
    end
    if packet_type(i) == 3
        transmissions_3(i) = packetsize_p(i);
    end
    i = i + 1;
    
end
subTime = cumulative_time(1:bound);

i = 1;
while time_p(jj)<=initial_p
    jj=jj+1;
end
while i<=data_points
while ((cumulative_time(jj)-initial_p)<=ag_time*i && jj<length(packetsize_p))
bytes_p(i)=bytes_p(i)+transmissions_3(jj);
jj=jj+1;
end
i=i+1;
end

timescale = linspace(0, 100, data_points);
bar(timescale, bytes_p);
%%%%%%%%
%corr_func = autocorr(bytes_p);
%plot(corr_func);
%set(gca, 'XTick', x_spacing);
%set(gca, 'XTickLabels', x_spacing);
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Note: Run the same MATLAB code for Exercise 1.3 and 1.4 but change the
%second line of the code in order to read the files 'poisson2.data' and
%'poisson3.data' respectively.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%