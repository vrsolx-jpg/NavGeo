/**
 * map.js — Lógica principal del mapa público de NavGeo
 */

let map;
let polylineasActivas = [];
let marcadoresActivos = [];
let rutaActivaId      = null;
let sentidoActual     = "ida";
let rutasCargadas     = [];
let paraderosCargados = [];

const CENTRO_NEIVA = { lat: 2.9273, lng: -75.2819 };
const PALETA_DALTONISMO_MAPA = ["#0072B2", "#E69F00", "#009E73", "#D55E00", "#CC79A7", "#000000"];

function actualizarNavPublica(seccionActiva) {
    document.querySelectorAll(".public-header__nav a").forEach(link => {
        link.classList.toggle("active", link.getAttribute("href") === `#${seccionActiva}`);
    });
}

function mostrarMapaPrincipal(event) {
    if (event) event.preventDefault();

    const mapEl = document.getElementById("map");
    const aboutPanel = document.getElementById("about-panel");

    if (mapEl) mapEl.style.display = "block";
    if (aboutPanel) aboutPanel.classList.remove("is-visible");
    actualizarNavPublica("inicio");
    history.replaceState(null, "", "#inicio");
}

function mostrarNosotros(event) {
    if (event) event.preventDefault();

    const mapEl = document.getElementById("map");
    const aboutPanel = document.getElementById("about-panel");

    if (mapEl) mapEl.style.display = "none";
    if (aboutPanel) aboutPanel.classList.add("is-visible");
    actualizarNavPublica("nosotros");
    history.replaceState(null, "", "#nosotros");
}

function modoDaltonismoActivo() {
    return Boolean(window.NavGeoPreferences && window.NavGeoPreferences.isColorblindEnabled());
}

function colorMapaAccesible(color, indice) {
    if (!modoDaltonismoActivo()) return color;
    return PALETA_DALTONISMO_MAPA[Math.abs(indice || 0) % PALETA_DALTONISMO_MAPA.length];
}

// ── Inicialización del mapa ───────────────────────────────────────
function initMap() {
    map = new google.maps.Map(document.getElementById("map"), {
        center:            CENTRO_NEIVA,
        zoom:              13,
        mapTypeId:         "roadmap",
        mapTypeControl:    false,
        streetViewControl: false,
        fullscreenControl: false,
        zoomControlOptions: {
            position: google.maps.ControlPosition.RIGHT_CENTER
        }
    });

    const marcadorNeiva = new google.maps.Marker({
        position: CENTRO_NEIVA,
        map:      map,
        title:    "Neiva, Huila, Colombia"
    });
    marcadoresActivos.push(marcadorNeiva);

    actualizarStatusBar("Mapa base de Neiva, Huila");
    cargarParaderos();
    cargarRutasDesdeBackend();
    normalizarAccesibilidadGoogleMaps();

    if (window.location.hash === "#nosotros") {
        mostrarNosotros();
    }
}

function normalizarAccesibilidadGoogleMaps() {
    const mapEl = document.getElementById("map");
    if (!mapEl || typeof MutationObserver === "undefined") return;

    const aplicar = () => {
        mapEl.querySelectorAll("area").forEach((area, index) => {
            if (!area.hasAttribute("alt") || !area.getAttribute("alt").trim()) {
                area.setAttribute("alt", `Control del mapa ${index + 1}`);
            }
            if (!area.hasAttribute("aria-label") || !area.getAttribute("aria-label").trim()) {
                area.setAttribute("aria-label", area.getAttribute("alt"));
            }
        });
    };

    aplicar();
    const observer = new MutationObserver(aplicar);
    observer.observe(mapEl, {
        childList: true,
        subtree: true,
        attributes: true,
        attributeFilter: ["alt", "aria-label"]
    });
}

// ── Carga rutas desde el backend y construye el dropdown ─────────
async function cargarRutasDesdeBackend() {
    try {
        const respuesta = await fetch('/api/rutas');
        if (!respuesta.ok) return;

        rutasCargadas = await respuesta.json();
        construirDropdown(rutasCargadas);

    } catch (e) {
        console.error('Error cargando rutas:', e);
    }
}

// ── Construye el dropdown personalizado ──────────────────────────
function construirDropdown(rutas) {
    const lista = document.getElementById('rutas-lista');
    if (!lista) return;

    lista.innerHTML = '';

    // Opción "Todas las rutas"
    const itemTodas = document.createElement('div');
    itemTodas.style.cssText = `
        padding: 12px 20px;
        cursor: pointer;
        font-size: 14px;
        font-weight: 600;
        color: #374151;
        display: flex;
        align-items: center;
        gap: 12px;
        border-bottom: 1px solid #f3f4f6;
        transition: background 0.15s;
    `;
    itemTodas.innerHTML = `
        <span style="width:12px;height:12px;border-radius:50%;
                     background:#6366F1;display:inline-block;flex-shrink:0;"></span>
        <span>Todas las rutas</span>
    `;
    itemTodas.onmouseenter = () => itemTodas.style.background = '#f9fafb';
    itemTodas.onmouseleave = () => itemTodas.style.background = 'transparent';
    itemTodas.onclick = () => {
        document.getElementById('map-search-input').value = '';
        document.getElementById('rutas-dropdown').style.display = 'none';
        mostrarTodasLasRutas();
    };
    lista.appendChild(itemTodas);

    // Una opción por cada ruta
    rutas.forEach(ruta => {
        const item = document.createElement('div');
        item.style.cssText = `
            padding: 12px 20px;
            cursor: pointer;
            font-size: 14px;
            color: #374151;
            display: flex;
            align-items: center;
            gap: 12px;
            border-bottom: 1px solid #f3f4f6;
            transition: background 0.15s;
        `;
        item.innerHTML = `
            <span style="width:12px;height:12px;border-radius:50%;
                         background:${ruta.color};display:inline-block;
                         flex-shrink:0;"></span>
            <div>
                <div style="font-weight:600;">${ruta.nombre}</div>
                <div style="font-size:11px;color:#9ca3af;margin-top:1px;">
                    ${ruta.descripcion}
                </div>
            </div>
        `;
        item.onmouseenter = () => item.style.background = '#f9fafb';
        item.onmouseleave = () => item.style.background = 'transparent';
        item.onclick = () => {
            document.getElementById('map-search-input').value = ruta.nombre;
            document.getElementById('rutas-dropdown').style.display = 'none';
            seleccionarRuta(ruta.id, ruta.nombre, ruta.color);
        };
        lista.appendChild(item);
    });
}

// ── Muestra el dropdown al hacer foco en el input ────────────────
function mostrarDropdown() {
    const dropdown = document.getElementById('rutas-dropdown');
    const lista    = document.getElementById('rutas-lista');
    if (dropdown && lista && lista.children.length > 0) {
        dropdown.style.display = 'block';
    }
}

// ── Filtra el dropdown mientras el usuario escribe ───────────────
function filtrarDropdown() {
    const v        = document.getElementById('map-search-input').value.toLowerCase();
    const items    = document.querySelectorAll('#rutas-lista > div');
    const dropdown = document.getElementById('rutas-dropdown');

    let hayResultados = false;
    items.forEach(item => {
        const texto = item.textContent.toLowerCase();
        if (texto.includes(v)) {
            item.style.display = 'flex';
            hayResultados = true;
        } else {
            item.style.display = 'none';
        }
    });

    dropdown.style.display = hayResultados ? 'block' : 'none';
}

// ── Busca y selecciona una ruta por nombre ────────────────────────
function buscarRuta() {
    const v = document.getElementById('map-search-input').value.trim();

    if (!v || v.toLowerCase() === 'todas') {
        mostrarTodasLasRutas();
        document.getElementById('rutas-dropdown').style.display = 'none';
        return;
    }

    const encontrada = rutasCargadas.find(ruta =>
        ruta.nombre.toLowerCase() === v.toLowerCase() ||
        ruta.nombre.toLowerCase().includes(v.toLowerCase())
    );

    document.getElementById('rutas-dropdown').style.display = 'none';

    if (encontrada) {
        seleccionarRuta(encontrada.id, encontrada.nombre, encontrada.color);
    } else {
        actualizarStatusBar(`No se encontró ninguna ruta con "${v}"`);
    }
}

// ── Muestra todas las rutas ───────────────────────────────────────
function mostrarTodasLasRutas() {
    limpiarPolilineas();
    rutaActivaId  = null;
    sentidoActual = "ida";
    actualizarStatusBar("Mostrando todas las rutas activas");

    const panelInfo = document.getElementById("panel-info");
    if (panelInfo) panelInfo.style.display = "none";
}

// ── Selecciona y carga una ruta específica ───────────────────────
async function seleccionarRuta(id, nombre, color) {
    rutaActivaId  = id;
    sentidoActual = "ida";
    limpiarPolilineas();

    actualizarStatusBar(`Cargando ${nombre}...`);
    actualizarPanelInfo(id, nombre);

    try {
        const respuesta = await fetch(`/api/rutas/${id}/coordenadas?sentido=ida`);
        if (!respuesta.ok) {
            actualizarStatusBar(`${nombre} — trazado pendiente de cargar.`);
            return;
        }

        const datos = await respuesta.json();

        if (datos.coordenadas && datos.coordenadas.length > 0) {
            dibujarPolilinea(datos.coordenadas, color || datos.color, 5, id);
            actualizarStatusBar(
                `${nombre} · Sentido: ida · ${datos.coordenadas.length} puntos`);
        } else {
            actualizarStatusBar(`${nombre} — trazado pendiente de cargar.`);
        }

    } catch (e) {
        console.error('Error cargando coordenadas:', e);
        actualizarStatusBar(`${nombre} — error al cargar trazado.`);
    }
}

// ── Cambia el sentido de la ruta activa ──────────────────────────
async function cambiarSentido(sentido) {
    if (!rutaActivaId) return;

    sentidoActual = sentido;
    limpiarPolilineas();
    activarBotonSentido(sentido === "ida" ? "btn-ida" : "btn-vuelta");

    const ruta   = rutasCargadas.find(r => r.id === rutaActivaId);
    const nombre = ruta ? ruta.nombre : `Ruta ${rutaActivaId}`;
    const color  = ruta ? ruta.color  : "#E53935";

    try {
        const respuesta = await fetch(
            `/api/rutas/${rutaActivaId}/coordenadas?sentido=${sentido}`);
        if (!respuesta.ok) return;

        const datos = await respuesta.json();
        if (datos.coordenadas && datos.coordenadas.length > 0) {
            dibujarPolilinea(datos.coordenadas, color, 5, rutaActivaId);
            actualizarStatusBar(
                `${nombre} · Sentido: ${sentido} · ${datos.coordenadas.length} puntos`);
        }
    } catch (e) {
        console.error('Error cambiando sentido:', e);
    }
}

// ── Dibuja una polilínea en el mapa ──────────────────────────────
function dibujarPolilinea(puntos, color, grosor, indiceColor) {
    if (!puntos || puntos.length === 0) return;

    const path = puntos.map(p => ({
        lat: parseFloat(p.lat),
        lng: parseFloat(p.lng)
    }));

    const polylinea = new google.maps.Polyline({
        path:          path,
        geodesic:      true,
        strokeColor:   colorMapaAccesible(color || "#E53935", indiceColor),
        strokeOpacity: 0.9,
        strokeWeight:  grosor || 5
    });

    polylinea.setMap(map);
    polylineasActivas.push(polylinea);

    if (path.length > 0) {
        map.setCenter(path[0]);
        map.setZoom(15);
    }
}

// ── Elimina todas las polilíneas activas ─────────────────────────
function limpiarPolilineas() {
    polylineasActivas.forEach(p => p.setMap(null));
    polylineasActivas = [];
}

function limpiarMarcadoresParaderos() {
    marcadoresActivos = marcadoresActivos.filter(marcador => {
        if (marcador.__navgeoParadero) {
            marcador.setMap(null);
            return false;
        }
        return true;
    });
}

// ── Carga los paraderos como marcadores ──────────────────────────
async function cargarParaderos() {
    try {
        const respuesta = await fetch('/api/paraderos');
        if (!respuesta.ok) return;

        paraderosCargados = await respuesta.json();
        limpiarMarcadoresParaderos();

        paraderosCargados.forEach((paradero, index) => {
            const colorParadero = colorMapaAccesible(paradero.color || "#4F46E5", index);

            const markerOptions = {
                position: {
                    lat: parseFloat(paradero.latitud),
                    lng: parseFloat(paradero.longitud)
                },
                map:   map,
                title: paradero.nombre,
                icon: {
                    path:        google.maps.SymbolPath.CIRCLE,
                    scale:       modoDaltonismoActivo() ? 8 : 7,
                    fillColor:   colorParadero,
                    fillOpacity: 1,
                    strokeColor: modoDaltonismoActivo() ? "#000" : "#fff",
                    strokeWeight: modoDaltonismoActivo() ? 3 : 2
                }
            };

            if (modoDaltonismoActivo()) {
                markerOptions.label = {
                    text: "P",
                    color: "#FFFFFF",
                    fontSize: "10px",
                    fontWeight: "700"
                };
            }

            const marcador = new google.maps.Marker(markerOptions);
            marcador.__navgeoParadero = true;

            const infoWindow = new google.maps.InfoWindow({
                content: `
                    <div style="font-family:sans-serif;padding:4px;">
                        <strong>${paradero.nombre}</strong>
                        <p style="font-size:11px;color:#666;margin-top:4px;">
                            📍 Paradero activo
                        </p>
                    </div>
                `
            });

            marcador.addListener("click", () =>
                infoWindow.open(map, marcador));
            marcadoresActivos.push(marcador);
        });

    } catch (e) {
        console.warn('Sin paraderos disponibles:', e);
    }
}

window.addEventListener('navgeo:accessibility-change', () => {
    cargarParaderos();

    if (rutaActivaId) {
        const ruta = rutasCargadas.find(r => r.id === rutaActivaId);
        cambiarSentido(sentidoActual || "ida");
        if (ruta) actualizarPanelInfo(ruta.id, ruta.nombre);
    }
});

// ── Actualiza el panel de información lateral ────────────────────
function actualizarPanelInfo(rutaId, nombre) {
    const ruta           = rutasCargadas.find(r => r.id === rutaId);
    const panelInfo      = document.getElementById("panel-info");
    const infoNombre     = document.getElementById("info-nombre");
    const infoDescripcion= document.getElementById("info-descripcion");

    if (panelInfo)        panelInfo.style.display      = "block";
    if (infoNombre)       infoNombre.textContent        = nombre || '';
    if (infoDescripcion)  infoDescripcion.textContent   =
        ruta ? ruta.descripcion : 'Trazado pendiente de cargar.';
}

// ── Utilidades de UI ─────────────────────────────────────────────
function actualizarBotonesActivos(btnId) {
    document.querySelectorAll(".route-btn").forEach(btn =>
        btn.classList.remove("active"));
    const btnActivo = document.getElementById(btnId);
    if (btnActivo) btnActivo.classList.add("active");
}

function actualizarStatusBar(mensaje) {
    const statusEl = document.getElementById("status-text");
    if (statusEl) {
        statusEl.innerHTML = `<i class="bi bi-reception-4"></i> ${mensaje}`;
    }
}

function activarBotonSentido(btnId) {
    document.querySelectorAll(".sentido-btn").forEach(b =>
        b.classList.remove("active"));
    const btn = document.getElementById(btnId);
    if (btn) btn.classList.add("active");
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
    actualizarStatusBar("Google Maps no está configurado");
}
