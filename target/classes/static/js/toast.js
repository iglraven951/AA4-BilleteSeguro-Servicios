/**
 * TOAST & NOTIFICATION SYSTEM
 * Implements Observer Pattern for real-time notifications
 * BilleteSeguro - Sistema Bancario
 *
 * Features:
 * - Real-time toast notifications
 * - Persistent storage with localStorage
 * - Cross-page synchronization
 * - Observer Pattern visualization
 */

// ==========================================
// NOTIFICATION STORAGE MANAGER
// ==========================================
class NotificationStorage {
    constructor() {
        this.STORAGE_KEY = 'billeteseguro_notifications';
        this.MAX_NOTIFICATIONS = 50;
    }

    getAll() {
        try {
            const data = localStorage.getItem(this.STORAGE_KEY);
            return data ? JSON.parse(data) : [];
        } catch (e) {
            console.error('Error reading notifications:', e);
            return [];
        }
    }

    save(notifications) {
        try {
            // Keep only the last MAX_NOTIFICATIONS
            const toSave = notifications.slice(0, this.MAX_NOTIFICATIONS);
            localStorage.setItem(this.STORAGE_KEY, JSON.stringify(toSave));
            // Dispatch event for cross-tab sync
            window.dispatchEvent(new CustomEvent('notificationsUpdated', { detail: toSave }));
        } catch (e) {
            console.error('Error saving notifications:', e);
        }
    }

    add(notification) {
        const notifications = this.getAll();
        notifications.unshift({
            ...notification,
            id: Date.now() + Math.random().toString(36).substr(2, 9),
            timestamp: new Date().toISOString(),
            read: false
        });
        this.save(notifications);
        return notifications;
    }

    markAsRead(id) {
        const notifications = this.getAll();
        const index = notifications.findIndex(n => n.id === id);
        if (index !== -1) {
            notifications[index].read = true;
            this.save(notifications);
        }
        return notifications;
    }

    markAllAsRead() {
        const notifications = this.getAll().map(n => ({ ...n, read: true }));
        this.save(notifications);
        return notifications;
    }

    clear() {
        this.save([]);
        return [];
    }

    getUnreadCount() {
        return this.getAll().filter(n => !n.read).length;
    }
}

// ==========================================
// TOAST NOTIFICATION SYSTEM
// ==========================================
class ToastNotificationSystem {
    constructor() {
        this.container = null;
        this.toasts = [];
        this.storage = new NotificationStorage();
        this.init();
    }

    init() {
        if (!document.getElementById('toast-container')) {
            this.container = document.createElement('div');
            this.container.id = 'toast-container';
            this.container.className = 'toast-container';
            document.body.appendChild(this.container);
        } else {
            this.container = document.getElementById('toast-container');
        }
    }

    show(options) {
        const {
            type = 'info',
            title = 'Notificacion',
            message = '',
            icon = null,
            duration = 5000,
            observers = [],
            persist = true
        } = options;

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;

        const iconMap = {
            success: 'bi-check-circle-fill',
            warning: 'bi-exclamation-triangle-fill',
            danger: 'bi-x-circle-fill',
            info: 'bi-info-circle-fill',
            notification: 'bi-bell-fill',
            audit: 'bi-journal-check',
            fraud: 'bi-shield-exclamation'
        };

        const selectedIcon = icon || iconMap[type] || 'bi-bell-fill';

        let observerBadges = '';
        if (observers.length > 0) {
            observerBadges = `
                <div class="toast-meta">
                    <i class="bi bi-eye"></i>
                    ${observers.map(obs => `<span class="observer-badge ${obs.type}">${obs.name}</span>`).join('')}
                </div>
            `;
        }

        toast.innerHTML = `
            <div class="toast-icon ${type}">
                <i class="bi ${selectedIcon}"></i>
            </div>
            <div class="toast-content">
                <div class="toast-title">${title}</div>
                <div class="toast-message">${message}</div>
                ${observerBadges}
            </div>
            <button class="toast-close" onclick="toastSystem.dismiss(this.parentElement)">
                <i class="bi bi-x-lg"></i>
            </button>
            <div class="toast-progress"></div>
        `;

        this.container.appendChild(toast);
        this.toasts.push(toast);

        // Save to persistent storage if enabled
        if (persist) {
            this.storage.add({
                type,
                title,
                message: message.replace(/<[^>]*>/g, ''), // Strip HTML for storage
                icon: selectedIcon,
                observers
            });
            // Update UI
            this.updateNotificationUI();
        }

        if (duration > 0) {
            setTimeout(() => this.dismiss(toast), duration);
        }

        return toast;
    }

    dismiss(toast) {
        if (!toast || toast.classList.contains('toast-exit')) return;

        toast.classList.add('toast-exit');
        setTimeout(() => {
            if (toast.parentElement) {
                toast.parentElement.removeChild(toast);
            }
            this.toasts = this.toasts.filter(t => t !== toast);
        }, 300);
    }

    success(title, message, observers = []) {
        return this.show({ type: 'success', title, message, observers });
    }

    warning(title, message, observers = []) {
        return this.show({ type: 'warning', title, message, observers });
    }

    error(title, message, observers = []) {
        return this.show({ type: 'danger', title, message, observers });
    }

    info(title, message, observers = []) {
        return this.show({ type: 'info', title, message, observers });
    }

    showBankingNotification(operationType, data) {
        const { cuenta, monto, saldoAnterior, saldoNuevo } = data;

        const observers = [
            { name: 'Notificacion', type: 'notification' },
            { name: 'Auditoria', type: 'audit' }
        ];

        if (monto > 10000) {
            observers.push({ name: 'Fraude', type: 'fraud' });
        }

        let type, title, icon;

        switch (operationType) {
            case 'DEPOSITO':
                type = 'success';
                title = 'Deposito Realizado';
                icon = 'bi-box-arrow-in-down-fill';
                break;
            case 'RETIRO':
                type = 'warning';
                title = 'Retiro Realizado';
                icon = 'bi-box-arrow-up-fill';
                break;
            case 'TRANSFERENCIA_ENVIADA':
                type = 'info';
                title = 'Transferencia Enviada';
                icon = 'bi-send-fill';
                break;
            case 'TRANSFERENCIA_RECIBIDA':
                type = 'success';
                title = 'Transferencia Recibida';
                icon = 'bi-download';
                break;
            default:
                type = 'info';
                title = 'Operacion Realizada';
                icon = 'bi-check-circle-fill';
        }

        const message = `
            <strong>S/. ${monto.toFixed(2)}</strong> en cuenta ${cuenta}<br>
            <small>Nuevo saldo: S/. ${saldoNuevo.toFixed(2)}</small>
        `;

        this.show({
            type,
            title,
            message,
            icon,
            observers,
            duration: 6000
        });

        if (monto > 10000) {
            setTimeout(() => {
                this.show({
                    type: 'fraud',
                    title: 'ALERTA DE FRAUDE',
                    message: `Movimiento sospechoso detectado: S/. ${monto.toFixed(2)} supera el umbral de S/. 10,000`,
                    icon: 'bi-shield-exclamation',
                    duration: 8000,
                    observers: [{ name: 'FraudeObserver', type: 'fraud' }]
                });
            }, 500);
        }
    }

    showObserverCascade(notifications, delay = 400) {
        notifications.forEach((notif, index) => {
            setTimeout(() => {
                let type = 'notification';
                let title = 'Observer';

                if (notif.includes('AUDITORIA') || notif.includes('Auditoria')) {
                    type = 'audit';
                    title = 'Auditoria Observer';
                } else if (notif.includes('ALERTA') || notif.includes('FRAUDE') || notif.includes('Fraude')) {
                    type = 'fraud';
                    title = 'Fraude Observer';
                } else if (notif.includes('NOTIFICACION') || notif.includes('Notificacion')) {
                    type = 'notification';
                    title = 'Notificacion Observer';
                }

                this.show({
                    type,
                    title,
                    message: notif,
                    duration: 5000
                });
            }, index * delay);
        });
    }

    // Update notification dropdown UI
    updateNotificationUI() {
        const badge = document.getElementById('notifBadge');
        const list = document.getElementById('notificationList');

        if (!badge || !list) return;

        const notifications = this.storage.getAll();
        const unreadCount = this.storage.getUnreadCount();

        // Update badge
        badge.textContent = unreadCount;
        badge.style.display = unreadCount > 0 ? 'flex' : 'none';

        // Update list
        if (notifications.length === 0) {
            list.innerHTML = `
                <div class="notification-empty">
                    <i class="bi bi-inbox"></i>
                    <p>No hay notificaciones</p>
                </div>
            `;
            return;
        }

        list.innerHTML = notifications.map(n => {
            const time = this.formatTime(n.timestamp);
            const unreadClass = n.read ? '' : 'unread';
            return `
                <div class="notification-item ${n.type} ${unreadClass}" data-id="${n.id}" onclick="toastSystem.markNotificationRead('${n.id}')">
                    <div class="notification-icon ${n.type}">
                        <i class="bi ${n.icon || this.getIconForType(n.type)}"></i>
                    </div>
                    <div class="notification-content">
                        <strong>${n.title}</strong>
                        <p>${n.message}</p>
                        <small><i class="bi bi-clock"></i> ${time}</small>
                    </div>
                    ${!n.read ? '<span class="unread-dot"></span>' : ''}
                </div>
            `;
        }).join('');
    }

    getIconForType(type) {
        const icons = {
            'success': 'bi-check-circle-fill',
            'warning': 'bi-exclamation-triangle-fill',
            'info': 'bi-info-circle-fill',
            'danger': 'bi-x-circle-fill',
            'audit': 'bi-journal-check',
            'fraud': 'bi-shield-exclamation',
            'notification': 'bi-bell-fill'
        };
        return icons[type] || 'bi-bell-fill';
    }

    formatTime(timestamp) {
        const date = new Date(timestamp);
        const now = new Date();
        const diff = now - date;

        if (diff < 60000) return 'Ahora mismo';
        if (diff < 3600000) return `Hace ${Math.floor(diff / 60000)} min`;
        if (diff < 86400000) return `Hace ${Math.floor(diff / 3600000)} hrs`;

        return date.toLocaleDateString('es-PE', {
            day: '2-digit',
            month: 'short',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    markNotificationRead(id) {
        this.storage.markAsRead(id);
        this.updateNotificationUI();
    }

    clearNotifications() {
        this.storage.clear();
        this.updateNotificationUI();
    }

    markAllRead() {
        this.storage.markAllAsRead();
        this.updateNotificationUI();
    }

    getNotifications() {
        return this.storage.getAll();
    }

    getUnreadCount() {
        return this.storage.getUnreadCount();
    }
}

// ==========================================
// GLOBAL FUNCTIONS
// ==========================================
let toastSystem = null;

function initToastSystem() {
    if (!toastSystem) {
        toastSystem = new ToastNotificationSystem();
        window.toastSystem = toastSystem;

        // Initial UI update
        setTimeout(() => {
            toastSystem.updateNotificationUI();
        }, 100);
    }
}

function toggleNotifications() {
    const dropdown = document.getElementById('notificationDropdown');
    if (dropdown) {
        dropdown.classList.toggle('show');
        if (dropdown.classList.contains('show')) {
            toastSystem.updateNotificationUI();
        }
    }
}

function clearNotifications() {
    if (toastSystem) {
        toastSystem.clearNotifications();
    }
}

function markAllNotificationsRead() {
    if (toastSystem) {
        toastSystem.markAllRead();
    }
}

// Initialize on DOMContentLoaded
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initToastSystem);
} else {
    initToastSystem();
}

// Close dropdown when clicking outside
document.addEventListener('click', function(e) {
    const wrapper = document.querySelector('.notification-wrapper');
    const dropdown = document.getElementById('notificationDropdown');
    if (wrapper && dropdown && !wrapper.contains(e.target)) {
        dropdown.classList.remove('show');
    }
});

// Cross-tab synchronization
window.addEventListener('storage', function(e) {
    if (e.key === 'billeteseguro_notifications' && toastSystem) {
        toastSystem.updateNotificationUI();
    }
});

// Listen for custom notification events
window.addEventListener('notificationsUpdated', function(e) {
    if (toastSystem) {
        toastSystem.updateNotificationUI();
    }
});
