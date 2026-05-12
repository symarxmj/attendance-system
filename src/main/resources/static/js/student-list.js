let currentPage = 1;
const pageSize = 10;
let totalPages = 1;

async function loadStudents() {
    const studentId = document.getElementById('filterStudentId').value.trim();
    const studentName = document.getElementById('filterStudentName').value.trim();
    const gender = document.getElementById('filterGender').value;

    const params = new URLSearchParams();
    params.append('page', currentPage);
    params.append('pageSize', pageSize);
    if (studentId) params.append('studentId', studentId);
    if (studentName) params.append('studentName', studentName);
    if (gender) params.append('gender', gender);

    const tbody = document.getElementById('studentTable');
    tbody.innerHTML = '<tr><td colspan="4" class="loading">加载中...</td></tr>';

    try {
        const res = await fetch('/student/list?' + params.toString());
        const data = await res.json();

        if (data.code === 1) {
            const rows = data.data.rows;
            totalPages = Math.ceil(data.data.total / pageSize) || 1;

            if (rows.length === 0) {
                tbody.innerHTML = '<tr><td colspan="4" class="empty">暂无数据</td></tr>';
            } else {
                tbody.innerHTML = rows.map(s => `
                    <tr>
                        <td>${s.studentId || '-'}</td>
                        <td>${s.studentName || '-'}</td>
                        <td>${s.gender || '-'}</td>
                        <td>${s.createTime || '-'}</td>
                    </tr>
                `).join('');
            }

            document.getElementById('pageInfo').textContent = `第 ${currentPage} 页 / 共 ${totalPages} 页 (共 ${data.data.total} 条)`;
            document.getElementById('prevBtn').disabled = currentPage <= 1;
            document.getElementById('nextBtn').disabled = currentPage >= totalPages;
        } else {
            tbody.innerHTML = `<tr><td colspan="4" class="empty">加载失败: ${data.msg}</td></tr>`;
        }
    } catch (err) {
        tbody.innerHTML = '<tr><td colspan="4" class="empty">网络错误，请刷新重试</td></tr>';
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
    currentPage = 1;
    loadStudents();
}

loadStudents();
