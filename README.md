# Reconecta Cidades

API em Java com Spring Boot, organizada para estudo em nível júnior.

## O que a API faz?

Permite cadastrar e consultar informações sobre problemas urbanos e propostas de melhoria. Cada registro informa o tema, a cidade, o bairro, a situação encontrada e uma sugestão de solução.

**Os dados são cadastrados pelos usuários.** A API não busca dados externos, não compara cidades e não determina automaticamente quais opções são as melhores.

### Categorias

| Valor enviado no JSON | Tema | Exemplo fictício |
| --- | --- | --- |
| MOBILIDADE_URBANA | Mobilidade urbana | Proposta de ciclovia entre bairros |
| TRANSPORTE | Transporte | Falta de conexão entre linhas |
| ACESSIBILIDADE | Acessibilidade | Ponto de ônibus sem rampa |
| SEGURANCA | Segurança | Iluminação insuficiente no ponto |
| MORADIA | Moradia | Bairro residencial distante do transporte |
| MELHORIA_TRANSPORTE_PUBLICO | Melhoria do transporte público | Proposta de aumentar a frequência dos ônibus |

## Como executar

Depois de baixar ou clonar o projeto, abra um terminal na pasta raiz do projeto e execute
apenas este comando:

```powershell
.\executar.bat
```

Esse comando localiza o Java, baixa/usa o Maven Wrapper, compila a aplicação quando
necessário e inicia a API. Também é possível dar dois cliques em `executar.bat` pelo
Explorador de Arquivos.

O único requisito externo é ter o **JDK 17 ou 21** instalado. Não é necessário instalar
Maven, banco H2 ou outra ferramenta. O banco H2 já faz parte do projeto.

Para forçar uma recompilação depois de alterar o código:

```powershell
.\executar.bat -Recompilar
```

O script já muda automaticamente para a pasta correta antes de compilar e iniciar a API.
Depois, abra http://localhost:8080/swagger-ui/index.html.

Para encerrar, pressione **Ctrl+C** no terminal.

Como alternativa, em outro computador com o JDK configurado, você pode executar diretamente:

```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd spring-boot:run
```

No Linux/macOS, use `sh mvnw` no lugar de `.\mvnw.cmd`. O primeiro build precisa de internet para baixar Maven e dependências.

## Primeiro cadastro no Swagger

1. Abra **POST /api/informacoes**.
2. Clique em **Try it out**.
3. Cole o exemplo abaixo.
4. Clique em **Execute**.
5. Copie o `identificador` da resposta para consultar, atualizar ou excluir.

```json
{
  "titulo": "Ponto de ônibus sem rampa",
  "descricao": "Exemplo fictício: o ponto possui um degrau que dificulta o acesso.",
  "categoria": "ACESSIBILIDADE",
  "cidade": "Cidade Exemplo",
  "bairro": "Centro",
  "propostaMelhoria": "Instalar uma rampa e melhorar o acesso à área de embarque."
}
```

O cadastro retorna **201**, o registro criado e seu endereço no cabeçalho `Location`.

## Rotas e formas de uso

O Swagger apresenta **5 operações**, porque a listagem normal e a listagem filtrada
usam o mesmo endpoint `GET /api/informacoes`. Considerando as duas formas de uso
desse endpoint, a API possui **6 rotas/formas de requisição** documentadas abaixo.

| Método | Endereço | O que faz |
| --- | --- | --- |
| POST | /api/informacoes | Cadastra uma informação (201) |
| GET | /api/informacoes | Lista todas as informações (200) |
| GET | /api/informacoes?categoria=ACESSIBILIDADE | Lista uma categoria (200) |
| GET | /api/informacoes/{identificador} | Consulta um registro (200) |
| PUT | /api/informacoes/{identificador} | Atualiza um registro (200) |
| DELETE | /api/informacoes/{identificador} | Exclui um registro (204) |

A listagem retorna um vetor JSON, por exemplo `[]` quando o banco está vazio.
Ela é ordenada por identificador e não possui paginação nesta versão didática.

## Campos e regras

| Campo | Regra |
| --- | --- |
| identificador | Gerado pelo banco; valores enviados pelo cliente são ignorados |
| titulo | Obrigatório; até 120 caracteres |
| descricao | Obrigatório; até 2000 caracteres |
| categoria | Uma das seis categorias da tabela |
| cidade | Obrigatória; até 100 caracteres |
| bairro | Obrigatório; até 100 caracteres |
| propostaMelhoria | Obrigatória; até 2000 caracteres |

Textos não podem conter apenas espaços. Espaços nas extremidades são removidos.
O PUT exige todos os campos obrigatórios, assim como o cadastro.
Dados inválidos retornam **400**; identificadores inexistentes retornam **404**.
Campos desconhecidos no JSON são rejeitados.

Exemplo de erro:
```json
{
  "mensagem": "Revise os campos enviados.",
  "campos": {
    "titulo": "O título é obrigatório."
  }
}
```

## Como entender o código

Os pacotes estão organizados por camadas dentro de `src/main/java/br/com/reconecta`:

```text
br/com/reconecta/
├── AplicacaoReconecta.java
├── controller/
│   └── ControladorInformacao.java
├── service/
│   └── ServicoInformacao.java
├── repository/
│   └── RepositorioInformacao.java
├── model/
│   ├── InformacaoUrbana.java
│   └── Categoria.java
├── tratamentoDeErro/
│   └── TratadorDeErros.java
└── documentacao/
    ├── ConfiguracaoErroDocumentado.java
    └── ExemplosApi.java
```

O `controller` recebe as requisições HTTP e delega ao `service`, que executa as
regras e controla as transações. O `repository` cuida da persistência, e o `model`
contém a entidade e a enumeração de categorias. As dependências são recebidas pelo
construtor: o controller usa o service, e o service usa o repository.
Os pacotes `erro` e `documentacao` concentram o tratamento de erros e os exemplos
e esquemas do Swagger, respectivamente.

O fluxo principal é:

```text
Swagger ou Postman
        ↓
ControladorInformacao — recebe a requisição HTTP
        ↓
ServicoInformacao — organiza cadastro, consulta, atualização e exclusão
        ↓
RepositorioInformacao — acessa o banco
        ↓
H2 — armazena os registros
```

| Arquivo | Responsabilidade |
| --- | --- |
| AplicacaoReconecta.java | Iniciar a aplicação |
| model/InformacaoUrbana.java | Representar os campos e suas validações |
| model/Categoria.java | Definir os temas disponíveis |
| controller/ControladorInformacao.java | Definir as rotas HTTP |
| service/ServicoInformacao.java | Executar as regras e controlar as transações |
| repository/RepositorioInformacao.java | Consultar e salvar no banco |
| erro/TratadorDeErros.java | Retornar mensagens de erro em português |

Classes, métodos próprios e variáveis usam português sem acentos nos identificadores, por exemplo `buscarPorIdentificador`, `obterTitulo` e `definirTitulo`.
Os nomes exigidos pelo Java e pelas bibliotecas permanecem, como `main`, `save`, `findById`, `@Entity` e `@GetMapping`.
A anotação `@JsonProperty` permite que os campos sejam enviados e recebidos em JSON mesmo com os métodos `obter` e `definir`.



## Banco local

Os registros ficam em `data/cidades.mv.db` e permanecem após reiniciar.
Os testes usam um banco independente em memória.
A estrutura é criada pelo Hibernate automaticamente.

## Testes e documentação

```powershell
.\mvnw.cmd verify
```

Os testes cobrem cadastro, consulta, atualização, exclusão, seis categorias, filtro, lista vazia, validações, erros, proteção do identificador e Swagger.
Relatórios de teste automatizados em `target/surefire-reports/br.com.reconecta.TesteinformaçaoUrbana`.

- Swagger: http://localhost:8080/swagger-ui/index.html
- Contrato OpenAPI: http://localhost:8080/v3/api-docs
- Postman: importe `docs/reconecta.postman_collection.json`.
- Springdoc: veja [como acessar e usar a documentação interativa](docs/SPRINGDOC.md).

Na coleção Postman, o primeiro cadastro salva automaticamente a variável `identificador`. Execute as requisições numeradas na ordem.


