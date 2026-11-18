package io.github.bumasello.financas.comandos;

import java.util.List;

/**
 * Resposta
 */
public record Resposta(String texto, List<String> botoes) {

    public Resposta(String texto) {
        this(texto, List.of());
    }

    public Resposta {
        if (texto == null) {
            throw new IllegalArgumentException("texto nao pode ser nulo");
        }
        if (botoes == null) {
            throw new IllegalArgumentException("a lista botoes nao pode ser nula");
        }
    }
}
