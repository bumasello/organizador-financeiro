package io.github.bumasello.financas.conversa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * RascunhoGastoTest
 */
public class RascunhoGastoTest {

    @Test
    public void testaIdaEVolta() throws Exception {
        RascunhoGasto rascunhoGasto = new RascunhoGasto("Uber", new BigDecimal("25.00"), LocalDate.of(2026, 11, 18),
                "Transporte", "Nubank");

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        String rascunhoString = mapper.writeValueAsString(rascunhoGasto);

        RascunhoGasto retornoRascunho = mapper.readValue(rascunhoString, RascunhoGasto.class);

        assertEquals(rascunhoGasto, retornoRascunho);
    }

    @Test
    public void testaComNulos() throws Exception {
        RascunhoGasto rascunhoGasto = new RascunhoGasto("Uber", new BigDecimal("25.00"), null,
                "Transporte", null);

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        String rascunhoString = mapper.writeValueAsString(rascunhoGasto);

        RascunhoGasto retornoRascunho = mapper.readValue(rascunhoString, RascunhoGasto.class);

        assertEquals(rascunhoGasto, retornoRascunho);
    }
}
