% positions.m
% Plot the Trajectories of Each Broken DiscBot (Viewed from the Center of the Cluster) 
% Additionally Display the 95% Confidence Interval for the Average DiscBot

hold on




% Center of the Cluster:
x_cent = repmat(x0,nr,1);
y_cent = repmat(y0,nr,1);

for i = 1:nb
    % Plot the Trajectories of the Broken DiscBots
    plot(r(:,i)-x_cent, r(:,i+1)-y_cent)
end

% Plot the 95% Confidence Interval: 
plot(r0*cos(theta), r0*sin(theta),'-g')         % Average DiscBot Distance        
plot(rConf*cos(theta), rConf*sin(theta),'--g')  % +2*sigma


title('Distance Traveled by Broken DiscBots')
xlabel('Distance x (sec)')
ylabel('Distance y (mm)')
legend(labels{:},'Average Distance', '+2 Standard Dev')

plot(0,0,'k+')  % Marker at the Center of the Cluster

hold off
