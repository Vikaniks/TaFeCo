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

        // Пример простой регистрации (хранить в localStorage)
        localStorage.setItem('loggedIn', 'true');
        localStorage.setItem('userProfile', JSON.stringify({ name: username }));

        alert('Вы успешно вошли!');
        window.location.href = 'index.html';
    });
}
