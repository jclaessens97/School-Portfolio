import * as signalR from '@aspnet/signalr';

let connection;

async function init() {
    connection = new signalR.HubConnectionBuilder().withUrl('/activityhub').build();

    connection.on('UpdateActivityFeed', (activity) => {
        updateActivityFeed(activity)
    });

    await connection.start();
    connection.invoke('JoinPage', 'all');
}

function updateActivityFeed(activity) {
    const feed = document.getElementById('activity-feed');

    const entry = document.createElement('div');
    entry.className = 'activity-entry';

    const content = document.createElement('p');
    content.innerHTML = `<span>${activity.user}</span>
        heeft ${activity.action} 
        <span>${activity.value}<span> 
        - <span class="activity-date">${activity.activityDate}</span>`;

    entry.appendChild(content);
    feed.appendChild(entry);

    feed.scrollTop = feed.scrollHeight;
}

function cleanup() {
    connection.invoke('LeavePage', 'all');
}

window.onload = init;
window.onunload = cleanup();