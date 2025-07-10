// /js/adminProducts.js

// adminProducts.js
let products = [];


export async function fetchProducts() {
  try {
    const token = localStorage.getItem("jwt");
    const response = await fetch("/api/products", {
      headers: {
        "Content-Type": "application/json",
        "Authorization": token ? `Bearer ${token}` : ""
      }
    });

    if (!response.ok) {
      throw new Error("Ошибка загрузки продуктов");
    }

    products = await response.json();;
    return products;
  } catch (error) {
    console.error("Ошибка при загрузке продуктов:", error);
    throw error;
  }
}

export async function createProduct(productDTO, archives = [], photos = []) {
  const formData = new FormData();
  formData.append("product", new Blob([JSON.stringify(productDTO)], {
    type: "application/json"
  }));

  for (const file of archives) {
    formData.append("archive", file);
  }

  for (const file of photos) {
    formData.append("photo", file);
  }

  const token = localStorage.getItem("jwt");

  const response = await fetch("/api/moderator/products", {
    method: "POST",
    headers: {
      "Authorization": token ? `Bearer ${token}` : ""
    },
    body: formData
  });

  if (!response.ok) {
    const errorText = await response.text();
    console.error("Ответ сервера:", errorText);
    throw new Error("Ошибка при создании продукта");
  }

  return await response.json();
}

export async function createCategory(type) {
  const token = localStorage.getItem("jwt");
  const response = await fetch("/api/moderator/categories", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`
    },
    body: JSON.stringify({ type })
  });

  if (!response.ok) throw new Error("Ошибка создания категории");
  return await response.json();
}

export async function createDimension(dimension) {
  const token = localStorage.getItem("jwt");
  const response = await fetch("/api/moderator/dimensions", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "Authorization": `Bearer ${token}`
    },
    body: JSON.stringify({ dimension })
  });

  if (!response.ok) throw new Error("Ошибка создания единицы измерения");
  return await response.json();
}

export async function updateProduct(product, photoFile, archiveFile) {
  const token = localStorage.getItem('jwt');

  const formData = new FormData();
  formData.append('product', new Blob([JSON.stringify(product)], { type: "application/json" }));

  if (photoFile) {
    formData.append('photo', photoFile);
  }

  if (archiveFile) {
    formData.append('archive', archiveFile);
  }

  const response = await fetch(`/api/moderator/products/${product.id}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`
    },
    body: formData
  });

  if (!response.ok) {
    throw new Error(`Ошибка обновления продукта: ${response.statusText}`);
  }

  return await response.json();
}


async function deleteProduct(id) {
  const token = localStorage.getItem('jwt');

  // 1. Получить текущий продукт
  const response = await fetch(`/api/moderator/products/${id}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  if (!response.ok) throw new Error('Не удалось загрузить продукт');

  const product = await response.json();

  // 2. Обновить только поле active
  product.active = false;

  const formData = new FormData();
  formData.append('product', new Blob([JSON.stringify(product)], { type: "application/json" }));

  // 3. Отправить обновлённый объект
  const updateResponse = await fetch(`/api/moderator/products/${id}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`
    },
    body: formData
  });

  if (!updateResponse.ok) throw new Error('Ошибка обновления продукта');

  console.log('Продукт деактивирован');
}



export function renderProductTable(products) {
  const container = document.getElementById('product-table-container');
  if (!container) return;

  container.innerHTML = `
      <table class="product-table">
          <thead>
              <tr>
                  <th>ID</th>
                  <th>Наименование</th>
                  <th>Цена</th>
                  <th>Категория</th>
                  <th>Ед. изм.</th>
                  <th>Активен</th>
                  <th>Действия</th>
              </tr>
          </thead>
          <tbody>
              ${products.map(p => `
                  <tr>
                      <td>${p.id}</td>
                      <td>${p.product}</td>
                      <td>${p.price}</td>
                      <td>${p.type || ''}</td>
                      <td>${p.dimensionName || ''}</td>
                      <td>${p.active ? 'Да' : 'Нет'}</td>
                      <td>
                          <button class="btn-edit" data-id="${p.id}"><i class="fa-solid fa-pen-to-square fa-lg custom-icon"></i></button>
                          <button class="btn-delete" data-id="${p.id}"><i class="fa-solid fa-trash fa-lg custom-icon"></i></button>
                      </td>
                  </tr>
              `).join('')}
          </tbody>
      </table>
  `;

   container.querySelectorAll('.btn-edit').forEach(btn => {
     btn.addEventListener('click', async (e) => {
       const id = e.currentTarget.dataset.id;
       await openEditModal(id);
     });
   });

  container.querySelectorAll('.btn-delete').forEach(btn =>
    btn.addEventListener('click', async () => {
      const id = btn.dataset.id;
      if (confirm('Удалить продукт?')) {
        try {
          await deleteProduct(id);
          await loadProducts(); // перезагрузить
        } catch (err) {
          alert('Ошибка при удалении');
        }
      }
    })
  );
}

function normalizeData(data) {
  // Если это массив — возвращаем как есть
  if (Array.isArray(data)) return data;
  // Если это объект с полем data — пробуем взять оттуда
  if (data && Array.isArray(data.data)) return data.data;
  // Иначе — возвращаем пустой массив, чтобы избежать ошибок
  return [];
}


async function loadSelectOptions(form) {
  // Здесь подтягиваем категории и единицы измерения
  try {
    const token = localStorage.getItem('jwt');

    const [rawCategories, rawDimensions] = await Promise.all([
      fetch('/api/categories/all', {
        headers: { 'Authorization': `Bearer ${token}` }
      }).then(res => res.json()),
      fetch('/api/dimensions/all', {
        headers: { 'Authorization': `Bearer ${token}` }
      }).then(res => res.json()),
    ]);
    const categories = normalizeData(rawCategories);
    const dimensions = normalizeData(rawDimensions);

    console.log('Категории:', categories);
    console.log('Ед. изм:', dimensions);

    const categorySelect = form.elements['category'];
    categorySelect.innerHTML = '';
    categories.forEach(cat => {
      const option = document.createElement('option');
      option.value = cat.id;
      option.textContent = cat.type;
      categorySelect.appendChild(option);
    });

    const dimensionSelect = form.elements['dimension'];
    dimensionSelect.innerHTML = '';
    dimensions.forEach(dim => {
      const option = document.createElement('option');
      option.value = dim.id;
      option.textContent = dim.dimension;
      dimensionSelect.appendChild(option);
    });

  } catch (error) {
    console.error('Ошибка загрузки категорий или единиц измерения:', error);
  }
}

export async function loadProducts() {
  try {
    const products = await fetchProducts();
    renderProductTable(products);
  } catch (err) {
    console.error('Ошибка при загрузке:', err);
  }
}

export async function openEditModal(id) {
  const editForm = document.getElementById('editProductForm');
  const editModal = document.getElementById('editModal');

  await loadSelectOptions(editForm); // если селекты подгружаются динамически

  try {
    const token = localStorage.getItem('jwt');
    const res = await fetch(`/api/moderator/products/${id}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });

    if (!res.ok) throw new Error('Ошибка загрузки продукта');

    const product = await res.json();

    // Заполняем форму
    editForm.querySelector('#edit-id').value = product.id || '';
    editForm.querySelector('#edit-name').value = product.product || '';
    editForm.querySelector('#edit-description').value = product.description || '';
    editForm.querySelector('#edit-price').value = product.price ?? 0;
    editForm.querySelector('#edit-category').value = product.category || product.categorise || '';
    editForm.querySelector('#edit-dimension').value = product.dimension || product.dimension || '';
    editForm.querySelector('#edit-active').checked = !!product.active;

    // Очищаем файлы
    editForm.querySelector('#edit-archive').value = '';
    editForm.querySelector('#edit-photo').value = '';

    // Показываем модальное окно
    editModal.style.display = 'block';
  } catch (err) {
    alert('Ошибка при открытии модального окна: ' + err.message);
  }
}

// 1. Загрузка категорий
async function fetchCategories() {
  const token = localStorage.getItem('jwt');
  const response = await fetch('/api/categories/all', {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  if (!response.ok) {
    throw new Error('Ошибка загрузки категорий');
  }
  return await response.json();
}

// 2. Заполнение select категорий
async function populateCategorySelect() {
  const categorySelect = document.getElementById('filterCategory');
  if (!categorySelect) {
    console.warn('Селект filterCategory не найден');
    return;
  }
  const categories = await fetchCategories();

  // Добавим "все" — сброс фильтра
  const defaultOption = document.createElement('option');
  defaultOption.value = '';
  defaultOption.textContent = 'Все категории';
  categorySelect.appendChild(defaultOption);

  categories.forEach(c => {
    const option = document.createElement('option');
    option.value = c.id;
    option.textContent = c.type;
    categorySelect.appendChild(option);
  });
}

// 3. Навешиваем обработчики фильтрации
function setupFilterHandlers() {
  const categorySelect = document.getElementById('filterCategory');
  const activeSelect = document.getElementById('filterActive');

  if (categorySelect) {
    categorySelect.addEventListener('change', applyFilters);
  } else {
    console.warn('Селект category не найден');
  }

  if (activeSelect) {
    activeSelect.addEventListener('change', applyFilters);
  } else {
    console.warn('Селект active не найден');
  }
}

// 4. Фильтрация
function applyFilters() {
  renderFilteredProducts();
}

function renderFilteredProducts() {
  const filtered = filterProducts(products);
  renderProductTable(filtered); // предполагается, что эта функция уже реализована
}

function filterProducts(list) {
  const categorySelect = document.getElementById('filterCategory');
  const activeSelect = document.getElementById('filterActive');

  const categoryId = categorySelect ? categorySelect.value : '';
  const isActive = activeSelect ? activeSelect.value : '';

  return list.filter(p => {
    const matchCategory = categoryId ? p.categorise == categoryId : true;
    const matchActive = isActive ? p.active === (isActive === 'true') : true;
    return matchCategory && matchActive;
  });
}

export async function initProductModule() {
  const modal = document.getElementById('create-product-modal');
  const openBtn = document.getElementById('open-create-product-modal');
  const closeBtn = document.getElementById('close-create-product-modal');
  const form = document.getElementById('create-product-form');

  // Открытие модального окна и загрузка категорий/единиц измерения
  openBtn.addEventListener('click', () => {
    modal.style.display = 'block';
    loadSelectOptions(form); // Загружаем данные только при открытии
  });

  // Закрытие по кнопке
  closeBtn.addEventListener('click', () => {
    modal.style.display = 'none';
  });

  // Закрытие по клику вне окна
  window.addEventListener('click', e => {
    if (e.target === modal) {
      modal.style.display = 'none';
    }
  });

  // Обработка формы создания продукта
  const openCreateBtn = document.getElementById('open-create-product-modal');
  const createModal = document.getElementById('create-product-modal');
  const closeCreateModal = document.getElementById('close-create-product-modal');
  const createForm = document.getElementById('create-product-form');

  // Открытие модального окна и загрузка select'ов
  openCreateBtn.addEventListener('click', async () => {
    createForm.reset(); // Очистка формы
    createModal.style.display = 'block';
    await loadSelectOptions(createForm); // Загрузка категорий и ед. изм.
  });

  // Закрытие модального окна
  closeCreateModal.addEventListener('click', () => {
    createModal.style.display = 'none';
  });

  // Обработка submit
  createForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const categorySelect = createForm.elements['category'];
    const dimensionSelect = createForm.elements['dimension'];

    const productDTO = {
      product: createForm.elements['name'].value.trim(),
      description: createForm.elements['description'].value.trim(),
      price: parseFloat(createForm.elements['price'].value),
      categorise: parseInt(categorySelect.value),
      dimension: parseInt(dimensionSelect.value),
      active: createForm.elements['active'].checked,
    };

    const archives = Array.from(createForm.elements['archive'].files);
    const photos = Array.from(createForm.elements['photo'].files);

    try {
      await createProduct(productDTO, archives, photos);
      alert('Продукт успешно создан');
      createModal.style.display = 'none';
      createForm.reset();
      await loadProducts(); // перезагрузить таблицу
    } catch (err) {
      alert('Ошибка при создании продукта: ' + err.message);
    }
  });

  // Обработка формы создания категории
  const categoryModal = document.getElementById("create-category-modal");
  const openCategoryBtn = document.getElementById("open-create-category-modal");
  const closeCategoryBtn = document.getElementById("close-create-category-modal");
  const categoryForm = document.getElementById("create-category-form");

  openCategoryBtn.addEventListener("click", () => {
    categoryModal.style.display = "block";
  });
  closeCategoryBtn.addEventListener("click", () => {
    categoryModal.style.display = "none";
  });
  categoryForm.addEventListener("submit", async e => {
    e.preventDefault();
    const type = categoryForm.elements['type'].value.trim();
    try {
      await createCategory(type);
      alert("Категория добавлена");
      categoryModal.style.display = "none";
      categoryForm.reset();
      loadSelectOptions(form); // обновим селект
    } catch (err) {
      alert("Ошибка при добавлении категории");
    }
  });

  // Обработка формы создания ед.изм.
  const dimensionModal = document.getElementById("create-dimension-modal");
  const openDimensionBtn = document.getElementById("open-create-dimension-modal");
  const closeDimensionBtn = document.getElementById("close-create-dimension-modal");
  const dimensionForm = document.getElementById("create-dimension-form");

  openDimensionBtn.addEventListener("click", () => {
    dimensionModal.style.display = "block";
  });
  closeDimensionBtn.addEventListener("click", () => {
    dimensionModal.style.display = "none";
  });
  dimensionForm.addEventListener("submit", async e => {
    e.preventDefault();
    const dimension = dimensionForm.elements['dimension'].value.trim();
    try {
      await createDimension(dimension);
      alert("Единица измерения добавлена");
      dimensionModal.style.display = "none";
      dimensionForm.reset();
      loadSelectOptions(form); // обновим селект
    } catch (err) {
      alert("Ошибка при добавлении единицы измерения");
    }
  });

  // Обработка кнопки изменения продукта
  const editForm = document.getElementById('editProductForm');
  const editModal = document.getElementById('editModal');
  const closeModalBtn = document.getElementById('closeModal');

  // Закрытие окна по кнопке
  closeModalBtn.addEventListener('click', () => {
    editModal.style.display = 'none';
  });

  // Закрытие по клику вне модального окна
  window.addEventListener('click', e => {
    if (e.target === editModal) {
      editModal.style.display = 'none';
    }
  });

  // Отправка формы редактирования
  editForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const product = {
      id: editForm.querySelector('#edit-id').value,
      product: editForm.querySelector('#edit-name').value.trim(),
      description: editForm.querySelector('#edit-description').value.trim(),
      price: parseFloat(editForm.querySelector('#edit-price').value),
      categorise: parseInt(editForm.querySelector('#edit-category').value),
      dimension: parseInt(editForm.querySelector('#edit-dimension').value),
      active: editForm.querySelector('#edit-active').checked,
    };

    const photoFile = editForm.querySelector('#edit-photo').files[0];
    const archiveFile = editForm.querySelector('#edit-archive').files[0];

    try {
      await updateProduct(product, photoFile, archiveFile); // обёртка над updateProduct
      editModal.style.display = 'none';
      editForm.reset();
      await loadProducts(); // перезагрузка списка
    } catch (err) {
      alert('Ошибка при обновлении: ' + err.message);
    }
  });

  const sortSelect = document.getElementById('sortProducts');

  sortSelect.addEventListener('change', () => {
    const value = sortSelect.value;
    let sorted = [...products];

    switch (value) {
      case 'name-asc':
        sorted.sort((a, b) => a.product.localeCompare(b.product)); // исправлено на .product
        break;
      case 'name-desc':
        sorted.sort((a, b) => b.product.localeCompare(a.product));
        break;
      case 'id-asc':
        sorted.sort((a, b) => a.id - b.id);
        break;
      case 'id-desc':
        sorted.sort((a, b) => b.id - a.id);
        break;
    }

    renderProductTable(sorted);
  });

// Кнопки фильтров по категории и активности
try {
    await populateCategorySelect();   // заполняем категории
    renderFilteredProducts();         // отрисовываем уже загруженные продукты
    setupFilterHandlers();            // навешиваем обработчики
  } catch (err) {
    console.error('Ошибка инициализации:', err);
  }

  // Изначальная загрузка списка продуктов
  await loadProducts();
}
