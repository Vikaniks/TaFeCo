// reportOrder.js
// Модуль для страницы отчетов по заказам: запросы на сервер и отрисовка таблиц

export function initReportModule() {
  const btnReportStatus = document.getElementById('btn-report-status');
  const startDate = document.getElementById('start-date');
  const endDate = document.getElementById('end-date');
  const btnReportPeriod = document.getElementById('btn-report-period');
  const btnReportSum = document.getElementById('btn-report-sum');
  const btnBackToOrders = document.getElementById('btn-back-to-orders');
  const reportResult = document.getElementById('report-result');
  const btnReportSummary = document.getElementById('btnReportSummary');

  if (!btnReportStatus || !btnReportPeriod || !btnReportSum || !btnBackToOrders || !reportResult) {
    console.warn('ReportOrder: Some elements not found, skipping init');
    return;
  }

  // Функция рендера таблицы
  function renderTable(headers, rows, totalRowIndex = null) {
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
    rows.forEach((row, index) => {
      const tr = document.createElement('tr');

      // Если это итоговая строка, добавляем класс
      if (index === totalRowIndex) {
        tr.classList.add('total-row');
      }

      row.forEach(cell => {
        const td = document.createElement('td');
        // Проверяем, если строка содержит HTML теги, вставляем как innerHTML
        if (typeof cell === 'string' && cell.includes('<strong>')) {
          td.innerHTML = cell;
        } else {
          td.textContent = cell;
        }
        tr.appendChild(td);
      });
      tbody.appendChild(tr);
    });
    table.appendChild(tbody);

    reportResult.appendChild(table);
  }

  // Общая функция получения отчёта с сервера
  async function fetchReport(filters = {}) {
    try {
      const jwt = localStorage.getItem('jwt');
      if (!jwt) {
        alert('Вы не авторизованы');
        return null;
      }

      const query = new URLSearchParams(filters).toString();

      const res = await fetch(`/api/moderator/orders/report?${query}`, {
        headers: { Authorization: `Bearer ${jwt}` }
      });

      if (!res.ok) throw new Error('Ошибка получения отчёта');
      return await res.json();
    } catch (e) {
      alert(e.message);
      return null;
    }
  }

  async function fetchSumReport(startDate, endDate) {
    try {
      const jwt = localStorage.getItem('jwt');
      if (!jwt) {
        alert('Вы не авторизованы');
        return null;
      }
      const query = new URLSearchParams({ startDate, endDate }).toString();
      const res = await fetch(`/api/moderator/orders/report/sum?${query}`, {
        headers: { Authorization: `Bearer ${jwt}` }
      });
      if (!res.ok) throw new Error('Ошибка получения отчёта по сумме заказов');
      return await res.json();
    } catch (e) {
      alert(e.message);
      return null;
    }
  }

  btnReportSummary.addEventListener('click', async () => {
    const periodSelect = document.getElementById('summary-period-select');
    const period = periodSelect.value;
    const { startDate, endDate } = getDateRangeByPeriod(period);

    const data = await fetchSumReport(startDate, endDate);
    if (!data || !data.orders) return;

    const headers = ['ID заказа', 'Дата', 'Клиент', 'Сумма'];

    // Форматируем дату и строки
    const rows = data.orders.map(order => [
      order.id ?? '—',
      order.date ? formatDate(order.date) : '—',
      order.customer ?? '—',
      order.sum.toLocaleString('ru-RU', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    ]);

    // добавляем итоговую строку, где ячейки с тегом <strong>
    const totalSumFormatted = data.totalSum.toLocaleString('ru-RU', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

    const totalRow = ['', '', '<strong>Итого:</strong>', `<strong>${totalSumFormatted}</strong>`];
    rows.push(totalRow);


    // рендерим таблицу, передавая индекс последней строки как итоговой
    renderTable(headers, rows, rows.length - 1);
  });

  // Форматирование даты "2025-07-07" → "07.07.2025"
  function formatDate(isoDate) {
    const [year, month, day] = isoDate.split('-');
    return `${day}.${month}.${year}`;
  }


  // Обработчики кнопок отчётов
  btnReportStatus.addEventListener('click', async () => {
    const data = await fetchReport({ groupBy: 'status' });
    if (!data) return;
    const headers = ['Статус', 'Количество'];
    const rows = data.map(item => [item.status, item.count]);
    renderTable(headers, rows);
  });

  btnReportPeriod.addEventListener('click', async () => {
    const start = startDate.value;
    const end = endDate.value;

    if (!start || !end) {
      alert('Пожалуйста, выберите начальную и конечную дату');
      return;
    }

    const data = await fetchReport({ groupBy: 'period', startDate: start, endDate: end });
    console.log('Полученные данные:', data);
    if (!data) return;

    const headers = ['Период', 'Количество заказов', 'Сумма'];

    // Если данных очень много, можно разбить рендеринг на части
    const rows = [];
    for (let i = 0; i < data.length; i++) {
      rows.push([data[i].period, data[i].count, data[i].sum]);
      if (i % 50 === 0) await new Promise(r => setTimeout(r, 0));  // даём браузеру обновиться
    }

    renderTable(headers, rows);
  });

  btnReportSum.addEventListener('click', async () => {
    const data = await fetchReport({ groupBy: 'sum' });
    if (!data) return;
    const headers = ['Диапазон суммы', 'Количество заказов'];
    const rows = data.map(item => [item.range, item.count]);
    renderTable(headers, rows);
  });

  function getDateRangeByPeriod(period) {
    const now = new Date();
    let startDate, endDate;

    // Конвертация даты в yyyy-mm-dd для отправки на сервер
    function formatDate(date) {
      return date.toISOString().split('T')[0];
    }

    endDate = now;

    switch (period) {
      case 'week':
        // Последние 7 дней включая сегодня
        startDate = new Date(now);
        startDate.setDate(now.getDate() - 6);
        break;
      case 'month':
        startDate = new Date(now.getFullYear(), now.getMonth(), 1);
        break;
      case 'quarter':
        const currentMonth = now.getMonth();
        const quarterStartMonth = currentMonth - (currentMonth % 3);
        startDate = new Date(now.getFullYear(), quarterStartMonth, 1);
        break;
      case 'year':
        startDate = new Date(now.getFullYear(), 0, 1);
        break;
      default:
        startDate = null;
        endDate = null;
    }

    return {
      startDate: startDate ? formatDate(startDate) : null,
      endDate: formatDate(endDate)
    };
  }


  btnBackToOrders.addEventListener('click', () => {
    window.location.href = '/admin/orders';
  });
}