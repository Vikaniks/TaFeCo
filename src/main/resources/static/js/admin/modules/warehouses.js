const warehouseTbody = document.getElementById("warehouse-tbody");
const transferModal = document.getElementById("transfer-modal");
const transferProductsContainer = document.getElementById("transfer-products");
const addTransferProductBtn = document.getElementById("add-transfer-product-btn");
const closeTransferModal = document.getElementById("close-transfer-modal");
const submitTransferBtn = document.getElementById("submit-transfer-btn");

// 🔁 Пример загрузки складов
async function loadWarehouses() {
  const token = localStorage.getItem("jwt");
  const res = await fetch("/api/moderator/warehouses", {
    headers: {
      Authorization: `Bearer ${token}`
    }
  });

  const warehouses = await res.json();
  warehouseTbody.innerHTML = "";
  warehouses.forEach(renderWarehouseRow);
}

// ➕ Добавление строки товара
function addTransferProductRow() {
  const div = document.createElement("div");
  div.classList.add("transfer-product-row");

  div.innerHTML = `
    <input type="text" class="transfer-product-name" placeholder="Название товара" />
    <input type="number" class="transfer-product-qty" placeholder="Количество" />
    <button class="remove-transfer-product-btn">Удалить</button>
  `;

  transferProductsContainer.appendChild(div);
}

// 🗑️ Удаление строки товара
transferProductsContainer.addEventListener("click", (e) => {
  if (e.target.classList.contains("remove-transfer-product-btn")) {
    e.target.parentElement.remove();
  }
});

// 🔘 Показ модалки по кнопке "Трансфер товаров"
warehouseTbody.addEventListener("click", (e) => {
  if (e.target.classList.contains("transfer-btn")) {
    const warehouseId = e.target.dataset.id;
    transferModal.dataset.sourceWarehouseId = warehouseId;
    transferProductsContainer.innerHTML = ""; // Очистить форму
    document.getElementById("targetStoreId").value = "";
    transferModal.classList.remove("hidden");
  }
});

// ➕ Кнопка добавления товара
addTransferProductBtn.addEventListener("click", addTransferProductRow);

// ❌ Закрытие модалки
closeTransferModal.addEventListener("click", () => {
  transferModal.classList.add("hidden");
});

// 📤 Отправка трансфера
submitTransferBtn.addEventListener("click", async () => {
  const token = localStorage.getItem("jwt");
  const targetStoreId = Number(document.getElementById("targetStoreId").value);
  const sourceWarehouseId = Number(transferModal.dataset.sourceWarehouseId);

  const names = document.querySelectorAll(".transfer-product-name");
  const qtys = document.querySelectorAll(".transfer-product-qty");

  const transferRequests = [];

  for (let i = 0; i < names.length; i++) {
    const name = names[i].value.trim();
    const qty = Number(qtys[i].value);

    if (!name || qty <= 0) continue;

    transferRequests.push({
      productName: name,
      quantityToTransfer: qty,
      targetStoreId,
      sourceWarehouseId
    });
  }

  if (transferRequests.length === 0) {
    alert("Добавьте хотя бы один товар");
    return;
  }

  try {
    const res = await fetch("/api/moderator/warehouses/transfer", {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json"
      },
      body: JSON.stringify(transferRequests)
    });

    if (!res.ok) throw new Error("Ошибка при трансфере");

    alert("Трансфер выполнен");
    transferModal.classList.add("hidden");
  } catch (err) {
    console.error(err);
    alert("Не удалось выполнить трансфер");
  }
});

// 📦 Рендер строки склада
function renderWarehouseRow(warehouse) {
  const row = document.createElement("tr");
  row.innerHTML = `
    <td>${warehouse.id}</td>
    <td>${warehouse.name}</td>
    <td>
      <button class="transfer-btn" data-id="${warehouse.id}">Трансфер товаров</button>
    </td>
  `;
  warehouseTbody.appendChild(row);
}

// ▶️ Инициализация
loadWarehouses();
