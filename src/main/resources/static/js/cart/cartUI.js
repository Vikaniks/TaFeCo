import {
    cart,
    increaseCartItem,
    decreaseCartItem,
    removeFromCart,
    updateCartCount
} from './cartService.js';

export function renderCartPage() {
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
            <td class="cart-img">
                <img src="${product.image}" alt="${productName}" width="60" height="60">
            </td>
            <td class="cart-name">${productName}</td>
            <td class="cart-quantity">
                <button class="button-decrease" data-name="${productName}">-</button>
                <span>${product.quantity} ${product.unit}</span>
                <button class="button-increase" data-name="${productName}">+</button>
            </td>
            <td>${product.price.toLocaleString('ru-RU', { minimumFractionDigits: 2 })} руб.</td>
            <td>${itemCost.toLocaleString('ru-RU', { minimumFractionDigits: 2 })} руб.</td>
            <td>
                <button class="button-remove" data-name="${productName}">Удалить</button>
            </td>
        `;

        cartItemsContainer.appendChild(row);
    });

    totalItemsDisplay.textContent = `Итого: ${totalSum.toLocaleString('ru-RU', { minimumFractionDigits: 2 })} руб.`;
    updateCartCount();
    attachCartEventHandlers(); // вешаем обработчики
}

function attachCartEventHandlers() {
    document.querySelectorAll('.button-increase').forEach(btn =>
        btn.addEventListener('click', () => {
            increaseCartItem(btn.dataset.name);
            renderCartPage();
        })
    );

    document.querySelectorAll('.button-decrease').forEach(btn =>
        btn.addEventListener('click', () => {
            decreaseCartItem(btn.dataset.name);
            renderCartPage();
        })
    );

    document.querySelectorAll('.button-remove').forEach(btn =>
        btn.addEventListener('click', () => {
            removeFromCart(btn.dataset.name);
            renderCartPage();
        })
    );
    console.log('cartUI.js подключен');
}
