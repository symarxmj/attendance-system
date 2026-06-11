if (myRole === 'STUDENT') {
    // 学生直接查自己的，不需要下拉
    fetchStats(myStudentId);
} else {
    // ADMIN/TEACHER 加载学生列表到下拉框
    fetch('/student')
        .then(function(r) { return r.json(); })
        .then(function(d) {
            if (d.code === 1 && d.data) {
                var sel = document.getElementById('studentSelect');
                d.data.forEach(function(s) {
                    sel.innerHTML += '<option value="' + escHtml(s.studentId) + '">'
                        + escHtml(s.studentName) + ' (' + escHtml(s.studentId) + ')</option>';
                });
            }
        })
        .catch(function() {});
}

function loadStatistics() {
    var sid = document.getElementById('studentSelect').value;
    if (!sid) { document.getElementById('statsContent').innerHTML = '<div class="empty-hint">请选择一名学生查看考勤统计</div>'; return; }
    fetchStats(sid);
}

function fetchStats(studentId) {
    var container = document.getElementById('statsContent');
    container.innerHTML = '<div class="empty-hint">加载中...</div>';
    fetch('/attendance/statistics?studentId=' + encodeURIComponent(studentId))
        .then(function(r) { return r.json(); })
        .then(function(d) {
            if (d.code === 1) {
                var s = d.data;
                var rateColor = s.attendanceRate >= 80 ? '#059669' : s.attendanceRate >= 60 ? '#d97706' : '#dc2626';
                container.innerHTML =
                    '<div class="stats-grid" style="grid-template-columns:repeat(5,1fr)">' +
                    '<div class="stat-item"><div class="num total">' + s.totalCount + '</div><div class="label">总记录数</div></div>' +
                    '<div class="stat-item"><div class="num present">' + (s.normalCount || 0) + '</div><div class="label">正常</div></div>' +
                    '<div class="stat-item"><div class="num" style="color:#f39c12;font-weight:700">' + (s.lateCount || 0) + '</div><div class="label">迟到</div></div>' +
                    '<div class="stat-item"><div class="num absent">' + s.absentCount + '</div><div class="label">缺勤</div></div>' +
                    '<div class="stat-item"><div class="num rate" style="color:' + rateColor + '">' + s.attendanceRate + '%</div><div class="label">出勤率</div></div>' +
                    '</div>' +
                    '<div style="margin-bottom:8px;font-weight:500;">出勤率</div>' +
                    '<div class="rate-bar-bg"><div class="rate-bar-fill" style="width:' + s.attendanceRate + '%"></div></div>';
            } else {
                container.innerHTML = '<div class="empty-hint">加载失败：' + escHtml(d.msg || '') + '</div>';
            }
        })
        .catch(function() { container.innerHTML = '<div class="empty-hint">网络错误，请重试</div>'; });
}

function escHtml(s) { if (!s) return ''; var d = document.createElement('div'); d.appendChild(document.createTextNode(s)); return d.innerHTML; }
