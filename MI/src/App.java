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
            if (matriz[x][i] == 1 || matriz[i][y] == 1) return true;
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
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.print(matriz[i][j] + " | ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Tamanho Matriz: ");
        int num = scanner.nextInt();

        int[][] matriz = new int[num][num];
        preencheMatrizVazia(matriz);
        printaMatriz(matriz);
    }
}
