function openRegistrationModal() {
    const modal = document.getElementById('registrationModal');
    const modalContent = document.getElementById('modalContent');
    
    // Cargar el contenido de registro.html
    fetch('registro.html')
        .then(response => response.text())
        .then(html => {
            // Extraer solo el body del HTML
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            const bodyContent = doc.body.innerHTML;
            
            modalContent.innerHTML = bodyContent;
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        })
        .catch(error => console.error('Error cargando registro:', error));
}

function closeRegistrationModal() {
    const modal = document.getElementById('registrationModal');
    modal.classList.remove('active');
    document.body.style.overflow = 'auto';
}

// Cerrar modal al hacer clic fuera de él
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('registrationModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeRegistrationModal();
        }
    });
    
    // Cerrar modal con tecla ESC
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            closeRegistrationModal();
        }
    });
});