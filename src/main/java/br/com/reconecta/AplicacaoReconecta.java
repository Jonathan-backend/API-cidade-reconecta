package br.com.reconecta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Reconecta ODS 11 Cidades", version = "2.0.0",
        description = """
                API para cadastrar informações e propostas de melhoria urbana.
                Os registros são enviados pelos usuários; não são dados oficiais, minha ideia era criar uma API que esteja em conformidade
                Com o ODS 11 Cidades e Comunidades Sustentáveis, onde o usuário conseguiria cadastrar uma uma "Sugestão de melhoria urbana" .

                ### COMO TESTAR
                1. Abra POST /api/informacoes, clique em Try it out e envie o exemplo.
                2. Copie o identificador retornado para consultar, atualizar ou excluir o registro.
                3. Use GET /api/informacoes para listar ou filtrar por categoria.

                ### Regras gerais
                - Envie os corpos de POST e PUT como application/json.
                - Todos os seis campos de cadastro são obrigatórios; textos não podem conter apenas espaços.
                - Espaços nas extremidades são removidos ao salvar.
                - O identificador é gerado pelo banco e valores enviados no corpo são ignorados.
                - O PUT substitui todos os campos editáveis e exige um cadastro completo.
                - Campos desconhecidos no JSON são rejeitados.
                - As listas são ordenadas por identificador, sem paginação, e podem retornar [].
                - Erros de entrada retornam 400; registros inexistentes retornam 404.
                """))
public class AplicacaoReconecta {
    // O Java exige o nome main para iniciar o programa.
    public static void main(String[] argumentos) {
        ConfigurableApplicationContext contexto = SpringApplication.run(AplicacaoReconecta.class, argumentos);

        String porta = contexto.getEnvironment().getProperty("local.server.port", "8080");
        String caminhoBase = contexto.getEnvironment().getProperty("server.servlet.context-path", "");
        String caminhoSwagger = contexto.getEnvironment().getProperty("springdoc.swagger-ui.path", "/swagger-ui.html");

        System.out.println("Swagger disponível em: http://localhost:" + porta + caminhoBase + caminhoSwagger);
    }
}
