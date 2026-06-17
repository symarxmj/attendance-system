var currentCourseId = '';

// ---------- 学生统计（原有功能）----------
if (myRole === 'STUDENT') {
    fetchStats(myStudentId);
} else {
    // ADMIN/TEACHER 加载学生列表
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

    // 加载课程下拉
    loadCourseDropdown();
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

// ---------- 按课程查看 ----------
function loadCourseDropdown() {
    fetch('/course/list?page=1&pageSize=1000')
        .then(function(r) { return r.json(); })
        .then(function(d) {
            if (d.code === 1 && d.data && d.data.rows) {
                var sel = document.getElementById('courseSelect');
                d.data.rows.forEach(function(c) {
                    sel.innerHTML += '<option value="' + escHtml(c.courseId) + '">'
                        + escHtml(c.courseName) + ' (' + escHtml(c.courseId) + ')</option>';
                });
            }
        })
        .catch(function() {});
}

function loadCourseAttendance() {
    var cid = document.getElementById('courseSelect').value;
    currentCourseId = cid;
    var exportBtn = document.getElementById('exportBtn');
    var tbody = document.getElementById('courseTableBody');

    if (!cid) {
        tbody.innerHTML = '<tr><td colspan="5" class="empty-hint">请选择课程查看考勤记录</td></tr>';
        exportBtn.disabled = true;
        return;
    }

    exportBtn.disabled = false;
    tbody.innerHTML = '<tr><td colspan="5" class="empty-hint">加载中...</td></tr>';

    fetch('/attendance/by-course?courseId=' + encodeURIComponent(cid))
        .then(function(r) { return r.json(); })
        .then(function(d) {
            if (d.code === 1) {
                var rows = d.data;
                if (!rows || rows.length === 0) {
                    tbody.innerHTML = '<tr><td colspan="5" class="empty-hint">该班级没有考勤记录</td></tr>';
                    return;
                }
                var html = '';
                rows.forEach(function(r) {
                    var chkTime = r.checkInTime;
                    var timeStr = chkTime ? (typeof chkTime === 'string' ? chkTime : '').substring(0, 16).replace('T', ' ') : '';
                    var hasRecord = r.id != null;
                    html += '<tr>' +
                        '<td>' + escHtml(r.studentId) + '</td>' +
                        '<td>' + escHtml(r.studentName || '-') + '</td>' +
                        '<td>' + (timeStr || '<span style="color:#9ca3af;">-</span>') + '</td>' +
                        '<td>' + statusTag(r.status) + '</td>' +
                        '<td>' + actionCell(r) + '</td>' +
                        '</tr>';
                });
                tbody.innerHTML = html;
            } else {
                tbody.innerHTML = '<tr><td colspan="5" class="empty-hint">加载失败：' + escHtml(d.msg || '') + '</td></tr>';
            }
        })
        .catch(function() {
            tbody.innerHTML = '<tr><td colspan="5" class="empty-hint">网络错误，请重试</td></tr>';
        });
}

function actionCell(r) {
    var html = '';
    if (r.id != null) {
        // 有记录：正常编辑和删除
        html += '<select onchange="handleEdit(' + r.id + ', this.value, true)" style="padding:2px 6px;font-size:12px;border:1px solid #d1d5db;border-radius:4px;">' +
            '<option value="">修改状态</option>' +
            '<option value="NORMAL">正常</option>' +
            '<option value="LATE">迟到</option>' +
            '<option value="EARLY">早退</option>' +
            '<option value="ABSENT">缺勤</option>' +
            '</select> ';
        html += '<button class="btn-action btn-delete" onclick="handleDelete(' + r.id + ', true)">删除</button>';
    } else {
        // 无记录（缺勤）：创建记录
        html += '<select onchange="createAttendance(' + r.sessionId + ', \'' + escHtml(r.studentId) + '\', this.value)" style="padding:2px 6px;font-size:12px;border:1px solid #d1d5db;border-radius:4px;">' +
            '<option value="">补签</option>' +
            '<option value="NORMAL">正常</option>' +
            '<option value="LATE">迟到</option>' +
            '<option value="EARLY">早退</option>' +
            '</select>';
    }
    return html;
}

function createAttendance(sessionId, studentId, status) {
    if (!status) return;
    if (!confirm('确认为学生 ' + studentId + ' 补签为「' + statusLabel(status) + '」吗？')) {
        loadCourseAttendance();
        return;
    }
    fetch('/attendance/check-in-force', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({sessionId: sessionId, studentId: studentId, status: status})
    })
    .then(function(r) { return r.json(); })
    .then(function(d) {
        if (d.code === 1) { showToast('补签成功'); loadCourseAttendance(); }
        else { showToast(d.msg || '补签失败', true); loadCourseAttendance(); }
    })
    .catch(function() { showToast('网络错误', true); });
}

function handleEdit(id, status, refreshCourse) {
    if (!status) return;
    if (!confirm('确定将考勤记录状态改为「' + statusLabel(status) + '」吗？')) {
        if (refreshCourse) loadCourseAttendance(); else loadList();
        return;
    }
    fetch('/attendance/' + id, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({status: status})
    })
    .then(function(r) { return r.json(); })
    .then(function(d) {
        if (d.code === 1) { showToast('更新成功'); if (refreshCourse) loadCourseAttendance(); else location.reload(); }
        else { showToast(d.msg || '更新失败', true); }
    })
    .catch(function() { showToast('网络错误', true); });
}

function handleDelete(id, refreshCourse) {
    if (!confirm('确定删除该考勤记录吗？')) return;
    fetch('/attendance/' + id, { method: 'DELETE' })
        .then(function(r) { return r.json(); })
        .then(function(d) {
            if (d.code === 1) { showToast('删除成功'); if (refreshCourse) loadCourseAttendance(); else location.reload(); }
            else { showToast(d.msg || '删除失败', true); }
        })
        .catch(function() { showToast('网络错误', true); });
}

function exportCSV() {
    if (!currentCourseId) return;
    window.open('/attendance/export?courseId=' + encodeURIComponent(currentCourseId), '_blank');
}

// ---------- 通用工具 ----------
function statusTag(s) {
    var map = {
        NORMAL: '<span style="display:inline-block;padding:2px 8px;border-radius:10px;font-size:12px;background:#eafaf1;color:#27ae60;">正常</span>',
        LATE:   '<span style="display:inline-block;padding:2px 8px;border-radius:10px;font-size:12px;background:#fef9e7;color:#f39c12;">迟到</span>',
        EARLY:  '<span style="display:inline-block;padding:2px 8px;border-radius:10px;font-size:12px;background:#eaf2f8;color:#2980b9;">早退</span>',
        ABSENT: '<span style="display:inline-block;padding:2px 8px;border-radius:10px;font-size:12px;background:#fdedec;color:#e74c3c;">缺勤</span>'
    };
    return map[s] || escHtml(s || '');
}

function statusLabel(s) {
    var map = {NORMAL:'正常',LATE:'迟到',EARLY:'早退',ABSENT:'缺勤'};
    return map[s] || s;
}

function escHtml(s) { if (!s) return ''; var d = document.createElement('div'); d.appendChild(document.createTextNode(s)); return d.innerHTML; }

function showToast(m, e) {
    var x = document.querySelector('.toast-notification'); if (x) x.remove();
    var t = document.createElement('div'); t.className = 'toast-notification' + (e ? ' toast-error' : '');
    t.textContent = m; document.body.appendChild(t);
    setTimeout(function() { t.classList.add('toast-fade'); setTimeout(function() { t.remove(); }, 300); }, 2000);
}
