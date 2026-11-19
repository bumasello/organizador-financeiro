package io.github.bumasello.financas.conversa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.github.bumasello.financas.domain.TipoPagamento;

/**
 * RascunhoCartaoTest
 */
public class RascunhoCartaoTest {

    @Test
    public void testaIdaEVolta() throws Exception {
        RascunhoCartao rascunhoCartao = new RascunhoCartao("Nome", TipoPagamento.CREDITO, null, null);

        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

        String rascunhoString = mapper.writeValueAsString(rascunhoCartao);

        RascunhoCartao retornoRascunho = mapper.readValue(rascunhoString, RascunhoCartao.class);

        assertEquals(rascunhoCartao, retornoRascunho);
    }

}
