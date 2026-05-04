// Decode JWT token
function parseJwt(token) {
    try {
        const base64 = token.split('.')[1];
        const decoded = JSON.parse(atob(base64));
        return decoded;
    } catch (e) {
        return null;
    }
}

// Get current user info
function getCurrentUser() {
    const token = localStorage.getItem('token');
    if (!token) return null;
    return parseJwt(token);
}

// Get role from token
function getUserRole() {
    const user = getCurrentUser();
    if (!user) return null;
    // Spring Security prefixes roles with ROLE_
    const role = user.role || '';
    return role.replace('ROLE_', '');
}

// Check if user is logged in
function requireLogin() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/login';
        return false;
    }
    return true;
}

// Check if user has required role
function requireRole(allowedRoles) {
    if (!requireLogin()) return false;
    const role = getUserRole();
    if (!allowedRoles.includes(role)) {
        alert('Access Denied! You do not have permission to view this page.');
        window.location.href = '/dashboard';
        return false;
    }
    return true;
}

// Logout
function logout() {
    localStorage.removeItem('token');
    window.location.href = '/login';
}
