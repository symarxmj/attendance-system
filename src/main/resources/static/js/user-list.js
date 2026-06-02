var currentPage = 1;
var pageSize = 10;
var totalPages = 1;

function loadUsers() {
    var params = new URLSearchParams();
    params.append('page', currentPage);
    params.append('pageSize', pageSize);

    var username = document.getElementById('filterUsername').value.trim();
    var realName = document.getElementById('filterRealName').value.trim();
    var role = document.getElementById('filterRole').value;

    if (username) params.append('username', username);
    if (realName) params.append('realName', realName);
    if (role) params.append('role', role);

    fetch('/user/list?' + params.toString())
        .then(function(res) { return res.json(); })
        .then(function(data) {
            var tbody = document.getElementById('userTable');
            if (data.code !== 1) {
                tbody.innerHTML = '<tr><td colspan="6" class="empty">' + escHtml(data.msg || '加载失败') + '</td></tr>';
                return;
            }
            var pageResult = data.data;
            var rows = pageResult.rows;
            totalPages = Math.ceil(pageResult.total / pageSize) || 1;
            document.getElementById('pageInfo').textContent = '第 ' + currentPage + ' / ' + totalPages + ' 页';
            document.getElementById('prevBtn').disabled = (currentPage <= 1);
            document.getElementById('nextBtn').disabled = (currentPage >= totalPages);

            if (!rows || rows.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="empty">暂无数据</td></tr>';
                return;
            }

            var roleMap = { 'ADMIN': '管理员', 'TEACHER': '教师', 'STUDENT': '学生' };
            var html = '';
            rows.forEach(function(u) {
                html += '<tr>' +
                    '<td>' + u.id + '</td>' +
                    '<td>' + escHtml(u.username) + '</td>' +
                    '<td>' + escHtml(u.realName) + '</td>' +
                    '<td>' + (roleMap[u.role] || u.role) + '</td>' +
                    '<td>' + (u.createTime ? u.createTime.substring(0, 10) : '') + '</td>' +
                    '<td><div class="action-cell">' +
                    '<a class="btn-action btn-edit" href="/user/edit-page/' + u.id + '">编辑</a>' +
                    '<button class="btn-action btn-delete" onclick="handleDelete(' + u.id + ')">删除</button>' +
                    '</div></td>' +
                    '</tr>';
            });
            tbody.innerHTML = html;
        })
        .catch(function() {
            document.getElementById('userTable').innerHTML = '<tr><td colspan="6" class="empty">网络错误，请重试</td></tr>';
        });
}

function handleDelete(id) {
    if (!confirm('确定要删除该用户吗？')) return;
    fetch('/user/' + id, { method: 'DELETE' })
        .then(function(res) { return res.json(); })
        .then(function(data) {
            if (data.code === 1) {
                showToast('删除成功');
                loadUsers();
            } else {
                showToast(data.msg || '删除失败', true);
            }
        })
        .catch(function() {
            showToast('网络错误，请重试', true);
        });
}

function changePage(delta) {
    var newPage = currentPage + delta;
    if (newPage < 1 || newPage > totalPages) return;
    currentPage = newPage;
    loadUsers();
}

function resetFilter() {
    document.getElementById('filterUsername').value = '';
    document.getElementById('filterRealName').value = '';
    document.getElementById('filterRole').value = '';
    currentPage = 1;
    loadUsers();
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

if (document.getElementById('adminContent')) {
    loadUsers();
}
