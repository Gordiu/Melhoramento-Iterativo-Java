# N-Rainhas com Simulated Annealing 👑

Solução para o problema das N-Rainhas usando o algoritmo **Simulated Annealing** (Têmpera Simulada), implementado em Java com representação por permutação e cálculo eficiente de custo via contagem de diagonais.

## 🧠 Sobre o Problema

O problema das N-Rainhas consiste em posicionar N rainhas em um tabuleiro N×N de forma que nenhuma rainha ataque outra — ou seja, sem conflitos em linhas, colunas ou diagonais.

Este projeto resolve o problema para valores arbitrários de N (testado com N = 8, 15, 24, 100, 1000).

## ⚙️ Como Funciona

### Representação
Cada solução é representada como uma **permutação de colunas**: `posicoes[i] = j` significa que a rainha da linha `i` está na coluna `j`. Isso garante, por construção, que não há conflitos em linhas nem em colunas.

### Custo
O custo é o número de **pares de rainhas que se atacam pelas diagonais**. Uma solução válida tem custo 0.

### Algoritmo
- **Movimento**: swap entre duas rainhas de linhas distintas
- **Aceitação**: aceita movimentos que reduzem o custo; aceita movimentos piores com probabilidade `e^(-delta/T)`
- **Resfriamento**: temperatura reduzida a cada iteração por fator `taxaResfriamento`
- **Reinício**: até 8 tentativas independentes caso não encontre solução ideal

### Eficiência
O cálculo de delta (variação de custo) é feito em **O(1)** por iteração, usando contagens de diagonais atualizadas incrementalmente — sem recalcular o tabuleiro inteiro a cada passo.

## 🚀 Como Usar

### Pré-requisitos
- Java 11+

### Compilar e executar

```bash
javac App.java
java App
```

### Entrada
```
Tamanho da matriz (ex: 8, 15, 24, 100, 1000): 8
```

### Saída
```
Tentativa 1 | Custo inicial: 3
Iter 40 | Custo: 1 | Temp: 99.800000
Finalizado (tentativa 1) em 80 iterações. Custo final: 0

Solução perfeita encontrada na tentativa 1!

Matriz resultante:
0 0 0 0 1 0 0 0
0 0 0 0 0 0 1 0
0 0 0 1 0 0 0 0
...

Resultado salvo em: resultado.txt
```

## 📁 Estrutura

```
Melhoramento-Iterativo-Java/
└── App.java    # Implementação completa
```

## 📊 Parâmetros do Algoritmo

| Parâmetro             | Valor padrão     |
|-----------------------|------------------|
| Temperatura inicial   | 100.000          |
| Taxa de resfriamento  | 0,99995          |
| Máx. iterações        | N² × 100         |
| Máx. tentativas       | 8                |

## 🎓 Contexto

Projeto desenvolvido para a disciplina de **Inteligência Artificial** no curso de Bacharelado em Ciência da Computação – UNIFUCAMP.

## 📝 Licença

MIT
