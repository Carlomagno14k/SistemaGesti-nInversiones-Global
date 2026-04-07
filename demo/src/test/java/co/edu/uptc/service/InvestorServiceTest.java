package co.edu.uptc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.google.gson.reflect.TypeToken;

import co.edu.uptc.exception.InsufficientCapitalException;
import co.edu.uptc.exception.InvestorNotFoundException;
import co.edu.uptc.model.Investor;
import co.edu.uptc.model.enums.RiskProfile;
import co.edu.uptc.repository.JsonRepository;

class InvestorServiceTest {

    @TempDir
    Path tempDir;

    private InvestorService service;

    @BeforeEach
    void setUp() {
        Type type = new TypeToken<List<Investor>>() {}.getType();
        JsonRepository<Investor> repo = new JsonRepository<>(tempDir.resolve("investors.json").toString(), type);
        service = new InvestorService(repo);
    }

    @Test
    void createInvestor_and_findById_persisted() {
        service.createInvestor("I001", "Ana", "ana@test.com", 5000.0, RiskProfile.MODERATE, Collections.emptyList());

        Investor found = service.findById("I001");
        assertNotNull(found);
        assertEquals("Ana", found.getName());
        assertEquals(5000.0, found.getAvailableCapital(), 0.0001);
        assertEquals(RiskProfile.MODERATE, found.getRiskProfile());
    }

    @Test
    void findById_returnsNullWhenMissing() {
        assertNull(service.findById("I999"));
    }

    @Test
    void updateCapital_reducesAvailableCapital() {
        service.createInvestor("I002", "Luis", "luis@test.com", 1000.0, RiskProfile.CONSERVATIVE, Collections.emptyList());

        service.updateCapital("I002", 250.0);

        assertEquals(750.0, service.getAvailableCapital("I002"), 0.0001);
    }

    @Test
    void updateCapital_throwsInsufficientCapital() {
        service.createInvestor("I003", "Bea", "bea@test.com", 50.0, RiskProfile.AGGRESSIVE, Collections.emptyList());

        assertThrows(InsufficientCapitalException.class, () -> service.updateCapital("I003", 100.0));
    }

    @Test
    void updateCapital_throwsWhenInvestorMissing() {
        assertThrows(InvestorNotFoundException.class, () -> service.updateCapital("I998", 10.0));
    }

    @Test
    void getRiskProfile_returnsProfile() {
        service.createInvestor("I004", "Cris", "cris@test.com", 100.0, RiskProfile.AGGRESSIVE, Collections.emptyList());

        assertEquals(RiskProfile.AGGRESSIVE, service.getRiskProfile("I004"));
    }

    @Test
    void getRiskProfile_throwsWhenMissing() {
        assertThrows(InvestorNotFoundException.class, () -> service.getRiskProfile("I997"));
    }

    @Test
    void getAvailableCapital_throwsWhenMissing() {
        assertThrows(InvestorNotFoundException.class, () -> service.getAvailableCapital("I996"));
    }
}
