async function loadTodaySessions() {
    const tbody = document.getElementById('sessionTable');
    if (!tbody) return;

    try {
        const res = await fetch('/attendance/today');
        const result = await res.json();
        if (result.code !== 1) {
            tbody.innerHTML = '<tr><td colspan="4" class="empty-state">加载失败：' + escHtml(result.msg || '') + '</td></tr>';
            return;
        }
        const sessions = result.data;
        if (!sessions || sessions.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="empty-state">今日暂无课程</td></tr>';
            return;
        }
        let html = '';
        sessions.forEach(function(s) {
            let statusHtml, actionHtml;
            if (s.checkedIn) {
                var label = statusLabel(s.status);
                statusHtml = '<span class="tag-checked">' + label + '</span>';
                actionHtml = '<span style="color:#95a5a6;font-size:13px;">' + escHtml(s.checkInTime ? s.checkInTime.substring(0,16).replace('T',' ') : '') + '</span>';
            } else {
                statusHtml = '<span class="tag-waiting">未签到</span>';
                actionHtml = '<button class="btn-checkin" onclick="doCheckIn(' + s.sessionId + ', this)">签到</button>';
            }
            html += '<tr>' +
                '<td>' + escHtml(s.courseName) + '</td>' +
                '<td>' + (s.sessionDate ? escHtml(s.sessionDate.substring(0, 16).replace('T', ' ')) : '') + '</td>' +
                '<td>' + statusHtml + '</td>' +
                '<td>' + actionHtml + '</td>' +
                '</tr>';
        });
        tbody.innerHTML = html;
    } catch (err) {
        tbody.innerHTML = '<tr><td colspan="4" class="empty-state">网络错误，请重试</td></tr>';
    }
}

async function doCheckIn(sessionId, btn) {
    btn.disabled = true;
    btn.textContent = '签到中...';
    try {
        const res = await fetch('/attendance/check-in', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ sessionId: sessionId })
        });
        const result = await res.json();
        if (result.code === 1) {
            showToast(result.data || '签到成功');
            loadTodaySessions();
        } else {
            showToast(result.msg || '签到失败', true);
            btn.disabled = false;
            btn.textContent = '签到';
        }
    } catch (err) {
        showToast('网络错误，请重试', true);
        btn.disabled = false;
        btn.textContent = '签到';
    }
}

function statusLabel(s) {
    var map = {NORMAL:'正常',LATE:'迟到',EARLY:'早退',ABSENT:'缺勤'};
    return map[s] || s || '已签到';
}

function escHtml(str) {
    if (!str) return '';
    var div = document.createElement('div');
    div.appendChild(document.createTextNode(str));
    return div.innerHTML;
}

function showToast(msg, isError) {
    var existing = document.querySelector('.toast-notification');
    if (existing) existing.remove();
    var toast = document.createElement('div');
    toast.className = 'toast-notification' + (isError ? ' toast-error' : '');
    toast.textContent = msg;
    document.body.appendChild(toast);
    setTimeout(function() {
        toast.classList.add('toast-fade');
        setTimeout(function() { toast.remove(); }, 300);
    }, 2000);
}

if (document.getElementById('checkinCard')) {
    loadTodaySessions();
}
