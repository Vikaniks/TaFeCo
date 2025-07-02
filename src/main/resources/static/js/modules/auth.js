// auth.js
export function setupAuthForm() {
    const form = document.getElementById('auth-form');
    if (!form) return;

    form.addEventListener('submit', function (event) {
        event.preventDefault();
        // Валидация и обработка логики регистрации/входа
        const username = form.querySelector('#username').value.trim();
        const password = form.querySelector('#password').value.trim();

        if (!username || !password) {
            alert('Введите логин и пароль');
            return;
        }
        const token = localStorage.getItem('token');
            if (!token) return false;

            try {
                const payload = JSON.parse(atob(token.split('.')[1]));

                // Проверка по времени истечения
                const currentTime = Math.floor(Date.now() / 1000); // в секундах
                if (payload.exp && payload.exp < currentTime) {
                    console.warn('JWT истёк');
                    return false;
                }

                return true;
            } catch (e) {
                console.error('Некорректный JWT:', e);
                return false;
            }

        localStorage.setItem('loggedIn', 'true');
        localStorage.setItem('userData', JSON.stringify({ name: name }));

        alert('Вы успешно вошли!');
        window.location.href = '/index';
    });
}


// auth.js

// Проверка, авторизован ли пользователь
export function isUserLoggedIn() {
  const token = localStorage.getItem('jwt');
      if (!token) return false;

      try {
          const payload = JSON.parse(atob(token.split('.')[1]));

          const currentTime = Math.floor(Date.now() / 1000);
          if (payload.exp && payload.exp < currentTime) {
              console.warn('JWT истёк');
              return false;
          }

          return true;
      } catch (e) {
          console.error('Некорректный JWT:', e);
          return false;
      }
}

// Перенаправление, если пользователь уже вошёл
export function redirectIfLoggedIn(redirectTo = '/login') {
  if (isUserLoggedIn()) {
    window.location.href = redirectTo;
  }
}

// Перенаправление, если пользователь НЕ вошёл (например, для защиты кабинета)
export function redirectIfNotLoggedIn(redirectTo = '/register') {
  if (!isUserLoggedIn()) {
    window.location.href = redirectTo;
  }
}

// Выход из аккаунта
export function logoutUser() {
  localStorage.removeItem('userData');
  localStorage.setItem('loggedIn', 'false');
  localStorage.removeItem('jwtToken');
  localStorage.removeItem('cart-items');

  sessionStorage.clear();

  if ('caches' in window) {
      caches.keys().then(names => {
        for (let name of names) caches.delete(name);
      });
  window.location.href = '/login';
}
}

export function authFetch(url, options = {}) {
  const token = localStorage.getItem('jwt');
  const headers = {
      ...(options.headers || {})
    };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  return fetch(url, {
    ...options,
    headers
  });
}

/*function userHasRole(role) {
  const userData = localStorage.getItem('userData');
  if (!userData) return false;

  try {
    const profile = JSON.parse(userData);
    return profile.roles && profile.roles.includes(role);
  } catch {
    return false;
  }
}*/