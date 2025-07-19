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
    generatePDF,
    checkAndRenderOrders,
    renderOrderMessage,
    fetchAndRenderUserOrders } from './modules/order.js';

import { setupAuthForm,
         isUserLoggedIn,
         redirectIfLoggedIn,
         redirectIfNotLoggedIn,
         logoutUser,
         authFetch,
         initForgotPassword,
         showChangePasswordBlock } from './modules/auth.js';

import {handleRegistration,
        setupFormAutoSave,
        updateUserNameDisplay,
        handleLogin,
        handleProfileFormSubmit,
        checkRoleAccess } from './modules/user.js';

import { renderProductList,
         renderProductListByCategory } from './modules/shop.js';

import { saveFormToStorage,
         restoreFormFromStorage,
         saveOrderData,
         saveUserData,
         mappings } from './modules/storage.js';


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
window.saveOrderData = saveUserData;
window.updateUserNameDisplay = updateUserNameDisplay;


document.addEventListener('DOMContentLoaded', async () => {

    updateCartCount();
    renderCartPage();
    setupAuthForm();
    restoreFormFromStorage();
    updateDeliveryCost();
    saveOrderData();
    saveUserData();
    setupFormAutoSave();
    updateUserNameDisplay();
    initForgotPassword('.container-row2');



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

    // Кнопка входа/регистрации в ордер или заказ без регистр
    const loginButton = document.getElementById("go-to-login");
    if (loginButton) {
        loginButton.addEventListener("click", (event) => {
            event.preventDefault();
            window.location.href = "/register";
        });
    }
    // Проверка наличие пользователя в localStorage
    const userProfile = localStorage.getItem('userData');

    if (userProfile && userProfile !== 'undefined') {
      try {
        const userData = JSON.parse(userProfile);
        saveUserData(userData.user);
        updateUserNameDisplay();
      } catch (e) {
        console.error('Ошибка при разборе userData из localStorage:', e);
        localStorage.removeItem('userData'); // очистим некорректные данные
      }
    }

    // 1. Обработка входа
      const loginForm = document.getElementById('login-form');
      if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
      }


      // 2. Обработка смены данных (с текущим паролем)
      const changeDataForm = document.getElementById('change-password-form');
      if (changeDataForm) {
        changeDataForm.addEventListener('submit', handleProfileFormSubmit); // твоя функция смены данных
      }

      // 3. Обработка обязательной смены пароля
      const forceChangePasswordForm = document.getElementById('force-change-password-form');
      if (forceChangePasswordForm) {
        forceChangePasswordForm.addEventListener('submit', showChangePasswordBlock);
      }

    // Выход
    const logout = document.getElementById("logout");
    if (logout) {
      logout.addEventListener("click", () => {
        console.log('Выход выполнен');
        logoutUser();
        updateUserNameDisplay();
      });
    }


    // Изменить пароль
    const form = document.getElementById('change-password-form');
      if (form) {
        form.addEventListener('submit', handleProfileFormSubmit);
      }

    // Кнопка "Сделать заказ"
    const checkoutButton = document.getElementById("checkout-button");
    if (checkoutButton) {
        checkoutButton.addEventListener("click", () => {
            if (localStorage.getItem('loggedIn') === 'true') {
                // Пользователь авторизован — идём на финальную страницу заказа
                window.location.href = "/finalOrder";
            } else {
                // Гость — на страницу оформления заказа
                window.location.href = "/order";
            }
        });
    }



    // Страница оформления заказа
    if (document.getElementById("order-form")) {
        setupOrderForm();

    }


    // Регистрация нового пользователя
    const registerForm = document.getElementById('register-form');
      if (registerForm) {
        registerForm.addEventListener('submit', handleRegistration);
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

    const params = new URLSearchParams(window.location.search);
    const categoryId = params.get("id");
    console.log("categoryId из URL:", categoryId, typeof categoryId);

    if (categoryId && categoryId !== 'null' && categoryId !== 'undefined') {
      renderProductListByCategory(categoryId);
    } else {
      renderProductList();
    }

});


// ЛК
document.addEventListener('DOMContentLoaded', () => {
  const ordersContainer = document.getElementById('orders-container');
  if (!ordersContainer) {
    return;
  }

  let orderData = null;
  const orderDataRaw = localStorage.getItem('orderData');
  if (orderDataRaw) {
    try {
      orderData = JSON.parse(orderDataRaw);
    } catch (e) {
      console.error('Ошибка парсинга orderData:', e);
      //localStorage.removeItem('orderData'); // на всякий случай
    }
  }

  let userData = null;
  const userDataRaw = localStorage.getItem('userData');
  if (userDataRaw) {
    try {
      userData = JSON.parse(userDataRaw);
    } catch (e) {
      console.error('Ошибка парсинга userData:', e);
      //localStorage.removeItem('userData');
    }
  }

  renderOrderMessage(ordersContainer, orderData, userData);
});

document.addEventListener('DOMContentLoaded', () => {
  const accountLink = document.getElementById('account-link');

  if (accountLink) {
    accountLink.addEventListener('click', async (event) => {
      event.preventDefault();

      const userDataRaw = localStorage.getItem('userData');
      const profile = userDataRaw ? JSON.parse(userDataRaw) : null;

      if (!profile || !profile.id) {
        window.location.href = '/register';
        return;
      }

      await redirectByRole(profile.roles);
    });
  }
});

// Заказы юзера в ЛК
document.addEventListener('DOMContentLoaded', () => {
  const ordersContainer = document.getElementById('orders-container');
  if (!ordersContainer) {
    return;
  }

  const userDataRaw = localStorage.getItem('userData');
  if (!userDataRaw) {
    console.error('Данные пользователя не найдены в localStorage');
    renderEmptyOrdersMessage(ordersContainer);
    return;
  }

  let userData;
  try {
    userData = JSON.parse(userDataRaw);
  } catch (e) {
    console.error('Ошибка парсинга userData:', e);
    renderEmptyOrdersMessage(ordersContainer);
    return;
  }

  // Получаем вложенный объект user
  const user = userData.user || userData;

  if (!user.id) {
    console.error('ID пользователя отсутствует');
    renderEmptyOrdersMessage(ordersContainer);
    return;
  }
  fetchAndRenderUserOrders(ordersContainer, user.id);
});


// Смена даннных пользователя или пароля
document.addEventListener('DOMContentLoaded', () => {
  const userDataRaw = localStorage.getItem('userData');
  if (!userDataRaw || userDataRaw === 'undefined') return;

  let userData;
  try {
    userData = JSON.parse(userDataRaw);
  } catch {
    console.error('Ошибка при разборе userData');
    localStorage.removeItem('userData');
    return;
  }

  // 1. Проверяем временный пароль
  const isTemporaryPassword = localStorage.getItem('temporaryPassword') === 'true';
  if (isTemporaryPassword || userData?.user?.temporaryPassword) {
    // Принудительная смена пароля
    showChangePasswordBlock();
    return; // остановим выполнение, чтобы не пошёл дальше update
  }

  // 2. Если не временный пароль — продолжаем обычную логику
  saveUserData(userData);
});

document.addEventListener("DOMContentLoaded", () => {
    if (document.getElementById("recipient")) {
        populateFinalOrder();
        setupConfirmButton();
    }
});

document.addEventListener('DOMContentLoaded', () => {
  // Обработчик Telegram
  document.querySelectorAll('.telegram-link').forEach(link => {
    link.addEventListener('click', event => {
      event.preventDefault();
      const user = link.dataset.telegram;
      if (user) {
        window.open(`https://t.me/${user}`, '_blank');
      }
    });
  });

  // Обработчик WhatsApp
  document.querySelectorAll('.whatsapp-link').forEach(link => {
    link.addEventListener('click', event => {
      event.preventDefault();
      const phone = link.dataset.whatsapp;
      if (phone) {
        window.open(`https://wa.me/${phone}`, '_blank');
      }
    });
  });
});


