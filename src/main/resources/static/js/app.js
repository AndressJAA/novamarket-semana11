(() => {
    const body = document.body;
    const menuButton = document.querySelector('[data-menu-toggle]');
    const menuClose = document.querySelector('[data-menu-close]');

    const setMenu = (open) => {
        body.classList.toggle('menu-open', open);
        menuButton?.setAttribute('aria-expanded', String(open));
    };

    menuButton?.addEventListener('click', () => {
        setMenu(!body.classList.contains('menu-open'));
    });
    menuClose?.addEventListener('click', () => setMenu(false));
    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
            setMenu(false);
        }
    });

    const orderForm = document.querySelector('[data-order-form]');
    if (!orderForm) {
        return;
    }

    const amountInput = orderForm.querySelector('[data-amount-input]');
    const categorySelect = orderForm.querySelector('[data-category-select]');
    const categoryOutput = document.querySelector('[data-preview-category]');
    const rateOutput = document.querySelector('[data-preview-rate]');
    const subtotalOutput = document.querySelector('[data-preview-subtotal]');
    const discountOutput = document.querySelector('[data-preview-discount]');
    const totalOutput = document.querySelector('[data-preview-total]');

    const currency = new Intl.NumberFormat('es-PE', {
        style: 'currency',
        currency: 'PEN',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });

    const roundMoney = (value) => Math.round((value + Number.EPSILON) * 100) / 100;

    const updatePreview = () => {
        const selectedOption = categorySelect?.selectedOptions[0];
        const parsedAmount = Number.parseFloat(amountInput?.value ?? '0');
        const amount = Number.isFinite(parsedAmount) && parsedAmount > 0 ? parsedAmount : 0;
        const rawRate = selectedOption?.getAttribute('data-rate');
        const parsedRate = rawRate === null ? Number.NaN : Number(rawRate);
        const rate = Number.isFinite(parsedRate) && parsedRate >= 0 ? parsedRate : 0;
        const discount = roundMoney(amount * rate);
        const total = roundMoney(amount - discount);

        if (categoryOutput) categoryOutput.textContent = selectedOption?.textContent?.trim() ?? 'Sin categoría';
        if (rateOutput) rateOutput.textContent = `${Math.round(rate * 100)}%`;
        if (subtotalOutput) subtotalOutput.textContent = currency.format(amount);
        if (discountOutput) discountOutput.textContent = `− ${currency.format(discount)}`;
        if (totalOutput) totalOutput.textContent = currency.format(total);
    };

    amountInput?.addEventListener('input', updatePreview);
    categorySelect?.addEventListener('change', updatePreview);
    updatePreview();
})();
