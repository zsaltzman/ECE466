clc;clear all;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Reading the data and putting the first 100000 entries in variables 
%Note that time is in seconds and framesize is in Bytes
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
no_entries=100000;
[time1, framesize1] = textread('Bel.data', '%f %f');
time=time1(1:no_entries);
framesize=framesize1(1:no_entries);
%%%%%%%%%%%%%%%%%%%%%%%%%Exercise %%%3.2%%%%%%%%%%%%%%%%%%%%%%%%%%%
%The following code will generate Plot 1; You generate Plot2 , Plot3.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
figure(1);
jj=1;
i=1;
initial_p=30;
ag_time=0.01;
bytes_p=zeros(1,100);
while time(jj)<=initial_p
    jj=jj+1;
end
while i<=100
while ((time(jj)-initial_p)<=ag_time*i && jj<no_entries)
bytes_p(i)=bytes_p(i)+framesize(jj);
jj=jj+1;
end
i=i+1;
end
%%%%%%%%
subplot(3,1,3);bar([30:0.01:30.99],bytes_p);

%figure(2)
%h = histogram(bytes_p);
%h.NumBins = 17;
%h.BinWidth = 2.5 * 10^4;

num_packets = 1000000;
num_bytes = 0; i=1;

while i<=num_packets 
   num_bytes = num_bytes + framesize1(i); 
   i = i + 1;
end
mean_rate = num_bytes/3142.82 %time of trace is 3142.82

i = 1;
pk_rate = framesize1(1)/time1(1);
while i<=num_packets-1
    rate = (framesize1(i+1)/(time1(i+1)-time1(i)));
    if pk_rate < rate
       pk_rate = rate; 
    end
    i = i + 1;
end

peak_to_avg = pk_rate/mean_rate;





