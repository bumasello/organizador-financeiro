package io.github.bumasello.financas.comandos;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * FormatoMoeda
 */
public class FormatoMoeda {
    public final static Locale LOCALE = Locale.of("pt", "BR");

    public static String formatar(BigDecimal valor) {
        NumberFormat formato = NumberFormat.getCurrencyInstance(LOCALE);

        return formato.format(valor);
    }

    public static BigDecimal ler(String text) {

        String textoLimpo = text.replace("R$", "").replace(".", "").replace(",", ".").strip();

        return new BigDecimal(textoLimpo);

    }

}
