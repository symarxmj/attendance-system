var currentPage = 1;
var pageSize = 10;
var totalPages = 1;

function loadList() {
    var params = new URLSearchParams();
    params.append('page', currentPage);
    params.append('pageSize', pageSize);
    var id = document.getElementById('filterId').value.trim();
    var name = document.getElementById('filterName').value.trim();
    if (id) params.append('id', id);
    if (name) params.append('classroomName', name);

    fetch('/classroom/list?' + params.toString())
        .then(function(res) { return res.json(); })
        .then(function(data) {
            var tbody = document.getElementById('dataTable');
            if (data.code !== 1) {
                tbody.innerHTML = '<tr><td colspan="7" class="empty">加载失败</td></tr>';
                return;
            }
            var pr = data.data;
            var rows = pr.rows;
            totalPages = Math.ceil(pr.total / pageSize) || 1;
            document.getElementById('pageInfo').textContent = '第 ' + currentPage + ' / ' + totalPages + ' 页';
            document.getElementById('prevBtn').disabled = (currentPage <= 1);
            document.getElementById('nextBtn').disabled = (currentPage >= totalPages);
            if (!rows || rows.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" class="empty">暂无数据</td></tr>';
                return;
            }
            var html = '';
            rows.forEach(function(r) {
                html += '<tr>' +
                    '<td>' + r.id + '</td>' +
                    '<td>' + escHtml(r.classroomName) + '</td>' +
                    '<td>' + r.rows + '</td>' +
                    '<td>' + r.cols + '</td>' +
                    '<td>' + escHtml(r.excludeSeats || '-') + '</td>' +
                    '<td>' + (r.createTime ? r.createTime.substring(0, 10) : '') + '</td>' +
                    '<td><div class="action-cell">' +
                    '<a class="btn-action btn-edit" href="/classroom/edit-page/' + r.id + '">编辑</a>' +
                    '<button class="btn-action btn-delete" onclick="handleDelete(' + r.id + ')">删除</button>' +
                    '</div></td></tr>';
            });
            tbody.innerHTML = html;
        })
        .catch(function() {
            document.getElementById('dataTable').innerHTML = '<tr><td colspan="7" class="empty">网络错误</td></tr>';
        });
}

function handleDelete(id) {
    if (!confirm('确定删除该教室吗？')) return;
    fetch('/classroom/' + id, { method: 'DELETE' })
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.code === 1) { showToast('删除成功'); loadList(); }
            else { showToast(data.msg || '删除失败', true); }
        })
        .catch(function() { showToast('网络错误', true); });
}

function changePage(delta) {
    var np = currentPage + delta;
    if (np < 1 || np > totalPages) return;
    currentPage = np;
    loadList();
}

function resetFilter() {
    document.getElementById('filterId').value = '';
    document.getElementById('filterName').value = '';
    currentPage = 1;
    loadList();
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
    setTimeout(function() { toast.classList.add('toast-fade'); setTimeout(function() { toast.remove(); }, 300); }, 2000);
}

loadList();
