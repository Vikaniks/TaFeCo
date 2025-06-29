// order.js
import { saveFormToStorage, restoreFormFromStorage, saveUserData } from './storage.js';


export function setupOrderForm() {
    const form = document.getElementById('order-form');
    if (!form) return;

    const nameInput = document.getElementById('name');
    const surnameInput = document.getElementById('surname');
    const phoneInput = document.getElementById('phone');
    const emailInput = document.getElementById('email');


    // Форматирование телефона
    function formatPhone(value) {
        let digits = value.replace(/\D/g, '');
        if (digits.startsWith("8")) {
            digits = "7" + digits.slice(1);
        } else if (!digits.startsWith("7")) {
            digits = "7" + digits;
        }
        digits = digits.slice(0, 11);

        let formatted = "+7 ";
        if (digits.length > 1) formatted += `(${digits.slice(1, 4)}) `;
        if (digits.length > 4) formatted += `${digits.slice(4, 7)} `;
        if (digits.length > 7) formatted += `${digits.slice(7, 9)} `;
        if (digits.length > 9) formatted += digits.slice(9, 11);

        return formatted.trim();
    }

    phoneInput.addEventListener("input", () => {
        const cursorPosition = phoneInput.selectionStart;
        const oldValue = phoneInput.value;

        phoneInput.value = formatPhone(oldValue);

        const newLength = phoneInput.value.length;
        const oldLength = oldValue.length;
        const positionDiff = newLength - oldLength;

        let newCursorPosition = cursorPosition + positionDiff;
        if (newCursorPosition < 0) newCursorPosition = 0;
        if (newCursorPosition > newLength) newCursorPosition = newLength;

        phoneInput.setSelectionRange(newCursorPosition, newCursorPosition);
    });

    phoneInput.addEventListener("keydown", (event) => {
        const fixedPositions = [0, 1, 2, 3, 4];
        if (fixedPositions.includes(phoneInput.selectionStart) && (event.key === "Backspace" || event.key === "Delete")) {
            event.preventDefault();
        }
    });

    // Навешиваем после всех input-элементов
    form.addEventListener("input", () => {
        const userData = getUserData();
        localStorage.setItem("userData", JSON.stringify(userData));
    });



    function showErrorMessage(field, message) {
        const errorSpan = field.nextElementSibling;
        if (!errorSpan || !errorSpan.classList.contains("error-message")) return;

        errorSpan.textContent = message;
        errorSpan.style.display = "block";
        field.classList.add("error");
    }

    function clearErrorMessage(field) {
        const errorSpan = field.nextElementSibling;
        if (!errorSpan || !errorSpan.classList.contains("error-message")) return;

        errorSpan.textContent = "";
        errorSpan.style.display = "none";
        field.classList.remove("error");
    }

    function validateField(field) {
        let isValid = true;

        if (field.value.trim() === "") {
            showErrorMessage(field, "Заполните правильно");
            isValid = false;
        } else {
            clearErrorMessage(field);
        }

        if (field === nameInput || field === surnameInput) {
            const namePattern = /^[А-Яа-яЁёA-Za-z]+$/;
            if (!namePattern.test(field.value.trim())) {
                showErrorMessage(field, "Допустимы только буквы");
                isValid = false;
            }
        }

        if (field === phoneInput) {
            const phonePattern = /^\+7 \(\d{3}\) \d{3} \d{2} \d{2}$/;
            if (!phonePattern.test(field.value.trim())) {
                showErrorMessage(field, "Введите корректный номер");
                isValid = false;
            }
        }

        if (field === emailInput) {
            const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailPattern.test(field.value.trim())) {
                showErrorMessage(field, "Введите корректный email");
                isValid = false;
            }
        }

        return isValid;
    }

    function getUserData() {
        return {
            name: document.getElementById("name")?.value.trim() || "",
            surname: document.getElementById("surname")?.value.trim() || "",
            phone: document.getElementById("phone")?.value.trim() || "",
            email: document.getElementById("email")?.value.trim() || "",
            locality: document.getElementById("locality")?.value.trim() || "",  // Город
            district: document.getElementById("district")?.value.trim() || "",  // Район
            region: document.getElementById("region")?.value.trim() || "",      // Регион
            street: document.getElementById("street")?.value.trim() || "",      // Улица
            house: document.getElementById("house")?.value.trim() || "",        // Дом
            apartment: document.getElementById("apartment")?.value.trim() || "",// Квартира
            addressExtra: document.getElementById("address-extra")?.value.trim() || "", // Доп. информация
            date: document.getElementById("date")?.value.trim() || "",
            time: document.getElementById("time")?.value || "",
            paymentOption: document.getElementById("payment-option")?.value || "cash",
        };
    }


    // Заполняем поля, если данные есть в localStorage
    function populateForm() {
        const userData = localStorage.getItem("userData");
        if (!userData) return;

        const tempData = JSON.parse(userData);

        if (tempData.name) document.getElementById("name").value = tempData.name;
        if (tempData.surname) document.getElementById("surname").value = tempData.surname;
        if (tempData.phone) document.getElementById("phone").value = tempData.phone;
        if (tempData.email) document.getElementById("email").value = tempData.email;
        if (tempData.locality) document.getElementById("locality").value = tempData.locality;
        if (tempData.district) document.getElementById("district").value = tempData.district;
        if (tempData.region) document.getElementById("region").value = tempData.region;
        if (tempData.street) document.getElementById("street").value = tempData.street;
        if (tempData.house) document.getElementById("house").value = tempData.house;
        if (tempData.apartment) document.getElementById("apartment").value = tempData.apartment;
        if (tempData.addressExtra) document.getElementById("address-extra").value = tempData.addressExtra;
        if (tempData.date) document.getElementById("date").value = tempData.date;
        if (tempData.time) document.getElementById("time").value = tempData.time;
        if (tempData.paymentOption && document.getElementById("payment-option")) {
            document.getElementById("payment-option").value = tempData.paymentOption;
        }
    }

    // Вешаем события валидации и очистки ошибок
    [nameInput, surnameInput, phoneInput, emailInput].forEach(field => {
        field.addEventListener("blur", () => validateField(field));
        field.addEventListener("input", () => clearErrorMessage(field));
    });

    // Обработка сабмита формы
    form.addEventListener("submit", (event) => {
        event.preventDefault();

        let isValid = true;
        [nameInput, surnameInput, phoneInput, emailInput].forEach(field => {
            if (!validateField(field)) isValid = false;
        });

        if (!isValid) {
            alert("Исправьте данные перед отправкой!");
            return;
        }
        const commentInput = document.getElementById("comment");
        if (commentInput) {
            sessionStorage.setItem("orderComment", commentInput.value.trim());
        }


        const userData = getUserData();
        localStorage.setItem("userData", JSON.stringify(userData));

        window.location.href = "/finalOrder";
    });

    populateForm();

}
// final order.js

export function populateFinalOrder() {
    const dateInput = document.getElementById("date");
    const timeSelect = document.getElementById("time");
    const deliveryDateTime = document.getElementById("delivery-datetime");
    const recipientEl = document.getElementById("recipient");
    const paymentSelect = document.getElementById("payment-option");
    const paymentDisplay = document.getElementById("payment-option-text");


    const userData = JSON.parse(localStorage.getItem("userData")) || {};

    if (!recipientEl) return; // если нет получателя, выходим (не на нужной странице)

    // Отображаем основные данные
    recipientEl.textContent = `${userData.name || "Не указано"} ${userData.surname || "Не указано"}`;
    document.getElementById("phone").textContent = userData.phone || "Не указано";
    // Адрес
        const addressEl = document.getElementById("address");
        if (addressEl) {
            const parts = [
                userData.region,
                userData.locality,
                userData.district,
                userData.street,
                userData.house,
                userData.apartment,
                userData.addressExtra
            ].filter(Boolean); // убираем пустые значения

            addressEl.textContent = parts.join(", ") || "Не указано";
        }

    if (dateInput && userData.date) {
        dateInput.value = userData.date;
    }
    if (timeSelect && userData.time) {
        timeSelect.value = userData.time;
    }

    function formatDate(dateString) {
        if (!dateString) return "Не указано";
        const [year, month, day] = dateString.split("-");
        return `${day}-${month}-${year}`;
    }

    function updateDeliveryDateTime() {
        if (!dateInput || !timeSelect || !deliveryDateTime) return;

        const date = dateInput.value;
        const time = timeSelect.value;

        if (date && time) {
            const formattedDate = formatDate(date);
            deliveryDateTime.innerHTML = `${formattedDate} &nbsp;|&nbsp; ${time}`;

            // Обновляем localStorage
            userData.date = date;
            userData.time = time;
            localStorage.setItem("userData", JSON.stringify(userData));
        } else {
            deliveryDateTime.textContent = "Выберите дату и время";
        }
    }

    // Отображаем дату и время из userData, если есть
    if (userData.date && userData.time && deliveryDateTime) {
        deliveryDateTime.innerHTML = `${formatDate(userData.date)} &nbsp;|&nbsp; ${userData.time}`;
    } else if (deliveryDateTime) {
        deliveryDateTime.textContent = "Не указано";
    }

    // Добавляем обработчики, если элементы есть
    if (dateInput && timeSelect) {
        dateInput.addEventListener("change", updateDeliveryDateTime);
        timeSelect.addEventListener("change", updateDeliveryDateTime);
    }

    // Отображаем метод оплаты
    if (paymentDisplay) {
        const paymentText = {
            "cash": "Оплата наличными при доставке",
            "card": "Банковская карта"
        }[userData.paymentOption] || "Не указано";

        paymentDisplay.textContent = paymentText;
    }
    // Отображаем комментарий к заказу из sessionStorage
       const comment = sessionStorage.getItem("orderComment");
       const commentDisplay = document.getElementById("comment");

           if (comment && commentDisplay) {
           commentDisplay.textContent = comment;
       }

       sessionStorage.removeItem("orderComment");

}


export function setupConfirmButton() {
    const confirmBtn = document.getElementById("confirm-order");
    const paymentSelect = document.getElementById("payment-option");
    const paymentDisplay = document.getElementById("payment-option-text");


    if (confirmBtn) {
        confirmBtn.addEventListener("click", () => {
            if (paymentSelect && paymentSelect.value === "") {
                alert("Пожалуйста, выберите способ оплаты.");
                return; // остановить выполнение
            }

            //  Обновляем paymentOption в localStorage
            const userData = JSON.parse(localStorage.getItem("userData")) || {};
            if (paymentSelect) {
                userData.paymentOption = paymentSelect.value;
                localStorage.setItem("userData", JSON.stringify(userData));
                 // сразу обновить отображение
                        if (paymentDisplay) {
                                    const paymentText = {
                                        "cash": "Оплата наличными при доставке",
                                        "card": "Банковская карта",
                                        "online": "Онлайн-оплата"
                                    };
                                    paymentDisplay.textContent = paymentText[userData.paymentOption] || "Не указано";

                        }

                 }
            const cart = JSON.parse(localStorage.getItem('cart-items')) || {};
            const cartEmpty = Object.keys(cart).length === 0;
            if (cartEmpty) {
                alert("Ваша корзина пуста. Добавьте товары перед подтверждением заказа.");
                return;
            }

            // Переход на страницу подтверждения
            window.location.href = "/confirmar_order";
        });
    }
}



let pdfListenerInitialized = false;

/*export function downloadPDF() {
    const downloadBtn = document.getElementById("download-pdf");
    if (!downloadBtn || pdfListenerInitialized) return;

    pdfListenerInitialized = true;

    // Чтение корзины
    function getCartFromStorage() {
        const storedCart = localStorage.getItem('orderData'); // или 'cartItems' — см. как ты назвал
        return storedCart ? JSON.parse(storedCart) : [];
    }

    // Чтение пользовательских данных
    function getUserDataFromStorage() {
        const storedUserData = localStorage.getItem('userData');
        return storedUserData ? JSON.parse(storedUserData) : {};
    }

    // Рендер корзины
    function renderCartToPDFTable(cart) {
        const cartItemsContainer = document.getElementById('cart-items');
        const totalItemsDisplay = document.getElementById('total-items');
        if (!cartItemsContainer || !totalItemsDisplay) return;

        cartItemsContainer.innerHTML = '';
        let totalSum = 0;

        cart.forEach((item) => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td><img src="${item.image}" width="50" height="50"></td>
                <td>${item.productName}</td>
                <td>${item.quantity} ${item.unit || ''}</td>
                <td>${item.price.toFixed(2)} руб.</td>
                <td>${(item.price * item.quantity).toFixed(2)} руб.</td>
                <td></td>
            `;
            cartItemsContainer.appendChild(row);
            totalSum += item.quantity * item.price;
        });

        totalItemsDisplay.textContent = `Сумма: ${totalSum.toFixed(2)} руб.`;
    }

    // Рендер пользовательских данных
    function renderUserData(userData) {
        const nameField = document.getElementById('recipient');
        const phoneField = document.getElementById('phone');
        const addressField = document.getElementById('address');
        const deliveryField = document.getElementById('delivery-datetime');
        const paymentField = document.getElementById('payment-option-text');
        const commentField = document.getElementById('comment-display');

        if (nameField) nameField.textContent = userData.username || '';
        if (phoneField) phoneField.textContent = userData.phone || '';
        if (addressField) addressField.textContent = userData.address || '';
        if (deliveryField) deliveryField.textContent = userData.deliveryDateTime || '';
        if (paymentField) paymentField.textContent = userData.paymentOption || '';
        if (commentField) commentField.textContent = userData.comment || '';
    }

    // Ожидание загрузки изображений
    function waitForImagesToLoad(element) {
        const images = element.querySelectorAll("img");
        const promises = Array.from(images).map(img => {
            if (img.complete) return Promise.resolve();
            return new Promise(resolve => {
                img.onload = img.onerror = resolve;
            });
        });
        return Promise.all(promises);
    }

    // Нажатие на кнопку
    downloadBtn.addEventListener("click", (event) => {
        event.preventDefault();
        const cart = getCartFromStorage();
        const userData = getUserDataFromStorage();

        renderCartToPDFTable(cart);
        renderUserData(userData);

        const original = document.getElementById("tablas");
        if (!original) return;

        const clone = original.cloneNode(true);
        clone.id = "tablas-clone";
        clone.style.position = 'relative';
        clone.style.visibility = 'visible';
        clone.style.top = '0';
        clone.style.left = '0';
        clone.style.width = '185mm';
        clone.style.display = 'block';
        clone.style.background = 'transparent';

        const table = clone.querySelector('.custom-table');
        if (table) {
            table.style.width = '100%';
            table.style.backgroundColor = 'white';
            table.style.borderCollapse = 'collapse';
            const cells = table.querySelectorAll('th, tr, td, tfoot');
            cells.forEach(cell => {
                cell.style.backgroundColor = 'white';
            });
        }

        const downloadLinkInClone = clone.querySelector('#download-pdf');
        if (downloadLinkInClone) downloadLinkInClone.remove();
        const goHomeLinkInClone = clone.querySelector('#go-home-link');
        if (goHomeLinkInClone) goHomeLinkInClone.remove();

        const header = clone.querySelector('.pdf-header');
        const footer = clone.querySelector('.pdf-footer');
        if (header) {
            header.style.display = 'block';
            header.classList.add('pdf-visible');
        }
        if (footer) {
            footer.style.display = 'block';
            footer.classList.add('pdf-visible');
        }

        document.body.appendChild(clone);

        waitForImagesToLoad(clone).then(() => {
            const opt = {
                margin: 10,
                filename: 'TaFeCo.pdf',
                image: { type: 'jpeg', quality: 0.98 },
                html2canvas: { scale: 1.5, useCORS: true, scrollX: 0, scrollY: 0 },
                jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' },
                pagebreak: { mode: ['avoid-all', 'css', 'legacy'] }
            };

            html2pdf().set(opt).from(clone).save().then(() => {
                document.body.removeChild(clone);
            });
        });
    });
} */


export function updateDeliveryCost() {
         const totalSum = parseFloat(localStorage.getItem('totalSum')) || 0;

         const totalSumElem = document.getElementById('total-sum');
           if (totalSumElem) {
             totalSumElem.textContent = `${totalSum.toFixed(2)} руб.`;
           }

         let delivery = 0;
         if (totalSum < 3000) {
           delivery = 300.00;
         }

         // Показываем на странице
         const deliveryElem = document.getElementById('delivery-cost');
         if (deliveryElem) {
           deliveryElem.textContent = `Доставка: ${delivery} руб.`;
         }

         // Сохранить доставку тоже
         localStorage.setItem('deliveryCost', delivery.toString());
       // Итог = товары - доставка
         const finalTotal = totalSum + delivery;

         // Показываем итог
         const totalAmountElem = document.getElementById('total-payment');
         if (totalAmountElem) {
           totalAmountElem.textContent = `${finalTotal.toFixed(2)} руб.`;
         }
       }

       updateDeliveryCost();

function getUserDataFromStorage() {
  const raw = localStorage.getItem('userData');
  if (!raw) return {};
  try {
    return JSON.parse(raw);
  } catch (e) {
    console.error('Ошибка при чтении userData:', e);
    return {};
  }
}

function renderCartToPDFTable(items) {
    const tableBody = document.getElementById('cart-items');
    const totalItemsDisplay = document.getElementById('total-items');
    if (!tableBody || !totalItemsDisplay) return;

    const cart = JSON.parse(localStorage.getItem('cart-items') || '{}');

    tableBody.innerHTML = '';
    let totalSum = 0;

    items.forEach((item, index) => {
        const productId = item.product;
        const cartProduct = cart[productId] || {};

        const productName = cartProduct.productName || 'Название отсутствует';
        const unit = cartProduct.unit || '';
        const price = item.priceAtOrderTime ?? cartProduct.price ?? 0;

        const row = document.createElement('tr');

        const numberCell = document.createElement('td');
        numberCell.textContent = index + 1;

        const nameCell = document.createElement('td');
        nameCell.textContent = productName;

        const quantityCell = document.createElement('td');
        quantityCell.textContent = `${item.quantity || 1} ${unit}`;

        const priceCell = document.createElement('td');
        priceCell.textContent = `${price.toFixed(2)} ₽`;

        const sum = price * (item.quantity || 1);
        totalSum += sum;

        const totalCell = document.createElement('td');
        totalCell.textContent = `${sum.toFixed(2)} ₽`;

        row.appendChild(numberCell);
        row.appendChild(nameCell);
        row.appendChild(quantityCell);
        row.appendChild(priceCell);
        row.appendChild(totalCell);

        tableBody.appendChild(row);
    });

    totalItemsDisplay.textContent = `Сумма: ${totalSum.toFixed(2)} руб.`;
}

export async function createAndRenderOrder() {

  const existing = localStorage.getItem('createdOrder');
    if (existing) {
      console.log('Заказ уже создан, повторное создание не требуется.');
      return;
    }

  const orderDataRaw = localStorage.getItem('orderData');
  if (!orderDataRaw) {
    alert("Данные заказа отсутствуют");
    return;
  }

  let orderData;
  try {
    orderData = JSON.parse(orderDataRaw);
  } catch (e) {
    alert("Ошибка разбора данных заказа");
    return;
  }

  if (!orderData.items || orderData.items.length === 0) {
    return;
  }

  // Убедимся, что items — массив
  if (!Array.isArray(orderData.items)) {
    orderData.items = Object.values(orderData.items);
  }

  try {
    const response = await fetch('/api/orders', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(orderData),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Ошибка при оформлении заказа: ${response.status} ${errorText}`);
    }

    const createdOrder = await response.json();
    localStorage.setItem('createdOrder', JSON.stringify(createdOrder));

    alert('Заказ успешно отправлен!');

    // Отобразим номер и дату заказа
    const orderNumberEl = document.getElementById('number_order');
    if (orderNumberEl) {
      const formattedDate = new Date(createdOrder.orderDate).toLocaleDateString();
      orderNumberEl.textContent = `№ ${createdOrder.id} от ${formattedDate}`;
    }

    // Отрисовать данные пользователя
    const userData = getUserDataFromStorage();
    saveUserData(userData);

    // Отрисовать корзину
    renderCartToPDFTable(orderData.items);

    const pdfBtn = document.getElementById('download-pdf');
      if (pdfBtn) {
        pdfBtn.style.display = 'inline-block';
      }

      let timeoutId;
      // Автоочистка через 10 минут
      function startTimer() {
        timeoutId = setTimeout(clearDataAndRedirect, 600000);
      }
      function resetTimer() {
        clearTimeout(timeoutId);
        startTimer();
      }
      startTimer();
      // Сброс таймера при активности
      document.addEventListener('mousemove', resetTimer);
      document.addEventListener('keydown', resetTimer);

     window.addEventListener('pagehide', (e) => {
       if (!e.persisted) {
         clearDataAndRedirect();
       }
     });

  } catch (error) {
    console.error('Ошибка при создании заказа:', error);
    alert('Ошибка: ' + error.message);
  }

}

function clearDataAndRedirect() {
  localStorage.removeItem('createdOrder');
  localStorage.removeItem('orderData');
  localStorage.removeItem('cart-items');
  updateCartCount();

  console.log('Заказ очищен. Переход на главную страницу...');
  window.location.href = '/';
}

export async function generatePDF() {
    const original = document.getElementById("tablas");
    if (!original) return;

    const clone = original.cloneNode(true);
    clone.id = "tablas-clone";
    clone.style.position = 'relative';
    clone.style.visibility = 'visible';
    clone.style.top = '0';
    clone.style.left = '0';
    clone.style.width = '185mm';
    clone.style.display = 'block';
    clone.style.background = 'transparent';

    // Стилизация таблицы
    const table = clone.querySelector('.custom-table');
    if (table) {
        table.style.width = '100%';
        table.style.tableLayout = 'fixed';
        table.style.backgroundColor = 'white';
        table.style.borderCollapse = 'collapse';
        const cells = table.querySelectorAll('th, tr, td, tfoot, tbody');
        cells.forEach(cell => {
            cell.style.backgroundColor = 'white';
        });

    }


    // Удаление ненужных элементов
    const downloadLinkInClone = clone.querySelector('#download-pdf');
    if (downloadLinkInClone) downloadLinkInClone.remove();

    const goHomeLinkInClone = clone.querySelector('#go-home-link');
    if (goHomeLinkInClone) goHomeLinkInClone.remove();

    // Показываем заголовки/футеры
    const header = clone.querySelector('.pdf-header');
    const footer = clone.querySelector('.pdf-footer');
    if (header) {
        header.style.display = 'block';
        header.classList.add('pdf-visible');
    }
    if (footer) {
        footer.style.display = 'block';
        footer.classList.add('pdf-visible');
    }

    document.body.appendChild(clone);

    await waitForImagesToLoad(clone);

    const opt = {
        margin: 10,
        filename: `TaFeCo-заказ.pdf`,
        image: { type: 'jpeg', quality: 0.98 },
        html2canvas: { scale: 2, useCORS: true, scrollX: 0, scrollY: 0},
        jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' },
        pagebreak: { mode: ['avoid-all', 'css', 'legacy'] }
    };

    await html2pdf().set(opt).from(clone).save();

    document.body.removeChild(clone);
}

// Ожидаем загрузку всех изображений
function waitForImagesToLoad(element) {
    const images = element.querySelectorAll("img");
    const promises = Array.from(images).map(img => {
        if (img.complete) return Promise.resolve();
        return new Promise(resolve => {
            img.onload = img.onerror = resolve;
        });
    });
    return Promise.all(promises);
}


