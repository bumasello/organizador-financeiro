package io.github.bumasello.financas.domain;

public record MeioPagamento(String nome, TipoPagamento tipo, int diaFechamento, int diaVencimento) {
    public MeioPagamento {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser nulo e não pode estar em branco");
        }

        if (tipo == null) {
            throw new IllegalArgumentException("O tipo não pode ser nulo");
        }

        if (tipo == TipoPagamento.CREDITO) {
            if (diaFechamento <= 0 || diaVencimento <= 0) {
                throw new IllegalArgumentException(
                        "Dia do Fechamento ou Dia do Vencimento não podem ser 0. diaFechamento: " + diaFechamento
                                + " diaVencimento: " + diaVencimento);
            }

            if (diaFechamento > 31 || diaVencimento > 31) {
                throw new IllegalArgumentException(
                        "Dia do Fechamento ou Dia do Vencimento não podem ser maior que 31. diaFechamento: "
                                + diaFechamento
                                + " diaVencimento: " + diaVencimento);
            }
        }

        if (tipo == TipoPagamento.IMEDIATO) {
            if (diaVencimento != 0 || diaFechamento != 0) {
                throw new IllegalArgumentException("Para tipos IMEDIATO, diaVencimento e diaFechamento devem ser 0");
            }
        }
    }
}
