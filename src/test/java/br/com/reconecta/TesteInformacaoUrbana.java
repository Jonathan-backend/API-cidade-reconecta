package br.com.reconecta;

import br.com.reconecta.model.Categoria;
import br.com.reconecta.repository.RepositorioInformacao;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TesteInformacaoUrbana {
    @Autowired MockMvc cliente;
    @Autowired ObjectMapper conversor;
    @Autowired RepositorioInformacao repositorio;

    private static final String DADOS = """
            {"titulo":"Ponto de ônibus sem rampa","descricao":"Exemplo fictício para estudo.",
             "categoria":"ACESSIBILIDADE","cidade":"Cidade Exemplo","bairro":"Centro",
             "propostaMelhoria":"Instalar uma rampa de acesso ao ponto."}
            """;

    @BeforeEach
    void limparBanco() {
        repositorio.deleteAll();
    }

    private long cadastrarExemplo() throws Exception {
        MvcResult resposta = cliente.perform(post("/api/informacoes")
                        .contentType(MediaType.APPLICATION_JSON).content(DADOS))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();
        return conversor.readTree(resposta.getResponse().getContentAsString())
                .get("identificador").asLong();
    }

    @Test
    void deveCadastrarConsultarAtualizarEExcluir() throws Exception {
        long identificador = cadastrarExemplo();
        cliente.perform(get("/api/informacoes/{identificador}", identificador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoria").value("ACESSIBILIDADE"))
                .andExpect(jsonPath("$.cidade").value("Cidade Exemplo"));
        cliente.perform(put("/api/informacoes/{identificador}", identificador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(DADOS.replace("Centro", "Vila Nova")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bairro").value("Vila Nova"))
                .andExpect(jsonPath("$.identificador").value(identificador));
        cliente.perform(delete("/api/informacoes/{identificador}", identificador))
                .andExpect(status().isNoContent());
        cliente.perform(get("/api/informacoes/{identificador}", identificador))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Informação urbana não encontrada."));
    }

    @Test
    void deveAceitarTodasAsCategoriasEFiltrar() throws Exception {
        for (Categoria categoria : Categoria.values()) {
            cliente.perform(post("/api/informacoes").contentType(MediaType.APPLICATION_JSON)
                            .content(DADOS.replace("ACESSIBILIDADE", categoria.name())))
                    .andExpect(status().isCreated());
        }
        cliente.perform(get("/api/informacoes")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6));
        for (Categoria categoria : Categoria.values()) {
            cliente.perform(get("/api/informacoes").param("categoria", categoria.name()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].categoria").value(categoria.name()));
        }
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaInformacoes() throws Exception {
        cliente.perform(get("/api/informacoes")).andExpect(status().isOk())
                .andExpect(content().json("[]"));
        cliente.perform(get("/api/informacoes?categoria=MORADIA"))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void deveValidarCamposObrigatoriosETamanhos() throws Exception {
        cliente.perform(post("/api/informacoes").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.titulo").exists())
                .andExpect(jsonPath("$.campos.descricao").exists())
                .andExpect(jsonPath("$.campos.categoria").exists())
                .andExpect(jsonPath("$.campos.cidade").exists())
                .andExpect(jsonPath("$.campos.bairro").exists())
                .andExpect(jsonPath("$.campos.propostaMelhoria").exists());
        cliente.perform(post("/api/informacoes").contentType(MediaType.APPLICATION_JSON)
                        .content(DADOS.replace("Ponto de ônibus sem rampa", " ")))
                .andExpect(status().isBadRequest());
        cliente.perform(post("/api/informacoes").contentType(MediaType.APPLICATION_JSON)
                        .content(DADOS.replace("Ponto de ônibus sem rampa", "a".repeat(121))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRejeitarJsonCategoriaEParametrosInvalidos() throws Exception {
        for (String corpo : new String[]{"{", DADOS.replace("ACESSIBILIDADE", "INVALIDA"),
                DADOS.replace("\"bairro\":", "\"campoDesconhecido\":")}) {
            cliente.perform(post("/api/informacoes").contentType(MediaType.APPLICATION_JSON).content(corpo))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.mensagem").exists());
        }
        for (String rota : new String[]{"/0", "/-1", "/abc", "?categoria=INVALIDA"}) {
            cliente.perform(get("/api/informacoes" + rota)).andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensagem").exists());
        }
    }

    @Test
    void deveRetornar404AoAlterarOuExcluirInformacaoInexistente() throws Exception {
        cliente.perform(put("/api/informacoes/999999").contentType(MediaType.APPLICATION_JSON).content(DADOS))
                .andExpect(status().isNotFound());
        cliente.perform(delete("/api/informacoes/999999")).andExpect(status().isNotFound());
    }

    @Test
    void devePreservarDadosQuandoAtualizacaoForInvalida() throws Exception {
        long identificador = cadastrarExemplo();
        cliente.perform(put("/api/informacoes/{identificador}", identificador)
                        .contentType(MediaType.APPLICATION_JSON).content(DADOS.replace("Centro", "")))
                .andExpect(status().isBadRequest());
        cliente.perform(get("/api/informacoes/{identificador}", identificador))
                .andExpect(jsonPath("$.bairro").value("Centro"));
    }

    @Test
    void deveIgnorarIdentificadorEnviadoPeloCliente() throws Exception {
        long identificador = cadastrarExemplo();
        String corpo = DADOS.replace("{", "{\"identificador\":999999,");
        cliente.perform(put("/api/informacoes/{identificador}", identificador)
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identificador").value(identificador));
        cliente.perform(post("/api/informacoes").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated());
        cliente.perform(get("/api/informacoes/999999")).andExpect(status().isNotFound());
    }

    @Test
    void deveExporSwaggerComNovoTemaESemRotasAntigas() throws Exception {
        cliente.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Reconecta ODS 11 Cidades"))
                .andExpect(jsonPath("$.paths['/api/informacoes'].post").exists())
                .andExpect(jsonPath("$.paths['/api/tarefas']").doesNotExist());
        cliente.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
        cliente.perform(get("/api/tarefas")).andExpect(status().isNotFound());
    }
}
