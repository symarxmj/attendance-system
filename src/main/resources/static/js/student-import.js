var selectedFile = null;

var uploadArea = document.getElementById('uploadArea');
var fileInput = document.getElementById('fileInput');
var fileName = document.getElementById('fileName');
var uploadBtn = document.getElementById('uploadBtn');

uploadArea.addEventListener('click', function() { fileInput.click(); });

uploadArea.addEventListener('dragover', function(e) {
    e.preventDefault();
    uploadArea.classList.add('dragover');
});

uploadArea.addEventListener('dragleave', function() {
    uploadArea.classList.remove('dragover');
});

uploadArea.addEventListener('drop', function(e) {
    e.preventDefault();
    uploadArea.classList.remove('dragover');
    handleFile(e.dataTransfer.files[0]);
});

fileInput.addEventListener('change', function() {
    handleFile(fileInput.files[0]);
});

function handleFile(file) {
    if (!file) return;
    var name = file.name.toLowerCase();
    if (!name.endsWith('.xlsx') && !name.endsWith('.xls')) {
        showToast('文件格式不正确，仅支持 .xlsx 和 .xls', true);
        return;
    }
    if (file.size > 10 * 1024 * 1024) {
        showToast('文件大小超过 10MB 限制', true);
        return;
    }
    selectedFile = file;
    fileName.textContent = '已选择：' + file.name;
    uploadBtn.disabled = false;
}

function doImport() {
    if (!selectedFile) return;
    uploadBtn.disabled = true;
    uploadBtn.textContent = '导入中...';
    document.getElementById('importStatus').textContent = '正在上传并解析文件，请稍候...';
    document.getElementById('resultContainer').innerHTML = '';

    var formData = new FormData();
    formData.append('file', selectedFile);

    fetch('/student/import', { method: 'POST', body: formData })
        .then(function(r) { return r.json(); })
        .then(function(d) {
            uploadBtn.disabled = false;
            uploadBtn.textContent = '开始导入';
            document.getElementById('importStatus').textContent = '';
            if (d.code === 1) {
                var result = d.data;
                var html = '<div class="result-box result-success">';
                html += '<h4>导入完成</h4>';
                html += '<p>成功：<strong>' + result.successCount + '</strong> 条，失败：<strong>' + result.failCount + '</strong> 条</p>';
                if (result.errors && result.errors.length > 0) {
                    html += '<ul class="error-list">';
                    result.errors.forEach(function(e) { html += '<li>' + escHtml(e) + '</li>'; });
                    html += '</ul>';
                }
                html += '</div>';
                document.getElementById('resultContainer').innerHTML = html;
                selectedFile = null;
                fileInput.value = '';
                fileName.textContent = '';
                uploadBtn.disabled = true;
                showToast('导入完成');
            } else {
                document.getElementById('resultContainer').innerHTML =
                    '<div class="result-box result-error"><h4>导入失败</h4><p>' + escHtml(d.msg || '') + '</p></div>';
                showToast(d.msg || '导入失败', true);
            }
        })
        .catch(function() {
            uploadBtn.disabled = false;
            uploadBtn.textContent = '开始导入';
            document.getElementById('importStatus').textContent = '';
            showToast('网络错误', true);
        });
}

function escHtml(s) { if (!s) return ''; var d = document.createElement('div'); d.appendChild(document.createTextNode(s)); return d.innerHTML; }
function showToast(m, e) { var x = document.querySelector('.toast-notification'); if (x) x.remove(); var t = document.createElement('div'); t.className = 'toast-notification' + (e ? ' toast-error' : ''); t.textContent = m; document.body.appendChild(t); setTimeout(function() { t.classList.add('toast-fade'); setTimeout(function() { t.remove(); }, 300); }, 2000); }
