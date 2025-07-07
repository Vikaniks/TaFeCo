import * as XLSX from 'https://cdn.sheetjs.com/xlsx-latest/package/xlsx.mjs';

// orders.js
export function initOrdersModule() {
  // Элементы управления
  const filterStatus = document.getElementById('filter-status');
  const filterEmail = document.getElementById('filter-email');
  const filterStartDate = document.getElementById('filter-startDate');
  const filterEndDate = document.getElementById('filter-endDate');
  const filterProduct = document.getElementById('filter-product');
  const filterWarehouse = document.getElementById('filter-warehouse');
  const filterStore = document.getElementById('filter-store');

  const btnFilter = document.getElementById('btn-filter');
  const exportCsvBtn = document.getElementById('btn-export-csv');
  const exportExcelBtn = document.getElementById('btn-export-excel');

  const tableBody = document.querySelector('#orders-table tbody');
  const pageInfo = document.getElementById('page-info');
  const prevPageBtn = document.getElementById('prev-page');
  const nextPageBtn = document.getElementById('next-page');

  const orderModal = document.getElementById('order-modal');
  const orderDetailsContainer = document.getElementById('order-details');
  const modalCloseBtn = document.getElementById('modal-close');

  const statusModal = document.getElementById('status-modal');
  const closeStatusModalBtn = document.getElementById('close-status-modal');
  const statusForm = document.getElementById('status-form');
  const statusSelect = document.getElementById('status-select');

  btnFilter.addEventListener('click', applyFilters);

  let allOrders = [];
  let filteredOrders = [];
  let currentPage = 0;
  const pageSize = 10;
  let currentOrderIdForStatus = null;

  // Загрузка заказов с сервера
async function fetchOrders() {
    try {
      const jwt = localStorage.getItem('jwt');
      if (!jwt) {
        alert('Вы не авторизованы');
        return;
      }

      const response = await fetch('/api/moderator/orders/all', {
        headers: { 'Authorization': `Bearer ${jwt}` }
      });
    console.log('Response status:', response.status);
      if (!response.ok) throw new Error('Ошибка при получении заказов');

      const data = await response.json();
      console.log('Response data:', data); // data — массив заказов

      allOrders = data || []; // присваиваем весь массив напрямую

      console.log('Orders data:', allOrders);

      applyFilters();
    } catch (error) {
      console.error('Ошибка загрузки заказов:', error);
      alert('Ошибка загрузки заказов. Проверьте консоль.');
    }
  }

  // Считываем фильтры из формы
function getFiltersFromForm() {
    return {
      status: filterStatus.value.trim(),
      email: filterEmail.value.trim(),
      startDate: filterStartDate.value,
      endDate: filterEndDate.value,
      productKeyword: filterProduct.value.trim(),
      warehouseId: filterWarehouse.value.trim(),
      storeId: filterStore.value.trim()
    };
  }

  // Применение фильтров к списку заказов
function applyFilters() {
    const { status, email, startDate, endDate, productKeyword, warehouseId, storeId } = getFiltersFromForm();

    filteredOrders = allOrders.filter(order => {
      const orderDate = order.orderDate ? new Date(order.orderDate) : null;

      const matchesStatus = !status || order.status === status;
      const matchesEmail = !email || (order.userEmail && order.userEmail.includes(email));
      const matchesStartDate = !startDate || (orderDate && orderDate >= new Date(startDate));
      const matchesEndDate = !endDate || (orderDate && orderDate <= new Date(endDate));
      const matchesProduct = !productKeyword || (
          order.items && order.items.some(item =>
              item.productName && item.productName.toLowerCase().includes(productKeyword.toLowerCase())
          )
      );

      const matchesWarehouse = !warehouseId || order.warehouseId == warehouseId;
      const matchesStore = !storeId || order.storeId == storeId;

      return matchesStatus && matchesEmail && matchesStartDate && matchesEndDate && matchesProduct && matchesWarehouse && matchesStore;
    });

    currentPage = 0;
    renderPage();
    setupViewButtons();
    setupStatusButtons();
    clearFiltersForm();
  }

function clearFiltersForm() {
      filterStatus.value = '';
      filterEmail.value = '';
      filterStartDate.value = '';
      filterEndDate.value = '';
      filterProduct.value = '';
      filterWarehouse.value = '';
      filterStore.value = '';
    }

document.getElementById('clear-filters-btn').addEventListener('click', () => {
  clearFiltersForm();
  applyFilters();
});


  // Отрисовка текущей страницы
function renderPage() {
    const start = currentPage * pageSize;
    const end = start + pageSize;
    const pageOrders = filteredOrders.slice(start, end);

    tableBody.innerHTML = '';

    pageOrders.forEach(order => {
      const row = document.createElement('tr');

      const date = order.orderDate ? new Date(order.orderDate).toLocaleDateString() : '—';
      const email = order.userEmail || (order.user && order.user.email) || '—';
      const total = order.totalPrice !== undefined ? `${order.totalPrice} ₽` : '—';
      const status = order.status || '—';

      row.innerHTML = `
        <td>${order.id}</td>
        <td>${email}</td>
        <td>${date}</td>
        <td>${total}</td>
        <td>${status}</td>
        <td>
          <button class="view-btn" data-id="${order.id}" title="Просмотр"><i class="fa-regular fa-eye fa-lg"></i></button>
          <button class="status-btn" data-id="${order.id}" data-email="${order.userEmail}">Изменить статус</button>
        </td>
      `;

      tableBody.appendChild(row);
    });

    pageInfo.textContent = `Страница ${currentPage + 1} из ${Math.max(1, Math.ceil(filteredOrders.length / pageSize))}`;
    prevPageBtn.disabled = currentPage === 0;
    nextPageBtn.disabled = end >= filteredOrders.length;
  }

  // Обработчики кнопок просмотра заказа
  function setupViewButtons() {
    document.querySelectorAll('.view-btn').forEach(btn => {
      btn.onclick = async () => {
        const orderId = btn.dataset.id;
        try {
          const jwt = localStorage.getItem('jwt');
          const resp = await fetch(`/api/moderator/orders/${orderId}`, {
            headers: { 'Authorization': `Bearer ${jwt}` }
          });
          if (!resp.ok) throw new Error('Ошибка загрузки заказа');

          const order = await resp.json();
          showOrderDetails(order);
        } catch (err) {
          alert('Не удалось загрузить детали заказа');
          console.error(err);
        }
      };
    });
  }

  // Показать модальное окно с деталями заказа
function showOrderDetails(order) {
  let html = `<p><strong>ID заказа:</strong> ${order.id}</p>`;
  html += `<p><strong>Пользователь:</strong> ${order.userEmail || '—'}</p>`;
  html += `<p><strong>Дата:</strong> ${order.orderDate ? new Date(order.orderDate).toLocaleString() : '—'}</p>`;
  html += `<p><strong>Статус:</strong> ${order.status || '—'}</p>`;
  html += `<p><strong>Сумма:</strong> ${order.totalPrice || 0} ₽</p>`;

  html += '<h3>Товары:</h3>';
  if (order.items && order.items.length) {
    html += `
      <table style="width: 100%; border-collapse: collapse; margin-top: 10px;">
        <thead>
          <tr>
            <th style="border: 1px solid #ccc; padding: 8px;">Продукт</th>
            <th style="border: 1px solid #ccc; padding: 8px;">Количество</th>
            <th style="border: 1px solid #ccc; padding: 8px;">Ед. изм.</th>
            <th style="border: 1px solid #ccc; padding: 8px;">Цена</th>
          </tr>
        </thead>
        <tbody>
    `;

    order.items.forEach(i => {
      const name = i.productName || i.productId;
      const quantity = i.quantity ?? '—';
      const unit = i.dimensionName || '—';
      const price = i.priceAtOrderTime !== undefined && i.priceAtOrderTime !== null
        ? Number(i.priceAtOrderTime).toFixed(2)
        : '—';

      html += `
        <tr>
          <td style="border: 1px solid #ccc; padding: 8px;">${name}</td>
          <td style="border: 1px solid #ccc; padding: 8px;">${quantity}</td>
          <td style="border: 1px solid #ccc; padding: 8px;">${unit}</td>
          <td style="border: 1px solid #ccc; padding: 8px;">${price} ₽</td>
        </tr>
      `;
    });

    html += '</tbody></table>';
  } else {
    html += '<p>Нет товаров</p>';
  }

  orderDetailsContainer.innerHTML = html;
  orderModal.style.display = 'block';
}


// Закрытие модалки
modalCloseBtn.onclick = () => {
  orderModal.style.display = 'none';
};

window.onclick = e => {
  if (e.target === orderModal) {
    orderModal.style.display = 'none';
  }
  if (e.target === statusModal) {
    statusModal.style.display = 'none';
  }
};

  // Кнопки смены статуса
function setupStatusButtons() {
  document.querySelectorAll('.status-btn').forEach(btn => {
    btn.onclick = () => {
      currentOrderIdForStatus = btn.dataset.id;
      const email = btn.dataset.email || '—';
      statusSelect.value = '';

      const orderInfo = document.getElementById('status-order-info');
      orderInfo.textContent = `Заказ #${currentOrderIdForStatus}, Email: ${email}`;

      statusModal.style.display = 'block';
    };
  });
}


  closeStatusModalBtn.onclick = () => {
    statusModal.style.display = 'none';
  };

  statusForm.onsubmit = async (e) => {
    e.preventDefault();
    const newStatus = statusSelect.value;
    if (!newStatus) {
      alert('Выберите новый статус');
      return;
    }
    if (!currentOrderIdForStatus) {
      alert('Ошибка: заказ не выбран');
      return;
    }

    try {
      const jwt = localStorage.getItem('jwt');
      const resp = await fetch(`/api/moderator/orders/${currentOrderIdForStatus}/status`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${jwt}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ status: newStatus })
      });

      if (!resp.ok) throw new Error('Ошибка обновления статуса');

      alert('Статус успешно обновлён');
      statusModal.style.display = 'none';
      await fetchOrders();  // обновить список
    } catch (err) {
      alert('Ошибка при обновлении статуса');
      console.error(err);
    }
  };

  // Пагинация
  prevPageBtn.onclick = () => {
    if (currentPage > 0) {
      currentPage--;
      renderPage();
      setupViewButtons();
      setupStatusButtons();
    }
  };

  nextPageBtn.onclick = () => {
    if ((currentPage + 1) * pageSize < filteredOrders.length) {
      currentPage++;
      renderPage();
      setupViewButtons();
      setupStatusButtons();
    }
  };

/*  // Экспорт CSV
  exportCsvBtn.onclick = () => {
    if (filteredOrders.length === 0) {
      alert('Нет данных для экспорта');
      return;
    }

    const headers = ['ID', 'Email', 'Дата', 'Сумма', 'Статус'];
    const rows = filteredOrders.map(o => [
      o.id,
      o.userEmail,
      o.orderDate ? new Date(o.orderDate).toLocaleString() : '',
      o.totalPrice,
      o.status
    ]);

    let csvContent = headers.join(',') + '\n' + rows.map(r => r.map(cell => `"${cell}"`).join(',')).join('\n');
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.href = url;
    a.download = 'orders_report.csv';
    a.click();
    URL.revokeObjectURL(url);
  };
*/
  // Экспорт Excel (через библиотеку SheetJS)
  exportExcelBtn.onclick = async () => {
    if (filteredOrders.length === 0) {
      alert('Нет данных для экспорта');
      return;
    }

    // Проверим, подключена ли библиотека SheetJS
    if (typeof XLSX === 'undefined') {
      alert('Библиотека XLSX не подключена');
      return;
    }

    const wb = XLSX.utils.book_new();

    const wsData = [
      ['ID', 'Email', 'Дата', 'Сумма', 'Статус'],
      ...filteredOrders.map(o => [
        o.id,
        o.userEmail,
        o.orderDate ? new Date(o.orderDate).toLocaleString() : '',
        o.totalPrice,
        o.status
      ])
    ];

    const ws = XLSX.utils.aoa_to_sheet(wsData);
    XLSX.utils.book_append_sheet(wb, ws, 'Orders');

    XLSX.writeFile(wb, 'orders_report.xlsx');
  };

  // Инициализация
  fetchOrders();
}


