/**
 * admin-paraderos.js
 * Lógica del mapa para registrar paraderos en el panel de administración.
 */

let mapParadero;
let marcadorActual = null;
let latSeleccionada = null;
let lngSeleccionada = null;

function initMapParadero() {
    const centroNeiva = { lat: 2.9273, lng: -75.2819 };

    mapParadero = new google.maps.Map(document.getElementById('mapa-paradero'), {
        center:            centroNeiva,
        zoom:              14,
        mapTypeControl:    false,
        streetViewControl: false,
        fullscreenControl: false
    });

    // Clic en el mapa → coloca el marcador
    mapParadero.addListener('click', (event) => {
        colocarMarcador(event.latLng.lat(), event.latLng.lng());
    });

    // Buscador de lugares
    const input = document.getElementById('buscador-paradero');
    if (input && google.maps.places) {
        const autocomplete = new google.maps.places.Autocomplete(input, {
            componentRestrictions: { country: 'co' },
            types: ['establishment', 'geocode'],
            bounds: new google.maps.LatLngBounds(
                new google.maps.LatLng(2.85, -75.35),
                new google.maps.LatLng(3.00, -75.20)
            )
        });

        autocomplete.addListener('place_changed', () => {
            const lugar = autocomplete.getPlace();
            if (!lugar.geometry || !lugar.geometry.location) return;
            mapParadero.setCenter(lugar.geometry.location);
            mapParadero.setZoom(17);
            colocarMarcador(
                lugar.geometry.location.lat(),
                lugar.geometry.location.lng()
            );
        });
    }
}

function colocarMarcador(lat, lng) {
    latSeleccionada = lat;
    lngSeleccionada = lng;

    // Elimina el marcador anterior si existe
    if (marcadorActual) marcadorActual.setMap(null);

    // Obtiene el color de forma segura
    const colorInput = document.getElementById('color-paradero');
    const color = (colorInput && colorInput.value) ? colorInput.value : '#4F46E5';

    marcadorActual = new google.maps.Marker({
        position: { lat, lng },
        map:      mapParadero,
        title:    'Paradero seleccionado',
        icon: {
            path:        google.maps.SymbolPath.CIRCLE,
            scale:       10,
            fillColor:   color,
            fillOpacity: 1,
            strokeColor: '#fff',
            strokeWeight: 2
        }
    });

    // Actualiza los campos ocultos y el resumen
    document.getElementById('latitud-paradero').value  = lat;
    document.getElementById('longitud-paradero').value = lng;

    const resumen = document.getElementById('coords-seleccionadas');
    if (resumen) {
        resumen.innerHTML = `
            <strong>Ubicación seleccionada:</strong><br>
            Latitud: ${lat.toFixed(7)}<br>
            Longitud: ${lng.toFixed(7)}
        `;
    }
}

function actualizarColorParadero(color) {
    document.getElementById('color-preview-paradero').style.background = color;
    document.getElementById('color-hex-paradero').textContent = color;

    // Actualiza el marcador si ya está colocado
    if (marcadorActual) {
        marcadorActual.setIcon({
            path:        google.maps.SymbolPath.CIRCLE,
            scale:       10,
            fillColor:   color,
            fillOpacity: 1,
            strokeColor: '#fff',
            strokeWeight: 2
        });
    }
}

async function guardarParadero() {
    const nombre   = document.getElementById('nombre-paradero').value.trim();
    const latitud  = document.getElementById('latitud-paradero').value;
    const longitud = document.getElementById('longitud-paradero').value;
    const color    = document.getElementById('color-paradero').value;

    if (!nombre) {
        mostrarMensajeParadero('Por favor ingresa el nombre del paradero.', 'error');
        return;
    }
    if (!latitud || !longitud) {
        mostrarMensajeParadero('Haz clic en el mapa para seleccionar la ubicación.', 'error');
        return;
    }

    const payload = {
        nombre:   nombre,
        latitud:  parseFloat(latitud),
        longitud: parseFloat(longitud),
        color:    color
    };

    mostrarMensajeParadero('Guardando...', 'info');

    try {
        const respuesta = await fetch('/admin/api/paraderos', {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify(payload)
        });

        if (respuesta.ok) {
            mostrarMensajeParadero(
                `Paradero "${nombre}" guardado correctamente.`, 'exito');
            // Limpia el formulario
            document.getElementById('nombre-paradero').value = '';
            document.getElementById('coords-seleccionadas').textContent =
                'Haz clic en el mapa para seleccionar la ubicación del paradero.';
            if (marcadorActual) { marcadorActual.setMap(null); marcadorActual = null; }
            latSeleccionada = null;
            lngSeleccionada = null;
        } else {
            const error = await respuesta.text();
            mostrarMensajeParadero(`Error: ${error}`, 'error');
        }
    } catch (err) {
        mostrarMensajeParadero('Error de conexión.', 'error');
        console.error(err);
    }
}

function mostrarMensajeParadero(texto, tipo) {
    const el = document.getElementById('msg-estado-paradero');
    if (!el) return;
    el.style.display = 'block';
    el.textContent   = texto;
    const estilos = {
        exito: 'background:rgba(16,185,129,0.15);color:#6EE7B7;border:1px solid rgba(16,185,129,0.3)',
        error: 'background:rgba(239,68,68,0.15);color:#FCA5A5;border:1px solid rgba(239,68,68,0.3)',
        info:  'background:rgba(99,102,241,0.15);color:#C7D2FE;border:1px solid rgba(99,102,241,0.3)'
    };
    el.style.cssText += '; ' + (estilos[tipo] || estilos.info);
}
