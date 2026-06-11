var currentPage = 1, pageSize = 10, totalPages = 1;
var wdMap = {1:'周一',2:'周二',3:'周三',4:'周四',5:'周五',6:'周六',7:'周日'};

function loadList() {
    var params = new URLSearchParams();
    params.append('page', currentPage);
    params.append('pageSize', pageSize);
    var id = document.getElementById('filterId').value.trim();
    var name = document.getElementById('filterName').value.trim();
    if (id) params.append('courseId', id);
    if (name) params.append('courseName', name);

    fetch('/course/list?' + params.toString())
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
                html += '<tr><td>' + escHtml(r.courseId) + '</td><td>' + escHtml(r.courseName) + '</td>' +
                    '<td>' + escHtml(r.teacherName || '') + '</td><td>' + (r.classroomId || '') + '</td>' +
                    '<td>' + (wdMap[r.weekday] || '') + '</td>' +
                    '<td>' + (r.startWeek||'') + '-' + (r.endWeek||'') + '</td>' +
                    '<td><div class="action-cell">' +
                    '<a class="btn-action btn-edit" href="/course/edit-page/' + encodeURIComponent(r.courseId) + '">编辑</a>' +
                    '<button class="btn-action btn-delete" onclick="handleDelete(\'' + escHtml(r.courseId) + '\')">删除</button>' +
                    '</div></td></tr>';
            });
            tbody.innerHTML = html;
        })
        .catch(function() { document.getElementById('dataTable').innerHTML = '<tr><td colspan="7" class="empty">网络错误</td></tr>'; });
}

function handleDelete(id) {
    if (!confirm('确定删除该课程吗？')) return;
    fetch('/course/' + encodeURIComponent(id), { method: 'DELETE' })
        .then(function(r) { return r.json(); })
        .then(function(d) { if (d.code === 1) { showToast('删除成功'); loadList(); } else { showToast(d.msg || '删除失败', true); } })
        .catch(function() { showToast('网络错误', true); });
}

function changePage(d) { var np = currentPage + d; if (np < 1 || np > totalPages) return; currentPage = np; loadList(); }
function resetFilter() { document.getElementById('filterId').value = ''; document.getElementById('filterName').value = ''; currentPage = 1; loadList(); }
function escHtml(s) { if (!s) return ''; var d = document.createElement('div'); d.appendChild(document.createTextNode(s)); return d.innerHTML; }
function showToast(m, e) { var x = document.querySelector('.toast-notification'); if (x) x.remove(); var t = document.createElement('div'); t.className = 'toast-notification' + (e ? ' toast-error' : ''); t.textContent = m; document.body.appendChild(t); setTimeout(function() { t.classList.add('toast-fade'); setTimeout(function() { t.remove(); }, 300); }, 2000); }
loadList();
