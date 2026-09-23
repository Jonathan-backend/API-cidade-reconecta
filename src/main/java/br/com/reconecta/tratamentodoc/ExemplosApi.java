package br.com.reconecta.tratamentodoc;


public final class ExemplosApi {
    private ExemplosApi() { }

    public static final String CADASTRO = """
            {"titulo":"Ponto de ônibus sem rampa","descricao":"Exemplo fictício para estudo.",
             "categoria":"ACESSIBILIDADE","cidade":"Cidade Exemplo","bairro":"Centro",
             "propostaMelhoria":"Instalar uma rampa de acesso ao ponto."}
            """;
    public static final String CAMPOS_INVALIDOS = """
            {"mensagem":"Revise os campos enviados.","campos":{"titulo":"O título é obrigatório."}}
            """;
    public static final String JSON_INVALIDO = """
            {"mensagem":"JSON inválido. Confira os campos e use uma categoria válida, conforme o Swagger."}
            """;
    public static final String PARAMETRO_INVALIDO = """
            {"mensagem":"Parâmetro inválido. Use um identificador inteiro positivo e uma categoria válida."}
            """;
    public static final String NAO_ENCONTRADO = """
            {"mensagem":"Informação urbana não encontrada."}
            """;
}
