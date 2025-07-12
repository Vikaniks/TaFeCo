export async function initWarehouseModule() {
  console.log("initWarehouseModule called");

  const transferModal = document.getElementById("transfer-modal");
  const transferProductsContainer = document.getElementById("transfer-products");
  const addTransferProductBtn = document.getElementById("add-transfer-product-btn");
  const closeTransferModal = document.getElementById("close-transfer-modal");
  const submitTransferBtn = document.getElementById("submit-transfer-btn");
  const showInactiveCheckbox = document.getElementById("show-inactive");
  const warehouseTbody = document.getElementById("warehouse-table-body");

  // Кнопка "Показать все склады"
  document.getElementById("find-all-wh").addEventListener("click", () => {
    loadWarehouses(showInactiveCheckbox.checked);
  });

  // Фильтр: показать неактивные
  showInactiveCheckbox.addEventListener("change", () => {
    loadWarehouses(showInactiveCheckbox.checked);
  });

  // Активация склада
  warehouseTbody.addEventListener("click", async (e) => {
    const btn = e.target.closest(".btn-activate");
    if (btn) {
      const warehouseId = btn.dataset.id;
      const token = localStorage.getItem("jwt");

      try {
        const res = await fetch(`/api/moderator/warehouses/${warehouseId}/activate`, {
          method: "PATCH",
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!res.ok) throw new Error("Ошибка при активации склада");

        alert("Склад успешно активирован");
        loadWarehouses(showInactiveCheckbox.checked);
      } catch (err) {
        console.error(err);
        alert("Не удалось активировать склад");
      }
    }
  });

  // Загрузка складов с фильтрацией
  async function loadWarehouses(showInactive = false, returnOnly = false) {
    console.log("loadWarehouses called");

    const token = localStorage.getItem("jwt");

    if (!token || token.split(".").length !== 3) {
      alert("Неавторизован. Пожалуйста, войдите в систему.");
      return;
    }

    try {
      const res = await fetch("/api/warehouses/all", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (!res.ok) throw new Error("Ошибка загрузки складов");

      let warehouses = await res.json();

      // Фильтрация активных/всех складов
      if (!showInactive) {
        warehouses = warehouses.filter((w) => w.active);
      }

      if (returnOnly) return warehouses;

      warehouseTbody.innerHTML = "";

      warehouses.forEach((warehouse) => {
        const row = document.createElement("tr");

        row.innerHTML = `
          <td>${warehouse.id}</td>
          <td>${warehouse.location || ""}</td>
          <td>${warehouse.active ? "Активен" : "Неактивен"}</td>
          <td>
            ${
              warehouse.active
                ? `
                  <button class="btn-edit-warehouse" data-id="${warehouse.id}">
                    <i class="fa-solid fa-pen-to-square fa-lg custom-icon"></i>
                  </button>
                  <button class="btn-deactivate" data-id="${warehouse.id}">
                    <i class="fa-solid fa-trash fa-lg custom-icon"></i>
                  </button>
                `
                : `
                  <button class="btn-activate" data-id="${warehouse.id}">
                    Активировать
                  </button>
                `
            }
          </td>
        `;

        warehouseTbody.appendChild(row);
      });

      // Кнопки "Скрыть"
      document.querySelectorAll(".btn-deactivate").forEach((btn) => {
        btn.addEventListener("click", async () => {
          const id = btn.dataset.id;
          const token = localStorage.getItem("jwt");

          try {
            const res = await fetch(`/api/moderator/warehouses/${id}/deactivate`, {
              method: "PATCH",
              headers: {
                Authorization: `Bearer ${token}`,
              },
            });

            if (!res.ok) throw new Error("Ошибка при деактивации");

            alert("Склад успешно скрыт");
            loadWarehouses(showInactiveCheckbox.checked);
          } catch (err) {
            console.error(err);
            alert("Не удалось скрыть склад");
          }
        });
      });

      // Кнопки "Изменить"
      document.querySelectorAll(".btn-edit-warehouse").forEach((btn) => {
        btn.addEventListener("click", () => {
          const id = btn.dataset.id;
          const warehouse = warehouses.find((w) => w.id == id);
          if (!warehouse) return;

          document.getElementById("editWarehouseId").value = warehouse.id;
          document.getElementById("editWarehouseLocation").value = warehouse.location;

          document.getElementById("editWarehouseModal").classList.remove("hidden");
        });
      });
    } catch (error) {
      console.error(error);
      alert("Ошибка при загрузке данных складов");
    }
  }

  // Очистка полей формы создания
  function clearCreateWarehouseForm() {
    document.getElementById("warehouse-location").value = "";
  }

  // Очистка полей формы редактирования
  function clearEditWarehouseForm() {
    document.getElementById("editWarehouseId").value = "";
    document.getElementById("editWarehouseLocation").value = "";
  }

  // --- Поиск продукта ---
  const productSearchInput = document.getElementById("productSearch");
  const productSuggestions = document.getElementById("productSuggestions");
  const productIdInput = document.getElementById("productId");

  productSearchInput.addEventListener("input", async () => {
    const token = localStorage.getItem("jwt");
    const keyword = productSearchInput.value.trim();
    if (keyword.length < 2) return;

    try {
      const res = await fetch(`/api/products/search?keyword=${encodeURIComponent(keyword)}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) throw new Error("Ошибка при поиске продуктов");

      const products = await res.json();
      productSuggestions.innerHTML = "";

      products.forEach((product) => {
        const option = document.createElement("option");
        option.value = product.name || product.product; // предполагается поле name
        option.dataset.id = product.id;
        productSuggestions.appendChild(option);
      });
    } catch (err) {
      console.error(err);
    }
  });

  productSearchInput.addEventListener("blur", () => {
    const options = productSuggestions.children;
    for (let option of options) {
      if (option.value === productSearchInput.value) {
        productIdInput.value = option.dataset.id;
        return;
      }
    }
    productIdInput.value = "";
  });

  // --- Модалка добавить товар в магазин ---
  async function loadStoresForSelect() {
    const token = localStorage.getItem("jwt");
    const warehouseSelect = document.getElementById("warehouseId");
    const storeSelect = document.getElementById("storeId");

    try {
        // Загружаем магазины
        const stores = await loadStores();
        storeSelect.innerHTML = "";

        if (stores.length === 0) {
          storeSelect.innerHTML = "<option value=''>Нет доступных магазинов</option>";
        } else {
          stores.forEach((store) => {
            const option = document.createElement("option");
            option.value = store.id;
            option.textContent = store.storeName || store.location || `Магазин #${store.id}`;
            storeSelect.appendChild(option);
          });
        }

        // Загружаем склады
        const warehouses = await loadWarehouses(false, true); // activeOnly = true, returnOnly = true
        warehouseSelect.innerHTML = "";

        if (warehouses.length === 0) {
          warehouseSelect.innerHTML = "<option value=''>Нет доступных складов</option>";
        } else {
          warehouses.forEach((warehouse) => {
            const option = document.createElement("option");
            option.value = warehouse.id;
            option.textContent = warehouse.location || `Склад #${warehouse.id}`;
            warehouseSelect.appendChild(option);
          });
        }

      } catch (error) {
        console.error("Ошибка при загрузке магазинов и складов:", error);
        storeSelect.innerHTML = "<option value=''>Ошибка загрузки</option>";
        warehouseSelect.innerHTML = "<option value=''>Ошибка загрузки</option>";
      }
  }

  async function loadStores() {
    const token = localStorage.getItem("jwt");
    const res = await fetch("/api/stores/all", {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error("Не удалось загрузить список магазинов");
    return await res.json();
  }

  const addProductModal = document.getElementById("addProductModal");
  const openAddProductModal = document.getElementById("openAddProductModal");
  const cancelAddProduct = document.getElementById("cancelAddProduct");

  openAddProductModal.addEventListener("click", () => {
    addProductModal.classList.remove("hidden");
    loadStoresForSelect();
  });

  cancelAddProduct.addEventListener("click", () => {
    addProductModal.classList.add("hidden");
    document.getElementById("addProductToWarehouseForm").reset();
    productIdInput.value = "";
  });

  document.getElementById("addProductToWarehouseForm").addEventListener("submit", async (e) => {
    e.preventDefault();
      const productIdInput = document.getElementById("productId");
      const warehouseSelect = document.getElementById("warehouseId");
      const storeSelect = document.getElementById("storeId");

      const productId = +productIdInput.value;
      const storeId = +storeSelect.value;
      const warehouseId = +warehouseSelect.value;
      const quantity = +document.getElementById("quantity").value;

    if (!productId) {
        alert("Выберите продукт из уже существующих, либо создайте новый продукт!");
        return;
      }

      if (!storeId) {
        alert("Выберите магазин!");
        return;
      }

      if (!warehouseId) {
        alert("Выберите склад!");
        return;
      }

    console.log("Отправляем:", { productId, quantity });

    try {
      const token = localStorage.getItem("jwt");
      const res = await fetch(`/api/moderator/stores/${storeId}/receive`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ productId, quantity: quantity }),
      });

      if (!res.ok) {
          if (res.status === 403 || res.status === 401) {
            throw new Error("Доступ запрещён. Войдите в систему.");
          } else {
            throw new Error("Ошибка при добавлении товара");
          }
        }


      alert("Продукт успешно добавлен в магазин");
      addProductModal.classList.add("hidden");
      document.getElementById("addProductToWarehouseForm").reset();
      productIdInput.value = "";

      // Можно обновить данные, если необходимо
      // loadStores();
    } catch (err) {
      console.error(err);
      alert("Не удалось добавить товар");
    }
  });

  // Создание нового склада
  const createWarehouseModal = document.getElementById("create-warehouse-modal");
  const createWarehouseBtn = document.getElementById("create-warehouse-btn");
  const submitCreateWarehouseBtn = document.getElementById("submit-create-warehouse");
  const closeCreateWarehouseBtn = document.getElementById("close-create-warehouse");

  createWarehouseBtn.addEventListener("click", () => {
    createWarehouseModal.classList.remove("hidden");
  });

  closeCreateWarehouseBtn.addEventListener("click", () => {
    createWarehouseModal.classList.add("hidden");
    clearCreateWarehouseForm();
  });

  submitCreateWarehouseBtn.addEventListener("click", async () => {
    const locationInput = document.getElementById("warehouse-location").value.trim();
    const token = localStorage.getItem("jwt");

    if (!locationInput) {
      alert("Укажите местоположение склада");
      return;
    }

    try {
      const res = await fetch("/api/moderator/warehouses/create", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ location: locationInput }),
      });

      if (!res.ok) throw new Error("Ошибка при создании склада");

      alert("Склад успешно создан");
      createWarehouseModal.classList.add("hidden");
      clearCreateWarehouseForm();
      loadWarehouses(showInactiveCheckbox.checked);
    } catch (err) {
      console.error(err);
      alert("Не удалось создать склад");
    }
  });

  // Редактирование склада
  const editWarehouseModal = document.getElementById("editWarehouseModal");
  const submitEditWarehouseBtn = document.getElementById("submitEditWarehouse");
  const closeEditWarehouseBtn = document.getElementById("closeEditWarehouse");

  closeEditWarehouseBtn.addEventListener("click", () => {
    editWarehouseModal.classList.add("hidden");
    clearEditWarehouseForm();
  });

  submitEditWarehouseBtn.addEventListener("click", async () => {
    const id = document.getElementById("editWarehouseId").value;
    const location = document.getElementById("editWarehouseLocation").value.trim();
    const token = localStorage.getItem("jwt");

    if (!location) {
      alert("Укажите местоположение склада");
      return;
    }

    try {
      const res = await fetch(`/api/moderator/warehouses/${id}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ location }),
      });

      if (!res.ok) throw new Error("Ошибка при обновлении склада");

      alert("Склад успешно обновлён");
      editWarehouseModal.classList.add("hidden");
      clearEditWarehouseForm();
      loadWarehouses(showInactiveCheckbox.checked);
    } catch (err) {
      console.error(err);
      alert("Не удалось обновить склад");
    }
  });

  // Инициализация загрузки складов
  loadWarehouses(false);
}
