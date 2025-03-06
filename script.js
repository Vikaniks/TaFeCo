document.addEventListener('DOMContentLoaded', function () {
    updateCartCount();
    if (document.getElementById('cart-items')) {
        renderCartPage();
    }
});

// Получаем данные из localStorage
const cart = JSON.parse(localStorage.getItem('cart-items')) || {};

// **Обновление счетчика товаров на иконке корзины**
function updateCartCount() {
    const cartCountDisplay = document.getElementById('cart-count');
    if (!cartCountDisplay) return;

    let totalItems = Object.values(cart).reduce((sum, product) => sum + product.quantity, 0);
    cartCountDisplay.textContent = totalItems >= 0 ? totalItems : '0';
}

// **Функции изменения количества в карточке товара**
function increaseQuantity(id) {
    const quantityInput = document.getElementById(id);
    if (quantityInput) quantityInput.value = parseInt(quantityInput.value) + 1;
}

function decreaseQuantity(id) {
    const quantityInput = document.getElementById(id);
    if (quantityInput && quantityInput.value > 1) {
        quantityInput.value = parseInt(quantityInput.value) - 1;
    }
}

// **Добавление товара в корзину**
function addToCart(productId) {
    const productCard = document.getElementById(productId);
    if (!productCard) return;

    const productName = productCard.querySelector('.product-title').textContent.trim();
    const quantityInput = productCard.querySelector('.quantity-input') || productCard.querySelector('input[name="quantity"]');
    const quantity = parseInt(quantityInput.value);
    const image = productCard.querySelector('.product-image').src;
    const unit = productCard.querySelector('.product-unit')?.textContent.trim() || '0';
    const priceText = productCard.querySelector('.product-price')?.textContent.trim();
    const price = parseFloat(priceText ? priceText.replace(/[^\d.]/g, '') : 0);

    if (isNaN(quantity) || quantity < 1) return;

    if (cart[productName]) {
        cart[productName].quantity += quantity;
    } else {
        cart[productName] = { quantity, image, unit, price };
    }

    localStorage.setItem('cart-items', JSON.stringify(cart));
    updateCartCount();
}

// **Отображение корзины на `cart.html`**
function renderCartPage() {
    const cartItemsContainer = document.getElementById('cart-items');
    const totalItemsDisplay = document.getElementById('total-items');

    if (!cartItemsContainer || !totalItemsDisplay) return;

    cartItemsContainer.innerHTML = '';
    let totalSum = 0;

    Object.entries(cart).forEach(([productName, product]) => {
        const row = document.createElement('tr');
        const itemCost = product.quantity * product.price;
        totalSum += itemCost;

        row.innerHTML = `
            <td><img src="${product.image}" alt="${productName}" width="50" height="50"></td>
            <td>${productName}</td>
            
            <td>${product.quantity} ${product.unit}</td>
            <td>${product.price.toFixed(2)} руб.</td>
            <td><button onclick="removeFromCart('${productName}')">Удалить</button></td>
            <td>${itemCost.toFixed(2)} руб.</td>
        `;

        cartItemsContainer.appendChild(row);
    });

    totalItemsDisplay.innerHTML = ` ${totalSum.toFixed(2)} руб.`;
}

// **Удаление товара из корзины**
function removeFromCart(productName) {
    delete cart[productName];
    localStorage.setItem('cart-items', JSON.stringify(cart));
    updateCartCount();
    renderCartPage();
}
