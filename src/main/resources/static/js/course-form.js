async function init() {
    try {
        var tr = await fetch('/user/list?pageSize=1000&role=TEACHER');
        var td = await tr.json();
        if (td.code === 1 && td.data.rows) {
            var sel = document.getElementById('teacherId');
            td.data.rows.forEach(function(u) {
                sel.innerHTML += '<option value="' + u.id + '">' + escHtml(u.realName) + ' (' + escHtml(u.username) + ')</option>';
            });
            if (isEdit && origTeacherId) sel.value = origTeacherId;
        }
        var cr = await fetch('/classroom/all');
        var cd = await cr.json();
        if (cd.code === 1 && cd.data) {
            var sel2 = document.getElementById('classroomId');
            cd.data.forEach(function(c) {
                sel2.innerHTML += '<option value="' + c.id + '">' + escHtml(c.classroomName) + '</option>';
            });
            if (isEdit && origClassroomId) sel2.value = origClassroomId;
        }
    } catch (e) {}
}

document.getElementById('mainForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    var data = {
        courseId: document.getElementById('courseId').value.trim(),
        courseName: document.getElementById('courseName').value.trim(),
        teacherId: document.getElementById('teacherId').value ? parseInt(document.getElementById('teacherId').value) : null,
        classroomId: document.getElementById('classroomId').value ? parseInt(document.getElementById('classroomId').value) : null,
        weekday: parseInt(document.getElementById('weekday').value) || null,
        startWeek: parseInt(document.getElementById('startWeek').value) || null,
        endWeek: parseInt(document.getElementById('endWeek').value) || null
    };
    if (!data.courseId) { showToast('课程编号不能为空', true); return; }
    if (!data.courseName) { showToast('课程名称不能为空', true); return; }

    var btn = document.getElementById('submitBtn'); btn.disabled = true; btn.textContent = '保存中...';
    try {
        var res;
        if (isEdit) {
            res = await fetch('/course/' + encodeURIComponent(origCourseId), {
                method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data)
            });
        } else {
            res = await fetch('/course', {
                method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data)
            });
        }
        var result = await res.json();
        if (result.code === 1) { showToast('保存成功'); setTimeout(function() { window.location.href = '/course/list-page'; }, 800); }
        else { showToast(result.msg || '操作失败', true); }
    } catch (err) { showToast('网络错误', true); }
    finally { btn.disabled = false; btn.textContent = '保存'; }
});

function escHtml(s) { if (!s) return ''; var d = document.createElement('div'); d.appendChild(document.createTextNode(s)); return d.innerHTML; }
function showToast(m, e) { var x = document.querySelector('.toast-notification'); if (x) x.remove(); var t = document.createElement('div'); t.className = 'toast-notification' + (e ? ' toast-error' : ''); t.textContent = m; document.body.appendChild(t); setTimeout(function() { t.classList.add('toast-fade'); setTimeout(function() { t.remove(); }, 300); }, 2000); }
init();
