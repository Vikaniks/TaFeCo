
export function saveFormToStorage() {
  const form = document.getElementById('order-form');
  const data = {};
  if (!form) {
      console.warn('Форма orderForm не найдена');
      return;
    }

    const elements = form.elements;
      for (let i = 0; i < elements.length; i++) {
        const el = elements[i];
        if (el.name) {
          localStorage.setItem(el.name, el.value);
        }
      }

  Array.from(form.elements).forEach(el => {
    if (el.name) data[el.name] = el.value;
  });
  localStorage.setItem('orderData', JSON.stringify(data));
}

export function restoreFormFromStorage() {
    const data = JSON.parse(localStorage.getItem('orderData'));
    const form = document.getElementById('order-form');
    if (!data || !form) return;

    // Восстановление значений
    Object.keys(data).forEach(name => {
        const el = form.elements[name];
        if (el) el.value = data[name];
    });

    // Автоматическое сохранение при изменениях
    form.addEventListener("input", () => {
        const formData = {};
        Array.from(form.elements).forEach(el => {
            if (el.name) {
                formData[el.name] = el.value;
            }
        });
        localStorage.setItem("orderData", JSON.stringify(formData));
    });
}

export function saveOrderData() {
  const orderDate = new Date().toISOString().split('T')[0];
  const userId = localStorage.getItem('userId');

  const rawItems = JSON.parse(localStorage.getItem('cart-items') || '{}');
  console.log('rawItems:', rawItems);

  const entries = Object.entries(rawItems);

  let totalPrice = 0;
  const orderItems = entries.map(([key, item]) => {
    const productId = item.id || item.productId;

    if (!item.quantity || !item.price || !productId) {
      throw new Error(`Неверные данные товара: ${item.productId || key || 'неизвестно'}`);
    }
    const price = parseFloat(item.price);
    const quantity = parseInt(item.quantity);
    totalPrice += price * quantity;

    return {
      id: null,
      product: parseInt(productId),
      quantity,
      priceAtOrderTime: price,
      orderId: null
    };
  });

  console.log('orderItems:', orderItems);
  console.log('Array.isArray(orderItems):', Array.isArray(orderItems));

  const orderDTO = {
    id: 0,
    orderDate,
    user: userId ? parseInt(userId) : null,  // гость, если userId нет
    items: Array.isArray(orderItems) ? orderItems : [orderItems],
    totalPrice: parseFloat(totalPrice.toFixed(2)),
    status: 'NEW'
  };

  console.log('orderDTO:', orderDTO);

  localStorage.setItem('orderData', JSON.stringify(orderDTO));
  return orderDTO;
}

// user
export function saveUserData(userData) {
  if (!userData || typeof userData !== 'object') return;

  for (const key in mappings) {
    const el = document.getElementById(mappings[key]);
    if (el && userData[key]) {
      if (el.tagName === 'INPUT' || el.tagName === 'TEXTAREA' || el.tagName === 'SELECT') {
              el.value = userData[key];
            } else {
              el.textContent = userData[key];
            }
    }
  }
}

export const mappings = {
           name: 'name',
           surname: 'surname',
           phone: 'phone',
           email: 'email',
           locality: 'locality',
           district: 'district',
           region: 'region',
           street: 'street',
           house: 'house',
           apartment: 'apartment',
           addressExtra: 'address-extra',
           date: 'date',
           time: 'time',
           paymentOption: 'payment-option'
         };


