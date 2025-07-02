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
      localStorage.setItem("userData", JSON.stringify(userData));
    }
  });
}

function getNameFromEmail(email) {
    if (!email || typeof email !== 'string') return 'Привет!';
    const namePart = email.split('@')[0];
    return namePart.charAt(0).toUpperCase() + namePart.slice(1);
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

            if (user.email && user.email !== email) {
                console.error('Несовпадение email с сервером');
                return;
            }

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
                roles: result.user.roles || ['USER'], // или result.roles
            };


             // Сохраняем JWT
             localStorage.setItem('jwt', result.token);

             // Сохраняем полный профиль в localStorage
             localStorage.setItem('userData', JSON.stringify(profile));
             localStorage.setItem('loggedIn', 'true');

             // Обновляем отображение имени пользователя
             updateUserNameDisplay();
            alert('Вы успешно вошли в личный кабинет!');
            window.location.href = '/login';

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

  const currentUserData = JSON.parse(localStorage.getItem('userData')) || {};

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
        'Content-Type': 'application/json'
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

      localStorage.setItem('userData', JSON.stringify(result));
      saveUserData(result);
      updateUserProfileDisplay(result);


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


/*export function setupUserMenu() {
    const userIcon = document.querySelector('.user-container img');
    const dropdownMenu = document.getElementById('dropdown-menu');
    const orderRegistrButton = document.getElementById('go-to-login');
    const logout = document.getElementById('logout');

    if (!userIcon || !dropdownMenu || !orderRegistrButton) return;

    userIcon.addEventListener('mouseover', () => {
        dropdownMenu.style.display = 'block';
    });

    dropdownMenu.addEventListener('mouseleave', () => {
        dropdownMenu.style.display = 'none';
    });


    if (logout) {
        logout.addEventListener('click', (e) => {
            e.preventDefault();
            try {
                localStorage.clear();
                sessionStorage.clear(); // Очистка sessionStorage для безопасности
                localStorage.setItem('loggedIn', 'false'); // Устанавливаем статус выхода
            } catch (error) {
                console.error("Ошибка при очистке localStorage:", error);
            }

            updateUserNameDisplay();
            updateCartCount();
            dropdownMenu.style.display = 'none';
            alert('Вы вышли из аккаунта.');
            window.location.href = 'index.html';
        });
    }
}*/


export function updateUserNameDisplay() {
    const loggedIn = localStorage.getItem('loggedIn');
    const userNameElem = document.getElementById('username-flower');
    if (!loggedIn || !userNameElem) return;

    if (loggedIn === 'true') {
        const userDataRaw = localStorage.getItem('userData');

        // 🛡️ проверка перед JSON.parse
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

        let displayName = 'Гость'; // по умолчанию

        if (userProfile.name && userProfile.name.trim() !== '') {
            displayName = `Привет, ${userProfile.name.trim()}!`;
        } else if (userProfile.email) {
            displayName = getNameFromEmail(userProfile.email);
        }

        userNameElem.textContent = displayName;
        return;
    }

    userNameElem.textContent = 'Гость';
}


