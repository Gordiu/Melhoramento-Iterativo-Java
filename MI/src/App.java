
import java.util.Random;
import java.util.Scanner;

public class App {

    public static void preencheMatrizVazia(int[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                matriz[i][j] = 0;
            }
        }
    }

    public static boolean verificaMatriz(int[][] matriz, int x, int y) {
        int n = matriz.length;

        // linha e coluna
        for (int i = 0; i < n; i++) {
            if (matriz[x][i] == 1 || matriz[i][y] == 1) {
                return true;
            }
        }

        // diagonais
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matriz[i][j] == 1 && (i - x == j - y || i - x == y - j)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void printaMatriz(int[][] matriz) {
        int n = matriz.length;
        int largura = String.valueOf(n).length(); // largura mínima para cada número

        // para matrizes muito grandes, imprime apenas até certo tamanho
        int limite = Math.min(n, 50); // imprime no máximo 50 colunas/linhas

        for (int i = 0; i < limite; i++) {
            for (int j = 0; j < limite; j++) {
                System.out.printf("%" + largura + "d ", matriz[i][j]);
            }
            if (n > limite) {
                System.out.print(" ..."); // indica que há mais colunas

            }
            System.out.println();
        }
    }

    // colocando rainhas na matriz
    public static boolean insereRainha(int[][] matriz, int col, Random random) {
        int n = matriz.length;

        // Caso base: todas as rainhas foram colocadas
        if (col >= n) {
            return true;
        }

        boolean[] usadas = new boolean[n]; // marca as linhas já testadas
        int tentativas = 0;

        while (tentativas < n) {
            int i = random.nextInt(n); // escolhe uma linha aleatória

            if (!usadas[i]) { // só tenta se ainda não testou essa linha
                usadas[i] = true;
                tentativas++;

                if (!verificaMatriz(matriz, i, col)) {
                    matriz[i][col] = 1;

                    // monitora temperatura
                    monitoraTemperatura(matriz);

                    if (insereRainha(matriz, col + 1, random)) {
                        return true;
                    }

                    // backtracking
                    matriz[i][col] = 0;
                }
            }
        }
        return false;
    }

    public static void monitoraTemperatura(int[][] matriz) {
        int n = matriz.length;
        int conflitos = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matriz[i][j] == 1) {
                    // contar ameaças
                    conflitos += contaConflitos(matriz, i, j);
                }
            }
        }

        System.out.println("Temperatura atual: " + conflitos);
        printaMatriz(matriz);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread was interrupted.");
        }
        System.out.println("\n\n\n");
    }

// Conta os conflitos de uma rainha na posição (x, y)
    public static int contaConflitos(int[][] matriz, int x, int y) {
        int n = matriz.length;
        int conflitos = 0;

        // linha
        for (int j = 0; j < n; j++) {
            if (j != y && matriz[x][j] == 1) {
                conflitos++;
            }
        }

        // coluna
        for (int i = 0; i < n; i++) {
            if (i != x && matriz[i][y] == 1) {
                conflitos++;
            }
        }

        // diagonais
        for (int i = -n; i < n; i++) {
            if (x + i >= 0 && x + i < n && y + i >= 0 && y + i < n) {
                if (!(i == 0) && matriz[x + i][y + i] == 1) {
                    conflitos++;
                }
            }
            if (x + i >= 0 && x + i < n && y - i >= 0 && y - i < n) {
                if (!(i == 0) && matriz[x + i][y - i] == 1) {
                    conflitos++;
                }
            }
        }

        return conflitos;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Tamanho Matriz: ");
        int num = scanner.nextInt();

        int[][] matriz = new int[num][num];
        preencheMatrizVazia(matriz);

        Random random = new Random(50);
        if (insereRainha(matriz, 0, random)) {
            printaMatriz(matriz);
        } else {
            System.out.println("Não foi possível posicionar as rainhas.");
        }
    }
}
