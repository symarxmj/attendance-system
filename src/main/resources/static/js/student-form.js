document.getElementById('studentForm').addEventListener('submit', function(e) {
    e.preventDefault();

    var studentId = document.getElementById('studentIdVal').value.trim();
    var studentName = document.getElementById('studentName').value.trim();
    var gender = document.getElementById('gender').value;

    if (!studentId) { showToast('学号不能为空', true); return; }
    if (!studentName) { showToast('姓名不能为空', true); return; }

    var data = {
        studentId: studentId,
        studentName: studentName,
        gender: gender
    };

    var submitBtn = document.getElementById('submitBtn');
    submitBtn.disabled = true;
    submitBtn.textContent = '保存中...';

    var url = isEdit ? '/student/' + origStudentId : '/student';
    var method = isEdit ? 'PUT' : 'POST';

    fetch(url, {
        method: method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
    .then(function(r) { return r.json(); })
    .then(function(result) {
        if (result.code === 1) {
            showToast('保存成功');
            setTimeout(function() { window.location.href = '/student/list-page'; }, 800);
        } else {
            showToast(result.msg || '操作失败', true);
        }
    })
    .catch(function() { showToast('网络错误，请重试', true); })
    .finally(function() {
        submitBtn.disabled = false;
        submitBtn.textContent = '保存';
    });
});

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
