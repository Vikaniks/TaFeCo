/// cart.js

 // Инициализация корзины из localStorage (если пользователь не залогинен — корзина пустая)
 export let cart = JSON.parse(localStorage.getItem('cart-items')) || {};

 // Обновление отображения количества товаров в корзине (например, в иконке)
 export function updateCartCount() {
     const cartCountDisplay = document.getElementById('cart-count');
     if (!cartCountDisplay) return;

     const totalItems = Object.values(cart).reduce((sum, product) => sum + product.quantity, 0);
     cartCountDisplay.textContent = totalItems >= 0 && totalItems <= 9 ? `\u200B${totalItems}` : totalItems;
 }

 // Увеличить количество в поле ввода количества товара
 export function increaseQuantity(id) {
     const quantityInput = document.getElementById(id);
     if (quantityInput) {
         quantityInput.value = parseInt(quantityInput.value) + 1;
     }
 }

 // Уменьшить количество в поле ввода (минимум 1)
 export function decreaseQuantity(id) {
     const quantityInput = document.getElementById(id);
     if (quantityInput && quantityInput.value > 1) {
         quantityInput.value = parseInt(quantityInput.value) - 1;
     }
 }

 // Добавить товар в корзину
 export function addToCart(buttonElement) {
     const productCard = buttonElement.closest('.product-card');
     if (!productCard) return;

     const productId = parseInt(productCard.dataset.id, 10);
         if (!productId) {
             console.error('Ошибка: productId отсутствует или некорректен');
             return;
         }
     const productName = productCard.dataset.name;
     /*const productName = productCard.querySelector('.product-title')?.textContent.trim();*/
     const quantityInput = productCard.querySelector('.quantity-input');
     const quantity = parseInt(quantityInput?.value || '1');
     const image = productCard.querySelector('img')?.src;

     const priceText = productCard.querySelector('.product-price')?.textContent.replace('руб.', '').trim();
     const price = parseFloat(priceText.replace(',', '.'));

     const unitElement = productCard.querySelector('.product-unit');
     const unit = unitElement ? unitElement.textContent.replace('ед.:', '').trim() : 'шт.';

     const category = productCard.dataset.type || 'Без категории';

     if (!productName || isNaN(quantity) || quantity < 1 || isNaN(price)) return;

     // Загружаем текущую корзину из localStorage (на всякий случай)
     const currentCart = JSON.parse(localStorage.getItem('cart-items') || '{}');

     if (currentCart[productId]) {
             currentCart[productId].quantity += quantity;
         } else {

         const isActive = productCard.dataset.active === 'true';
             currentCart[productId] = {
                 id: productId,
                 productName,
                 quantity,
                 price,
                 image,
                 unit,
                 category,
                 active: isActive
             };
         }

     // Сохраняем в localStorage и обновляем отображение
     localStorage.setItem('cart-items', JSON.stringify(currentCart));
     cart = currentCart;

     updateCartCount();

 }

 // Рендер страницы корзины
 export function renderCartPage() {
     const cartItemsContainer = document.getElementById('cart-items');
     const totalItemsDisplay = document.getElementById('total-items');
     if (!cartItemsContainer || !totalItemsDisplay) return;

     cartItemsContainer.innerHTML = '';
     let totalSum = 0;

     Object.entries(cart).forEach(([productId, product]) => {
         const row = document.createElement('tr');

         // Картинка
         const imgCell = document.createElement('td');
         imgCell.className = 'cart-img';
         const img = document.createElement('img');
         img.src = product.image;
         img.alt = productId;
         img.width = 50;
         img.height = 50;
         imgCell.appendChild(img);

         // Название
         const nameCell = document.createElement('td');
         nameCell.className = 'cart-name';
         nameCell.textContent = product.active === false
             ? `${product.productName} (снят с продажи)`
             : product.productName;


         // Кол-во и кнопки +
         const quantityCell = document.createElement('td');
         quantityCell.className = 'cart-quantity';
         quantityCell.innerHTML = `
             <button class="button-decrease" onclick="decreaseCartItem('${productId}')">-</button>
             <span>${product.quantity} ${product.unit}</span>
             <button class="button-increase" onclick="increaseCartItem('${productId}')">+</button>
         `;

         // Цена за штуку
         const priceCell = document.createElement('td');
         priceCell.textContent = `${product.price.toLocaleString('ru-RU', { minimumFractionDigits: 2 })} руб.`;

         // Общая стоимость позиции
         const itemCost = product.quantity * product.price;
         totalSum += itemCost;
         const costCell = document.createElement('td');
         costCell.textContent = `${itemCost.toLocaleString('ru-RU', { minimumFractionDigits: 2 })} руб.`;

         // Кнопка удаления
         const removeCell = document.createElement('td');
         const removeButton = document.createElement('button');
         removeButton.className = 'button-remove';
         removeButton.textContent = 'Удалить';
         removeButton.addEventListener('click', () => removeFromCart(productId));
         removeCell.appendChild(removeButton);

         // Собираем строку таблицы
         row.appendChild(imgCell);
         row.appendChild(nameCell);
         row.appendChild(quantityCell);
         row.appendChild(priceCell);
         row.appendChild(costCell);
         row.appendChild(removeCell);

         cartItemsContainer.appendChild(row);
     });

     totalItemsDisplay.textContent = `сумма: ${totalSum.toLocaleString('ru-RU', { minimumFractionDigits: 2 })} руб.`;
     localStorage.setItem('totalSum', totalSum.toFixed(2));

 }

 // Увеличить количество товара в корзине
 export function increaseCartItem(productId) {
     if (cart[productId]) {
         cart[productId].quantity++;
         localStorage.setItem('cart-items', JSON.stringify(cart));
         renderCartPage();
         updateCartCount();
     }
 }

 // Уменьшить количество товара в корзине
 export function decreaseCartItem(productId) {
     if (cart[productId] && cart[productId].quantity > 1) {
         cart[productId].quantity--;
         localStorage.setItem('cart-items', JSON.stringify(cart));
         renderCartPage();
         updateCartCount();
     }
 }

 // Удалить товар из корзины
 export function removeFromCart(productId) {
     delete cart[productId];
     localStorage.setItem('cart-items', JSON.stringify(cart));
     renderCartPage();
     updateCartCount();
 }

