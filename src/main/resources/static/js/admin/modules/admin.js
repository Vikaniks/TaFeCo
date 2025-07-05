/*
export async function fetchOrders(jwt) {
  const response = await fetch('/api/admin/orders', {
    headers: {
      'Authorization': `Bearer ${jwt}`
    }
  });

  if (!response.ok) {
    throw new Error('Ошибка загрузки заказов');
  }

  return await response.json();
}

export function renderOrders(orders) {
  const container = document.getElementById('orders-container');
  if (!container) return;
  container.innerHTML = orders.map(order => `
    <div class="order-card">
      <p>Заказ #${order.id} — ${order.status}</p>
    </div>
  `).join('');
}
*/

// Поиск всех юзеров с выводом постранично
export async function fetchUsers(jwt) {
  console.log('JWT перед вызовом fetchUsers:', jwt);
  const response = await fetch(`/api/admin/users/all`, {
    headers: {
      'Authorization': `Bearer ${jwt}`
    }
  });
  if (!response.ok) throw new Error('Ошибка при загрузке пользователей');
  const users = await response.json();

  // Оборачиваем в объект с ключом content, чтобы совместить с текущей логикой рендера
  return { content: users };
}

export function renderUsers(usersPage) {
  const container = document.getElementById('users-container');
  const section = document.getElementById('users-section');
  const prevBtn = document.getElementById('prev-page');
  const nextBtn = document.getElementById('next-page');
  const pageNumberSpan = document.getElementById('page-number');

  if (!container || !section) return;

  const users = usersPage.content;

  if (!users || users.length === 0) {
    section.style.display = 'none';
    return;
  }
  section.style.display = 'block';

  const startIndex = usersPage.number * usersPage.size;

  container.innerHTML = `
    <table class="user-table">
      <thead>
        <tr>
          <th>№</th>
          <th>ФИО</th>
          <th>Email</th>
          <th>Телефон</th>
          <th>Адрес</th>
          <th>Роли</th>
          <th>Действия</th>
        </tr>
      </thead>
      <tbody>
        ${users.map((user, index) => {
          const rowNumber = startIndex + index + 1;
          const fullName = [user.name, user.surname].filter(Boolean).join(' ');
          const addressParts = [
            user.locality,
            user.street,
            user.house ? `д. ${user.house}` : null,
            user.apartment ? `кв. ${user.apartment}` : null
          ];
          const address = addressParts.filter(Boolean).join(', ');

          return `
            <tr>
              <td>${rowNumber}</td>
              <td>${fullName || '-'}</td>
              <td>${user.email || '-'}</td>
              <td>${user.phone || '-'}</td>
              <td>${address || '-'}</td>
              <td>${user.roles?.join(', ') || '-'}</td>
              <td>
                <button class="delete-btn" data-username="${user.username}" data-email="${user.email}">Удалить</button>
                <button class="change-role-btn" data-username="${user.username}" data-email="${user.email}">Изменить роль</button>
              </td>
            </tr>
          `;
        }).join('')}
      </tbody>
    </table>
  `;

  if (pageNumberSpan) {
    pageNumberSpan.textContent = usersPage.number + 1;
  }

  if (prevBtn) {
    prevBtn.disabled = usersPage.first;
  }

  if (nextBtn) {
    nextBtn.disabled = usersPage.last;
  }
}


// Поиск юзера по фильтру емаил или телефон, удалить, сменить роль
export async function fetchUserByEmail(jwt, email) {
  const response = await fetch(`/api/admin/users/email?value=${encodeURIComponent(email)}`, {
    headers: {
      'Authorization': `Bearer ${jwt}`
    }
  });
  if (!response.ok) throw new Error('Пользователь по email не найден');
  return await response.json();
}

export async function fetchUserByPhone(jwt, phone) {
  const response = await fetch(`/api/admin/users/phone?value=${encodeURIComponent(phone)}`, {
    headers: {
      'Authorization': `Bearer ${jwt}`
    }
  });
  if (!response.ok) throw new Error('Пользователь по телефону не найден');
  return await response.json();
}


// Удалить юзера
export async function deleteUser(jwt, email) {
  const response = await fetch(`/api/admin/users/${email}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${jwt}`
    }
  });
  if (!response.ok) throw new Error('Не удалось удалить пользователя');
}

export async function updateUserRole(jwt, email, role) {
  const response = await fetch(`/api/admin/users/${email}/role?role=${role}`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${jwt}`
    }
  });
  if (!response.ok) throw new Error('Не удалось изменить роль пользователя');
}

