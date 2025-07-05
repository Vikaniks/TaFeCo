import {
  fetchUsers,
  fetchUserByEmail,
  fetchUserByPhone,
  deleteUser,
  updateUserRole,
  renderUsers
} from './modules/admin.js';

import {
  renderOrdersTable,
  setupStatusChangeHandler,
  setupExportButton,
  setJwt
} from './modules/ordersTable.js';


let allUsers = [];
let currentPage = 0;
const pageSize = 10;
let totalPages = 0;
let ordersData = [];

// === Начальная инициализация ===
const jwt = localStorage.getItem('jwt');
if (jwt) {
  //loadAllUsers(jwt);
  setJwt(jwt);
}

// === Поиск пользователя по email или телефону ===
document.getElementById('btn-username').addEventListener('click', async () => {
  const emailInput = document.getElementById('input-email');
  const phoneInput = document.getElementById('input-phone');
  const email = emailInput.value.trim();
  const phone = phoneInput.value.trim();
  const jwt = localStorage.getItem('jwt');

  try {
    let user = null;

    if (email !== '') {
      user = await fetchUserByEmail(jwt, email);
    } else if (phone !== '') {
      user = await fetchUserByPhone(jwt, phone);
    } else {
      alert('Введите email или телефон для поиска');
      return;
    }

    renderUsers({ content: [user], number: 0, first: true, last: true });
    emailInput.value = '';
    phoneInput.value = '';
  } catch (error) {
    alert(error.message);
    document.getElementById('users-section').style.display = 'none';
  }
});

// === Обработка кнопок "Удалить" и "Изменить роль" ===
document.getElementById('users-container').addEventListener('click', async (e) => {
  const jwt = localStorage.getItem('jwt');
  const username = e.target.dataset.username;
  const email = e.target.dataset.email;

  if (!username || !email) return;

  if (e.target.classList.contains('delete-btn')) {
    if (confirm(`Удалить пользователя ${email}?`)) {
      try {
        await deleteUser(jwt, email);
        alert('Пользователь удалён');
        await loadAllUsers(jwt);
      } catch (err) {
        alert(err.message);
      }
    }
  }

  if (e.target.classList.contains('change-role-btn')) {
    const role = prompt('Введите новую роль (ROLE_USER, ROLE_ADMIN, ROLE_MODERATOR)');
    if (!role) return;

    try {
      await updateUserRole(jwt, email, role);
      alert('Роль обновлена');
      await loadAllUsers(jwt);
    } catch (err) {
      alert(err.message);
    }
  }
});

// === Загрузка всех пользователей ===
async function loadAllUsers(jwt) {
  try {
    const response = await fetch(`/api/admin/users/all`, {
      headers: { 'Authorization': `Bearer ${jwt}` }
    });
    if (!response.ok) throw new Error('Ошибка загрузки пользователей');
    allUsers = await response.json();
    currentPage = 0;
    renderCurrentPage();
  } catch (error) {
    alert(error.message);
  }
}

// === Отрисовка текущей страницы ===
function renderCurrentPage() {
  const start = currentPage * pageSize;
  const end = start + pageSize;
  const usersPage = {
    content: allUsers.slice(start, end),
    number: currentPage,
    size: pageSize,
    first: currentPage === 0,
    last: end >= allUsers.length
  };
  renderUsers(usersPage);
}

// === Обработчики пагинации ===
document.getElementById('prev-page').addEventListener('click', () => {
  if (currentPage > 0) {
    currentPage--;
    renderCurrentPage();
  }
});

document.getElementById('next-page').addEventListener('click', () => {
  if ((currentPage + 1) * pageSize < allUsers.length) {
    currentPage++;
    renderCurrentPage();
  }
});

document.getElementById('go-to-page').addEventListener('click', () => {
  const input = document.getElementById('page-input');
  const page = parseInt(input.value.trim(), 10);
  if (!isNaN(page) && page >= 1 && (page - 1) * pageSize < allUsers.length) {
    currentPage = page - 1;
    renderCurrentPage();
  } else {
    alert('Введите корректный номер страницы');
  }
});

// === Обработчик кнопки "Все пользователи" ===
document.getElementById('btn-users').addEventListener('click', async () => {
  const jwt = localStorage.getItem('jwt');
  if (!jwt) {
    alert('Вы не авторизованы');
    return;
  }
  await loadAllUsers(jwt);
});

// Страница заказов
async function fetchOrders() {
  const status = document.getElementById('filter-status').value;
  const startDate = document.getElementById('filter-startDate').value;
  const endDate = document.getElementById('filter-endDate').value;
  const email = document.getElementById('filter-email').value.trim();

  const params = new URLSearchParams({
    page: currentPage,
    size: pageSize,
  });
  if (status) params.append('status', status);
  if (startDate) params.append('startDate', startDate);
  if (endDate) params.append('endDate', endDate);
  if (email) params.append('email', email);

  const res = await fetch(`/api/admin/orders?${params.toString()}`, {
    headers: { Authorization: `Bearer ${jwt}` },
  });

  if (!res.ok) {
    alert('Ошибка загрузки заказов');
    return;
  }

  const data = await res.json();
  totalPages = data.totalPages;
  renderOrdersTable(data.content);
  updatePagination();
  fetchOrderSummary(startDate, endDate);
}

function updatePagination() {
  document.getElementById('page-info').textContent = `Страница ${currentPage + 1} из ${totalPages}`;
  document.getElementById('prev-page').disabled = currentPage === 0;
  document.getElementById('next-page').disabled = currentPage + 1 >= totalPages;
}

async function fetchOrderSummary(startDate, endDate) {
  const params = new URLSearchParams();
  if (startDate) params.append('startDate', startDate);
  if (endDate) params.append('endDate', endDate);

  const res = await fetch(`/api/admin/orders/report?${params.toString()}`, {
    headers: { Authorization: `Bearer ${jwt}` },
  });

  if (!res.ok) {
    document.getElementById('summary-content').textContent = 'Ошибка загрузки отчёта';
    return;
  }

  const summary = await res.json();
  renderOrderSummary(summary);
}

function renderOrderSummary(summary) {
  const container = document.getElementById('summary-content');
  container.innerHTML = `
    <p>Всего заказов: ${summary.totalOrders}</p>
    <p>Общая выручка: ${summary.totalRevenue.toFixed(2)}</p>
    <p>По статусам:</p>
    <ul>
      ${Object.entries(summary.ordersByStatus || {})
        .map(([status, count]) => `<li>${status}: ${count}</li>`)
        .join('')}
    </ul>
  `;
}

// --- Обработчики UI ---
document.getElementById('btn-filter').addEventListener('click', () => {
  currentPage = 0;
  fetchOrders();
});

document.getElementById('prev-page').addEventListener('click', () => {
  if (currentPage > 0) {
    currentPage--;
    fetchOrders();
  }
});

document.getElementById('next-page').addEventListener('click', () => {
  if (currentPage + 1 < totalPages) {
    currentPage++;
    fetchOrders();
  }
});

setupStatusChangeHandler(fetchOrders);
setupExportButton();
fetchOrders();



// Объект с фильтрами и параметрами запроса
const filters = {
  page: 0,          // текущая страница (по умолчанию 0)
  size: 20,         // размер страницы (по умолчанию 20)
  status: null,     // фильтр по статусу
  startDate: null,  // дата начала периода
  endDate: null,    // дата окончания периода
  email: null,      // фильтр по email пользователя
  productId: null,  // фильтр по ID продукта (если реализовано)
  warehouseId: null,// фильтр по складу (если реализовано)
  storeId: null     // фильтр по точке продаж (если реализовано)
};

// Функция загрузки заказов с учётом фильтров
async function fetchOrders() {
  const params = new URLSearchParams();

  // Добавляем в URL только непустые параметры
  for (const key in filters) {
    if (filters[key] !== null && filters[key] !== '') {
      params.append(key, filters[key]);
    }
  }

  try {
    const res = await fetch(`/api/admin/orders?${params.toString()}`, {
      headers: {
        Authorization: `Bearer ${jwt}`
      }
    });

    if (!res.ok) throw new Error('Ошибка загрузки заказов');

    const data = await res.json();

    // Отправляем полученные данные в функцию отрисовки
    renderOrdersTable(data.content || []);
  } catch (err) {
    alert(err.message);
  }
}

// Устанавливаем обработчики на фильтры
function setupFilters() {
  // Находим все элементы с классом .filter-input и вешаем обработчик на изменение
  document.querySelectorAll('.filter-input').forEach(input => {
    input.addEventListener('input', () => {
      // Сохраняем новое значение в filters (если пустое — сбрасываем)
      filters[input.name] = input.value || null;
    });
  });

  // Кнопка "Применить фильтры"
  document.getElementById('btn-filter').addEventListener('click', () => {
    filters.page = 0; // сбрасываем страницу при фильтрации
    fetchOrders();    // загружаем отфильтрованные данные
  });
}

// Основная инициализация после загрузки страницы
document.addEventListener('DOMContentLoaded', () => {
  setupFilters();              // подключаем обработку фильтров
  fetchOrders();               // загружаем заказы при старте
  setupStatusChangeHandler(fetchOrders); // при изменении статуса — обновить данные
  setupExportButton();         // кнопка экспорта в Excel
});
