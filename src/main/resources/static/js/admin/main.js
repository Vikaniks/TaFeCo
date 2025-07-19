import * as XLSX from 'https://cdn.sheetjs.com/xlsx-latest/package/xlsx.mjs';

import {
  fetchUsers,
  fetchUserByEmail,
  fetchUserByPhone,
  deleteUser,
  updateUserRole,
  renderUsers
} from './modules/admin.js';

import {
  initOrdersModule
} from './modules/ordersTable.js';

import {
  initReportModule
} from './modules/reportOrder.js';

import {
  initProductModule, initDeleteProductModal
} from './modules/adminProducts.js';

import {
  initReportProductModule
} from './modules/reportProducts.js';

import {
  initStoreModule
} from './modules/stores.js';

import {
  initWarehouseModule
} from './modules/warehouses.js';

import { checkRoleAccess } from './modules/authGuard.js';

document.addEventListener('DOMContentLoaded', async () => {

  console.log('📦 DOMContentLoaded сработал');
  const access = await checkRoleAccess(['ROLE_ADMIN', 'ROLE_MODERATOR', 'ROLE_SUPERADMIN']);

  if (!access) {
    alert('У вас нет доступа');
    window.location.href = '/register';
    return;
  }

  console.log('✅ Доступ подтверждён. Продолжаем...');

let allUsers = [];
let currentPage = 0;
const pageSize = 10;
let totalPages = 0;
let ordersData = [];

// === Начальная инициализация ===
const jwt = localStorage.getItem('jwt');
if (jwt) {
  //loadAllUsers(jwt);
  //setJwt(jwt);
}

// Выход
/*    const logout = document.getElementById("logout");
    if (logout) {
      logout.addEventListener("click", () => {
        console.log('Выход выполнен');
        e.preventDefault();
              logoutAdmin();
      });
    }
*/
// Запускаем нужный модуль по ID страницы
  if (document.getElementById('orders-page')) {
    initOrdersModule();
  } else if (document.getElementById('report-page')) {
    initReportModule();
  } else if (document.getElementById('admin-products')) {
    initProductModule();
    const deleteProductButton = document.getElementById('delete-product');
      if (deleteProductButton) {
        initDeleteProductModal();
      }
  } else if (document.getElementById('reports-section')) {
    initReportProductModule();
  } else if (document.getElementById('store-section')) {
    initStoreModule();
  } else if (document.getElementById('warehouses-section')) {
    initWarehouseModule();
  }


// === Поиск пользователя по email или телефону ===
const usernameButton = document.getElementById('btn-username');
if (usernameButton) {
  usernameButton.addEventListener('click', async () => {

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
}

// === Обработка кнопок "Удалить" и "Изменить роль" ===
const usersContainer = document.getElementById('users-container');
if (usersContainer) {
  usersContainer.addEventListener('click', async (e) => {
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
}

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
const prevBtn = document.getElementById('prev-page');
if (prevBtn) {
  prevBtn.addEventListener('click', () => {
    if (currentPage > 0) {
      currentPage--;
      renderCurrentPage();
    }
  });
}

const nextBtn = document.getElementById('next-page');
if (nextBtn) {
  nextBtn.addEventListener('click', () => {
    if ((currentPage + 1) * pageSize < allUsers.length) {
      currentPage++;
      renderCurrentPage();
    }
  });
}

const goToPageBtn = document.getElementById('go-to-page');
const pageInput = document.getElementById('page-input');
if (goToPageBtn && pageInput) {
  goToPageBtn.addEventListener('click', () => {
    const page = parseInt(pageInput.value.trim(), 10);
    if (!isNaN(page) && page >= 1 && (page - 1) * pageSize < allUsers.length) {
      currentPage = page - 1;
      renderCurrentPage();
    } else {
      alert('Введите корректный номер страницы');
    }
  });
}

// === Обработчик кнопки "Все пользователи" ===
const btnUsers = document.getElementById('btn-users');
if (btnUsers) {
  btnUsers.addEventListener('click', async () => {
    const jwt = localStorage.getItem('jwt');
    if (!jwt) {
      alert('Вы не авторизованы');
      return;
    }
    await loadAllUsers(jwt);
  });
}



});