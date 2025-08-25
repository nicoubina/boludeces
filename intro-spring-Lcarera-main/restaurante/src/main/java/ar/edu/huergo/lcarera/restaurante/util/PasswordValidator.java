package ar.edu.huergo.lcarera.restaurante.util;

import java.util.regex.Pattern;

/**
 * Clase utilitaria para la validación de contraseñas.
 * Provee métodos para validar la fortaleza de una contraseña antes de codificarla.
 */
public class PasswordValidator {
    
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{16,}$"
    );
    
    private static final String PASSWORD_MESSAGE = 
        "La contraseña debe tener al menos 16 caracteres, una mayúscula, una minúscula, un número y un carácter especial";
    
    /**
     * Valida si una contraseña cumple con los requisitos de seguridad.
     * 
     * @param password la contraseña a validar
     * @return true si la contraseña es válida, false en caso contrario
     */
    public static boolean isValid(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Valida una contraseña y lanza una excepción si no es válida.
     * 
     * @param password la contraseña a validar
     * @throws IllegalArgumentException si la contraseña no es válida
     */
    public static void validate(String password) {
        if (!isValid(password)) {
            throw new IllegalArgumentException(PASSWORD_MESSAGE);
        }
    }
    
    /**
     * Obtiene el mensaje de validación de la contraseña.
     * 
     * @return el mensaje de validación
     */
    public static String getValidationMessage() {
        return PASSWORD_MESSAGE;
    }
}
