const CART_KEY = 'cart';

export let cart = loadCart();

function loadCart() {
    const cartData = localStorage.getItem(CART_KEY);
    return cartData ? JSON.parse(cartData) : {};
}

function saveCart() {
    localStorage.setItem(CART_KEY, JSON.stringify(cart));
}

export function addToCart(product) {
    const name = product.name;

    if (cart[name]) {
        cart[name].quantity += product.quantity;
    } else {
        cart[name] = {
            price: product.price,
            quantity: product.quantity,
            unit: product.unit,
            image: product.image
        };
    }

    saveCart();
    updateCartCount();
}

export function increaseCartItem(name) {
    if (cart[name]) {
        cart[name].quantity += 1;
        saveCart();
        updateCartCount();
    }
}

export function decreaseCartItem(name) {
    if (cart[name]) {
        cart[name].quantity -= 1;
        if (cart[name].quantity <= 0) {
            delete cart[name];
        }
        saveCart();
        updateCartCount();
    }
}

export function removeFromCart(name) {
    if (cart[name]) {
        delete cart[name];
        saveCart();
        updateCartCount();
    }
}

export function updateCartCount() {
    const cartCountEl = document.getElementById('cart-count');
    if (!cartCountEl) return;

    const totalItems = Object.values(cart).reduce((sum, item) => sum + item.quantity, 0);
    cartCountEl.textContent = totalItems;
}
