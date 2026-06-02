if (document.getElementById('adminContent')) {
    if (isEdit) {
        document.getElementById('role').value = userRole || '';
    }
}

document.getElementById('userForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    var data = {
        username: document.getElementById('username').value.trim(),
        password: document.getElementById('password').value,
        realName: document.getElementById('realName').value.trim(),
        role: document.getElementById('role').value
    };

    if (!data.username) {
        showError('usernameError', '用户名不能为空');
        return;
    }
    if (!isEdit && !data.password) {
        showError('passwordError', '密码不能为空');
        return;
    }
    if (!data.realName) {
        showError('realNameError', '真实姓名不能为空');
        return;
    }
    if (!data.role) {
        showToast('请选择角色', true);
        return;
    }

    var submitBtn = document.getElementById('submitBtn');
    submitBtn.disabled = true;
    submitBtn.textContent = '保存中...';

    try {
        var res;
        if (isEdit) {
            var body = { username: data.username, realName: data.realName, role: data.role };
            if (data.password) {
                body.password = data.password;
            }
            res = await fetch('/user/' + userId, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body)
            });
        } else {
            res = await fetch('/user', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
        }
        var result = await res.json();
        if (result.code === 1) {
            showToast('保存成功');
            setTimeout(function() { window.location.href = '/user/list-page'; }, 800);
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
    setTimeout(function() {
        toast.classList.add('toast-fade');
        setTimeout(function() { toast.remove(); }, 300);
    }, 2000);
}
