package br.com.reconecta.documentacao;

import java.util.Map;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Erro da API. O campo campos é opcional e aparece quando há detalhes de validação por campo.")
public record ConfiguracaoErroDocumentado(
        @Schema(description = "Explicação do erro em português", requiredMode = Schema.RequiredMode.REQUIRED,
                example = "Informação urbana não encontrada.") String mensagem,
        @Schema(description = "Nome do campo e respectiva mensagem de validação", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Map<String, String> campos) {
}
