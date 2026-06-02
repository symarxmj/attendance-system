async function init() {
    try {
        var res = await fetch('/course/all');
        var data = await res.json();
        if (data.code === 1 && data.data) {
            var sel = document.getElementById('courseId');
            data.data.forEach(function(c) {
                sel.innerHTML += '<option value="' + escHtml(c.courseId) + '">' + escHtml(c.courseName) + '</option>';
            });
            if (isEdit && origCourseId) sel.value = origCourseId;
        }
    } catch(e) {}
    if (isEdit) document.getElementById('status').value = origStatus;
}

document.getElementById('mainForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    var data = {
        courseId: document.getElementById('courseId').value,
        sessionDate: document.getElementById('sessionDate').value,
        weekNumber: parseInt(document.getElementById('weekNumber').value) || null,
        status: parseInt(document.getElementById('status').value) || 1
    };
    if (!data.courseId) { showToast('请选择课程', true); return; }
    if (!data.sessionDate) { showToast('请选择上课日期', true); return; }
    var btn = document.getElementById('submitBtn'); btn.disabled = true; btn.textContent = '保存中...';
    try {
        var res;
        if (isEdit) {
            res = await fetch('/course-session/' + entityId, {
                method: 'PUT', headers: {'Content-Type':'application/json'}, body: JSON.stringify(data)
            });
        } else {
            res = await fetch('/course-session', {
                method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify(data)
            });
        }
        var result = await res.json();
        if (result.code === 1) { showToast('保存成功'); setTimeout(function() { window.location.href = '/course-session/list-page'; }, 800); }
        else { showToast(result.msg || '操作失败', true); }
    } catch(err) { showToast('网络错误', true); }
    finally { btn.disabled = false; btn.textContent = '保存'; }
});

function escHtml(s) { if (!s) return ''; var d = document.createElement('div'); d.appendChild(document.createTextNode(s)); return d.innerHTML; }
function showToast(m, e) { var x = document.querySelector('.toast-notification'); if (x) x.remove(); var t = document.createElement('div'); t.className = 'toast-notification' + (e ? ' toast-error' : ''); t.textContent = m; document.body.appendChild(t); setTimeout(function() { t.classList.add('toast-fade'); setTimeout(function() { t.remove(); }, 300); }, 2000); }
init();
