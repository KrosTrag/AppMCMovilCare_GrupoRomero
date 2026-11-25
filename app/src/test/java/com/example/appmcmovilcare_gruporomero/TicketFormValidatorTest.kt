package com.example.appmcmovilcare_gruporomero

import com.example.appmcmovilcare_gruporomero.model.TicketFormValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TicketFormValidatorTest {
    @Test
    fun acceptsCompleteFormAndEmailWithPlusAlias() {
        val result = TicketFormValidator.validar("Ana", "ana+soporte@example.com", "Laptop", "SN-4")

        assertTrue(result.esValido)
        assertEquals(null, result.errorCorreo)
    }

    @Test
    fun reportsMissingFieldsAndInvalidEmail() {
        val result = TicketFormValidator.validar(" ", "correo-invalido", "", "")

        assertEquals("El nombre es obligatorio", result.errorNombre)
        assertEquals("Formato de correo inválido", result.errorCorreo)
        assertEquals("El modelo es obligatorio", result.errorModelo)
        assertEquals("El N° de serie es obligatorio", result.errorSerie)
        assertTrue(!result.esValido)
    }

    @Test
    fun requiresEmail() {
        assertEquals(
            "El correo es obligatorio",
            TicketFormValidator.validar("Ana", "", "Laptop", "SN-4").errorCorreo
        )
    }
}