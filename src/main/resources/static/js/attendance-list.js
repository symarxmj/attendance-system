var currentPage = 1, pageSize = 10, totalPages = 1;
function loadList() {
    var params = new URLSearchParams();
    params.append('page', currentPage); params.append('pageSize', pageSize);
    var sid = document.getElementById('filterStudentId').value.trim();
    var status = document.getElementById('filterStatus').value;
    var begin = document.getElementById('filterBegin').value;
    var end = document.getElementById('filterEnd').value;
    if (sid) params.append('studentId', sid);
    if (status) params.append('status', status);
    if (begin) params.append('begin', begin + 'T00:00:00');
    if (end) params.append('end', end + 'T23:59:59');
    fetch('/attendance/list?' + params.toString())
        .then(function(r) { return r.json(); })
        .then(function(d) {
            var tbody = document.getElementById('dataTable');
            if (d.code !== 1) { tbody.innerHTML = '<tr><td colspan="7" class="empty">加载失败</td></tr>'; return; }
            var rows = d.data.rows;
            totalPages = Math.ceil(d.data.total / pageSize) || 1;
            document.getElementById('pageInfo').textContent = '第 ' + currentPage + ' / ' + totalPages + ' 页';
            document.getElementById('prevBtn').disabled = currentPage <= 1;
            document.getElementById('nextBtn').disabled = currentPage >= totalPages;
            if (!rows || rows.length === 0) { tbody.innerHTML = '<tr><td colspan="7" class="empty">暂无数据</td></tr>'; return; }
            var html = '';
            rows.forEach(function(r) {
                var stLabel = r.status === 'PRESENT' ? '<span class="tag" style="background:#eafaf1;color:#27ae60;padding:2px 8px;border-radius:10px;font-size:12px;">已签到</span>' : escHtml(r.status);
                html += '<tr><td>' + r.id + '</td><td>' + r.sessionId + '</td><td>' + escHtml(r.studentId) + '</td>' +
                    '<td>' + (r.checkInTime ? r.checkInTime.substring(0, 16) : '') + '</td>' +
                    '<td>' + stLabel + '</td><td>' + escHtml(r.ip || '-') + '</td>' +
                    '<td><button class="btn-action btn-delete" onclick="handleDelete(' + r.id + ')">删除</button></td></tr>';
            });
            tbody.innerHTML = html;
        })
        .catch(function() { document.getElementById('dataTable').innerHTML = '<tr><td colspan="7" class="empty">网络错误</td></tr>'; });
}
function handleDelete(id) {
    if (!confirm('确定删除该考勤记录吗？')) return;
    fetch('/attendance/' + id, { method: 'DELETE' })
        .then(function(r) { return r.json(); })
        .then(function(d) { if (d.code === 1) { showToast('删除成功'); loadList(); } else { showToast(d.msg || '删除失败', true); } })
        .catch(function() { showToast('网络错误', true); });
}
function changePage(d) { var np = currentPage + d; if (np < 1 || np > totalPages) return; currentPage = np; loadList(); }
function resetFilter() { document.getElementById('filterStudentId').value = ''; document.getElementById('filterStatus').value = ''; document.getElementById('filterBegin').value = ''; document.getElementById('filterEnd').value = ''; currentPage = 1; loadList(); }
function escHtml(s) { if (!s) return ''; var d = document.createElement('div'); d.appendChild(document.createTextNode(s)); return d.innerHTML; }
function showToast(m, e) { var x = document.querySelector('.toast-notification'); if (x) x.remove(); var t = document.createElement('div'); t.className = 'toast-notification' + (e ? ' toast-error' : ''); t.textContent = m; document.body.appendChild(t); setTimeout(function() { t.classList.add('toast-fade'); setTimeout(function() { t.remove(); }, 300); }, 2000); }
loadList();
