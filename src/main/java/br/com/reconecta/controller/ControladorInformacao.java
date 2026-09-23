package br.com.reconecta.controller;

import br.com.reconecta.model.Categoria;
import br.com.reconecta.model.InformacaoUrbana;
import br.com.reconecta.service.ServicoInformacao;

import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import br.com.reconecta.documentacao.ConfiguracaoErroDocumentado;
import br.com.reconecta.documentacao.ExemplosApi;

@RestController
@RequestMapping("/api/informacoes")
@Tag(name = "Informações urbanas", description = "Informações e propostas cadastradas pelos usuários")
public class ControladorInformacao {
    private final ServicoInformacao servico;

    public ControladorInformacao(ServicoInformacao servico) {
        this.servico = servico;
    }

    @PostMapping
    @Operation(summary = "Cadastrar uma informação e sua proposta de melhoria",
            description = "Cria um registro com os seis campos obrigatórios. Retorna o registro com o ID gerado e sua URL no cabeçalho Location.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                    description = "Dados completos da informação urbana, sem necessidade de informar o identificador.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InformacaoUrbana.class),
                            examples = @ExampleObject(name = "Cadastro válido", value = ExemplosApi.CADASTRO))))
    @ApiResponse(responseCode = "201", description = "Informação cadastrada",
            headers = @Header(name = "Location", description = "URL do registro criado",
                    schema = @Schema(type = "string", example = "http://localhost:8080/api/informacoes/1")),
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = InformacaoUrbana.class)))
    @ApiResponse(responseCode = "400", description = "Corpo ausente, campos inválidos, JSON malformado, campo desconhecido ou categoria inválida",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfiguracaoErroDocumentado.class),
                    examples = {@ExampleObject(name = "Campos inválidos", value = ExemplosApi.CAMPOS_INVALIDOS),
                            @ExampleObject(name = "JSON ou categoria inválida", value = ExemplosApi.JSON_INVALIDO)}))
    public ResponseEntity<InformacaoUrbana> cadastrar(@RequestBody @Valid InformacaoUrbana dados) {
        InformacaoUrbana informacao = servico.cadastrar(dados);
        URI endereco = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{identificador}").buildAndExpand(informacao.obterIdentificador()).toUri();
        return ResponseEntity.created(endereco).body(informacao);
    }

    @GetMapping
    @Operation(summary = "Listar informações, com filtro opcional por categoria",
            description = "Retorna um vetor ordenado por identificador, sem paginação. Sem categoria, lista todos os registros. Sem resultados, retorna [].")
    @ApiResponse(responseCode = "200", description = "Lista de informações, possivelmente vazia",
            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = InformacaoUrbana.class))))
    @ApiResponse(responseCode = "400", description = "Categoria de filtro inválida",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfiguracaoErroDocumentado.class),
                    examples = @ExampleObject(value = ExemplosApi.PARAMETRO_INVALIDO)))
    public List<InformacaoUrbana> listar(
            @Parameter(description = "Filtro opcional. Use o valor da categoria em letras maiúsculas.", example = "ACESSIBILIDADE")
            @RequestParam(required = false) Categoria categoria) {
        return servico.listar(categoria);
    }

    @GetMapping("/{identificador}")
    @Operation(summary = "Buscar uma informação pelo identificador",
            description = "Consulta o registro correspondente ao identificador retornado no cadastro.")
    @ApiResponse(responseCode = "200", description = "Informação encontrada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = InformacaoUrbana.class)))
    @ApiResponse(responseCode = "400", description = "Identificador inválido",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfiguracaoErroDocumentado.class),
                    examples = @ExampleObject(value = ExemplosApi.PARAMETRO_INVALIDO)))
    @ApiResponse(responseCode = "404", description = "Informação não encontrada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfiguracaoErroDocumentado.class),
                    examples = @ExampleObject(value = ExemplosApi.NAO_ENCONTRADO)))
    public InformacaoUrbana buscarPorIdentificador(
            @Parameter(description = "Identificador inteiro positivo do registro", example = "1", schema = @Schema(type = "integer", format = "int64", minimum = "1"))
            @PathVariable @Positive Long identificador) {
        return servico.buscarPorIdentificador(identificador);
    }

    @PutMapping("/{identificador}")
    @Operation(summary = "Atualizar todos os campos de uma informação",
            description = "Substitui os seis campos editáveis de um registro existente. Todos são obrigatórios. O ID do corpo é ignorado; prevalece o ID da URL. Uma entrada inválida não altera os dados salvos.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
                    description = "Todos os campos editáveis, inclusive aqueles que permanecerão iguais.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = InformacaoUrbana.class),
                            examples = @ExampleObject(name = "Atualização completa", value = ExemplosApi.CADASTRO))))
    @ApiResponse(responseCode = "200", description = "Informação atualizada",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = InformacaoUrbana.class)))
    @ApiResponse(responseCode = "400", description = "Identificador, campos ou JSON inválidos. A validação do PUT pode retornar a mensagem genérica de parâmetro inválido.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfiguracaoErroDocumentado.class),
                    examples = {@ExampleObject(name = "Parâmetro ou campo inválido", value = ExemplosApi.PARAMETRO_INVALIDO),
                            @ExampleObject(name = "JSON inválido", value = ExemplosApi.JSON_INVALIDO)}))
    @ApiResponse(responseCode = "404", description = "Informação não encontrada; nenhum registro é criado pelo PUT",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfiguracaoErroDocumentado.class),
                    examples = @ExampleObject(value = ExemplosApi.NAO_ENCONTRADO)))
    public InformacaoUrbana atualizar(
                                     @Parameter(description = "Identificador inteiro positivo do registro", example = "1", schema = @Schema(type = "integer", format = "int64", minimum = "1"))
                                     @PathVariable @Positive Long identificador,
                                     @RequestBody @Valid InformacaoUrbana dados) {
        return servico.atualizar(identificador, dados);
    }

    @DeleteMapping("/{identificador}")
    @Operation(summary = "Excluir uma informação",
            description = "Remove o registro existente. Em caso de sucesso, retorna 204 sem corpo. Uma nova consulta ao ID excluído retorna 404.")
    @ApiResponse(responseCode = "204", description = "Informação excluída; resposta sem corpo", content = @Content)
    @ApiResponse(responseCode = "400", description = "Identificador inválido",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfiguracaoErroDocumentado.class),
                    examples = @ExampleObject(value = ExemplosApi.PARAMETRO_INVALIDO)))
    @ApiResponse(responseCode = "404", description = "Informação não encontrada ou já excluída",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfiguracaoErroDocumentado.class),
                    examples = @ExampleObject(value = ExemplosApi.NAO_ENCONTRADO)))
    public ResponseEntity<Void> excluir(
            @Parameter(description = "Identificador inteiro positivo do registro", example = "1", schema = @Schema(type = "integer", format = "int64", minimum = "1"))
            @PathVariable @Positive Long identificador) {
        servico.excluir(identificador);
        return ResponseEntity.noContent().build();
    }
}

