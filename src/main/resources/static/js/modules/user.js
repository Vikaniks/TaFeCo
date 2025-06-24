// user.js

// Функция обработки регистрации
const handleRegistration = (event) => {
    event.preventDefault(); // Предотвращаем стандартное поведение

    // Получаем данные из формы
    const username = document.getElementById('name').value.trim();
    const surname = document.getElementById('surname').value.trim();
    const phone = document.getElementById('phone').value.trim();
    const email = document.getElementById('email').value.trim();
    const city = document.getElementById('city').value.trim();
    const address = document.getElementById('address').value.trim();
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;

    // Проверяем обязательные поля
    if (!username || !surname || !phone || !email || !city || !address || !password || !confirmPassword) {
        alert('Пожалуйста, заполните все обязательные поля.');
        return;
    }

    // Проверяем требования к паролю
    const passwordRequirements = /^(?=.*[A-Z])(?=.*\d).{8,}$/;
    if (!passwordRequirements.test(password)) {
        alert('Пароль должен содержать минимум 8 латинских символов, одну заглавную букву и одну цифру.');
        return;
    }

    // Проверяем совпадение пароля и его подтверждения
    if (password !== confirmPassword) {
        alert('Пароль и подтверждение пароля не совпадают.');
        return;
    }

    // Формируем данные для сохранения
    const userData = {
        username,
        surname,
        phone,
        email,
        city,
        address,
        password, // В реальном проекте лучше не хранить пароль в localStorage
    };

    // Сохраняем данные в localStorage
    localStorage.setItem('userProfile', JSON.stringify(userData));

    alert('Регистрация успешна! Добро пожаловать в личный кабинет.');

    // Перенаправляем на страницу личного кабинета
    window.location.href = 'login.html';
};


export function setupUserMenu() {
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
}

export function updateUserNameDisplay() {
    const loggedIn = localStorage.getItem('loggedIn');
    const userNameElem = document.getElementById('user-name');
    if (!userNameElem) return;

    if (loggedIn === 'true') {
        const userProfile = JSON.parse(localStorage.getItem('userProfile'));
        if (userProfile) {
            userNameElem.textContent = `${userProfile.name || 'Пользователь'}`;
            return;
        }
    }
    userNameElem.textContent = 'Гость';
}
