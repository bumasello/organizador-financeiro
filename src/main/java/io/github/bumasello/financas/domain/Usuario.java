package io.github.bumasello.financas.domain;

import java.math.BigDecimal;

/**
 * Usuario
 */
public record Usuario(long id, long telegramChatId, String nome, Situacao situacao,
        BigDecimal rendaMensal, Integer diaSalario, BigDecimal aporteMensal, BigDecimal metaReserva,
        EstadoConversa estadoConversa) {

    public Usuario(long id, long telegramChatId, Situacao situacao, BigDecimal aporteMensal, BigDecimal metaReserva) {
        this(id, telegramChatId, null, situacao, null,
                null,
                aporteMensal, metaReserva, EstadoConversa.OCIOSO);
    }

    public Usuario {
        if (situacao == null) {
            throw new IllegalArgumentException("situacao não pode ser nula");
        }

        if (estadoConversa == null) {
            throw new IllegalArgumentException("estadoConversa não pode ser nulo");
        }

        if (nome != null && nome.isBlank()) {
            throw new IllegalArgumentException("nome precisa estar preenchido corretamente");
        }

        if (rendaMensal != null && rendaMensal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("rendaMensal precisa ser positiva ou nula");
        }

        if (aporteMensal == null || aporteMensal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("aporteMensal precisa ser positivo");
        }

        if (metaReserva != null && metaReserva.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("metaReserva precisa ser positiva ou nula");
        }

        if (diaSalario != null && (diaSalario < 1 || diaSalario > 31)) {
            throw new IllegalArgumentException("diaSalario precisa estar entre 1 e 31");
        }
    }

}
