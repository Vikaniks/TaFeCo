import { addToCart } from './cart.js';

export function renderProductList() {

console.log('renderProductList called');

    const container = document.getElementById('product-list');
    if (!container) return;

    container.innerHTML = '';

    fetch('/api/products')
        .then(response => {
            if (!response.ok) throw new Error('Ошибка загрузки');
            return response.json();
        })
        .then(products => {
        console.log("Продукты:", products);
            products.forEach((product, index) => {
                const card = document.createElement('div');
                card.className = 'product-card';

                const isActive = product.active ?? true;
                const baseName = product.product || 'Без названия';
                const productName = isActive ? baseName : `${baseName} (снят с продажи)`;


                const imageUrl = (product.photos && product.photos.length > 0)
                  ? `${product.photos[0].photo}`
                  : '/img/empty.png';

                card.dataset.id = product.id;
                card.dataset.name = productName;
                card.dataset.price = product.price;
                card.dataset.description = product.description || '';
                card.dataset.image = product.photo;
                card.dataset.unit = product.dimension || 'ед.';
                card.dataset.type = product.type || 'Без категории';

                card.innerHTML = `
                    <img src="${imageUrl}" alt="${productName}" class="product-image">

                    <div class="product-details">
                        <h3 class="product-title">${productName}</h3>
                        <p class="product-description">${product.description || ''}</p>
                        <p class="product-price">${product.price.toFixed(2)} руб.</p>
                        <p class="product-unit">ед.: ${product.dimensionName || 'шт.'}</p>

                        <div class="product-quantity">
                            <label for="quantity-${index}">Количество:</label>
                            <div class="quantity-controls">
                            <button class="quantity-btn-decrease" onclick="decreaseQuantity('quantity-${index}')">-</button>
                            <input id="quantity-${index}" type="text" class="quantity-input" name="quantity" pattern="[0-9]*" min="1" value="1">
                            <button class="quantity-btn-increase" onclick="increaseQuantity('quantity-${index}')">+</button>
                        </div>

                        <button class="add-to-cart-btn" ${isActive ? '' : 'disabled title="Товар снят с продажи"'}>Добавить в корзину</button>

                    </div>
                `;

                const button = card.querySelector('.add-to-cart-btn');
                if (isActive) {
                    button.addEventListener('click', () => addToCart(button));
                }
                button.addEventListener('click', () => addToCart(button));

                container.appendChild(card);
            });
        })
        .catch(error => {
            console.error('Ошибка загрузки продуктов:', error);
            container.innerHTML = '<p class="error">Не удалось загрузить список продуктов</p>';
        });
}

export function renderProductListByCategory(category) {
console.log('renderProductListByCategory вызвана с categoryId:', category, typeof category);
  const container = document.getElementById('product-list');
  if (!container) return;

  container.innerHTML = '<p>Загрузка...</p>';

  console.log('Отправляем запрос для category:', category);
  if (!category || category === 'null' || category === 'undefined') {
    console.error('category некорректен, запрос не выполняется');
    return; // не выполнять fetch с некорректным id
  }

  fetch(`/api/products/category/${category}`)
    .then(response => {
      if (!response.ok) throw new Error('Ошибка загрузки продуктов по категории');
      return response.json();
    })
    .then(products => {
      container.innerHTML = '';

      if (products.length === 0) {
        container.innerHTML = '<p>Нет продуктов в этой категории.</p>';
        return;
      }

      products.forEach((product, index) => {
        const card = document.createElement('div');
        card.className = 'product-card';

        const isActive = product.active ?? true;
        const baseName = product.product || 'Без названия';
        const productName = isActive ? baseName : `${baseName} (снят с продажи)`;


        const imageUrl = (product.photos && product.photos.length > 0)
          ? `${product.photos[0].photo}`
          : '/img/empty.png';

        card.dataset.id = product.id;
        card.dataset.name = productName;
        card.dataset.price = product.price;
        card.dataset.description = product.description || '';
        card.dataset.image = imageUrl;
        card.dataset.unit = product.dimension || 'ед.';
        card.dataset.type = product.type || 'Без категории';

        card.innerHTML = `
          <img src="${imageUrl}" alt="${productName}" class="product-image">
          <div class="product-details">
            <h3 class="product-title">${productName}</h3>
            <p class="product-description">${product.description || ''}</p>
            <p class="product-price">${product.price.toFixed(2)} руб.</p>
            <p class="product-unit">ед.: ${product.dimensionName || 'шт.'}</p>
            <div class="product-quantity">
              <label for="quantity-${index}">Количество:</label>
              <div class="quantity-controls">
                <button class="quantity-btn-decrease" onclick="decreaseQuantity('quantity-${index}')">-</button>
                <input id="quantity-${index}" type="text" class="quantity-input" name="quantity" pattern="[0-9]*" min="1" value="1">
                <button class="quantity-btn-increase" onclick="increaseQuantity('quantity-${index}')">+</button>
              </div>
              <button class="add-to-cart-btn" ${isActive ? '' : 'disabled title="Товар снят с продажи"'}>Добавить в корзину</button>

            </div>
          </div>
        `;

        const button = card.querySelector('.add-to-cart-btn');
        if (isActive) {
            button.addEventListener('click', () => addToCart(button));
        }


        container.appendChild(card);
      });
    })
    .catch(error => {
      console.error('Ошибка при загрузке продуктов по категории:', error);
      container.innerHTML = '<p class="error">Не удалось загрузить продукты по категории</p>';
    });
}
