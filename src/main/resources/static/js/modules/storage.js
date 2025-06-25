
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
  if (!data) return;
  const form = document.getElementById('order-form');
  Object.keys(data).forEach(name => {
    const el = form.elements[name];
    if (el) el.value = data[name];
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
}
