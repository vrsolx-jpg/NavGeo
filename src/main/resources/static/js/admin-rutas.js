/**
 * admin-rutas.js
 * Lógica del mapa de trazado para el panel de administración.
 */

let mapAdmin;
let polylineActual;
let puntosActuales = [];
let marcadores     = [];
let sentidoActual  = 'ida';

// ── Inicialización del mapa ────────────────────────────────────────
function initMapAdmin() {

    const centroNeiva = { lat: 2.9273, lng: -75.2819 };

    mapAdmin = new google.maps.Map(document.getElementById('mapa-trazado'), {
        center:            centroNeiva,
        zoom:              14,
        mapTypeControl:    false,
        streetViewControl: false,
        fullscreenControl: false
    });

    polylineActual = new google.maps.Polyline({
        path:          puntosActuales,
        geodesic:      true,
        strokeColor:   obtenerColorSeleccionado(),
        strokeOpacity: 0.9,
        strokeWeight:  5
    });

    polylineActual.setMap(mapAdmin);

    // Cada clic agrega un punto al trazado
    mapAdmin.addListener('click', (event) => {
        agregarPunto({
            lat: event.latLng.lat(),
            lng: event.latLng.lng()
        });
    });

    // ── Buscador de lugares (Google Places Autocomplete) ──────────
    const inputBuscador = document.getElementById('buscador-lugares');
    if (inputBuscador && google.maps.places) {

        const autocomplete = new google.maps.places.Autocomplete(inputBuscador, {
            componentRestrictions: { country: 'co' },
            types:                 ['establishment', 'geocode'],
            bounds: new google.maps.LatLngBounds(
                new google.maps.LatLng(2.85, -75.35),
                new google.maps.LatLng(3.00, -75.20)
            ),
            strictBounds: false
        });

        autocomplete.addListener('place_changed', () => {
            const lugar = autocomplete.getPlace();
            if (!lugar.geometry || !lugar.geometry.location) return;

            // Centra el mapa en el lugar encontrado
            mapAdmin.setCenter(lugar.geometry.location);
            mapAdmin.setZoom(17);

            // Marcador azul de referencia (no es punto de ruta)
            new google.maps.Marker({
                position: lugar.geometry.location,
                map:      mapAdmin,
                title:    lugar.name,
                icon: {
                    path:        google.maps.SymbolPath.CIRCLE,
                    scale:       8,
                    fillColor:   '#1E88E5',
                    fillOpacity: 0.8,
                    strokeColor: '#fff',
                    strokeWeight: 2
                }
            });
        });
    }
}

// ── Agrega un punto al trazado ────────────────────────────────────
function agregarPunto(punto) {
    puntosActuales.push(punto);
    polylineActual.setPath(puntosActuales);

    const marcador = new google.maps.Marker({
        position: punto,
        map:      mapAdmin,
        icon: {
            path:        google.maps.SymbolPath.CIRCLE,
            scale:       5,
            fillColor:   obtenerColorSeleccionado(),
            fillOpacity: 1,
            strokeColor: '#fff',
            strokeWeight: 1.5
        },
        title: `Punto ${puntosActuales.length}`
    });

    marcadores.push(marcador);
    actualizarUI();
}

// ── Deshace el último punto ───────────────────────────────────────
function deshacerUltimoPunto() {
    if (puntosActuales.length === 0) return;
    puntosActuales.pop();
    polylineActual.setPath(puntosActuales);
    const ultimo = marcadores.pop();
    if (ultimo) ultimo.setMap(null);
    actualizarUI();
}

// ── Limpia todo el trazado ────────────────────────────────────────
function limpiarTrazado() {
    puntosActuales = [];
    polylineActual.setPath([]);
    marcadores.forEach(m => m.setMap(null));
    marcadores = [];
    actualizarUI();
}

// ── Cambia el sentido seleccionado ────────────────────────────────
function seleccionarSentido(sentido) {
    sentidoActual = sentido;
    document.getElementById('tab-ida').classList
        .toggle('active', sentido === 'ida');
    document.getElementById('tab-vuelta').classList
        .toggle('active', sentido === 'vuelta');
}

// ── Actualiza el color de la polyline y los marcadores ───────────
function actualizarColorPreview(color) {
    const preview = document.getElementById('color-preview');
    const label   = document.getElementById('color-hex-label');
    if (preview) preview.style.background = color;
    if (label)   label.textContent        = color;

    if (polylineActual) polylineActual.setOptions({ strokeColor: color });

    marcadores.forEach(m => {
        m.setIcon({
            path:        google.maps.SymbolPath.CIRCLE,
            scale:       5,
            fillColor:   color,
            fillOpacity: 1,
            strokeColor: '#fff',
            strokeWeight: 1.5
        });
    });
}

// ── Obtiene el color seleccionado ─────────────────────────────────
function obtenerColorSeleccionado() {
    const sel = document.getElementById('color');
    return sel ? sel.value : '#E53935';
}

// ── Actualiza el contador y resumen en la UI ──────────────────────
function actualizarUI() {
    const contador = document.getElementById('contador-puntos');
    const resumen  = document.getElementById('resumen-puntos');
    if (!contador || !resumen) return;

    contador.textContent = puntosActuales.length;

    if (puntosActuales.length === 0) {
        resumen.textContent = 'Aún no hay puntos. Haz clic sobre el mapa para comenzar.';
        return;
    }

    const primero = puntosActuales[0];
    const ultimo  = puntosActuales[puntosActuales.length - 1];
    resumen.innerHTML = `
        <strong>Inicio:</strong> lat ${primero.lat.toFixed(6)}, lng ${primero.lng.toFixed(6)}<br>
        <strong>Fin:</strong>    lat ${ultimo.lat.toFixed(6)},  lng ${ultimo.lng.toFixed(6)}<br>
        <strong>Total:</strong>  ${puntosActuales.length} puntos · sentido: ${sentidoActual}
    `;
}

// ── Envía el trazado al backend ───────────────────────────────────
async function guardarRuta() {

    const nombre      = document.getElementById('nombre').value.trim();
    const descripcion = document.getElementById('descripcion').value.trim();
    const color       = document.getElementById('color').value;

    if (!nombre) {
        mostrarMensaje('Por favor ingresa el nombre de la ruta.', 'error');
        return;
    }
    if (!descripcion) {
        mostrarMensaje('Por favor ingresa la descripción.', 'error');
        return;
    }
    if (puntosActuales.length < 2) {
        mostrarMensaje('Debes trazar al menos 2 puntos en el mapa.', 'error');
        return;
    }

    const payload = {
        nombre:      nombre,
        descripcion: descripcion,
        color:       color,
        sentido:     sentidoActual,
        coordenadas: puntosActuales.map(p => ({ lat: p.lat, lng: p.lng }))
    };

    mostrarMensaje('Guardando...', 'info');

    try {
        const respuesta = await fetch('/admin/api/rutas/completa', {
            method:  'POST',
            headers: { 'Content-Type': 'application/json' },
            body:    JSON.stringify(payload)
        });

        if (respuesta.ok) {
            const data = await respuesta.json();
            mostrarMensaje(
                `✅ Ruta "${nombre}" guardada correctamente con ` +
                `${puntosActuales.length} puntos (sentido: ${sentidoActual}).`,
                'exito'
            );
            limpiarTrazado();
            document.getElementById('nombre').value      = '';
            document.getElementById('descripcion').value = '';
        } else {
            const error = await respuesta.text();
            mostrarMensaje(`❌ Error: ${error}`, 'error');
        }

    } catch (err) {
        mostrarMensaje('❌ Error de conexión con el servidor.', 'error');
        console.error(err);
    }
}

// ── Muestra mensaje de estado ─────────────────────────────────────
function mostrarMensaje(texto, tipo) {
    const el = document.getElementById('msg-estado');
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