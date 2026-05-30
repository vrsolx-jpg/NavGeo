(function () {
    const STORAGE_LANG = 'navgeo-lang';
    const STORAGE_COLORBLIND = 'navgeo-colorblind';

    const translations = {
        es: {
            languageLabel: 'Idioma',
            accessibilityLabel: 'Daltonismo',
            accessibilityOn: 'Modo daltonismo activado',
            accessibilityOff: 'Modo daltonismo desactivado',
            Inicio: 'Inicio',
            Rutas: 'Rutas',
            Nosotros: 'Nosotros',
            Administracion: 'Administración',
            Buscar: 'Buscar',
            'Cerrar Sesion': 'Cerrar Sesión',
            Atras: 'Atrás',
            'REGISTRA RUTAS': 'REGISTRA RUTAS',
            'REGISTRA PARADEROS': 'REGISTRA PARADEROS',
            ELIMINA: 'ELIMINA',
            'CONSULTA ESTADISTICAS': 'CONSULTA ESTADISTICAS',
            EDITA: 'EDITA',
            'AGREGAR EDITOR': 'AGREGAR EDITOR',
            'CONOCE NUESTRAS RUTAS Y PARADEROS': 'CONOCE NUESTRAS RUTAS Y PARADEROS',
            'Transporte Publico NEIVA': 'Transporte Público NEIVA',
            CONTACTANOS: 'CONTÁCTANOS',
            SIGUENOS: 'SÍGUENOS',
            'Mostrando todas las rutas activas': 'Mostrando todas las rutas activas',
            'Por favor llena la siguiente informacion': 'Por favor llena la siguiente información',
            'Nombre de la ruta': 'Nombre de la ruta',
            'Descripcion de la ruta': 'Descripción de la ruta',
            'Color de la ruta': 'Color de la ruta',
            'Sentido del trazado': 'Sentido del trazado',
            Ida: 'Ida',
            Vuelta: 'Vuelta',
            'Guardar ruta': 'Guardar ruta',
            'Nombre del paradero': 'Nombre del paradero',
            'Color del marcador': 'Color del marcador',
            'Buscar lugar en el mapa': 'Buscar lugar en el mapa',
            'Guardar paradero': 'Guardar paradero',
            'Guardar cambios': 'Guardar cambios',
            'ID del elemento': 'ID del elemento',
            'Campo a editar': 'Campo a editar',
            'Nuevo valor': 'Nuevo valor',
            'Nuevo color': 'Nuevo color',
            Eliminar: 'Eliminar',
            Ruta: 'Ruta',
            Paradero: 'Paradero',
            Empleado: 'Empleado',
            'Resumen del sistema': 'Resumen del sistema',
            'Rutas registradas': 'Rutas registradas',
            'Paraderos activos': 'Paraderos activos',
            'Estado del sistema': 'Estado del sistema',
            Activo: 'Activo',
            'Admin Panel': 'Admin Panel',
            'Inicia Sesion': 'Inicia Sesión',
            'Usuario*': 'Usuario*',
            'Contrasena*': 'Contraseña*',
            Siguiente: 'Siguiente',
            'Ingresa tu nombre de usuario': 'Ingresa tu nombre de usuario',
            Bienvenido: 'Bienvenido',
            'Quienes somos': '¿Quiénes somos?',
            AboutNavGeo: 'NavGeo es una plataforma web de acceso libre interactiva, nacida en el proyecto final de Programación web en la Universidad Surcolombiana. Nuestro propósito fundamental es el fácil acceso a la información del transporte público urbano en la ciudad de Neiva. A través de la digitalización, centralizamos los recorridos y paraderos de los buses para ofrecer una herramienta moderna e intuitiva que transforme la experiencia de movilidad de los ciudadanos.'
            ,
            'Registrar editor autorizado': 'Registrar editor autorizado',
            'Nombre completo': 'Nombre completo',
            'Correo autorizado': 'Correo autorizado',
            'Contraseña': 'Contraseña',
            'Guardar editor': 'Guardar editor'
            ,
            'ID del empleado': 'ID del empleado',
            Nombre: 'Nombre',
            Correo: 'Correo',
            'Guardar empleado': 'Guardar empleado'
        },
        en: {
            languageLabel: 'Language',
            accessibilityLabel: 'Color blindness',
            accessibilityOn: 'Color-blind mode on',
            accessibilityOff: 'Color-blind mode off',
            Inicio: 'Home',
            Rutas: 'Routes',
            Nosotros: 'About us',
            Administracion: 'Admin',
            Buscar: 'Search',
            'Cerrar Sesion': 'Log out',
            Atras: 'Back',
            'REGISTRA RUTAS': 'REGISTER ROUTES',
            'REGISTRA PARADEROS': 'REGISTER STOPS',
            ELIMINA: 'DELETE',
            'CONSULTA ESTADISTICAS': 'VIEW STATISTICS',
            EDITA: 'EDIT',
            'AGREGAR EDITOR': 'ADD EDITOR',
            'CONOCE NUESTRAS RUTAS Y PARADEROS': 'DISCOVER OUR ROUTES AND STOPS',
            'Transporte Publico NEIVA': 'NEIVA Public Transport',
            CONTACTANOS: 'CONTACT US',
            SIGUENOS: 'FOLLOW US',
            'Mostrando todas las rutas activas': 'Showing all active routes',
            'Por favor llena la siguiente informacion': 'Please fill in the following information',
            'Nombre de la ruta': 'Route name',
            'Descripcion de la ruta': 'Route description',
            'Color de la ruta': 'Route color',
            'Sentido del trazado': 'Route direction',
            Ida: 'Outbound',
            Vuelta: 'Return',
            'Guardar ruta': 'Save route',
            'Nombre del paradero': 'Stop name',
            'Color del marcador': 'Marker color',
            'Buscar lugar en el mapa': 'Search place on the map',
            'Guardar paradero': 'Save stop',
            'Guardar cambios': 'Save changes',
            'ID del elemento': 'Item ID',
            'Campo a editar': 'Field to edit',
            'Nuevo valor': 'New value',
            'Nuevo color': 'New color',
            Eliminar: 'Delete',
            Ruta: 'Route',
            Paradero: 'Stop',
            Empleado: 'Employee',
            'Resumen del sistema': 'System summary',
            'Rutas registradas': 'Registered routes',
            'Paraderos activos': 'Active stops',
            'Estado del sistema': 'System status',
            Activo: 'Active',
            'Admin Panel': 'Admin Panel',
            'Inicia Sesion': 'Log in',
            'Usuario*': 'User*',
            'Contrasena*': 'Password*',
            Siguiente: 'Next',
            'Ingresa tu nombre de usuario': 'Enter your username',
            Bienvenido: 'Welcome',
            'Quienes somos': 'Who are we?',
            AboutNavGeo: 'NavGeo is a free-access interactive web platform created as the final project for Web Programming at Universidad Surcolombiana. Our main purpose is to make information about urban public transport in Neiva easier to access. Through digitalization, we centralize bus routes and stops to offer a modern, intuitive tool that improves citizens mobility experience.'
            ,
            'Registrar editor autorizado': 'Register authorized editor',
            'Nombre completo': 'Full name',
            'Correo autorizado': 'Authorized email',
            'Contraseña': 'Password',
            'Guardar editor': 'Save editor'
            ,
            'ID del empleado': 'Employee ID',
            Nombre: 'Name',
            Correo: 'Email',
            'Guardar empleado': 'Save employee'
        },
        it: {
            languageLabel: 'Lingua',
            accessibilityLabel: 'Daltonismo',
            accessibilityOn: 'Modalità daltonismo attiva',
            accessibilityOff: 'Modalità daltonismo disattivata',
            Inicio: 'Home',
            Rutas: 'Percorsi',
            Nosotros: 'Chi siamo',
            Administracion: 'Amministrazione',
            Buscar: 'Cerca',
            'Cerrar Sesion': 'Esci',
            Atras: 'Indietro',
            'REGISTRA RUTAS': 'REGISTRA PERCORSI',
            'REGISTRA PARADEROS': 'REGISTRA FERMATE',
            ELIMINA: 'ELIMINA',
            'CONSULTA ESTADISTICAS': 'CONSULTA STATISTICHE',
            EDITA: 'MODIFICA',
            'AGREGAR EDITOR': 'AGGIUNGI EDITOR',
            'CONOCE NUESTRAS RUTAS Y PARADEROS': 'SCOPRI I NOSTRI PERCORSI E FERMATE',
            'Transporte Publico NEIVA': 'Trasporto Pubblico NEIVA',
            CONTACTANOS: 'CONTATTACI',
            SIGUENOS: 'SEGUICI',
            'Mostrando todas las rutas activas': 'Visualizzazione di tutti i percorsi attivi',
            'Por favor llena la siguiente informacion': 'Compila le seguenti informazioni',
            'Nombre de la ruta': 'Nome del percorso',
            'Descripcion de la ruta': 'Descrizione del percorso',
            'Color de la ruta': 'Colore del percorso',
            'Sentido del trazado': 'Direzione del percorso',
            Ida: 'Andata',
            Vuelta: 'Ritorno',
            'Guardar ruta': 'Salva percorso',
            'Nombre del paradero': 'Nome della fermata',
            'Color del marcador': 'Colore del marker',
            'Buscar lugar en el mapa': 'Cerca luogo sulla mappa',
            'Guardar paradero': 'Salva fermata',
            'Guardar cambios': 'Salva modifiche',
            'ID del elemento': "ID dell'elemento",
            'Campo a editar': 'Campo da modificare',
            'Nuevo valor': 'Nuovo valore',
            'Nuevo color': 'Nuovo colore',
            Eliminar: 'Elimina',
            Ruta: 'Percorso',
            Paradero: 'Fermata',
            Empleado: 'Dipendente',
            'Resumen del sistema': 'Riepilogo del sistema',
            'Rutas registradas': 'Percorsi registrati',
            'Paraderos activos': 'Fermate attive',
            'Estado del sistema': 'Stato del sistema',
            Activo: 'Attivo',
            'Admin Panel': 'Pannello Admin',
            'Inicia Sesion': 'Accedi',
            'Usuario*': 'Utente*',
            'Contrasena*': 'Password*',
            Siguiente: 'Avanti',
            'Ingresa tu nombre de usuario': 'Inserisci il tuo nome utente',
            Bienvenido: 'Benvenuto',
            'Quienes somos': 'Chi siamo?',
            AboutNavGeo: 'NavGeo è una piattaforma web interattiva ad accesso libero, nata come progetto finale di Programmazione Web presso l’Universidad Surcolombiana. Il nostro scopo principale è facilitare l’accesso alle informazioni sul trasporto pubblico urbano nella città di Neiva. Attraverso la digitalizzazione, centralizziamo percorsi e fermate degli autobus per offrire uno strumento moderno e intuitivo che migliori l’esperienza di mobilità dei cittadini.'
            ,
            'Registrar editor autorizado': 'Registra editor autorizzato',
            'Nombre completo': 'Nome completo',
            'Correo autorizado': 'Email autorizzata',
            'Contraseña': 'Password',
            'Guardar editor': 'Salva editor'
            ,
            'ID del empleado': 'ID dipendente',
            Nombre: 'Nome',
            Correo: 'Email',
            'Guardar empleado': 'Salva dipendente'
        }
    };

    const aliases = {
        'Administración': 'Administracion',
        'Cerrar Sesión': 'Cerrar Sesion',
        'Atrás': 'Atras',
        'Transporte Público NEIVA': 'Transporte Publico NEIVA',
        'CONTÁCTANOS': 'CONTACTANOS',
        'SÍGUENOS': 'SIGUENOS',
        Contactanos: 'CONTACTANOS',
        Siguenos: 'SIGUENOS',
        'Por favor llena la siguiente información': 'Por favor llena la siguiente informacion',
        'Descripción de la ruta': 'Descripcion de la ruta',
        'Inicia Sesión': 'Inicia Sesion',
        'Contraseña*': 'Contrasena*',
        '¡Bienvenido': 'Bienvenido',
        '¿Quiénes somos?': 'Quienes somos',
        'NavGeo es una plataforma web de acceso libre interactiva, nacida en el proyecto final de Programación web en la Universidad Surcolombiana. Nuestro propósito fundamental es el fácil acceso a la información del transporte público urbano en la ciudad de Neiva. A través de la digitalización, centralizamos los recorridos y paraderos de los buses para ofrecer una herramienta moderna e intuitiva que transforme la experiencia de movilidad de los ciudadanos.': 'AboutNavGeo'
    };

    function keyFor(text) {
        return aliases[text] || text;
    }

    function getLang() {
        return localStorage.getItem(STORAGE_LANG) || 'es';
    }

    function isColorblindEnabled() {
        return localStorage.getItem(STORAGE_COLORBLIND) === 'true';
    }

    function t(key) {
        const lang = getLang();
        return (translations[lang] && translations[lang][key]) || translations.es[key] || key;
    }

    function addControls() {
        if (document.querySelector('.preference-controls')) return;

        const host = document.querySelector('.public-header__nav') || document.querySelector('.admin-header__actions');

        const controls = document.createElement('div');
        controls.className = 'preference-controls';
        controls.innerHTML = `
            <div class="preference-controls__field">
                <label for="navgeo-language-select" data-pref-label="language"></label>
                <select id="navgeo-language-select">
                    <option value="es">Español</option>
                    <option value="en">English</option>
                    <option value="it">Italiano</option>
                </select>
            </div>
            <button type="button" id="navgeo-colorblind-toggle"
                    class="preference-controls__toggle"
                    aria-label="Daltonismo"
                    aria-pressed="false">
                <i class="bi bi-eye"></i>
                <span data-pref-label="accessibility"></span>
            </button>
        `;

        if (host) {
            host.appendChild(controls);
        } else {
            controls.classList.add('preference-controls--floating');
            document.body.prepend(controls);
        }

        const select = controls.querySelector('#navgeo-language-select');
        select.value = getLang();
        select.addEventListener('change', () => {
            localStorage.setItem(STORAGE_LANG, select.value);
            applyPreferences();
        });

        controls.querySelector('#navgeo-colorblind-toggle').addEventListener('click', () => {
            localStorage.setItem(STORAGE_COLORBLIND, String(!isColorblindEnabled()));
            applyPreferences();
            window.dispatchEvent(new CustomEvent('navgeo:accessibility-change', {
                detail: { colorblind: isColorblindEnabled() }
            }));
        });
    }

    function translateKnownTextNodes(root) {
        const walker = document.createTreeWalker(root, NodeFilter.SHOW_TEXT, {
            acceptNode(node) {
                if (!node.nodeValue.trim()) return NodeFilter.FILTER_REJECT;
                if (node.parentElement.closest('script, style')) return NodeFilter.FILTER_REJECT;
                if (node.parentElement.closest('#navgeo-language-select')) return NodeFilter.FILTER_REJECT;
                return NodeFilter.FILTER_ACCEPT;
            }
        });

        const nodes = [];
        while (walker.nextNode()) nodes.push(walker.currentNode);

        nodes.forEach(node => {
            const original = node.__navgeoOriginalText || node.nodeValue;
            node.__navgeoOriginalText = original;
            const trimmed = original.trim();
            const key = keyFor(trimmed);
            if (!translations.es[key]) return;

            node.nodeValue = original.replace(trimmed, t(key));
        });
    }

    function translateExplicitElements() {
        document.querySelectorAll('[data-i18n]').forEach(el => {
            el.textContent = t(el.dataset.i18n);
        });
        document.querySelectorAll('[data-i18n-placeholder]').forEach(el => {
            el.setAttribute('placeholder', t(el.dataset.i18nPlaceholder));
        });
    }

    function translateAttributes() {
        document.querySelectorAll('[placeholder]').forEach(el => {
            const original = el.__navgeoOriginalPlaceholder || el.getAttribute('placeholder');
            el.__navgeoOriginalPlaceholder = original;
            const key = keyFor(original.trim());
            if (translations.es[key]) {
                el.setAttribute('placeholder', t(key));
            }
        });
    }

    function applyPreferences() {
        const lang = getLang();
        document.documentElement.lang = lang;
        document.body.classList.toggle('theme-colorblind', isColorblindEnabled());

        document.querySelectorAll('[data-pref-label="language"]').forEach(el => {
            el.textContent = t('languageLabel');
        });
        document.querySelectorAll('[data-pref-label="accessibility"]').forEach(el => {
            el.textContent = t('accessibilityLabel');
        });

        const toggle = document.getElementById('navgeo-colorblind-toggle');
        if (toggle) {
            const enabled = isColorblindEnabled();
            toggle.classList.toggle('is-active', enabled);
            toggle.setAttribute('aria-pressed', String(enabled));
            toggle.setAttribute('aria-label', t('accessibilityLabel'));
            toggle.removeAttribute('title');
        }

        translateExplicitElements();
        translateAttributes();
        translateKnownTextNodes(document.body);
    }

    window.NavGeoPreferences = {
        getLang,
        isColorblindEnabled,
        translate: t,
        apply: applyPreferences
    };

    document.addEventListener('DOMContentLoaded', () => {
        addControls();
        applyPreferences();
    });
})();
