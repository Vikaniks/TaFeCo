async function initDaData() {
  const pathname = window.location.pathname;
  if (!pathname.includes('order') && !pathname.includes('register')) {
    // Выход, если не нужная страница
    return;
  }

  if (typeof $.fn.suggestions !== 'function') {
    console.error('DaData Suggestions плагин не загружен');
    return;
  }


  const token = '50c5ef072a8b754bec28a7a5fd524d8c1d23d7cb';

    const regionInput = document.getElementById('region');
    const districtInput = document.getElementById('district');
    const localityInput = document.getElementById('locality');
    const streetInput = document.getElementById('street');
    const houseInput = document.getElementById('house');
    const apartmentInput = document.getElementById('apartment');


  if (!regionInput || !districtInput || !localityInput || !streetInput || !houseInput || !apartmentInput) {
      console.warn('❗ Не найдены одно или несколько полей адреса');
      return;
    }

  const detectedCity = await detectCityByIP(token);
  if (detectedCity) {
    cityInput.value = detectedCity;
  }

  $(localityInput).suggestions({
      serviceUrl: 'https://suggestions.dadata.ru/suggestions/api/4_1/rs',
      token,
      type: 'ADDRESS',
      bounds: 'city', // или 'settlement' при необходимости
      onSelect: function (suggestion) {
        const data = suggestion.data;

        regionInput.value = data.region_with_type || '';

        // приоритет городскому району, если он есть
        districtInput.value =
          data.city_district_with_type ||
          data.area_with_type ||
          '';

        // Населённый пункт (город или посёлок)
        localityInput.value =
          data.city_with_type ||
          data.settlement_with_type ||
          '';
          streetInput.value = '';
                houseInput.value = '';
                apartmentInput.value = '';

                initStreetSuggestions(data);
              }
            });


  // Улица
    function initStreetSuggestions(parentData) {
      $(streetInput).suggestions({
        serviceUrl: 'https://suggestions.dadata.ru/suggestions/api/4_1/rs',
        token,
        type: 'ADDRESS',
        bounds: 'street',
        constraints: {
          locations: [{
            region: parentData.region,
            area: parentData.area,
            city: parentData.city,
            settlement: parentData.settlement
          }]
        },
        onSelect: function (suggestion) {
          const data = suggestion.data;
          streetInput.value = data.street_with_type || '';

          houseInput.value = '';
          apartmentInput.value = '';

          initHouseSuggestions(data);
        }
      });
    }

    // Дом
    function initHouseSuggestions(parentData) {
      $(houseInput).suggestions({
        serviceUrl: 'https://suggestions.dadata.ru/suggestions/api/4_1/rs',
        token,
        type: 'ADDRESS',
        bounds: 'house',
        constraints: {
          locations: [{
            region: parentData.region,
            area: parentData.area,
            city: parentData.city,
            settlement: parentData.settlement,
            street: parentData.street
          }]
        },
        onSelect: function (suggestion) {
          houseInput.value = suggestion.data.house || '';
        }
      });
    }


  async function detectCityByIP(token) {
    try {
      const response = await fetch('https://suggestions.dadata.ru/suggestions/api/4_1/rs/iplocate/address', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
          'Authorization': 'Token ' + token,
        },
        body: JSON.stringify({ ip: '' })
      });
      const data = await response.json();
      return data.suggestions?.[0]?.data?.city || null;
    } catch (e) {
      console.error('Ошибка определения города по IP', e);
      return null;
    }
  }
}

// Инициализация
document.addEventListener('DOMContentLoaded', () => {
  initDaData();
});
