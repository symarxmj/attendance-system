document.getElementById('registerForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    const errorMsg = document.getElementById('errorMsg');
    const submitBtn = document.getElementById('submitBtn');

    submitBtn.disabled = true;
    submitBtn.textContent = '注册中...';
    errorMsg.classList.remove('show');

    const user = {
        username: document.getElementById('username').value.trim(),
        password: document.getElementById('password').value,
        realName: document.getElementById('realName').value.trim()
    };

    try {
        const res = await fetch('/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(user)
        });
        const data = await res.json();

        if (data.code === 1) {
            window.location.href = '/login';
        } else {
            errorMsg.textContent = data.msg || '注册失败';
            errorMsg.classList.add('show');
        }
    } catch (err) {
        errorMsg.textContent = '网络错误，请稍后重试';
        errorMsg.classList.add('show');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = '注册';
    }
});
