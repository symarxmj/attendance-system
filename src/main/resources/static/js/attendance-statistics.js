async function init() {
    try {
        var res = await fetch('/student');
        var data = await res.json();
        if (data.code === 1 && data.data) {
            var sel = document.getElementById('studentSelect');
            data.data.forEach(function(s) {
                sel.innerHTML += '<option value="' + escHtml(s.studentId) + '">'
                    + escHtml(s.studentName) + ' (' + escHtml(s.studentId) + ')</option>';
            });
        }
    } catch(e) {}
}

async function loadStatistics() {
    var studentId = document.getElementById('studentSelect').value;
    var container = document.getElementById('statsContent');
    if (!studentId) {
        container.innerHTML = '<div class="empty-hint">请选择一名学生查看考勤统计</div>';
        return;
    }
    container.innerHTML = '<div class="empty-hint">加载中...</div>';
    try {
        var res = await fetch('/attendance/statistics?studentId=' + encodeURIComponent(studentId));
        var data = await res.json();
        if (data.code === 1) {
            var d = data.data;
            var rateColor = d.attendanceRate >= 80 ? '#059669' : d.attendanceRate >= 60 ? '#d97706' : '#dc2626';
            container.innerHTML =
                '<div class="stats-grid">' +
                '<div class="stat-item"><div class="num total">' + d.totalCount + '</div><div class="label">总记录数</div></div>' +
                '<div class="stat-item"><div class="num present">' + d.presentCount + '</div><div class="label">已签到</div></div>' +
                '<div class="stat-item"><div class="num absent">' + d.absentCount + '</div><div class="label">缺勤</div></div>' +
                '<div class="stat-item"><div class="num rate" style="color:' + rateColor + '">' + d.attendanceRate + '%</div><div class="label">出勤率</div></div>' +
                '</div>' +
                '<div style="margin-bottom:8px;font-weight:500;">出勤率</div>' +
                '<div class="rate-bar-bg"><div class="rate-bar-fill" style="width:' + d.attendanceRate + '%"></div></div>';
        } else {
            container.innerHTML = '<div class="empty-hint">加载失败：' + escHtml(data.msg || '') + '</div>';
        }
    } catch(e) {
        container.innerHTML = '<div class="empty-hint">网络错误，请重试</div>';
    }
}

function escHtml(s) { if (!s) return ''; var d = document.createElement('div'); d.appendChild(document.createTextNode(s)); return d.innerHTML; }

init();
