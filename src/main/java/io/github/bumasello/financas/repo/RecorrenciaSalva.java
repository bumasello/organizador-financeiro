package io.github.bumasello.financas.repo;

import io.github.bumasello.financas.domain.Recorrencia;

/**
 * RecorrenciaSalva
 */
public record RecorrenciaSalva(long id, long meioPagamentoId, Recorrencia recorrencia) {

}
