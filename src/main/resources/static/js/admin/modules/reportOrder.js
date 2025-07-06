// reportOrder.js
// Модуль для страницы отчетов по заказам: запросы на сервер и отрисовка таблиц

export function initReportModule() {
  const btnReportStatus = document.getElementById('btn-report-status');
  const btnReportPeriod = document.getElementById('btn-report-period');
  const btnReportSum = document.getElementById('btn-report-sum');
  const btnBackToOrders = document.getElementById('btn-back-to-orders');
  const reportResult = document.getElementById('report-result');

  if (!btnReportStatus || !btnReportPeriod || !btnReportSum || !btnBackToOrders || !reportResult) {
    console.warn('ReportOrder: Some elements not found, skipping init');
    return;
  }

  // Функция рендера таблицы
  function renderTable(headers, rows) {
    reportResult.innerHTML = '';
    const table = document.createElement('table');
    table.style.width = '100%';
    table.border = '1';

    const thead = document.createElement('thead');
    const trHead = document.createElement('tr');
    headers.forEach(h => {
      const th = document.createElement('th');
      th.textContent = h;
      trHead.appendChild(th);
    });
    thead.appendChild(trHead);
    table.appendChild(thead);

    const tbody = document.createElement('tbody');
    rows.forEach(row => {
      const tr = document.createElement('tr');
      row.forEach(cell => {
        const td = document.createElement('td');
        td.textContent = cell;
        tr.appendChild(td);
      });
      tbody.appendChild(tr);
    });
    table.appendChild(tbody);

    reportResult.appendChild(table);
  }

  // Общая функция получения отчёта с сервера
  async function fetchReport(type) {
    try {
      const jwt = localStorage.getItem('jwt');
      if (!jwt) {
        alert('Вы не авторизованы');
        return null;
      }
      const res = await fetch(`/api/moderator/orders/report/${type}`, {
        headers: { Authorization: `Bearer ${jwt}` }
      });
      if (!res.ok) throw new Error('Ошибка получения отчёта');
      return await res.json();
    } catch (e) {
      alert(e.message);
      return null;
    }
  }

  // Обработчики кнопок отчётов
  btnReportStatus.addEventListener('click', async () => {
    const data = await fetchReport('status');
    if (!data) return;
    const headers = ['Статус', 'Количество'];
    const rows = data.map(item => [item.status, item.count]);
    renderTable(headers, rows);
  });

  btnReportPeriod.addEventListener('click', async () => {
    const data = await fetchReport('period');
    if (!data) return;
    const headers = ['Период', 'Количество заказов', 'Сумма'];
    const rows = data.map(item => [item.period, item.count, item.sum]);
    renderTable(headers, rows);
  });

  btnReportSum.addEventListener('click', async () => {
    const data = await fetchReport('sum');
    if (!data) return;
    const headers = ['Диапазон суммы', 'Количество заказов'];
    const rows = data.map(item => [item.range, item.count]);
    renderTable(headers, rows);
  });

  btnBackToOrders.addEventListener('click', () => {
    window.location.href = '/admin/orders';
  });
}
