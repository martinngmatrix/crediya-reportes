package co.com.bancolombia.model.report.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ConstantsTest {

    @Test
    void approvedStatus_shouldBeAprobado() {
        assertEquals("Aprobado", Constants.APPROVED_STATUS,
                "APPROVED_STATUS debe ser 'Aprobado'");
    }

    @Test
    void approvedStatus_shouldNotBeNullOrEmpty() {
        assertNotNull(Constants.APPROVED_STATUS, "APPROVED_STATUS no debe ser null");
        assertFalse(Constants.APPROVED_STATUS.isEmpty(), "APPROVED_STATUS no debe estar vacío");
    }
}
