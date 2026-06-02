document.getElementById('mainForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    var data = {
        classroomName: document.getElementById('classroomName').value.trim(),
        rows: parseInt(document.getElementById('rows').value) || 0,
        cols: parseInt(document.getElementById('cols').value) || 0,
        excludeSeats: document.getElementById('excludeSeats').value.trim() || null
    };
    if (!data.classroomName) { showError('classroomNameError', '教室名称不能为空'); return; }
    if (!data.rows || !data.cols) { showToast('行数和列数不能为0', true); return; }

    var btn = document.getElementById('submitBtn');
    btn.disabled = true;
    btn.textContent = '保存中...';
    try {
        var res;
        if (isEdit) {
            res = await fetch('/classroom/' + entityId, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        } else {
            res = await fetch('/classroom', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        }
        var result = await res.json();
        if (result.code === 1) {
            showToast('保存成功');
            setTimeout(function() { window.location.href = '/classroom/list-page'; }, 800);
        } else {
            showToast(result.msg || '操作失败', true);
        }
    } catch (err) {
        showToast('网络错误', true);
    } finally {
        btn.disabled = false;
        btn.textContent = '保存';
    }
});

function showError(id, msg) {
    var el = document.getElementById(id);
    el.textContent = msg;
    el.style.display = 'block';
    setTimeout(function() { el.style.display = 'none'; }, 3000);
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
