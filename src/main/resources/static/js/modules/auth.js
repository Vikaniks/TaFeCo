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
  localStorage.removeItem('jwt');
  localStorage.removeItem('loggedIn');
  localStorage.removeItem('cart-items');

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

// auth.js

export function initForgotPassword(containerSelector) {
  const container = document.querySelector(containerSelector);
  if (!container) return;

  const forgotLink = container.querySelector('#forgot-password-link');
  const forgotForm = container.querySelector('#forgot-password-form');
  const sendBtn = container.querySelector('#send-forgot-password');
  const message = container.querySelector('#forgot-password-message');
  const emailInput = container.querySelector('#forgot-email');

  if (!forgotLink || !forgotForm || !sendBtn || !message || !emailInput) return;

  forgotLink.addEventListener('click', (e) => {
    e.preventDefault(); // отменяем переход по ссылке
    forgotForm.style.display = 'block'; // показываем блок с формой
    message.textContent = ''; // очищаем сообщение
  });

  sendBtn.addEventListener('click', async () => {
    const email = emailInput.value.trim();

    if (!email) {
      message.style.color = 'red';
      message.textContent = 'Пожалуйста, введите email.';
      return;
    }

    try {
      const response = await fetch('/api/auth/forgot-password', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email }),
      });

      if (response.ok) {
        message.style.color = 'green';
        message.textContent = 'Если такой пользователь существует, вам на почту отправлено письмо.';
        emailInput.value = '';

        // Скрываем форму
        forgotForm.style.display = 'none';

        // Убедимся, что message виден
        message.style.display = 'block';

        // Через 3 минуты очищаем сообщение и показываем форму обратно, если надо
        setTimeout(() => {
          message.textContent = '';
          // Если нужно, можно показать форму снова:
          // forgotForm.style.display = 'none'; // или 'block' в зависимости от логики
        }, 180000);
      }  else {
        const data = await response.json();
        message.style.color = 'red';
        message.textContent = data.message || 'Ошибка при отправке письма.';
      }
    } catch (error) {
      message.style.color = 'red';
      message.textContent = 'Ошибка сети. Попробуйте позже.';
    }
  });
}

let isChangePasswordListenerAdded = false;

export function showChangePasswordBlock() {
  event.preventDefault();
  const container = document.getElementById('change-password-container');
  if (!container) return;

  container.style.display = 'block';

  const form = container.querySelector('form');
  const currentPasswordInput = form.querySelector('#current-password');

  const isTemporaryPassword = localStorage.getItem('temporaryPassword') === 'true';


  if (!isChangePasswordListenerAdded) {
    form.addEventListener('submit', async (e) => {
      e.preventDefault();

      const newPassword = form.querySelector('#new-password').value.trim();
      const confirmPassword = form.querySelector('#confirm-password').value.trim();

      if (newPassword.length < 8) {
        alert('Пароль должен быть минимум 8 символов');
        return;
      }

      if (newPassword !== confirmPassword) {
        alert('Пароли не совпадают');
        return;
      }

      try {
        const bodyData = { newPassword };

        if (!isTemporaryPassword && currentPassword) {
          bodyData.currentPassword = currentPassword;
        }

        console.log('Отправляем данные смены пароля:', bodyData);


        const response = await fetch('/api/user/change-password', {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('jwt'),
          },
          body: JSON.stringify(bodyData),
        });

        if (response.ok) {
          alert('Пароль успешно изменён');

          const userDataStr = localStorage.getItem('userData');
          if (userDataStr) {
            try {
              const userData = JSON.parse(userDataStr);
              if (!userData.user) userData.user = {};
              userData.user.temporaryPassword = false;
              localStorage.setItem('userData', JSON.stringify(userData));
            } catch (err) {
              console.warn('Ошибка при обновлении userData:', err);
            }
          }

          localStorage.setItem('temporaryPassword', 'false');
          container.style.display = 'none';
        } else {
          const data = await response.json();
          alert(data.message || 'Ошибка смены пароля');
        }
      } catch {
        alert('Ошибка сети. Попробуйте позже.');
      }
    });

    isChangePasswordListenerAdded = true;
  }
}


