let currentPage = 1;
const pageSize = 10;
let totalPages = 1;

async function loadStudents() {
    const studentId = document.getElementById('filterStudentId').value.trim();
    const studentName = document.getElementById('filterStudentName').value.trim();
    const gender = document.getElementById('filterGender').value;
    const begin = document.getElementById('filterBegin').value;
    const end = document.getElementById('filterEnd').value;

    const params = new URLSearchParams();
    params.append('page', currentPage);
    params.append('pageSize', pageSize);
    if (studentId) params.append('studentId', studentId);
    if (studentName) params.append('studentName', studentName);
    if (gender) params.append('gender', gender);
    if (begin) params.append('begin', begin + 'T00:00:00');
    if (end) params.append('end', end + 'T23:59:59');

    const tbody = document.getElementById('studentTable');
    tbody.innerHTML = '<tr><td colspan="5" class="loading">加载中...</td></tr>';

    try {
        const res = await fetch('/student/list?' + params.toString());
        const data = await res.json();

        if (data.code === 1) {
            const rows = data.data.rows;
            totalPages = Math.ceil(data.data.total / pageSize) || 1;

            if (rows.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" class="empty">暂无数据</td></tr>';
            } else {
                tbody.innerHTML = rows.map(s => `
                    <tr>
                        <td>${escHtml(s.studentId || '-')}</td>
                        <td>${escHtml(s.studentName || '-')}</td>
                        <td>${escHtml(s.gender || '-')}</td>
                        <td>${s.createTime ? s.createTime.substring(0, 10) : '-'}</td>
                        <td class="action-cell">
                            <a class="btn-action btn-edit" href="/student/edit-page/${escHtml(s.studentId)}">编辑</a>
                            <button class="btn-action btn-delete" onclick="handleDelete('${escHtml(s.studentId)}', '${escHtml(s.studentName)}')">删除</button>
                        </td>
                    </tr>
                `).join('');
            }

            document.getElementById('pageInfo').textContent = `第 ${currentPage} 页 / 共 ${totalPages} 页 (共 ${data.data.total} 条)`;
            document.getElementById('prevBtn').disabled = currentPage <= 1;
            document.getElementById('nextBtn').disabled = currentPage >= totalPages;
        } else {
            tbody.innerHTML = `<tr><td colspan="5" class="empty">加载失败: ${data.msg}</td></tr>`;
        }
    } catch (err) {
        tbody.innerHTML = '<tr><td colspan="5" class="empty">网络错误，请刷新重试</td></tr>';
    }
}

function changePage(delta) {
    const newPage = currentPage + delta;
    if (newPage >= 1 && newPage <= totalPages) {
        currentPage = newPage;
        loadStudents();
    }
}

function resetFilter() {
    document.getElementById('filterStudentId').value = '';
    document.getElementById('filterStudentName').value = '';
    document.getElementById('filterGender').value = '';
    document.getElementById('filterBegin').value = '';
    document.getElementById('filterEnd').value = '';
    currentPage = 1;
    loadStudents();
}

function handleDelete(studentId, studentName) {
    if (!confirm(`确定要删除学生「${studentName}」(学号: ${studentId}) 吗？`)) {
        return;
    }
    fetch('/student/' + studentId, { method: 'DELETE' })
        .then(res => res.json())
        .then(data => {
            if (data.code === 1) {
                showToast('删除成功');
                loadStudents();
            } else {
                showToast('删除失败: ' + (data.msg || '未知错误'));
            }
        })
        .catch(() => showToast('网络错误，删除失败'));
}

function showToast(msg) {
    const existing = document.querySelector('.toast-notification');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.className = 'toast-notification';
    toast.textContent = msg;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('toast-fade');
        setTimeout(() => toast.remove(), 300);
    }, 2000);
}

function escHtml(str) {
    const div = document.createElement('div');
    div.appendChild(document.createTextNode(str));
    return div.innerHTML;
}

loadStudents();
