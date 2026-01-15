document.addEventListener('DOMContentLoaded', function () {
    const userId = document.querySelector('#notificationDropdown').dataset.userId;

    const countBadge = document.getElementById('unread-count');
    const notificationList = document.getElementById('notification-list');
    const emptyMessage = document.getElementById('empty-message');

    function updateCount() {
        fetch(`/api/users/${userId}/notifications/unread/count`)
            .then(res => res.json())
            .then(count => {
                if (count > 0) {
                    countBadge.innerText = count > 9 ? '9+' : count;
                    countBadge.classList.remove('d-none');
                } else {
                    countBadge.classList.add('d-none');
                }
            });
    }

    function loadNotifications() {
        fetch(`/api/users/${userId}/notifications/unread`)
            .then(res => res.json())
            .then(notifications => {

                // remove all existing notifications
                const items = notificationList.querySelectorAll('.dynamic-notification');
                items.forEach(el => el.remove());

                if (notifications.length === 0) {
                    emptyMessage.classList.remove('d-none');
                } else {
                    emptyMessage.classList.add('d-none');
                    notifications.forEach(notif => {
                        const li = document.createElement('li');
                        li.className = 'dynamic-notification dropdown-item d-flex justify-content-between align-items-center';
                        li.innerHTML = `
                            <div onclick="window.location.href='${notif.link}'" style="cursor:pointer; flex-grow: 1;">
                                <div class="small fw-bold">${notif.type}</div>
                                <div class="text-wrap" style="max-width: 300px;">${notif.message}</div>
                            </div>
                            <div class="ms-2">
                                <button class="btn btn-sm text-success p-0 me-2" onclick="markAsRead(event, ${notif.id})">
                                    <span class="material-symbols-outlined" style="font-size: 18px;">check_circle</span>
                                </button>
                                <button class="btn btn-sm text-danger p-0" onclick="deleteNotification(event, ${notif.id})">
                                    <span class="material-symbols-outlined" style="font-size: 18px;">delete</span>
                                </button>
                            </div>
                        `;
                        notificationList.insertBefore(li, emptyMessage.nextElementSibling);
                    });
                }
            });
    }

    window.markAsRead = function (event, notificationId) {
        event.stopPropagation();
        fetch(`/api/users/${userId}/notifications/${notificationId}`, { method: 'PUT' })
            .then(() => {
                updateCount();
                loadNotifications();
            });
    };

    window.deleteNotification = function (event, notificationId) {
        event.stopPropagation();
        fetch(`/api/users/${userId}/notifications/${notificationId}`, { method: 'DELETE' })
            .then(() => {
                updateCount();
                loadNotifications();
            });
    };

    updateCount();
    setInterval(updateCount, 30000);

    // load unread notifications when dropdown gets opened
    document.getElementById('notificationDropdown').addEventListener('show.bs.dropdown', loadNotifications);
});