/**
 * DevOps Qualification Verification System (QVS 2.0)
 * Frontend Interactive Client with Blockchain, AI Agent & Real-Time Telemetry
 */

// Helper to get stored auth token
function getAuthToken() {
    return localStorage.getItem('qvs_token');
}

function getStoredUser() {
    const user = localStorage.getItem('qvs_user');
    return user ? JSON.parse(user) : null;
}

function saveAuthSession(authResponse) {
    localStorage.setItem('qvs_token', authResponse.token);
    localStorage.setItem('qvs_user', JSON.stringify(authResponse));
}

function logout() {
    localStorage.removeItem('qvs_token');
    localStorage.removeItem('qvs_user');
    window.location.href = '/login';
}

// ============================================================================
// 1. PUBLIC VERIFICATION & BLOCKCHAIN / AI PROOF LOOKUP
// ============================================================================
async function verifyCredential(queryOverride) {
    const queryInput = document.getElementById('verificationInput');
    const query = queryOverride || (queryInput ? queryInput.value.trim() : '');

    if (!query) {
        alert('Please enter a certificate number, student ID, or digital fingerprint hash.');
        return;
    }

    if (queryInput) queryInput.value = query;

    const resultCard = document.getElementById('resultCard');
    const loadingSpinner = document.getElementById('loadingIndicator');

    if (loadingSpinner) loadingSpinner.style.display = 'block';
    if (resultCard) resultCard.style.display = 'none';

    try {
        const token = getAuthToken();
        const user = getStoredUser();
        const headers = { 'Content-Type': 'application/json' };
        if (token) {
            headers['Authorization'] = 'Bearer ' + token;
        }

        const verifierName = user ? (user.fullName || user.username) : 'Authorized Officer';
        const verifierOrg = user ? (user.institutionName || (user.role ? user.role.replace('ROLE_', '') : 'Authorized Entity')) : 'Authorized Verifier';

        const response = await fetch('/api/v1/verify', {
            method: 'POST',
            headers: headers,
            body: JSON.stringify({
                query: query,
                verifierName: verifierName,
                verifierOrganization: verifierOrg
            })
        });

        const data = await response.json();
        displayVerificationResult(data);

        // Also fetch AI analysis for this certificate
        if (data.certificateNumber) {
            fetchAiAnalysis(data.certificateNumber);
        }
    } catch (error) {
        console.error('Verification error:', error);
        alert('An error occurred during verification. Please check if the server is running.');
    } finally {
        if (loadingSpinner) loadingSpinner.style.display = 'none';
    }
}

function displayVerificationResult(data) {
    const resultCard = document.getElementById('resultCard');
    if (!resultCard) return;

    resultCard.style.display = 'block';

    const statusBadge = document.getElementById('statusBadge');
    const statusMessage = document.getElementById('statusMessage');
    const certDetails = document.getElementById('certDetails');

    let badgeClass = 'status-badge-warning';
    let badgeText = data.verificationStatus.replace(/_/g, ' ');

    if (data.verificationStatus === 'GENUINE_AND_VALID') {
        badgeClass = 'status-badge-success';
        badgeText = '✓ GENUINE & VALID';
        resultCard.style.borderLeftColor = 'var(--accent)';
    } else if (data.verificationStatus === 'REVOKED_CREDENTIAL') {
        badgeClass = 'status-badge-danger';
        badgeText = '⚠ REVOKED CREDENTIAL';
        resultCard.style.borderLeftColor = 'var(--danger)';
    } else if (data.verificationStatus === 'SUSPICIOUS_OR_ALTERED') {
        badgeClass = 'status-badge-danger';
        badgeText = '⛔ TAMPERED / INVALID';
        resultCard.style.borderLeftColor = 'var(--danger)';
    } else {
        badgeClass = 'status-badge-warning';
        badgeText = '✕ RECORD NOT FOUND';
        resultCard.style.borderLeftColor = 'var(--warning)';
    }

    statusBadge.className = 'status-badge ' + badgeClass;
    statusBadge.innerText = badgeText;
    statusMessage.innerText = data.message;

    if (data.certificateNumber) {
        if (certDetails) certDetails.style.display = 'block';
        const setVal = (id, val) => { const el = document.getElementById(id); if (el) el.innerText = val || 'N/A'; };
        setVal('resStudentName', data.studentFullName);
        setVal('resStudentId', data.studentIdNumber);
        setVal('resAwardTitle', data.awardTitle);
        setVal('resSpecialization', data.majorSpecialization);
        setVal('resClassification', data.classification);
        setVal('resInstitution', data.institutionName);
        setVal('resAwardDate', data.awardDate);
        setVal('resCertNumber', data.certificateNumber);
        setVal('resFingerprint', data.digitalFingerprint);
        setVal('resAuditId', 'AUDIT-' + (data.auditLogId || '0'));
    } else {
        if (certDetails) certDetails.style.display = 'none';
    }

    resultCard.scrollIntoView({ behavior: 'smooth' });
}

// Fetch AI analysis for the verification card
async function fetchAiAnalysis(certNumber) {
    try {
        const response = await fetch(`/api/ai/analyze/${encodeURIComponent(certNumber)}`);
        if (!response.ok) return;
        const ai = await response.json();

        const aiBox = document.getElementById('resAiAssessmentBox');
        const aiBadge = document.getElementById('resAiRiskBadge');
        const aiText = document.getElementById('resAiExplanation');

        if (aiBox && aiBadge && aiText) {
            aiBadge.className = 'ai-badge ' + (ai.riskLevel === 'LOW' ? 'ai-badge-low' : 'ai-badge-critical');
            aiBadge.innerText = `${ai.riskLevel} RISK (${ai.authenticityScore}/100)`;
            aiText.innerText = `${ai.recommendation} — ${ai.agentExplanation}`;
        }
    } catch (e) {
        console.warn('AI analysis lookup error:', e);
    }
}

// ============================================================================
// 2. BLOCKCHAIN LEDGER EXPLORER
// ============================================================================
async function loadBlockchainTable() {
    const tbody = document.getElementById('blockchainTableBody');
    if (!tbody) return;

    try {
        const response = await fetch('/api/blockchain/chain');
        const blocks = await response.json();

        tbody.innerHTML = '';
        if (blocks.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: #64748b;">No blocks on chain.</td></tr>';
            return;
        }

        blocks.forEach(b => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td><strong>#${b.blockIndex}</strong></td>
                <td><small>${new Date(b.timestamp).toLocaleString()}</small></td>
                <td><code>${b.certificateNumber}</code></td>
                <td><small class="font-mono">${b.dataHash ? b.dataHash.substring(0, 16) + '...' : 'N/A'}</small></td>
                <td><small class="font-mono">${b.previousHash.substring(0, 16)}...</small></td>
                <td><small class="font-mono" style="color: #2563eb; font-weight: 600;">${b.blockHash.substring(0, 16)}...</small></td>
                <td><span class="chip-btn chip-success">${b.actionType}</span></td>
            `;
            tbody.appendChild(row);
        });

        const statBlocks = document.getElementById('statBlocks');
        if (statBlocks) statBlocks.innerText = blocks.length;
    } catch (e) {
        console.error('Error loading blockchain:', e);
    }
}

async function loadDashboardBlockchain() {
    const tbody = document.getElementById('dashBlockchainTableBody');
    if (!tbody) return;

    try {
        const response = await fetch('/api/blockchain/chain');
        const blocks = await response.json();

        tbody.innerHTML = '';
        blocks.forEach(b => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td><strong>#${b.blockIndex}</strong></td>
                <td><small>${new Date(b.timestamp).toLocaleString()}</small></td>
                <td><code>${b.certificateNumber}</code></td>
                <td><small class="font-mono">${b.dataHash ? b.dataHash.substring(0, 16) + '...' : 'N/A'}</small></td>
                <td><small class="font-mono">${b.previousHash.substring(0, 16)}...</small></td>
                <td><small class="font-mono" style="color: #2563eb; font-weight: 600;">${b.blockHash.substring(0, 16)}...</small></td>
                <td><span class="chip-btn chip-success">${b.actionType}</span></td>
            `;
            tbody.appendChild(row);
        });
    } catch (e) {
        console.error('Error loading dashboard blockchain:', e);
    }
}

async function validateBlockchain() {
    try {
        const response = await fetch('/api/blockchain/validate');
        const res = await response.json();

        if (res.valid) {
            alert(`✅ BLOCKCHAIN CONSENSUS CONFIRMED!\n\nAll ${res.chainHeight} blocks are cryptographically intact with zero tampering detected.`);
        } else {
            alert(`⚠️ INTEGRITY WARNING!\n\nBlockchain data mismatch detected.`);
        }
    } catch (e) {
        console.error('Validate blockchain error:', e);
        alert('Could not complete blockchain validation check.');
    }
}

// ============================================================================
// 3. AGENTIC AI CHATBOT ASSISTANT
// ============================================================================
function toggleAiChat() {
    const win = document.getElementById('aiChatWindow');
    if (!win) return;
    win.style.display = win.style.display === 'none' || win.style.display === '' ? 'flex' : 'none';
}

async function sendAiMessage() {
    const input = document.getElementById('aiChatInput');
    const msg = input ? input.value.trim() : '';
    if (!msg) return;

    appendAiMessage('user', msg);
    if (input) input.value = '';

    try {
        const response = await fetch('/api/ai/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message: msg })
        });
        const res = await response.json();
        appendAiMessage('bot', res.reply);
    } catch (e) {
        appendAiMessage('bot', 'Sorry, I encountered an error communicating with the AI service.');
    }
}

function sendAiPrompt(promptText) {
    const input = document.getElementById('aiChatInput');
    if (input) input.value = promptText;
    sendAiMessage();
}

function appendAiMessage(sender, text) {
    const container = document.getElementById('aiChatMessages');
    if (!container) return;

    const div = document.createElement('div');
    div.className = `ai-message ai-${sender}`;

    // Convert newlines and markdown-like bold to HTML
    let formatted = text
        .replace(/\n/g, '<br>')
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
        .replace(/`(.*?)`/g, '<code>$1</code>');

    div.innerHTML = formatted;
    container.appendChild(div);
    container.scrollTop = container.scrollHeight;
}

// AI Anomaly Scanner on Dashboard
async function runAiScan() {
    const certInput = document.getElementById('aiScanInput');
    const cert = certInput ? certInput.value.trim() : '';
    if (!cert) {
        alert('Please enter a certificate number to scan.');
        return;
    }

    try {
        const response = await fetch(`/api/ai/analyze/${encodeURIComponent(cert)}`);
        const data = await response.json();

        const resBox = document.getElementById('aiScanResult');
        if (resBox) resBox.style.display = 'block';

        document.getElementById('aiScanCert').innerText = data.certificateNumber;
        const badge = document.getElementById('aiScanScoreBadge');
        badge.className = 'ai-badge ' + (data.riskLevel === 'LOW' ? 'ai-badge-low' : 'ai-badge-critical');
        badge.innerText = `SCORE: ${data.authenticityScore}/100 (${data.riskLevel} RISK)`;

        document.getElementById('aiScanRecommendation').innerText = data.recommendation;
        document.getElementById('aiScanExplanation').innerText = data.agentExplanation;

        const passedList = document.getElementById('aiPassedList');
        passedList.innerHTML = '';
        data.passedChecks.forEach(p => {
            const li = document.createElement('li');
            li.innerText = p;
            passedList.appendChild(li);
        });

        const anomalyList = document.getElementById('aiAnomalyList');
        anomalyList.innerHTML = '';
        data.anomalyFlags.forEach(a => {
            const li = document.createElement('li');
            li.innerText = a;
            anomalyList.appendChild(li);
        });
    } catch (e) {
        console.error('AI scan error:', e);
        alert('Failed to run AI fraud scan.');
    }
}

// ============================================================================
// 4. REAL-TIME SYSTEM TELEMETRY
// ============================================================================
async function fetchTelemetry() {
    try {
        const response = await fetch('/api/metrics/realtime');
        if (!response.ok) return;
        const m = await response.json();

        const setVal = (id, val) => { const el = document.getElementById(id); if (el) el.innerText = val; };

        // Top stats on landing page
        setVal('statQualifications', m.totalQualifications);
        setVal('statBlocks', m.totalBlocks);
        setVal('statVerifications', `${m.verificationSuccessRate}%`);

        // Dashboard Telemetry
        const usedMb = Math.round(m.usedMemoryBytes / (1024 * 1024));
        const totalMb = Math.round(m.totalMemoryBytes / (1024 * 1024));
        setVal('metricRam', `${usedMb} MB`);
        setVal('metricRamTotal', `Total Allocated: ${totalMb} MB (${m.availableProcessors} CPU Cores)`);
        setVal('metricUptime', `${m.systemUptimeSeconds}s`);
        setVal('metricVerifCount', m.totalVerifications);
        setVal('metricSuccessRate', `Success Rate: ${m.verificationSuccessRate}% (${m.genuineVerifications} Genuine)`);
        setVal('metricChainHeight', `#${m.totalBlocks}`);
        setVal('metricChainStatus', `Consensus: ${m.blockchainHealthy ? 'Valid & Verified' : 'Attention Needed'}`);
    } catch (e) {
        console.warn('Telemetry polling error:', e);
    }
}

// ============================================================================
// 5. QUALIFICATION MANAGEMENT & AUTH
// ============================================================================
async function handleLogin(event) {
    event.preventDefault();
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value.trim();
    const errorMsg = document.getElementById('loginError');

    try {
        const response = await fetch('/api/v1/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            const err = await response.json();
            if (errorMsg) {
                errorMsg.style.display = 'block';
                errorMsg.innerText = err.message || 'Invalid credentials.';
            }
            return;
        }

        const data = await response.json();
        saveAuthSession(data);
        window.location.href = '/dashboard';
    } catch (e) {
        console.error('Login error:', e);
        if (errorMsg) {
            errorMsg.style.display = 'block';
            errorMsg.innerText = 'Server connection error.';
        }
    }
}

async function loadQualifications() {
    const tableBody = document.getElementById('qualificationsTableBody');
    if (!tableBody) return;

    try {
        const response = await fetch('/api/v1/qualifications');
        const list = await response.json();

        tableBody.innerHTML = '';
        if (list.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="7" style="text-align: center; color: #64748b;">No qualifications found.</td></tr>';
            return;
        }

        const user = getStoredUser();
        const isAdmin = user && user.role === 'ROLE_ADMIN';

        list.forEach(q => {
            const row = document.createElement('tr');
            const statusClass = q.status === 'ACTIVE' ? 'color: #059669; font-weight: bold;' : 'color: #dc2626; font-weight: bold;';
            let actionButtons = `<button class="chip-btn chip-neutral" onclick="verifyFromDashboard('${q.certificateNumber}')">Verify</button>`;

            if (q.status === 'ACTIVE') {
                if (isAdmin) {
                    actionButtons += ` <button class="chip-btn chip-danger" onclick="revokeCert(${q.id})">Revoke</button>`;
                } else {
                    actionButtons += ` <span class="chip-btn" style="background:#f1f5f9; color:#94a3b8; font-size:0.72rem; cursor:not-allowed;" title="Only System Administrator can revoke credentials">🔒 Revoke (Admin Only)</span>`;
                }
            }

            row.innerHTML = `
                <td><strong>${q.certificateNumber}</strong></td>
                <td>${q.studentFullName}<br><small style="color: #64748b;">${q.studentIdNumber}</small></td>
                <td>${q.awardTitle}<br><small style="color: #64748b;">${q.majorSpecialization}</small></td>
                <td>${q.classification}</td>
                <td>${q.institutionName}</td>
                <td><span style="${statusClass}">${q.status}</span></td>
                <td>${actionButtons}</td>
            `;
            tableBody.appendChild(row);
        });
    } catch (e) {
        console.error('Error loading qualifications:', e);
    }
}

function verifyFromDashboard(certNumber) {
    const tabBtnVerify = document.getElementById('tabBtnVerify');
    if (tabBtnVerify && typeof switchTab === 'function') {
        switchTab('verifyTab', tabBtnVerify);
        const input = document.getElementById('verificationInput');
        if (input) input.value = certNumber;
        verifyCredential(certNumber);
    } else {
        window.location.href = '/dashboard?tab=verifyTab&cert=' + encodeURIComponent(certNumber);
    }
}

async function revokeCert(id) {
    const user = getStoredUser();
    if (!user || user.role !== 'ROLE_ADMIN') {
        alert('Access Denied: Only users with System Administrator (ROLE_ADMIN) privileges are authorized to revoke academic credentials.');
        return;
    }

    const reason = prompt('Please enter reason for revocation:');
    if (!reason) return;

    const token = getAuthToken();
    if (!token) {
        alert('You must be logged in as an Administrator to revoke qualifications.');
        window.location.href = '/login';
        return;
    }

    try {
        const response = await fetch(`/api/v1/qualifications/${id}/revoke?reason=${encodeURIComponent(reason)}`, {
            method: 'PUT',
            headers: {
                'Authorization': 'Bearer ' + token
            }
        });

        if (response.ok) {
            alert('Qualification successfully revoked and notarized on blockchain!');
            loadQualifications();
            loadDashboardBlockchain();
        } else {
            alert('Failed to revoke qualification. Ensure you have ROLE_ADMIN privileges.');
        }
    } catch (e) {
        console.error('Revoke error:', e);
    }
}

async function handleRegisterQualification(event) {
    event.preventDefault();
    const token = getAuthToken();
    const user = getStoredUser();

    if (!token || !user || (user.role !== 'ROLE_ADMIN' && user.role !== 'ROLE_INSTITUTION')) {
        alert('Access Denied: You must be signed in as an accredited Registrar Officer or Administrator to issue qualifications.');
        window.location.href = '/login';
        return;
    }

    const payload = {
        certificateNumber: document.getElementById('certNumber').value.trim(),
        studentFullName: document.getElementById('studentName').value.trim(),
        studentIdNumber: document.getElementById('studentId').value.trim(),
        awardTitle: document.getElementById('awardTitle').value.trim(),
        majorSpecialization: document.getElementById('majorSpec').value.trim(),
        classification: document.getElementById('classification').value.trim(),
        awardDate: document.getElementById('awardDate').value,
        institutionId: 1
    };

    try {
        const response = await fetch('/api/v1/qualifications', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            alert('Qualification registered and cryptographically mined on Blockchain!');
            document.getElementById('qualificationForm').reset();
            loadQualifications();
            loadDashboardBlockchain();
        } else {
            const err = await response.json();
            alert('Failed to register: ' + (err.message || 'Validation error'));
        }
    } catch (e) {
        console.error('Registration error:', e);
    }
}

// 6. Audit Trail Logs
async function loadAuditLogs() {
    const tableBody = document.getElementById('auditTableBody');
    if (!tableBody) return;

    const user = getStoredUser();
    if (!user || user.role !== 'ROLE_ADMIN') {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 2.5rem 1rem;">
                    <div style="font-size: 1.75rem; margin-bottom: 0.5rem;">🔒</div>
                    <strong style="color: #dc2626; font-size: 1.05rem;">Access Denied: System Administrator Authorization Required</strong>
                    <p style="color: #64748b; font-size: 0.85rem; margin-top: 0.5rem; max-width: 500px; margin-left: auto; margin-right: auto; line-height: 1.5;">
                        Audit trails and client IP security logs are restricted to <strong>ROLE_ADMIN</strong> accounts.
                        Your current active role is: <strong style="color: #0f172a;">${user ? user.role : 'UNAUTHENTICATED'}</strong>.
                    </p>
                    <a href="/dashboard" class="btn-primary btn-sm" style="margin-top: 1rem; display: inline-block;">Return to Dashboard</a>
                </td>
            </tr>
        `;
        return;
    }

    try {
        const token = getAuthToken();
        const headers = {};
        if (token) headers['Authorization'] = 'Bearer ' + token;

        const response = await fetch('/api/v1/audit-logs', { headers });
        const logs = await response.json();

        tableBody.innerHTML = '';
        if (logs.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="6" style="text-align: center; color: #64748b;">No verification audit logs recorded yet.</td></tr>';
            return;
        }

        logs.forEach(log => {
            const row = document.createElement('tr');
            let outcomeColor = '#059669';
            if (log.verificationOutcome === 'REVOKED_CREDENTIAL') outcomeColor = '#dc2626';
            else if (log.verificationOutcome === 'SUSPICIOUS_OR_ALTERED') outcomeColor = '#d97706';
            else if (log.verificationOutcome === 'RECORD_NOT_FOUND') outcomeColor = '#64748b';

            row.innerHTML = `
                <td><strong>#${log.id}</strong></td>
                <td>${new Date(log.timestamp).toLocaleString()}</td>
                <td><code>${log.queryParameter}</code></td>
                <td><span style="color: ${outcomeColor}; font-weight: 700;">${log.verificationOutcome}</span></td>
                <td>${log.verifierIdentifier}<br><small style="color: #64748b;">IP: ${log.ipAddress}</small></td>
                <td><small>${log.details || 'N/A'}</small></td>
            `;
            tableBody.appendChild(row);
        });
    } catch (e) {
        console.error('Error loading audit logs:', e);
    }
}

// ============================================================================
// INITIALIZATION ON PAGE LOAD
// ============================================================================
document.addEventListener('DOMContentLoaded', () => {
    // Mobile navigation toggle
    const navToggle = document.getElementById('navToggle');
    const navMenu = document.getElementById('navMenu');
    if (navToggle && navMenu) {
        navToggle.addEventListener('click', () => {
            navMenu.classList.toggle('open');
        });
    }

    // Check direct certificate query in URL
    const urlParams = new URLSearchParams(window.location.search);
    const certQuery = urlParams.get('cert');
    if (certQuery) {
        if (document.getElementById('verificationInput')) {
            verifyCredential(certQuery);
        } else {
            window.location.href = '/dashboard?tab=verifyTab&cert=' + encodeURIComponent(certQuery);
        }
    }

    // Load blockchain block table on landing page
    loadBlockchainTable();

    // Fetch live system telemetry
    fetchTelemetry();

    // Check login state for nav
    const user = getStoredUser();
    const navAuthContainer = document.getElementById('navAuthStatus');
    if (navAuthContainer && user) {
        const roleLabel = user.role ? user.role.replace('ROLE_', '') : 'USER';
        navAuthContainer.innerHTML = `
            <span style="color: #0c4a6e; font-size: 0.85rem; margin-right: 0.5rem; font-weight: 600;">User: <strong>${user.username}</strong> <small style="color: #0369a1; font-weight: 700;">(${roleLabel})</small></span>
            <a href="/dashboard?tab=verifyTab" class="chip-btn chip-success" style="text-decoration: none; margin-right: 0.35rem;">Console</a>
            <button class="chip-btn chip-danger" onclick="logout()">Logout</button>
        `;
    }
});
