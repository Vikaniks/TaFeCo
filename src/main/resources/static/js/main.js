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
    setupConfirmButton,
    updateDeliveryCost,
    createAndRenderOrder,
    generatePDF
} from './modules/order.js';

import { setupAuthForm } from './modules/auth.js';
import { setupUserMenu, updateUserNameDisplay } from './modules/user.js';
import { renderProductList, renderProductListByCategory } from './modules/shop.js';
import { saveFormToStorage, restoreFormFromStorage, saveOrderData, saveUserData } from './modules/storage.js';



// Глобальные функции
window.increaseCartItem = increaseCartItem;
window.decreaseCartItem = decreaseCartItem;
window.removeFromCart = removeFromCart;
window.increaseQuantity = increaseQuantity;
window.decreaseQuantity = decreaseQuantity;
window.addToCart = addToCart;
window.updateCartCount = updateCartCount;
window.renderCartPage = renderCartPage;
window.saveOrderData = saveOrderData;



document.addEventListener('DOMContentLoaded', async () => {

console.log("main.js загружен");

    updateCartCount();
    renderCartPage();
    setupUserMenu();
    setupAuthForm();
    updateUserNameDisplay();
    restoreFormFromStorage();
    updateDeliveryCost();
    saveOrderData();
    saveUserData();



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

    const downloadBtn = document.getElementById("download-pdf");
        if (!downloadBtn) return;

        downloadBtn.addEventListener("click", async (event) => {
            event.preventDefault();
            await generatePDF();
        });


    if (window.location.pathname === '/cart') {
            renderCartPage();
    }


    // Страница подтверждения заказа, коммент
      const comment = sessionStorage.getItem("orderComment");
      const commentInput = document.getElementById("comment");

      if (comment && comment.trim() !== "") {
        commentInput.textContent = comment;
      }

});
window.addEventListener('load', async () => {

if (window.location.pathname !== '/confirmar_order') return;
  const savedOrderRaw = localStorage.getItem('createdOrder');

  if (savedOrderRaw) {
    try {
      const createdOrder = JSON.parse(savedOrderRaw);

      const orderDataRaw = localStorage.getItem('orderData');
      let items = [];

      if (orderDataRaw) {
        const orderData = JSON.parse(orderDataRaw);
        items = Array.isArray(orderData.items)
          ? orderData.items
          : Object.values(orderData.items || {});
      }

      if (createdOrder?.id) {
        renderFinalOrderPage(createdOrder, items);
        return;
      }
    } catch (e) {
      console.warn('Ошибка в сохранённых данных заказа:', e);
    }
  }

  await createAndRenderOrder();
});
document.querySelectorAll('.category-link').forEach(link => {
  link.addEventListener('click', (e) => {
    e.preventDefault();
    const categoryId = link.dataset.categoryId;
    renderProductListByCategory(categoryId);
  });
});

document.addEventListener('DOMContentLoaded', async () => {
    console.log("DOMContentLoaded fired");

    const params = new URLSearchParams(window.location.search);
    const categoryId = params.get("id");
    console.log("categoryId из URL:", categoryId, typeof categoryId);

    if (categoryId && categoryId !== 'null' && categoryId !== 'undefined') {
      console.log("Выполняем renderProductListByCategory");
      renderProductListByCategory(categoryId);
    } else {
      console.log("Выполняем renderProductList");
      renderProductList();
    }

});

