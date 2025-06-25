// order.js
import { saveFormToStorage, restoreFormFromStorage } from './storage.js';


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

        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            const data = new FormData(form);
            const response = await fetch('/api/order', {
                method: 'POST',
                body: data
            });
            if (response.ok) {
                localStorage.removeItem('orderData');
                window.location.href = '/order/success';
            } else {
                alert("Ошибка оформления заказа");
            }
        });
        form.addEventListener("input", () => {
            const formData = {
                name: nameInput.value,
                surname: surnameInput.value,
                phone: phoneInput.value,
                email: emailInput.value
            };
            localStorage.setItem("orderData", JSON.stringify(formData));
        });


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

    function getOrderData() {
        return {
            name: nameInput.value.trim(),
            surname: surnameInput.value.trim(),
            phone: phoneInput.value.trim(),
            email: emailInput.value.trim(),
            city: document.getElementById("city")?.value.trim() || "",
            barrio: document.getElementById("barrio")?.value.trim() || "",
            address: document.getElementById("address")?.value.trim() || "",
            addressExtra: document.getElementById("address-extra")?.value.trim() || "",
            date: document.getElementById("date")?.value.trim() || "",
            time: document.getElementById("time")?.value.trim() || "",
            paymentOption: document.getElementById("payment-option")?.value || "cash"
        };
    }

    // Заполняем поля, если данные есть в localStorage
    function populateForm() {
        const orderData = localStorage.getItem("orderData");
        if (orderData) {
            const tempData = JSON.parse(orderData);
            if (tempData.name) nameInput.value = tempData.name;
            if (tempData.surname) surnameInput.value = tempData.surname;
            if (tempData.phone) phoneInput.value = tempData.phone;
            if (tempData.email) emailInput.value = tempData.email;
            if (tempData.city) document.getElementById("city").value = tempData.city;
            if (tempData.barrio) document.getElementById("barrio").value = tempData.barrio;
            if (tempData.address) document.getElementById("address").value = tempData.address;
            if (tempData.addressExtra) document.getElementById("address-extra").value = tempData.addressExtra;
            if (tempData.date) document.getElementById("date").value = tempData.date;
            if (tempData.time) document.getElementById("time").value = tempData.time;
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

        const orderData = getOrderData();
        localStorage.setItem("orderData", JSON.stringify(orderData));

        window.location.href = "/finalOrder";
    });

    populateForm();

}
// final order.js

export function populateFinalOrder() {
    const orderData = JSON.parse(localStorage.getItem("orderData")) || {};
    const dateInput = document.getElementById("date");
    const timeSelect = document.getElementById("time");
    const deliveryDateTime = document.getElementById("delivery-datetime");
    const recipientEl = document.getElementById("recipient");
    const paymentSelect = document.getElementById("payment-option");
    const paymentDisplay = document.getElementById("payment-option-text");

    if (!recipientEl) return; // если нет получателя, выходим (не на нужной странице)

    // Отображаем основные данные
    recipientEl.textContent = `${orderData.name || "Не указано"} ${orderData.surname || "Не указано"}`;
    document.getElementById("phone").textContent = orderData.phone || "Не указано";
    document.getElementById("address").textContent = `${orderData.city || "Не указано"}, ${orderData.barrio || "Не указано"}, ${orderData.address || "Не указано"}`;

    if (dateInput && orderData.date) {
        dateInput.value = orderData.date;
    }
    if (timeSelect && orderData.time) {
        timeSelect.value = orderData.time;
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
            orderData.date = date;
            orderData.time = time;
            localStorage.setItem("orderData", JSON.stringify(orderData));
        } else {
            deliveryDateTime.textContent = "Выберите дату и время";
        }
    }

    // Отображаем дату и время из orderData, если есть
    if (orderData.date && orderData.time && deliveryDateTime) {
        deliveryDateTime.innerHTML = `${formatDate(orderData.date)} &nbsp;|&nbsp; ${orderData.time}`;
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
        }[orderData.paymentOption] || "Не указано";

        paymentDisplay.textContent = paymentText;
    }
}


export function getOrderData() {
    return JSON.parse(localStorage.getItem("orderData")) || {};
}



export function setupConfirmButton() {
    const confirmBtn = document.getElementById("confirm-order");
    const paymentSelect = document.getElementById("payment-option");

    if (confirmBtn) {
        confirmBtn.addEventListener("click", () => {
            if (paymentSelect && paymentSelect.value === "") {
                alert("Пожалуйста, выберите способ оплаты.");
                return; // остановить выполнение
            }

            //  Обновляем paymentOption в localStorage
            const orderData = JSON.parse(localStorage.getItem("orderData")) || {};
            if (paymentSelect) {
                orderData.paymentOption = paymentSelect.value;
                localStorage.setItem("orderData", JSON.stringify(orderData));
                 // сразу обновить отображение
                        if (paymentDisplay) {
                            const paymentText = {
                                "cash": "Оплата наличными при доставке",
                                "card": "Банковская карта"
                            }[orderData.paymentOption] || "Не указано";
                            paymentDisplay.textContent = paymentText;
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

export function downloadPDF() {
    const downloadBtn = document.getElementById("download-pdf");
    if (!downloadBtn || pdfListenerInitialized) return;

    pdfListenerInitialized = true;

    function getCartFromStorage() {
        const storedCart = localStorage.getItem('cart-items');
        return storedCart ? JSON.parse(storedCart) : {};
    }

    function renderCartToPDFTable(cart) {
        const cartItemsContainer = document.getElementById('cart-items');
        const totalItemsDisplay = document.getElementById('total-items');
        if (!cartItemsContainer || !totalItemsDisplay) return;

        cartItemsContainer.innerHTML = '';
        let totalSum = 0;

        Object.entries(cart).forEach(([productName, product]) => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td><img src="${product.image}" width="50" height="50"></td>
                <td>${productName}</td>
                <td>${product.quantity} ${product.unit}</td>
                <td>${product.price.toFixed(2)} руб.</td>
                <td>${(product.price * product.quantity).toFixed(2)} руб.</td>
                <td></td>
            `;
            cartItemsContainer.appendChild(row);
            totalSum += product.quantity * product.price;
        });

        totalItemsDisplay.textContent = `Сумма: ${totalSum.toFixed(2)} руб.`;
    }

    downloadBtn.addEventListener("click", () => {
        const cart = getCartFromStorage();
        renderCartToPDFTable(cart);

        const header = document.querySelector('.pdf-header');
        const footer = document.querySelector('.pdf-footer');

        if (header) header.style.display = 'block';
        if (footer) footer.style.display = 'block';

        setTimeout(() => {
            const element = document.getElementById("tablas");
            if (!element) return;

            const opt = {
                margin: 5,
                filename: 'order.pdf',
                image: { type: 'jpeg', quality: 0.98 },
                html2canvas: { scale: 2 },
                jsPDF: { unit: 'mm', format: 'a4', orientation: 'portrait' },
                pagebreak: { mode: ['avoid-all', 'css', 'legacy'] }
            };

            html2pdf().set(opt).from(element).save().then(() => {
                if (header) header.style.display = 'none';
                if (footer) footer.style.display = 'none';
            });
        }, 100);
    });
}



