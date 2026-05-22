let map;
let polylineasActivas = [];
let marcadoresActivos = [];
let rutaActivaId = null;
let sentidoActual = "ida";

const CENTRO_NEIVA = { lat: 2.9273, lng: -75.2819 };

function initMap() {
    map = new google.maps.Map(document.getElementById("map"), {
        center: CENTRO_NEIVA,
        zoom: 13,
        mapTypeId: "roadmap",
        mapTypeControl: false,
        streetViewControl: false,
        fullscreenControl: false,
        zoomControlOptions: {
            position: google.maps.ControlPosition.RIGHT_CENTER
        }
    });

    const marcadorNeiva = new google.maps.Marker({
        position: CENTRO_NEIVA,
        map: map,
        title: "Neiva, Huila, Colombia"
    });
    marcadoresActivos.push(marcadorNeiva);

    actualizarBotonesActivos("btn-todas");
    actualizarStatusBar("Mapa base de Neiva, Huila");
}

function mostrarTodasLasRutas() {
    limpiarPolilineas();
    rutaActivaId = null;
    sentidoActual = "ida";

    if (map) {
        map.setCenter(CENTRO_NEIVA);
        map.setZoom(13);
    }

    actualizarBotonesActivos("btn-todas");
    const panelInfo = document.getElementById("panel-info");
    if (panelInfo) panelInfo.style.display = "none";
    actualizarStatusBar("Mapa base de Neiva, Huila");
}

function seleccionarRuta(id, nombre) {
    rutaActivaId = id;
    sentidoActual = "ida";
    limpiarPolilineas();

    if (map) {
        map.setCenter(CENTRO_NEIVA);
        map.setZoom(13);
    }

    actualizarBotonesActivos("btn-ruta-" + nombre.replace("Ruta ", ""));
    actualizarPanelInfo(id);
    activarBotonSentido("btn-ida");
    actualizarStatusBar(`${nombre} seleccionada. Trazado pendiente de cargar.`);
}

function cambiarSentido(sentido) {
    if (!rutaActivaId) return;

    sentidoActual = sentido;
    limpiarPolilineas();
    activarBotonSentido(sentido === "ida" ? "btn-ida" : "btn-vuelta");

    const datos = datosRuta(rutaActivaId);
    if (datos) {
        actualizarStatusBar(`${datos.nombre} seleccionada. Sentido ${sentido} pendiente de cargar.`);
    }
}

function limpiarPolilineas() {
    polylineasActivas.forEach(p => p.setMap(null));
    polylineasActivas = [];
}

function actualizarBotonesActivos(btnId) {
    document.querySelectorAll(".route-btn").forEach(btn => btn.classList.remove("active"));
    const btnActivo = document.getElementById(btnId);
    if (btnActivo) btnActivo.classList.add("active");
}

function actualizarStatusBar(mensaje) {
    const statusEl = document.getElementById("status-text");
    if (statusEl) {
        statusEl.innerHTML = `<i class="bi bi-reception-4"></i> ${mensaje}`;
    }
}

function actualizarPanelInfo(rutaId) {
    const datos = datosRuta(rutaId);
    if (!datos) return;

    const panelInfo = document.getElementById("panel-info");
    const infoNombre = document.getElementById("info-nombre");
    const infoDescripcion = document.getElementById("info-descripcion");

    if (panelInfo) panelInfo.style.display = "block";
    if (infoNombre) infoNombre.textContent = datos.nombre;
    if (infoDescripcion) infoDescripcion.textContent = datos.descripcion;
}

function activarBotonSentido(btnId) {
    document.querySelectorAll(".sentido-btn").forEach(b => b.classList.remove("active"));
    const btn = document.getElementById(btnId);
    if (btn) btn.classList.add("active");
}

function datosRuta(rutaId) {
    const rutas = {
        1: {
            nombre: "Ruta 11",
            descripcion: "Las Granjas - Centro. El trazado se cargara cuando esten listas las coordenadas."
        },
        2: {
            nombre: "Ruta 19",
            descripcion: "Timanco - Zona Industrial. El trazado se cargara cuando esten listas las coordenadas."
        },
        3: {
            nombre: "Ruta 33",
            descripcion: "Miraflores - Terminal. El trazado se cargara cuando esten listas las coordenadas."
        },
        4: {
            nombre: "Ruta 60",
            descripcion: "Sur - Hospital Universitario. El trazado se cargara cuando esten listas las coordenadas."
        }
    };

    return rutas[rutaId];
}

function mostrarErrorMapa(mensaje) {
    const mapEl = document.getElementById("map");
    if (!mapEl) return;

    mapEl.innerHTML = `
        <div class="map-error">
            <i class="bi bi-exclamation-triangle-fill"></i>
            <span>${mensaje}</span>
        </div>
    `;
    actualizarStatusBar("Google Maps no esta configurado");
}
