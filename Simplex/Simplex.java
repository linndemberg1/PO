package pck;

public class Simplex {

    // Dados do problema organizados para o Tableau
    // Colunas: x1, x2, x3, x4, x5, b
    private double[][] tableau = {
        {1, 0, 1, 0, 0, 4},  // x1 + x3 = 4
        {0, 2, 0, 1, 0, 12}, // 2x2 + x4 = 12
        {3, 2, 0, 0, 1, 18}, // 3x1 + 2x2 + x5 = 18
        {-3, -5, 0, 0, 0, 0} // z - 3x1 - 5x2 = 0
    };

    // Custos originais da função objetivo (c) para a fórmula do dual
    private double[] custosOriginais = {3, 5, 0, 0, 0};

    // Mapeamento correto dos índices (0-based): x3=2, x4=3, x5=4
    private int[] variaveisNaBase = {2, 3, 4}; 

    private int iteracao = 0;

    public void mostrarProblema() {
        System.out.println("========================================");
        System.out.println("       PROBLEMA NA FORMA PADRÃO");
        System.out.println("========================================");
        System.out.println("Max z = 3x1 + 5x2");
        System.out.println("Sujeito a:");
        System.out.println("  1x1 + 0x2 + 1x3 + 0x4 + 0x5 = 4");
        System.out.println("  0x1 + 2x2 + 0x3 + 1x4 + 0x5 = 12");
        System.out.println("  3x1 + 2x2 + 0x3 + 0x4 + 1x5 = 18");
        System.out.println("  x1, x2, x3, x4, x5 >= 0");
        System.out.println("========================================\n");
    }

    public void resolver() {
        mostrarProblema();
        
        System.out.println("Gerando Tabela Inicial...");
        imprimirTableau();

        while (true) {
            int colunaPivo = encontrarColunaPivo();
            if (colunaPivo == -1) break; 

            int linhaPivo = encontrarLinhaPivo(colunaPivo);
            if (linhaPivo == -1) throw new RuntimeException("Problema não é limitado.");

            // Atualiza qual variável entrou na base nessa linha específica
            variaveisNaBase[linhaPivo] = colunaPivo;

            iteracao++;
            executarPivoteamento(linhaPivo, colunaPivo);
            
            System.out.println("\n>>> PASSO " + iteracao + " <<<");
            System.out.println("Variável x" + (colunaPivo + 1) + " entra na base e retira x" + (variaveisNaBase[linhaPivo] + 1));
            imprimirTableau();
        }
        
        System.out.println("\n========================================");
        System.out.println("SOLUÇÃO ÓTIMA ALCANÇADA!");
        System.out.printf("Valor máximo de Z: %.2f\n", tableau[tableau.length - 1][tableau[0].length - 1]);
        System.out.println("========================================");

        // Executa os cálculos pós-otimização
        calcularEExibirDual();
        analiseSensibilidadeMatricial();
    }

    private int encontrarColunaPivo() {
        int indexNegativo = -1;
        double menorValor = -0.000001; 
        int linhaZ = tableau.length - 1;

        for (int j = 0; j < tableau[0].length - 1; j++) {
            if (tableau[linhaZ][j] < menorValor) {
                menorValor = tableau[linhaZ][j];
                indexNegativo = j;
            }
        }
        return indexNegativo;
    }

    private int encontrarLinhaPivo(int colunaPivo) {
        int linhaPivo = -1;
        double menorRazao = Double.MAX_VALUE;

        for (int i = 0; i < tableau.length - 1; i++) {
            if (tableau[i][colunaPivo] > 1e-6) { // Evita divisão por zero ou números extremamente pequenos
                double razao = tableau[i][tableau[0].length - 1] / tableau[i][colunaPivo];
                if (razao < menorRazao) {
                    menorRazao = razao;
                    linhaPivo = i;
                }
            }
        }
        return linhaPivo;
    }

    private void executarPivoteamento(int linhaPivo, int colunaPivo) {
        double valorPivo = tableau[linhaPivo][colunaPivo];

        for (int j = 0; j < tableau[0].length; j++) {
            tableau[linhaPivo][j] /= valorPivo;
        }

        for (int i = 0; i < tableau.length; i++) {
            if (i != linhaPivo) {
                double fator = tableau[i][colunaPivo];
                for (int j = 0; j < tableau[0].length; j++) {
                    tableau[i][j] -= (fator * tableau[linhaPivo][j]);
                }
            }
        }
    }

    private void calcularEExibirDual() {
        int numRestricoes = tableau.length - 1; 
        
        double[] c_B = new double[numRestricoes];
        System.out.print("\nVetor c_B^T (Custos das variáveis na base): [");
        for (int i = 0; i < numRestricoes; i++) {
            c_B[i] = custosOriginais[variaveisNaBase[i]];
            System.out.printf(" c%d=%.1f ", variaveisNaBase[i] + 1, c_B[i]);
        }
        System.out.println("]");

        double[][] B_inversa = new double[numRestricoes][numRestricoes];
        int colunaInicialFolga = 2; 
        
        System.out.println("\nMatriz B^-1 extraída do Tableau (Colunas de x3, x4, x5):");
        for (int i = 0; i < numRestricoes; i++) {
            for (int j = 0; j < numRestricoes; j++) {
                B_inversa[i][j] = tableau[i][colunaInicialFolga + j];
                System.out.printf("%8.2f", B_inversa[i][j]);
            }
            System.out.println();
        }

        double[] y = new double[numRestricoes];
        for (int j = 0; j < numRestricoes; j++) { 
            double soma = 0;
            for (int i = 0; i < numRestricoes; i++) { 
                soma += c_B[i] * B_inversa[i][j];
            }
            y[j] = soma;
        }

        System.out.println("\n========================================");
        System.out.println("        VALORES DAS VARIÁVEIS DUAIS");
        System.out.println("         Fórmula: y^T = c_B^T * B^-1");
        System.out.println("========================================");
        for (int i = 0; i < y.length; i++) {
            System.out.printf("y%d (Sombra da Restrição %d) = %.2f\n", (i + 1), (i + 1), y[i]);
        }
        System.out.println("========================================");
    }

    private void analiseSensibilidadeMatricial() {
        System.out.println("\n========================================");
        System.out.println(" ANÁLISE DE SENSIBILIDADE (RANGE DE b2)");
        System.out.println("========================================");

        int colFolgaInicio = 2; 
        int numRestricoes = 3;

        double[][] S = new double[numRestricoes][numRestricoes];
        double[] b_atual = new double[numRestricoes];

        for (int i = 0; i < numRestricoes; i++) {
            b_atual[i] = tableau[i][tableau[0].length - 1]; 
            for (int j = 0; j < numRestricoes; j++) {
                S[i][j] = tableau[i][colFolgaInicio + j]; 
            }
        }

        System.out.println("Matriz S (Inversa da Base B^-1) extraída:");
        for (int i = 0; i < numRestricoes; i++) {
            System.out.printf(" [ %.4f %.4f %.4f ]%n", S[i][0], S[i][1], S[i][2]);
        }
        System.out.printf("Vetor b* (Solução Atual): [ %.1f, %.1f, %.1f ]^T%n%n", b_atual[0], b_atual[1], b_atual[2]);

        double maxDeltaB2 = Double.MAX_VALUE;
        double minDeltaB2 = -Double.MAX_VALUE;

        System.out.println("Avaliando as inequações (S * Δb + b* >= 0):");
        for (int i = 0; i < numRestricoes; i++) {
            double coefDelta = S[i][1]; 
            double termoIndep = b_atual[i];

            System.out.printf(" Restrição %d: (%.4f) * Δb2 + %.2f >= 0", (i + 1), coefDelta, termoIndep);

            if (Math.abs(coefDelta) > 1e-6) {
                double limite = -termoIndep / coefDelta;
                if (coefDelta > 0) {
                    System.out.printf(" => Δb2 >= %.2f%n", limite);
                    if (limite > minDeltaB2) minDeltaB2 = limite;
                } else {
                    System.out.printf(" => Δb2 <= %.2f%n", limite);
                    if (limite < maxDeltaB2) maxDeltaB2 = limite;
                }
            } else {
                System.out.println(" => Sempre válido (não depende de Δb2)");
            }
        }

        double b2_original = 12.0;
        
        System.out.println("\n----------------------------------------");
        System.out.println(" CONCLUSÃO DOS LIMITES");
        System.out.println("----------------------------------------");
        System.out.printf("Intervalo de variação: %.2f <= Δb2 <= %.2f%n", minDeltaB2, maxDeltaB2);
        
        if (maxDeltaB2 == Double.MAX_VALUE) {
            System.out.println("Aumento Permitido: Infinito");
            System.out.printf("Range permitido para b2 original (%.1f): %.2f <= b2 <= Infinito%n", b2_original, (b2_original + minDeltaB2));
        } else {
            double allowableDecrease = (minDeltaB2 == -Double.MAX_VALUE) ? Double.POSITIVE_INFINITY : Math.abs(minDeltaB2);
            double allowableIncrease = maxDeltaB2;
            System.out.printf("Aumento Permitido: %.2f%n", allowableIncrease);
            System.out.printf("Redução Permitida: %.2f%n", allowableDecrease);
            System.out.printf("Range permitido para b2 original (%.1f): %.2f <= b2 <= %.2f%n", b2_original, (b2_original - allowableDecrease), (b2_original + allowableIncrease));
        }
        System.out.println("========================================");
    }

    private void imprimirTableau() {
        System.out.println("--------------------------------------------------");
        System.out.println("    x1      x2      x3      x4      x5      b");
        for (int i = 0; i < tableau.length; i++) {
            for (int j = 0; j < tableau[i].length; j++) {
                System.out.printf("%8.2f", tableau[i][j]);
            }
            if (i == tableau.length - 1) System.out.print("  [Linha Z]");
            else System.out.print("  [Rest. " + (i+1) + " (x" + (variaveisNaBase[i]+1) + ")]");
            System.out.println();
        }
        System.out.println("--------------------------------------------------");
    }

    public static void main(String[] args) {
        Simplex simplex = new Simplex();
        simplex.resolver();
    }
}
