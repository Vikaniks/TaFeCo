import { redirectIfLoggedIn, authFetch } from './auth.js';
import { saveUserData, mappings } from './storage.js';

// Сбор данных с формы
function collectUserDataFromForm() {
  const form = document.getElementById('register-form');
  if (!form) return;

  const nameInput = document.getElementById('name');
  const surnameInput = document.getElementById('surname');
  const phoneInput = document.getElementById('phone');
  const emailInput = document.getElementById('email');

  const formData = {};

  const savedData = localStorage.getItem("userData");
  if (savedData) {
      const userData = JSON.parse(savedData);
      if (userData.name) nameInput.value = userData.name;
      if (userData.surname) surnameInput.value = userData.surname;
      if (userData.phone) phoneInput.value = userData.phone;
      if (userData.email) emailInput.value = userData.email;
  } else {
      saveUserData();
  }

  for (const key in mappings) {
    const el = document.getElementById(mappings[key]);
    if (el) {
      formData[key] = el.value?.trim?.() || '';
    }
  }

  // Пароль и подтверждение
  formData.password = document.getElementById('password')?.value || '';
  formData.confirmPassword = document.getElementById('confirm-password')?.value || '';

  return formData;
}

// Обработчик регистрации
export function handleRegistration(event) {
  event.preventDefault();

  const nameInput = document.getElementById('name');
  const surnameInput = document.getElementById('surname');
  const phoneInput = document.getElementById('phone');
  const emailInput = document.getElementById('email');

  const formData = collectUserDataFromForm();

  function showErrorMessage(field, message) {
          const errorSpan = field.nextElementSibling;
          if (!errorSpan || !errorSpan.classList.contains("error-message")) return;

          errorSpan.textContent = message;
          errorSpan.style.display = "block";
          field.classList.add("error");
      }

      function clearErrorMessage(field) {
          const errorSpan = field.nextElementSibling;
          if (!errorSpan || !errorSpan.classList.contains("error-message")) return;

          errorSpan.textContent = "";
          errorSpan.style.display = "none";
          field.classList.remove("error");
      }

  function validateField(field) {
          let isValid = true;

          if (field.value.trim() === "") {
              showErrorMessage(field, "Заполните правильно");
              isValid = false;
          } else {
              clearErrorMessage(field);
          }

          if (field === nameInput || field === surnameInput) {
              const namePattern = /^[А-Яа-яЁёA-Za-z]+$/;
              if (!namePattern.test(field.value.trim())) {
                  showErrorMessage(field, "Допустимы только буквы");
                  isValid = false;
              }
          }


          if (field === phoneInput) {
              const phonePattern = /^\+7 \(\d{3}\) \d{3} \d{2} \d{2}$/;
              if (!phonePattern.test(field.value.trim())) {
                  showErrorMessage(field, "Введите корректный номер");
                  isValid = false;
              }
          }

          if (field === emailInput) {
              const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
              if (!emailPattern.test(field.value.trim())) {
                  showErrorMessage(field, "Введите корректный email");
                  isValid = false;
              }
          }

          return isValid;
      }

  // Проверка обязательных полей
  const requiredFields = ['name', 'surname', 'phone', 'email', 'locality', 'street', 'house', 'apartment', 'password', 'confirmPassword'];
  for (const field of requiredFields) {
    if (!formData[field]) {
      alert('Пожалуйста, заполните все обязательные поля.');
      return;
    }
  }

  // Проверка пароля
  const passwordRegex = /^(?=.*[A-Z])(?=.*\d).{8,}$/;
  if (!passwordRegex.test(formData.password)) {
    alert('Пароль должен содержать минимум 8 латинских символов, одну заглавную букву и одну цифру.');
    return;
  }

  if (formData.password !== formData.confirmPassword) {
    alert('Пароли не совпадают.');
    return;
  }

  // Отправка на сервер
  const requestData = { ...formData };
  delete requestData.confirmPassword;

  fetch('/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(requestData)
  })
    .then(response => {
      if (!response.ok) throw new Error('Ошибка регистрации');
      return response.json();
    })
    .then(data => {
      localStorage.setItem('userData', JSON.stringify(data));
      localStorage.setItem('loggedIn', 'true');

          // Обновляем отображение имени пользователя в шапке
          updateUserNameDisplay();
      alert('Вы успешно зарегистрированы!');
      window.location.href = '/login';
    })
    .catch(err => {
      console.error(err);
      alert('Пользователь уже существует');
    });
}

// --- Автосохранение данных при вводе ---
export function setupFormAutoSave() {
  const form = document.getElementById('register-form');
  if (!form) return;

  form.addEventListener("input", () => {
    const userData = saveUserData();
    if (userData) {
      localStorage.setItem("userData", JSON.stringify({ user: userData }));
    }
  });
}

function getNameFromEmail(email) {
    if (!email || typeof email !== 'string') return 'Привет!';
    const namePart = email.split('@')[0];
    return namePart.charAt(0).toUpperCase() + namePart.slice(1);
}


function parseJwt(token) {
  try {
    const base64Payload = token.split('.')[1];
    const payload = atob(base64Payload);
    return JSON.parse(payload);
  } catch (e) {
    console.error('Ошибка парсинга JWT:', e);
    return null;
  }
}

////////////////////////

export async function checkRoleAccess(allowedRoles) {
  const jwt = localStorage.getItem('jwt');
  if (!jwt) return false;

  try {
    const res = await fetch('/api/auth/roles', {
      headers: {
        'Authorization': `Bearer ${jwt}`
      }
    });

    if (!res.ok) return false;

    const roles = await res.json();
    return roles.some(role => allowedRoles.includes(role));
  } catch (e) {
    console.error('Ошибка при проверке ролей:', e);
    return false;
  }
}


/*
export async function userHasRole(role) {
  const userData = localStorage.getItem('userData');
  const jwt = localStorage.getItem('jwt');

  if (!userData || !jwt) return false;

  try {
    const parsed = JSON.parse(userData);
    const roles = parsed.user?.roles || [];

    if (!roles.includes(role)) return false;

    console.log(atob(jwt.split('.')[1]));
    console.log('userHasRole вызван с ролью:', role);

    // 👉 Просто делаем переход, если роль подходящая
    if (role === 'ROLE_ADMIN' || role === 'ROLE_SUPERADMIN' || role === 'ROLE_MODERATOR') {
      window.location.href = '/admin';
    }

    return true;
  } catch (err) {
    console.error('Ошибка в userHasRole:', err);
    return false;
  }
}
*/
export async function redirectByRole(roles) {
  console.log('📢 redirectByRole вызван с:', roles);

  const jwt = localStorage.getItem('jwt');
  if (!jwt) {
    console.warn('❌ JWT не найден. Перенаправляем на главную');
    window.location.href = '/index';
    return;
  }

  try {
    const response = await fetch('/api/auth/roles', {
      headers: {
        'Authorization': `Bearer ${jwt}`
      }
    });

    if (!response.ok) {
      console.warn('⚠️ Не удалось получить роли с сервера. Перенаправляем на /login');
      window.location.href = '/register';
      return;
    }

    const serverRoles = await response.json();
    console.log('✅ Роли с сервера:', serverRoles);

    if (
      serverRoles.includes('ROLE_ADMIN') ||
      serverRoles.includes('ROLE_MODERATOR') ||
      serverRoles.includes('ROLE_SUPERADMIN')
    ) {
      console.log('✅ Редиректим в /admin');
      window.location.href = '/admin';
    } else {
      console.log('❌ Вы пользователь. Переход в магазин');
      window.location.href = '/shop';
    }
  } catch (error) {
    console.error('Ошибка при проверке ролей:', error);
    window.location.href = '/register';
  }
}



export async function handleLogin(event) {
  event.preventDefault();

  const form = event.target.closest('form');
  const emailInput = form.querySelector('#login-email');
  const passwordInput = form.querySelector('#login-password');

  if (!emailInput || !passwordInput) {
    console.error('Не найдены поля email или password');
    return;
  }

  const email = emailInput.value.trim();
  const password = passwordInput.value.trim();

  try {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username: email,
        password: password
      })
    });

    if (response.ok) {
      const result = await response.json();
      console.log('result:', result);

      const user = result.user;
      if (!user) {
        alert('Ошибка сервера: данные пользователя не получены');
        return;
      }

      if (user.email && user.email !== email) {
        console.error('Несовпадение email с сервером');
        return;
      }

      const payload = parseJwt(result.token);
      localStorage.setItem('temporaryPassword', payload?.temporaryPassword ? 'true' : 'false');

      const name = (user.name && user.name.trim() !== '') ? user.name : getNameFromEmail(user.email);

      const profile = {
        id: user.id,
        name: name,
        surname: user.surname || '',
        phone: user.phone || '',
        email: user.email || email,
        locality: user.locality || '',
        district: user.district || '',
        region: user.region || '',
        street: user.street || '',
        house: user.house || '',
        apartment: user.apartment || '',
        addressExtra: user.addressExtra || '',
        active: user.active || false,
        roles: user.roles || ['USER'],
      };

      localStorage.setItem('jwt', result.token);
      localStorage.setItem('userData', JSON.stringify({ user: profile }));
      localStorage.setItem('loggedIn', 'true');

      const roles = user.roles || [];
      console.log('🟡 Роли, полученные от сервера:', roles);

      // или напрямую из localStorage:
      const storedUser = JSON.parse(localStorage.getItem('userData'));
      console.log('🔵 Роли из localStorage:', storedUser.user.roles);

      updateUserNameDisplay();
      // 👉 Редирект по ролям
      await redirectByRole(profile.roles);

    } else if (response.status === 401) {
      alert('Неверный логин или пароль.');
    } else {
      const errorText = await response.text();
      alert('Ошибка сервера: ' + errorText);
    }

  } catch (error) {
    console.error('Ошибка при входе:', error);
    alert('Ошибка сети. Попробуйте позже.');
  }
}

function updateUserProfileDisplay(user) {
  document.getElementById('name').textContent = user.name || '';
  document.getElementById('email').textContent = user.email || '';
  document.getElementById('phone').textContent = user.phone || '';
  document.getElementById('locality').textContent = user.locality || '';
  document.getElementById('district').textContent = user.district || '';
  document.getElementById('region').textContent = user.region || '';
  document.getElementById('street').textContent = user.street || '';
  document.getElementById('house').textContent = user.house || '';
  document.getElementById('apartment').textContent = user.apartment || '';
  document.getElementById('address-extra').textContent = user.addressExtra || '';
}



export async function handleProfileFormSubmit(event) {
  event.preventDefault();

  const password = document.getElementById('password').value.trim();

  if (!password) {
    alert('Введите текущий пароль');
    return;
  }

  // Получаем данные пользователя из localStorage с учётом вложенности userData.user
  const rawUserData = JSON.parse(localStorage.getItem('userData')) || {};
  const currentUserData = rawUserData.user || {};

  function getFieldValue(id, currentValue) {
    const val = document.getElementById(id).value.trim();
    const result = val === '' ? currentValue || '' : val;
    console.log(id, 'formVal:', val, 'currentVal:', currentValue, 'used:', result);
    return result;
  }

  const updatedData = {
    name: getFieldValue('new-name', currentUserData.name),
    phone: getFieldValue('new-phone', currentUserData.phone),
    locality: getFieldValue('new-locality', currentUserData.locality),
    district: getFieldValue('new-district', currentUserData.district),
    region: getFieldValue('new-region', currentUserData.region),
    street: getFieldValue('new-street', currentUserData.street),
    house: getFieldValue('new-house', currentUserData.house),
    apartment: getFieldValue('new-apartment', currentUserData.apartment),
    addressExtra: getFieldValue('new-address-extra', currentUserData.addressExtra),
    currentPassword: password,
  };

  console.log('Отправляем данные на сервер:', updatedData);

  try {
    const token = localStorage.getItem('jwt');

    if (!token) {
      alert('Вы не авторизованы, пожалуйста, войдите заново');
      return;
    }

    const response = await authFetch('/api/user/profile', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      body: JSON.stringify(updatedData)
    });

    if (response.ok) {
      const result = await response.json();
      alert('Данные успешно обновлены');

      console.log('Ответ сервера:', result);

      if (!result || !result.id) {
        alert('Сервер не вернул данные пользователя');
        return;
      }

      // Сохраняем в localStorage с вложенностью user
      localStorage.setItem('userData', JSON.stringify({ user: result }));

      // Обновляем данные в приложении
      saveUserData({ user: result });

      // Обновляем отображение профиля, передаем непосредственно объект пользователя
      updateUserProfileDisplay(result);

      // Очищаем поле пароля
      document.getElementById('password').value = '';

    } else if (response.status === 401) {
      alert('Неверный текущий пароль');
    } else {
      const errorData = await response.json();
      alert('Ошибка обновления: ' + (errorData.message || 'Неизвестная ошибка'));
    }
  } catch (error) {
    alert('Ошибка сети или сервера: ' + error.message);
  }
}

export function updateUserNameDisplay() {
    const loggedIn = localStorage.getItem('loggedIn');
    const userNameElem = document.getElementById('username-flower');
    if (!loggedIn || !userNameElem) return;

    if (loggedIn === 'true') {
        const userDataRaw = localStorage.getItem('userData');

        if (!userDataRaw || userDataRaw === 'undefined') {
            console.warn('userData в localStorage отсутствует или поврежден');
            userNameElem.textContent = 'Гость';
            return;
        }

        let userProfile;
        try {
            userProfile = JSON.parse(userDataRaw);
        } catch (e) {
            console.error('Ошибка при JSON.parse(userData):', e);
            userNameElem.textContent = 'Гость';
            return;
        }

        // Здесь берем вложенный объект user
        const user = userProfile.user || {};

        let displayName = 'Гость'; // по умолчанию

        if (user.name && user.name.trim() !== '') {
            displayName = `Привет, ${user.name.trim()}!`;
        } else if (user.email) {
            displayName = getNameFromEmail(user.email);
        }

        userNameElem.textContent = displayName;
        return;
    }

    userNameElem.textContent = 'Гость';
}

