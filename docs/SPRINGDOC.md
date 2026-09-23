# Documentação da API com Springdoc

A documentação interativa é gerada pelo Springdoc OpenAPI a partir das rotas,
validações e anotações Java da API Reconecta Cidades.

OBSERVAÇÃO: Eu escolhi  usa o H2, um banco de dados relacional integrado à aplicação Java.
Escolhi porque ele é simples para esse projeto acadêmico não precisa instalar ou configurar
um servidor de banco separado e funciona bem com Spring Boot e JPA.

Na execução normal, os dados ficam salvos no arquivo data/cidades.mv.db então permanecem após fechar e abrir a API.
Nos testes, usei um H2 temporário em memória separado do banco principal,
para testar cadastros e exclusões sem alterar seus dados.

## Executar e acessar

Na pasta do projeto, execute no PowerShell:

```powershell
.\executar.ps1 -Recompilar
```

Se a API já estiver executando, encerre-a com Ctrl+C antes de recompilar.

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Atalho do Swagger: http://localhost:8080/swagger-ui.html
- Contrato OpenAPI JSON: http://localhost:8080/v3/api-docs
- Contrato OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml

Em outro computador com JDK 17 ou 21 e JAVA_HOME configurado, use
`.\mvnw.cmd verify` e `.\mvnw.cmd spring-boot:run`.

## Usar o Swagger

1. Abra **POST /api/informacoes** e clique em **Try it out**.
2. Confira o exemplo JSON e clique em **Execute**.
3. Verifique o código **201**, o identificador gerado e o cabeçalho **Location**.
4. Informe esse identificador nas operações GET, PUT ou DELETE.
5. Na listagem GET, escolha uma categoria para filtrar ou deixe o filtro sem valor para listar todas.

Os botões executam requisições reais e alteram o banco local quando usados em POST,
PUT e DELETE. Os exemplos são fictícios.

## Conteúdo documentado

O Swagger exibe cinco operações HTTP. A listagem normal e a listagem filtrada são
duas formas da mesma operação `GET /api/informacoes`, pois o filtro `categoria` é
um parâmetro opcional. Por isso, a tabela de uso tem seis linhas, enquanto a
interface exibe cinco blocos de operação.

| Método e rota | Respostas documentadas |
| --- | --- |
| POST /api/informacoes | 201 e 400 |
| GET /api/informacoes | 200 e 400 |
| GET /api/informacoes/{identificador} | 200, 400 e 404 |
| PUT /api/informacoes/{identificador} | 200, 400 e 404 |
| DELETE /api/informacoes/{identificador} | 204, 400 e 404 |

O Swagger apresenta descrições, corpos de exemplo, filtro por categoria, IDs positivos,
campos obrigatórios, limites de tamanho, modelo de resposta e exemplos de erros.
O identificador está marcado como somente leitura. A exclusão está documentada sem
corpo de resposta. A listagem é um vetor, sem paginação, e pode retornar `[]`.

## Onde manter a documentação

- `AplicacaoReconecta.java`: título, versão, apresentação e regras gerais.
- `controller/ControladorInformacao.java`: operações, parâmetros, exemplos de entrada e respostas HTTP.
- `model/InformacaoUrbana.java`: descrições e exemplos dos campos; as validações fornecem os limites.
- `documentacao/ExemplosApi.java`: exemplos JSON fictícios usados pelas anotações.
- `documentacao/ErroDocumentado.java`: esquema dos erros retornados pelo tratador existente.
- `application.properties`: configuração da interface Swagger e geração das respostas.

A propriedade `springdoc.override-with-generic-response=false` evita adicionar
automaticamente respostas genéricas do tratador de exceções a todas as operações.
As respostas de cada rota estão descritas explicitamente nas anotações.

Referência: [documentação oficial do Springdoc para Spring Boot 3](https://springdoc.org/v2/).

## Exportar o contrato para entregar

Com a aplicação em execução:

```powershell
Invoke-WebRequest http://localhost:8080/v3/api-docs -OutFile docs/openapi.json
Invoke-WebRequest http://localhost:8080/v3/api-docs.yaml -OutFile docs/openapi.yaml
```

O contrato também pode ser importado no Postman. Gere novamente os arquivos após
alterar rotas ou modelos para manter a cópia de entrega atualizada.
