import {
    updateCartCount,
    renderCartPage,
    increaseQuantity,
    decreaseQuantity,
    addToCart,
    increaseCartItem,
    decreaseCartItem,
    removeFromCart
} from './modules/cart.js';

import {
    setupOrderForm,
    populateFinalOrder,
    getOrderData,
    setupConfirmButton,
    downloadPDF,
} from './modules/order.js';

import { setupAuthForm } from './modules/auth.js';
import { setupUserMenu, updateUserNameDisplay } from './modules/user.js';
import { renderProductList } from './modules/shop.js';
import { saveFormToStorage, restoreFormFromStorage } from './modules/storage.js';



// Глобальные функции
window.increaseCartItem = increaseCartItem;
window.decreaseCartItem = decreaseCartItem;
window.removeFromCart = removeFromCart;
window.increaseQuantity = increaseQuantity;
window.decreaseQuantity = decreaseQuantity;
window.addToCart = addToCart;
window.updateCartCount = updateCartCount;
window.renderCartPage = renderCartPage;



document.addEventListener('DOMContentLoaded', async () => {
console.log('DOM загружен');
    updateCartCount();
    renderCartPage();
    setupUserMenu();
    setupAuthForm();
    updateUserNameDisplay();
    downloadPDF();
    renderProductList();
    restoreFormFromStorage();



    // Показываем имя пользователя
    const loggedIn = localStorage.getItem('loggedIn');
    const userName = document.getElementById('user-name');
    if (userName) {
        if (loggedIn === 'true') {
            const userProfile = JSON.parse(localStorage.getItem('userProfile'));
            if (userProfile) {
                userName.textContent = `${userProfile.name} ${userProfile.surname}`;
            }
        } else {
            userName.textContent = 'Гость';
        }
    }

    // Кнопка назад в корзину
    const backButton = document.getElementById("back-to-cart");
    if (backButton) {
        backButton.addEventListener("click", () => history.back());
    }

    // Кнопка входа/регистрации
    const loginButton = document.getElementById("go-to-login");
    if (loginButton) {
        loginButton.addEventListener("click", (event) => {
            event.preventDefault();
            window.location.href = "/register";
        });
    }

    // Выход
    const logout = document.getElementById("logout");
    if (logout) {
        logout.addEventListener("click", () => {
            console.log('Выход выполнен');
        });
    }

    // Кнопка "Сделать заказ"
    const checkoutButton = document.getElementById("checkout-button");
    if (checkoutButton) {
        checkoutButton.addEventListener("click", () => {
            const loggedIn = localStorage.getItem("loggedIn");

            if (loggedIn === "true") {
                window.location.href = "/finalOrder"; // уже авторизован
            } else {
                window.location.href = "/order"; // гость
            }
        });
    }


    // Страница оформления заказа
    if (document.getElementById("order-form")) {
        setupOrderForm();

    }


    /*const form = document.getElementById('order-form');
      form.addEventListener('input', saveFormToStorage);

      */

    // Страница finalOrder.html
    if (document.getElementById("recipient")) {
        populateFinalOrder();
        setupConfirmButton();
    }

    if (document.getElementById("auth-form")) {
        setupAuthForm();
    }

    if (document.getElementById("user-menu")) {
        setupUserMenu();
        updateUserNameDisplay();
    }

    if (document.getElementById("user-name")) {
        updateUserNameDisplay();
    }

    if (document.getElementById("download-pdf")) {
        downloadPDF();
    }

    if (document.getElementById("cart-items")) {
        renderCartPage();
    }

    const goHomeLink = document.getElementById("go-home-link");
    if (goHomeLink) {
        goHomeLink.addEventListener("click", () => {
            localStorage.removeItem("orderData");
            localStorage.removeItem("cart-items"); // используем правильный ключ

            // Обнуляем визуальный счётчик товаров в корзине, если он есть
            const cartCountEl = document.getElementById("cart-count");
            if (cartCountEl) {
                cartCountEl.textContent = "0";
            }
        });
    }
    if (window.location.pathname === '/cart') {
            renderCartPage();
    }



});


