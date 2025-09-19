import java.util.Scanner;
import java.util.Random;

public class App {
    public static void preencheMatrizVazia(int[][] matriz) {
        for (int i = 0; i < matriz[i].length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                matriz[i][j] = 0;
            }
        }
    }

    public static boolean verificaMatriz(int[][] matriz, int x, int y) {
        for (int i = 0; i < matriz[i].length; i++) {
            if (matriz[x][i] == 1 || matriz[i][y] == 1) {
                return true;
            }
        }
        for (int i = 0; i < matriz[i].length; i++) {
            for (int j = 0; j < matriz[j].length; j++) {
                if (matriz[i][j] == 1 && (i - x == j - y || i - x == y - j)) {
                    return true;
                }
            }

        }
        return false;
    }

    public static void printaMatriz(int[][] matriz) {
        for (int i = 0; i < matriz[i].length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.print(matriz[i][j] + " | ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        java.util.Scanner scanner = new Scanner(System.in);
        int num;

        System.out.println("Tamanho Matriz: ");
        num = scanner.nextInt();
        int[][] matriz = new int[num][num];
        preencheMatrizVazia(matriz);
        printaMatriz(matriz);
        verificaMatriz(matriz, num, num);
    }
}
