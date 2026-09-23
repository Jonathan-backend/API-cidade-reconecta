package br.com.reconecta.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Schema(description = "Informação urbana e proposta de melhoria cadastradas pelo usuário. Exemplos fictícios.")
public class InformacaoUrbana {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Gerado pelo banco. Não precisa ser enviado; valores recebidos no corpo são ignorados.",
            example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long identificador;

    @JsonProperty
    @NotBlank(message = "O título é obrigatório.")
    @Size(max = 120, message = "O título deve ter até 120 caracteres.")
    @Column(nullable = false, length = 120)
    @Schema(description = "Título do problema ou da proposta; não pode conter apenas espaços.",
            example = "Ponto de ônibus sem rampa", requiredMode = Schema.RequiredMode.REQUIRED)
    private String titulo;

    @JsonProperty
    @NotBlank(message = "A descrição é obrigatória.")
    @Size(max = 2000, message = "A descrição deve ter até 2000 caracteres.")
    @Column(nullable = false, length = 2000)
    @Schema(description = "Descrição da situação encontrada; não pode conter apenas espaços.",
            example = "Exemplo fictício para estudo.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String descricao;

    @JsonProperty
    @NotNull(message = "A categoria é obrigatória.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "Tema da informação. Use exatamente um dos seis valores listados.",
            example = "ACESSIBILIDADE", requiredMode = Schema.RequiredMode.REQUIRED)
    private Categoria categoria;

    @JsonProperty
    @NotBlank(message = "A cidade é obrigatória.")
    @Size(max = 100, message = "A cidade deve ter até 100 caracteres.")
    @Column(nullable = false, length = 100)
    @Schema(description = "Cidade relacionada à informação; não pode conter apenas espaços.",
            example = "Cidade Exemplo", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cidade;

    @JsonProperty
    @NotBlank(message = "O bairro é obrigatório.")
    @Size(max = 100, message = "O bairro deve ter até 100 caracteres.")
    @Column(nullable = false, length = 100)
    @Schema(description = "Bairro relacionado à informação; não pode conter apenas espaços.",
            example = "Centro", requiredMode = Schema.RequiredMode.REQUIRED)
    private String bairro;

    @JsonProperty
    @NotBlank(message = "A proposta de melhoria é obrigatória.")
    @Size(max = 2000, message = "A proposta deve ter até 2000 caracteres.")
    @Column(nullable = false, length = 2000)
    @Schema(description = "Solução ou melhoria sugerida; não pode conter apenas espaços.",
            example = "Instalar uma rampa de acesso ao ponto.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String propostaMelhoria;

    public InformacaoUrbana() {
    }

    public Long obterIdentificador() {
        return identificador;
    }

    public String obterTitulo() {
        return titulo;
    }

    public void definirTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String obterDescricao() {
        return descricao;
    }

    public void definirDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Categoria obterCategoria() {
        return categoria;
    }

    public void definirCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String obterCidade() {
        return cidade;
    }

    public void definirCidade(String cidade) {
        this.cidade = cidade;
    }

    public String obterBairro() {
        return bairro;
    }

    public void definirBairro(String bairro) {
        this.bairro = bairro;
    }

    public String obterPropostaMelhoria() {
        return propostaMelhoria;
    }

    public void definirPropostaMelhoria(String propostaMelhoria) {
        this.propostaMelhoria = propostaMelhoria;
    }
}
