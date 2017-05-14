clc;clear all;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Reading data from the file
%Note: - time is in miliseconds and framesize is in Bytes
%      - file is sorted in transmit sequence
%  Column 1:   index of frame (in display sequence)
%  Column 2:   time of frame in ms (in display sequence)
%  Column 3:   type of frame (I, P, B)
%  Column 4:   size of frame (in Bytes)
%  Column 5-7: not used
%
% Since we are interested in the transmit sequence we ignore Columns 1 and
% 2. So, we are only interested in the following columns: 
%       Column 3:  assigned to type_f
%       Column 4:   assigned to framesize_f
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
[index, time, type_f, framesize_f, dummy1, dymmy2, dymmy3 ] = textread('movietrace.data', '%f %f %c %f %f %f %f');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%   CODE FOR EXERCISE 2.2   (version: Spring 2007)
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Extracting the I,P,B frmes characteristics from the source file
%frame size of I frames  : framesize_I
%frame size of P frames  : framesize_p 
%frame size of B frames  : framesize_B
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

a=0;
b=0;
c=0;
for i=1:length(index)
    if type_f(i)=='I'
        a=a+1;
        framesize_I(a)=framesize_f(i);
    end
     if type_f(i)=='B'
         b=b+1;
         framesize_B(b)=framesize_f(i);
         end
     if type_f(i)=='P'
         c=c+1;
         framesize_P(c)=framesize_f(i);
     end

end

i = 1;
total_bytes = 0;
while i<=53997
    total_bytes = total_bytes + framesize_f(i);
    i = i + 1;
end

mean_b = mean(framesize_B);
min_b = min(framesize_B);
max_b = max(framesize_B);
mean_i = mean(framesize_I);
min_i = min(framesize_I);
max_i = max(framesize_I);
mean_P = mean(framesize_P);
min_P = min(framesize_P);
max_P = max(framesize_P);

mean_frame = mean(framesize_f);
max_frame = max([max_i, max_b, max_P]);

frame_time = 1/30; %30 FPS
mean_bitrate = mean_frame/frame_time;
peak_bitrate = max_frame/frame_time;

pk_to_avg = peak_bitrate/mean_bitrate;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Hint1: You may use the MATLAB functions 'length()','mean()','max()','min()'.
%       which calculate the length,mean,max,min of a
%       vector (for example max(framesize_P) will give you the size of
%       largest P frame
%Hint2: Use the 'plot' function to graph the framesize as a function of the frame
%       sequence number. 
%Hint3: Use the function 'hist' to show the distribution of the frames. Before 
%       that function type 'figure(2);' to indicate your figure number.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%plot(index, framesize_f);
%figure(2);

remapped_type_f = linspace(1, 53997, 53997);
i = 1;
while i <= 53997
   if type_f(i) == 'P'
      remapped_type_f(i) = 0;
   end
   if type_f(i) == 'I'
      remapped_type_f(i) = 1 ;
   end
   if type_f(i) == 'B'
       remapped_type_f(i) = 2;
   end
   i = i + 1;
end
%C = categorical(remapped_type_f,[0 1 2],{'P Frames','I Frames','B Frames'});
%histogram(C)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%
%   CODE FOR EXERCISE 2.3   (version: Spring 2007)
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%The following code will generates Plot 1. You generate Plot2 , Plot3 on
%your own. 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% The next line assigns a label (figure number) to the figure 
figure(3);

initial_point=2000;
ag_frame=5;
jj=initial_point;
i=1;
bytes_f=zeros(1,100);
while i<=100
while ((jj-initial_point)<=ag_frame*i && jj<length(framesize_f))
bytes_f(i)=bytes_f(i)+framesize_f(jj);
jj=jj+1;
end
i=i+1;
end
plotting_frame = linspace(initial_point, ag_frame * 100 + initial_point, 100);
subplot(3,1,3);bar(plotting_frame, bytes_f);

