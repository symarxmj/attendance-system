async function init() {
    try {
        var sr = await fetch('/student');
        var sd = await sr.json();
        if (sd.code === 1 && sd.data) {
            var sel = document.getElementById('studentId');
            sd.data.forEach(function(s) {
                sel.innerHTML += '<option value="' + escHtml(s.studentId) + '">' + escHtml(s.studentName) + ' (' + escHtml(s.studentId) + ')</option>';
            });
            if (isEdit && origStudentId) sel.value = origStudentId;
        }
        var cr = await fetch('/course/all');
        var cd = await cr.json();
        if (cd.code === 1 && cd.data) {
            var sel2 = document.getElementById('courseId');
            cd.data.forEach(function(c) {
                sel2.innerHTML += '<option value="' + escHtml(c.courseId) + '">' + escHtml(c.courseName) + '</option>';
            });
            if (isEdit && origCourseId) sel2.value = origCourseId;
        }
    } catch(e) {}
}

document.getElementById('mainForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    var data = {
        studentId: document.getElementById('studentId').value,
        courseId: document.getElementById('courseId').value
    };
    if (!data.studentId) { showToast('请选择学生', true); return; }
    if (!data.courseId) { showToast('请选择课程', true); return; }
    var btn = document.getElementById('submitBtn'); btn.disabled = true; btn.textContent = '保存中...';
    try {
        var res;
        if (isEdit) {
            res = await fetch('/course-selection/' + entityId, {
                method: 'PUT', headers: {'Content-Type':'application/json'}, body: JSON.stringify(data)
            });
        } else {
            res = await fetch('/course-selection', {
                method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify(data)
            });
        }
        var result = await res.json();
        if (result.code === 1) { showToast('保存成功'); setTimeout(function() { window.location.href = '/course-selection/list-page'; }, 800); }
        else { showToast(result.msg || '操作失败', true); }
    } catch(err) { showToast('网络错误', true); }
    finally { btn.disabled = false; btn.textContent = '保存'; }
});

function escHtml(s) { if (!s) return ''; var d = document.createElement('div'); d.appendChild(document.createTextNode(s)); return d.innerHTML; }
function showToast(m, e) { var x = document.querySelector('.toast-notification'); if (x) x.remove(); var t = document.createElement('div'); t.className = 'toast-notification' + (e ? ' toast-error' : ''); t.textContent = m; document.body.appendChild(t); setTimeout(function() { t.classList.add('toast-fade'); setTimeout(function() { t.remove(); }, 300); }, 2000); }
init();
