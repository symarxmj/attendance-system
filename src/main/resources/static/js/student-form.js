document.getElementById('studentForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const data = {
        studentId: document.getElementById('studentId').value.trim(),
        studentName: document.getElementById('studentName').value.trim(),
        gender: document.getElementById('gender').value
    };

    if (!data.studentId) {
        showError('studentIdError', '学号不能为空');
        return;
    }
    if (!data.studentName) {
        showError('studentNameError', '姓名不能为空');
        return;
    }

    const submitBtn = document.getElementById('submitBtn');
    submitBtn.disabled = true;
    submitBtn.textContent = '保存中...';

    try {
        let res;
        if (isEdit) {
            res = await fetch('/student/' + studentId, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        } else {
            res = await fetch('/student', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        }
        const result = await res.json();
        if (result.code === 1) {
            showToast('保存成功');
            setTimeout(() => { window.location.href = '/student/list-page'; }, 800);
        } else {
            showToast(result.msg || '操作失败', true);
        }
    } catch (err) {
        showToast('网络错误，请重试', true);
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = '保存';
    }
});

function showError(id, msg) {
    const el = document.getElementById(id);
    el.textContent = msg;
    el.style.display = 'block';
    setTimeout(() => { el.style.display = 'none'; }, 3000);
}

function showToast(msg, isError) {
    const existing = document.querySelector('.toast-notification');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.className = 'toast-notification' + (isError ? ' toast-error' : '');
    toast.textContent = msg;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.classList.add('toast-fade');
        setTimeout(() => toast.remove(), 300);
    }, 2000);
}
