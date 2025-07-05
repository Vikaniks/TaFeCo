// ordersTable.js
import * as XLSX from 'https://cdn.sheetjs.com/xlsx-latest/package/xlsx.mjs';

let ordersData = [];
let jwt = '';

export function setJwt(token) {
  jwt = token;
}

export function renderOrdersTable(orders) {
  ordersData = orders;
  const tbody = document.querySelector('#orders-table tbody');
  tbody.innerHTML = '';
  for (const order of orders) {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${order.id}</td>
      <td>${order.userEmail || '-'}</td>
      <td>${new Date(order.createdDate).toLocaleDateString()}</td>
      <td>${order.totalPrice.toFixed(2)}</td>
      <td>
        <select class="status-select" data-order-id="${order.id}">
          <option value="NEW" ${order.status === 'NEW' ? 'selected' : ''}>NEW</option>
          <option value="IN_PROGRESS" ${order.status === 'IN_PROGRESS' ? 'selected' : ''}>IN_PROGRESS</option>
          <option value="DONE" ${order.status === 'DONE' ? 'selected' : ''}>DONE</option>
          <option value="CANCELLED" ${order.status === 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
        </select>
      </td>
      <td><button class="btn-details" data-order-id="${order.id}">Подробнее</button></td>
    `;
    tbody.appendChild(tr);
  }
}

export function setupStatusChangeHandler(refreshCallback) {
  document.querySelector('#orders-table tbody').addEventListener('change', async (e) => {
    if (e.target.classList.contains('status-select')) {
      const orderId = e.target.dataset.orderId;
      const newStatus = e.target.value;

      const confirmed = confirm(`Изменить статус заказа ${orderId} на ${newStatus}?`);
      if (!confirmed) {
        refreshCallback(); // откат
        return;
      }

      try {
        const res = await fetch(`/api/admin/orders/${orderId}/status?newStatus=${newStatus}`, {
          method: 'PUT',
          headers: { Authorization: `Bearer ${jwt}` },
        });
        if (!res.ok) throw new Error('Ошибка обновления статуса');
        alert('Статус обновлен');
      } catch (err) {
        alert(err.message);
        refreshCallback();
      }
    }
  });
}

export function setupExportButton() {
  document.getElementById('btn-export').addEventListener('click', () => {
    if (ordersData.length === 0) {
      alert('Нет данных для экспорта');
      return;
    }

    const worksheetData = ordersData.map(order => ({
      ID: order.id,
      Email: order.userEmail || '',
      Дата: new Date(order.createdDate).toLocaleDateString(),
      Сумма: order.totalPrice.toFixed(2),
      Статус: order.status,
    }));

    const worksheet = XLSX.utils.json_to_sheet(worksheetData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Заказы');
    XLSX.writeFile(workbook, `orders_export.xlsx`);
  });
}
