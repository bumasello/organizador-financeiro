package io.github.bumasello.financas.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.Optional;

import io.github.bumasello.financas.domain.ResumoMes;
import io.github.bumasello.financas.domain.TipoAjuste;
import io.github.bumasello.financas.domain.Usuario;
import io.github.bumasello.financas.repo.AjusteRepository;
import io.github.bumasello.financas.repo.CompraRepository;
import io.github.bumasello.financas.repo.UsuarioRepository;

/**
 * DisponivelService
 */
public class DisponivelService {

    private final UsuarioRepository usuarioRepository;
    private final CompraRepository compraRepository;
    private final AjusteRepository ajusteRepository;

    public DisponivelService(UsuarioRepository usuarioRepository, CompraRepository compraRepository,
            AjusteRepository ajusteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.compraRepository = compraRepository;
        this.ajusteRepository = ajusteRepository;
    }

    public Optional<ResumoMes> calcular(long chatId, YearMonth mes) throws SQLException {

        if (chatId == 0) {
            throw new IllegalArgumentException("chatId nao pode ser zero");
        }

        if (mes == null) {
            throw new IllegalArgumentException("mes nao pode ser null");
        }

        Optional<Usuario> optionalUsuario = this.usuarioRepository.buscaPorChatId(chatId);

        if (optionalUsuario.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = optionalUsuario.get();

        if (usuario.rendaMensal() == null) {
            return Optional.empty();
        }

        BigDecimal renda = usuario.rendaMensal();
        BigDecimal aporte = usuario.aporteMensal();
        BigDecimal ajustesRenda = this.ajusteRepository.totalMes(usuario.id(), mes, TipoAjuste.RENDA);
        BigDecimal ajustesAporte = this.ajusteRepository.totalMes(usuario.id(), mes, TipoAjuste.APORTE);
        BigDecimal parcelas = this.compraRepository.totalMes(usuario.id(), mes);

        return Optional.of(new ResumoMes(renda, ajustesRenda, aporte, ajustesAporte, parcelas));
    }

}
