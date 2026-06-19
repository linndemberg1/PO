Algoritmo Simplex e Análise de Sensibilidade

Este projeto implementa o Algoritmo Simplex em Java para resolução de um problema de Programação Linear, incluindo também o cálculo do problema dual e análise de sensibilidade.

- Problema Modelado

O problema tratado é uma formulação de maximização dada por:

max z = 3x₁ + 5x₂

sujeito a:

x₁ + x₃ = 4
2x₂ + x₄ = 12
3x₁ + 2x₂ + x₅ = 18
x₁, x₂, x₃, x₄, x₅ ≥ 0

As variáveis x₃, x₄ e x₅ são variáveis de folga adicionadas para transformar as restrições em igualdades.

- Implementação

A implementação foi desenvolvida em Java, utilizando a IDE Eclipse, e baseada na estrutura de tableau simplex.

O algoritmo executa automaticamente todas as etapas do método:

Escolha da variável entrante (coluna pivô)
Escolha da variável saínte (regra da razão mínima)
Pivoteamento do tableau
Atualização da base
Iterações até atingir a solução ótima

- Funcionamento do Algoritmo

O Simplex é executado de forma iterativa até que não existam mais coeficientes negativos na linha da função objetivo.

A cada iteração:

Uma variável entra na base
Outra variável sai da base
O tableau é atualizado
A solução é refinada

- Solução Ótima

Ao final do processo, o algoritmo exibe:

Valor ótimo da função objetivo
Variáveis básicas finais
Estrutura final do tableau


- Problema Dual

Após a otimização, o algoritmo calcula as variáveis duais (preços sombra) utilizando:

yᵀ = c_Bᵀ · B⁻¹

onde:

c_B são os custos das variáveis básicas
B⁻¹ é extraído do tableau final

Esses valores indicam o impacto de cada restrição na função objetivo.

- Análise de Sensibilidade

O projeto também realiza uma análise de sensibilidade para o termo independente b₂.

Essa análise determina o intervalo em que o valor de b₂ pode variar sem alterar a base ótima, garantindo estabilidade da solução.

A condição analisada é:

S · Δb + b* ≥ 0

com base na matriz inversa da base extraída do tableau final.

- Tecnologias Utilizadas
Java
Eclipse IDE
Programação Linear
Método Simplex (Primal)
Dual Simplex (análise pós-otimização)

- Objetivo

O objetivo deste projeto é implementar e compreender de forma prática o funcionamento do Algoritmo Simplex, incluindo:

Resolução de problemas de Programação Linear
Interpretação do problema dual
Análise de sensibilidade da solução
