package io.github.bumasello.financas.conversa;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.bumasello.financas.domain.TipoPagamento;

/**
 * RascunhoTest
 */
public class RascunhoTest {

    @Test
    public void testaIdaEVolta() throws Exception {
        RascunhoCartao rascunhoCartao = new RascunhoCartao("Nome", TipoPagamento.CREDITO, null, null);

        ObjectMapper mapper = new ObjectMapper();

        String rascunhoString = mapper.writeValueAsString(rascunhoCartao);

        RascunhoCartao retornoRascunho = mapper.readValue(rascunhoString, RascunhoCartao.class);

        assertEquals(rascunhoCartao, retornoRascunho);
    }

}
