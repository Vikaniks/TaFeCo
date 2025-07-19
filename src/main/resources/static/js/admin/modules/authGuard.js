export async function checkRoleAccess(allowedRoles) {
  console.log('➡️ checkRoleAccess вызван с ролями:', allowedRoles);
  const jwt = localStorage.getItem('jwt');
  if (!jwt) return false;

  try {
    const res = await fetch('/api/auth/roles', {
      headers: {
        'Authorization': `Bearer ${jwt}`
      }
    });

    if (!res.ok) return false;

    const roles = await res.json(); // ['ROLE_ADMIN', 'ROLE_USER']
    return roles.some(role => allowedRoles.includes(role));
  } catch (e) {
    console.error('Ошибка при проверке ролей:', e);
    return false;
  }
}


// Выход из аккаунта
/*export function logoutAdmin() {
  localStorage.removeItem('userData');
  localStorage.removeItem('jwt');
  localStorage.removeItem('loggedIn');

  sessionStorage.clear();

  if ('caches' in window) {
    caches.keys().then(names => {
      for (let name of names) {
        caches.delete(name);
      }
    });
  }

  window.location.href = '/register';
}

*/
