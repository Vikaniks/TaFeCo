document.addEventListener('DOMContentLoaded', () => {
    const path = window.location.pathname;

    if (path.includes('index.html')) {
        import('./pages/indexPage.js').then(module => module.init());
    } else if (path.includes('order.html')) {
        import('./pages/orderPage.js').then(module => module.init());
    } else if (path.includes('finalOrder.html')) {
        import('./pages/finalOrderPage.js').then(module => module.init());
    }
});
