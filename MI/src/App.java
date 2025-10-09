import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * N-Rainhas via Simulated Annealing com representação por permutação (swap).
 * - Garante uma rainha por coluna (não há posições "inválidas" por coluna).
 * - Mantém contagens de diagonais para custo e atualiza delta de forma eficiente.
 */
public class App {

    public static void preencheMatrizVazia(int[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            Arrays.fill(matriz[i], 0);
        }
    }

    public static void printaMatriz(int[][] matriz) {
        int n = matriz.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(matriz[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void salvaMatrizTxt(int[][] matriz, String nomeArquivo) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            for (int i = 0; i < matriz.length; i++) {
                for (int j = 0; j < matriz[i].length; j++) {
                    writer.write(matriz[i][j] + " ");
                }
                writer.newLine();
            }
            System.out.println("\nResultado salvo em: " + nomeArquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 
     * Calcula custo inicial usando contagem de diagonais (O(n)).
     * Retorna número de pares de rainhas que se atacam (somando ambas diagonais).
     */
    private static int calculaCustoInicial(int[] posicoes, int[] diag1Counts, int[] diag2Counts) {
        int n = posicoes.length;
        int size = 2 * n - 1;
        Arrays.fill(diag1Counts, 0);
        Arrays.fill(diag2Counts, 0);

        int offset = n - 1;
        for (int i = 0; i < n; i++) {
            int d1 = posicoes[i] - i + offset;
            int d2 = posicoes[i] + i;
            diag1Counts[d1]++;
            diag2Counts[d2]++;
        }

        int pares = 0;
        for (int c : diag1Counts) pares += c * (c - 1) / 2;
        for (int c : diag2Counts) pares += c * (c - 1) / 2;
        return pares;
    }

    /**
     * Calcula delta (variação no número de pares atacantes) causado por trocar colunas
     * entre as linhas r1 e r2 — usa apenas as contagens das diagonais afetadas.
     */
    private static int deltaSwap(int[] posicoes, int[] diag1Counts, int[] diag2Counts, int r1, int r2) {
        int n = posicoes.length;
        int offset = n - 1;

        int p1 = posicoes[r1];
        int p2 = posicoes[r2];

        int d1_p1 = p1 - r1 + offset;
        int d2_p1 = p1 + r1;
        int d1_p2 = p2 - r2 + offset;
        int d2_p2 = p2 + r2;

        int d1_p1_new = p2 - r1 + offset; // se p1 passa a ser p2
        int d2_p1_new = p2 + r1;
        int d1_p2_new = p1 - r2 + offset;
        int d2_p2_new = p1 + r2;

        int delta = 0;

        // processa diag1 (indices d1)
        int[] diag1Idxs = {d1_p1, d1_p2, d1_p1_new, d1_p2_new};
        delta += deltaForIndices(diag1Idxs, diag1Counts, new int[]{1, 1}, new int[]{1, 1}); 
        // processa diag2 (indices d2)
        int[] diag2Idxs = {d2_p1, d2_p2, d2_p1_new, d2_p2_new};
        delta += deltaForIndices(diag2Idxs, diag2Counts, new int[]{1, 1}, new int[]{1, 1});

        return delta;
    }

    /**
     * Auxiliar que recebe:
     * - idxs: [old1, old2, new1, new2]
     * - counts: array de contagem da diagonal correspondente
     * - occOld: quantas ocorrências antigas (sempre 1 e 1 neste uso)
     * - occNew: quantas ocorrências novas (sempre 1 e 1 neste uso)
     *
     * Retorna diferença (after - before) na soma de C(count,2) apenas para os índices envolvidos.
     */
    private static int deltaForIndices(int[] idxs, int[] counts, int[] occOld, int[] occNew) {
        // idxs: [oldA, oldB, newA, newB]
        // occOld/occNew not strictly needed aqui (mantidos para clareza/flexibilidade)
        // Construir conjunto único de índices envolvidos
        int[] unique = new int[8];
        int uniqueCount = 0;
        for (int i = 0; i < idxs.length; i++) {
            int v = idxs[i];
            boolean seen = false;
            for (int k = 0; k < uniqueCount; k++) if (unique[k] == v) { seen = true; break; }
            if (!seen) unique[uniqueCount++] = v;
        }

        int before = 0;
        int after = 0;

        for (int k = 0; k < uniqueCount; k++) {
            int idx = unique[k];
            int orig = counts[idx];

            // quantas remoções (0/1/2) - verificar se idx aparece entre oldA, oldB
            int remove = 0;
            if (idx == idxs[0]) remove++;
            if (idx == idxs[1]) remove++;

            // quantas adições
            int add = 0;
            if (idx == idxs[2]) add++;
            if (idx == idxs[3]) add++;

            before += orig * (orig - 1) / 2;
            int afterCount = orig - remove + add;
            after += afterCount * (afterCount - 1) / 2;
        }

        return after - before;
    }

    /**
     * Simulated Annealing usando swap (preserva permutação de colunas).
     * Retorna true se encontrou solução (custo == 0).
     */
    public static boolean simulatedAnnealing(int[][] matriz, int tentativa, double temperaturaInicial, double taxaResfriamento, int maxIteracoes) {
        int n = matriz.length;
        Random random = new Random();

        // representação por permutação: uma rainha em cada coluna
        int[] posicoes = new int[n];
        for (int i = 0; i < n; i++) posicoes[i] = i;
        // embaralha
        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int tmp = posicoes[i];
            posicoes[i] = posicoes[j];
            posicoes[j] = tmp;
        }

        int sizeDiag = 2 * n - 1;
        int[] diag1Counts = new int[sizeDiag];
        int[] diag2Counts = new int[sizeDiag];

        int custoAtual = calculaCustoInicial(posicoes, diag1Counts, diag2Counts);

        double temperatura = temperaturaInicial;
        int iteracao = 0;

        System.out.println("Tentativa " + tentativa + " | Custo inicial: " + custoAtual);

        while (temperatura > 1e-6 && iteracao < maxIteracoes) {
            // move: swap entre duas linhas (r1, r2)
            int r1 = random.nextInt(n);
            int r2 = random.nextInt(n);
            while (r2 == r1) r2 = random.nextInt(n);

            int delta = deltaSwap(posicoes, diag1Counts, diag2Counts, r1, r2);

            if (delta < 0 || random.nextDouble() < Math.exp(-delta / temperatura)) {
                // aceita swap -> atualiza estruturas
                int p1 = posicoes[r1];
                int p2 = posicoes[r2];

                int offset = n - 1;
                // antigos
                int d1_p1 = p1 - r1 + offset;
                int d2_p1 = p1 + r1;
                int d1_p2 = p2 - r2 + offset;
                int d2_p2 = p2 + r2;
                // novos (após swap)
                int d1_p1_new = p2 - r1 + offset;
                int d2_p1_new = p2 + r1;
                int d1_p2_new = p1 - r2 + offset;
                int d2_p2_new = p1 + r2;

                // decrementa antigos
                diag1Counts[d1_p1]--; diag2Counts[d2_p1]--;
                diag1Counts[d1_p2]--; diag2Counts[d2_p2]--;
                // incrementa novos
                diag1Counts[d1_p1_new]++; diag2Counts[d2_p1_new]++;
                diag1Counts[d1_p2_new]++; diag2Counts[d2_p2_new]++;

                // efetiva swap
                posicoes[r1] = p2;
                posicoes[r2] = p1;

                custoAtual += delta;
            }

            temperatura *= taxaResfriamento;
            iteracao++;

            if (custoAtual == 0) break;

            // log ocasional
            if (iteracao % Math.max(1, n * 5) == 0) {
                System.out.println("Iter " + iteracao + " | Custo: " + custoAtual + " | Temp: " + String.format("%.6f", temperatura));
            }
        }

        // monta matriz final a partir de posicoes (uma rainha por linha)
        preencheMatrizVazia(matriz);
        for (int i = 0; i < n; i++) matriz[i][posicoes[i]] = 1;

        System.out.println("Finalizado (tentativa " + tentativa + ") em " + iteracao + " iterações. Custo final: " + custoAtual);
        return custoAtual == 0;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Tamanho da matriz (ex: 8, 15, 24, 100, 1000): ");
        int n = scanner.nextInt();
        scanner.close();

        if (n <= 0) {
            System.out.println("Erro: o tamanho deve ser maior que 0.");
            return;
        }
        if (n == 2 || n == 3) {
            System.out.println("Não existe solução para N = " + n);
            return;
        }

        int[][] matriz = new int[n][n];
        preencheMatrizVazia(matriz);

        double temperaturaInicial = 100000.0;
        double taxaResfriamento = 0.99995;
        int maxIteracoesPorTentativa = n * n * 100; 
        int maxTentativas = 8;

        boolean sucesso = false;
        long inicio = System.currentTimeMillis();

        for (int t = 1; t <= maxTentativas; t++) {
            sucesso = simulatedAnnealing(matriz, t, temperaturaInicial, taxaResfriamento, maxIteracoesPorTentativa);
            if (sucesso) {
                System.out.println("\n Solução perfeita encontrada na tentativa " + t + "!");
                break;
            } else {
                System.out.println("Reiniciando busca...\n");
            }
        }

        long fim = System.currentTimeMillis();
        System.out.println("\nTempo total: " + (fim - inicio) + " ms");

        if (!sucesso) {
            System.out.println("\n Nenhuma solução ideal encontrada após " + maxTentativas + " tentativas.");
        } else {
            System.out.println("\nMatriz resultante:");
            printaMatriz(matriz);
            salvaMatrizTxt(matriz, "resultado.txt");
        }
    }
}
