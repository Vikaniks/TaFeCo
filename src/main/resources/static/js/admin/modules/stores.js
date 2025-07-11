export async function initStoreModule() {

  const API_BASE = "/api/moderator/stores";
  const createModal = document.getElementById("create-modal");
  const editModal = document.getElementById("edit-modal");

  const showInactiveCheckbox = document.getElementById("show-inactive");

  // Кнопка "Показать все магазины"
  document.getElementById("find-all-btn").addEventListener("click", () => {
    loadStores(showInactiveCheckbox.checked);
  });

  // Кнопка "Создать магазин"
  document.getElementById("new-store-btn").addEventListener("click", () => {
    createModal.classList.remove("hidden");
  });

  // Кнопки закрытия модалок
  document.getElementById("close-create-modal").addEventListener("click", () => {
    createModal.classList.add("hidden");
  });

  document.getElementById("close-edit-modal").addEventListener("click", () => {
    editModal.classList.add("hidden");
  });

  // Фильтр: показать неактивные
  showInactiveCheckbox.addEventListener("change", () => {
    loadStores(showInactiveCheckbox.checked);
  });

  // Кнопка создания магазина
  document.getElementById("create-store-btn").addEventListener("click", async () => {
    const token = localStorage.getItem("jwt");

    const store = {
      storeName: document.getElementById("storeName").value,
      location: document.getElementById("location").value,
      warehouse: Number(document.getElementById("warehouse").value)
    };

    try {
      const res = await fetch("/api/moderator/stores/create", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json"
        },
        body: JSON.stringify(store)
      });

      if (!res.ok) throw new Error("Ошибка при создании");

      createModal.classList.add("hidden");
      clearCreateForm();
      loadStores(showInactiveCheckbox.checked);
    } catch (err) {
      console.error(err);
      alert("Не удалось создать магазин");
    }
  });


  // Кнопка обновления магазина
  document.getElementById("update-store-btn").addEventListener("click", async () => {
    const token = localStorage.getItem("jwt");
    const id = document.getElementById("editStoreId").value;
    const store = {
      storeName: document.getElementById("editStoreName").value,
      location: document.getElementById("editLocation").value,
      warehouse: Number(document.getElementById("editWarehouse").value)
    };

    try {
      const res = await fetch(`/api/moderator/stores/${id}`, {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json"
        },
        body: JSON.stringify(store)
      });

      if (!res.ok) throw new Error("Ошибка при обновлении");

      editModal.classList.add("hidden");
      clearEditForm();
      loadStores(showInactiveCheckbox.checked);
    } catch (err) {
      console.error(err);
      alert("Не удалось обновить магазин");
    }
  });

  // Загрузка магазинов
  async function loadStores(showInactive = false) {
    const token = localStorage.getItem("jwt");
    const tableBody = document.getElementById("store-table-body");

      if (!token || token.split('.').length !== 3) {
        alert("Неавторизован. Пожалуйста, войдите в систему.");
        return;
      }

      try {
        const res = await fetch("/api/stores/all", {
          headers: {
            Authorization: `Bearer ${token}`
          }
        });

        if (!res.ok) throw new Error("Ошибка загрузки магазинов");

        const stores = await res.json();

        console.log("stores:", stores);
        tableBody.innerHTML = "";

      const filtered = showInactive
        ? stores
        : stores.filter(s => s.active !== false);

      filtered.forEach(store => {
        const row = document.createElement("tr");

        // Добавим класс для неактивных строк
        if (store.active === false) {
          row.classList.add("bg-gray-100", "text-red-600"); // Tailwind классы
        }

        row.innerHTML = `
          <td>${store.id}</td>
          <td>${store.storeName}</td>
          <td>${store.location}</td>
          <td>${store.warehouseName || store.warehouse || ""}</td>
          <td>
            ${store.active !== false ? "Активен" : "Неактивен"}
          </td>
          <td>
            ${store.active === true
              ? `
                <button class="btn-edit" data-id="${store.id}">
                  <i class="fa-solid fa-pen-to-square fa-lg custom-icon"></i>
                </button>
                <button class="btn-delete" data-id="${store.id}">
                  <i class="fa-solid fa-trash fa-lg custom-icon"></i>
                </button>
              `
              : `
                <span class="text-red-600"></span><br>
                <button class="btn-activate" data-id="${store.id}">
                  Активировать
                </button>
              `
            }
          </td>
        `;
        tableBody.appendChild(row);
      });

tableBody.addEventListener("click", async (e) => {
          if (e.target.closest(".btn-activate")) {
            const storeId = e.target.closest(".btn-activate").dataset.id;
            const token = localStorage.getItem("jwt");

            try {
              const res = await fetch(`/api/moderator/stores/activate/${storeId}`, {
                method: "PUT",
                headers: {
                  Authorization: `Bearer ${token}`
                }
              });

              if (!res.ok) throw new Error("Ошибка активации магазина");

              await loadStores(document.getElementById("show-inactive").checked);
            } catch (error) {
              alert("Не удалось активировать магазин");
              console.error(error);
            }
          }
        });



      // Кнопки "Скрыть"
      document.querySelectorAll(".btn-delete").forEach(btn => {
        btn.addEventListener("click", async () => {
          const id = btn.dataset.id;
          try {
            const res = await fetch(`${API_BASE}/${id}/deactivate`, {
              method: 'PATCH',
              headers: {
                Authorization: `Bearer ${token}`
              }
            });

            if (!res.ok) throw new Error("Ошибка при деактивации");

            loadStores(showInactiveCheckbox.checked);
          } catch (err) {
            console.error(err);
            alert("Не удалось скрыть магазин");
          }
        });
      });

      // Кнопки "Изменить"
      document.querySelectorAll(".btn-edit").forEach(btn => {
        btn.addEventListener("click", () => {
          const id = btn.dataset.id;
          const store = filtered.find(s => s.id == id);
          if (!store) return;

          document.getElementById("editStoreId").value = store.id;
          document.getElementById("editStoreName").value = store.storeName;
          document.getElementById("editLocation").value = store.location;
          document.getElementById("editWarehouse").value = store.warehouse;

          editModal.classList.remove("hidden");
        });
      });

    } catch (err) {
      console.error(err);
      alert("Ошибка загрузки списка магазинов");
    }
  }

  // Очистка полей формы создания
  function clearCreateForm() {
    document.getElementById("storeName").value = "";
    document.getElementById("location").value = "";
    document.getElementById("warehouse").value = "";
  }

  // Очистка полей формы редактирования
  function clearEditForm() {
    document.getElementById("editStoreId").value = "";
    document.getElementById("editStoreName").value = "";
    document.getElementById("editLocation").value = "";
    document.getElementById("editWarehouse").value = "";
  }

  // Первая загрузка
  loadStores(false);
}
