
export async function initReportProductModule() {

const warehouseSelect = document.createElement('select');
const storeSelect = document.createElement('select');

const reportsSection = document.getElementById('reports-section');
const reportFilters = document.getElementById('report-filters');
const reportTable = document.getElementById('report-table');

const activeFilter = document.getElementById('activeFilter');
const reportButtons = document.querySelectorAll('.btn-report');

let currentType = null; // 'warehouse' или 'store'
let currentId = null;   // выбранный id склада или магазина
let currentReport = [];

function createOption(value, text) {
  const option = document.createElement('option');
  option.value = value;
  option.textContent = text;
  return option;
}

async function fetchWarehouses() {
  const token = localStorage.getItem('jwt');
  const res = await fetch('/api/warehouses/all', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  if (!res.ok) throw new Error('Ошибка загрузки складов');

  const result = await res.json();
  console.log(result);
  return result;
}


async function fetchStores() {
  const token = localStorage.getItem('jwt');
  const res = await fetch('/api/stores/all', {
     headers: {
     'Authorization': `Bearer ${token}`
     }
  });
  if (!res.ok) throw new Error('Ошибка загрузки магазинов');
  return await res.json();
}

async function fetchReport() {
  if (!currentId) return [];

  const token = localStorage.getItem('jwt');
  if (!token) throw new Error('Нет JWT токена');

  let url = '';

  if (currentType === 'warehouse') {
    url = `/api/moderator/warehouses/${currentId}/stock-report`;
  } else if (currentType === 'store') {
    url = `/api/moderator/stores/${currentId}/stock-report`;
  } else {
    return [];
  }

  const res = await fetch(url, {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });

  if (!res.ok) throw new Error('Ошибка загрузки отчёта');
  return await res.json();
}

async function fetchFullStoreReport() {
  const token = localStorage.getItem('jwt');
  const res = await fetch('/api/moderator/stores/stock-report', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  if (!res.ok) throw new Error('Ошибка загрузки полного отчёта');
  return await res.json();
}


function filterByActive(data, activeValue) {
  if (activeValue === '') return data;
  const boolVal = activeValue === 'true';
  return data.filter(item => item.active === boolVal);
}

function renderTable(data) {
  reportTable.innerHTML = '';

  if (data.length === 0) {
    reportTable.innerHTML = '<tr><td>Данные не найдены</td></tr>';
    return;
  }

  // Заголовок - динамически по ключам первого объекта
  const header = document.createElement('thead');
  const headerRow = document.createElement('tr');
  Object.keys(data[0]).forEach(key => {
    const th = document.createElement('th');
    th.textContent = key;
    headerRow.appendChild(th);
  });
  header.appendChild(headerRow);
  reportTable.appendChild(header);

  // Тело
  const tbody = document.createElement('tbody');
  data.forEach(item => {
    const tr = document.createElement('tr');
    Object.values(item).forEach(value => {
      const td = document.createElement('td');
      td.textContent = value ?? '';
      tr.appendChild(td);
    });
    tbody.appendChild(tr);
  });
  reportTable.appendChild(tbody);
}

async function loadWarehouses() {
  try {
    const warehouses = await fetchWarehouses();
    warehouseSelect.innerHTML = '';
    warehouseSelect.appendChild(createOption('', 'Выберите склад'));
    warehouses.forEach(w => {
      warehouseSelect.appendChild(createOption(w.id, w.location || `Склад ${w.id}`));
    });
  } catch (e) {
    alert(e.message);
  }
}

async function loadStores() {
  try {
    const stores = await fetchStores();
    storeSelect.innerHTML = '';
    storeSelect.appendChild(createOption('', 'Выберите магазин'));
    stores.forEach(s => {
      storeSelect.appendChild(createOption(s.id, s.name || `Магазин ${s.id}`));
    });
  } catch (e) {
    alert(e.message);
  }
}

async function onReportTypeChange(type) {
  currentType = type;
  currentId = null;
  currentReport = [];

  // Очистить селекты
  warehouseSelect.style.display = 'none';
  storeSelect.style.display = 'none';

  if (type === 'warehouse') {
    warehouseSelect.style.display = 'inline-block';
    await loadWarehouses();
  } else if (type === 'store') {
    storeSelect.style.display = 'inline-block';
    await loadStores();
  }

  renderTable([]);
}

async function onSelectionChange(e) {
  currentId = e.target.value;
  if (!currentId) {
    renderTable([]);
    return;
  }
  try {
    let report = await fetchReport();
    // фильтруем по активности
    report = filterByActive(report, activeFilter.value);
    currentReport = report;
    renderTable(report);
  } catch (e) {
    alert(e.message);
  }
}

function onActiveFilterChange() {
  if (!currentReport) return;
  const filtered = filterByActive(currentReport, activeFilter.value);
  renderTable(filtered);
}

// Инициализация UI

// Добавляем селекты после кнопок
const buttonsContainer = document.querySelector('.report-buttons');
warehouseSelect.id = 'warehouseSelect';
storeSelect.id = 'storeSelect';
warehouseSelect.style.display = 'none';
storeSelect.style.display = 'none';
buttonsContainer.appendChild(warehouseSelect);
buttonsContainer.appendChild(storeSelect);

// Слушатели на кнопки отчётов
reportButtons.forEach(btn => {
  btn.addEventListener('click', () => onReportTypeChange(btn.dataset.report));
});

// Слушатели на селекты
warehouseSelect.addEventListener('change', onSelectionChange);
storeSelect.addEventListener('change', onSelectionChange);

// Слушатель на фильтр активности
activeFilter.addEventListener('change', onActiveFilterChange);

// Слушатель на кнопку "Отчёт по всем магазинам"
const fullStoreReportBtn = document.getElementById('load-full-store-report');
if (fullStoreReportBtn) {
  fullStoreReportBtn.addEventListener('click', async () => {
    try {
      const data = await fetchFullStoreReport();
      renderTable(data);
    } catch (err) {
      console.error(err);
      alert('Не удалось загрузить отчёт по всем магазинам');
    }
  });
}

// Изначально пустая таблица
renderTable([]);

}