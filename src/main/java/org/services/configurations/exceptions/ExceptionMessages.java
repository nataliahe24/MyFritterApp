package org.services.configurations.exceptions;

public final class ExceptionMessages {
    
    // Product Exceptions - Mensajes en Español
    public static final String PRODUCT_NOT_FOUND_MESSAGE_ES = "Producto no encontrado con id: ";
    public static final String PRODUCT_CREATION_ERROR_MESSAGE_ES = "Error al crear el producto: ";
    public static final String PRODUCT_UPDATE_ERROR_MESSAGE_ES = "Error al actualizar el producto: ";
    public static final String PRODUCT_DELETE_ERROR_MESSAGE_ES = "Error al eliminar el producto: ";
    
    // Image Exceptions - Mensajes en Español
    public static final String INVALID_IMAGE_FORMAT_MESSAGE_ES = "Solo se permiten archivos de imagen. Tipos soportados: JPEG, PNG, GIF, WEBP";
    public static final String IMAGE_TOO_LARGE_MESSAGE_ES = "El tamaño del archivo no puede exceder 10MB";
    public static final String IMAGE_UPLOAD_ERROR_MESSAGE_ES = "Error al subir la imagen: ";
    public static final String IMAGE_NOT_FOUND_MESSAGE_ES = "Imagen no encontrada con id: ";
    public static final String IMAGE_FILE_EMPTY_MESSAGE_ES = "El archivo de imagen no puede estar vacío";
    public static final String IMAGE_CONTENT_TYPE_NULL_MESSAGE_ES = "El tipo de contenido de la imagen no puede ser nulo";
    
    // User Exceptions - Mensajes en Español
    public static final String USER_NOT_FOUND_MESSAGE_ES = "Usuario no encontrado con id: ";
    public static final String USER_ALREADY_EXISTS = "El usuario ya existe :";
    public static final String INVALID_PASSWORD_MESSAGE_ES = "Contraseña incorrecta";
    public static final String USER_CREATION_ERROR_MESSAGE_ES = "Error al crear el usuario: ";
    public static final String USER_UPDATE_ERROR_MESSAGE_ES = "Error al actualizar el usuario: ";
    public static final String LOGIN_ERROR_MESSAGE_ES = "Email o contraseña incorrectos";
    public static final String INVALID_EMAIL_FORMAT_MESSAGE_ES = "Formato de email inválido";
    public static final String ROLE_NOT_FOUND_MESSAGE_ES = "Rol no encontrado: ";
    
    // Validation Exceptions - Mensajes en Español
    public static final String VALIDATION_ERROR_MESSAGE_ES = "Error de validación: ";
    public static final String REQUIRED_FIELD_MESSAGE_ES = "Campo requerido: ";
    public static final String INVALID_PARAMETER_TYPE_MESSAGE_ES = "Tipo de parámetro inválido";
    
    // Generic Exceptions - Mensajes en Español
    public static final String INTERNAL_SERVER_ERROR_MESSAGE_ES = "Ha ocurrido un error interno del servidor";
    public static final String RESOURCE_NOT_FOUND_MESSAGE_ES = "Recurso no encontrado";
    public static final String BAD_REQUEST_MESSAGE_ES = "Solicitud incorrecta";
    public static final String UNEXPECTED_ERROR_MESSAGE_ES = "Error inesperado";
    
    // Success Messages - Mensajes de Éxito en Español
    public static final String PRODUCT_CREATED_SUCCESS_MESSAGE_ES = "Producto creado exitosamente";
    public static final String PRODUCT_UPDATED_SUCCESS_MESSAGE_ES = "Producto actualizado exitosamente";
    public static final String PRODUCT_DELETED_SUCCESS_MESSAGE_ES = "Producto eliminado exitosamente";
    public static final String USER_CREATED_SUCCESS_MESSAGE_ES = "Usuario creado exitosamente";
    public static final String USER_UPDATED_SUCCESS_MESSAGE_ES = "Usuario actualizado exitosamente";
    public static final String LOGIN_SUCCESS_MESSAGE_ES = "Inicio de sesión exitoso";
    public static final String IMAGE_UPLOADED_SUCCESS_MESSAGE_ES = "Imagen subida exitosamente";

    public static final String ORDER_EMPTY = "Orden no puede estar vacia";
    public static final String ADDRESS_IS_REQUIRED = "La direccion es requerida";
    public static final String PAYMENT_IS_REQUIRED = "Metodo de p-ago es requerido";
    public static final String INVALID_PAYMENT_METHOD = "Metodo de pago invalido";
    public static final String ERROR_TRACKING_CODE = "No se pudo generar un código de seguimiento único";
    public static final String ORDER_NOT_FOUND = "Orden no encontrada";
    

}