% loader.m
% Load the Data from the Simulation Output and Initialize all the Relevant
% Statistical Variables and Calculations

% Load the data from the Simulation Output:
load r.dat;
load x.dat;
load y.dat;

% Initialize Variables:
nr = size(r,1);     % Number of location samples collected for broken bots
nd = size(x,2);     % Number of DiscBots
nb = size(r,2)/2;   % Number of broken DiscBots
labels = cell(nb,1);    % Labels for Graph Legend
for i = 1:nb
    labels{i} = strcat('Broken Bot ', int2str(i));
end

t0 = 49;            % Length of Recording in seconds (arbitrary but used for scaling)
dt = t0/nr;         % Time Step Size
t = dt*(1:nr);      % Vector of Time Steps

% DiscBot Location Statistics (At Each Time Step):
    % Position of DiscBot Cluster Center
    x_centers = mean(x,2);              
    y_centers = mean(y,2);               
    
    % Farthest DiscBot Position from Cluster Center
    %maxx = max(abs(x-x_centers),[],2);      
    %maxy = max(abs(y-y_centers),[],2); 
    maxx = abs(max(x,[],2) - min(x,[],2));      
    maxy = abs(max(y,[],2) - min(y,[],2));
    
    % Average DiscBot Displacement from Cluster Center
    shape = [1,nd];
    barx = mean(abs(x-repmat(x_centers,shape)),2);       
    bary = mean(abs(y-repmat(y_centers,shape)),2);       

% DiscBot Location Statistics (Over Entire Recording Period):
    % Farthest DiscBot Position from Center
    xbar = mean(maxx);
    ybar = mean(maxy);
    
    % Average Position of DiscBot Cluster
    x0 = mean(x_centers(:,1));
    y0 = mean(y_centers(:,1));

    % Farthest Distance of DiscBot From Cluster Center
    rmbar = max(sqrt(maxx.^2 + maxy.^2)); 
   
    % Average Distance of Average 
    rbar = sqrt( (barx).^2 +(bary).^2 ); 
%     r0 = mean(rbar);    % avg dist of avg bot
%     rsig = std(rbar);
    
    a = abs(x - repmat(mean(x,2),shape));
    b = abs(y - repmat(mean(y,2),shape));
    c = sqrt( a.^2 + b.^2 );
    r0 = mean(c(:));            % Average Distance of Average DiscBot
    rsig = std(c(:));           % Standard Deviation in Distances
    rConf = 2*rsig + r0;        % +2*sigma 
    rConf2 = -2*rsig + r0;      % -2*sigma
    
theta = linspace(0,2*pi,1000);  % For Plotting Circles