/*********************************************
 Orienteering Problem - RAH
 IBM ILOG CPLEX OPL
 
 Author: Jose Lindenberg de Andrade
*********************************************/

// quantidade de nós
int N = ...;

// conjunto dos nós
range V = 0..N-1;

// dados
float latitude[V] = ...;
float longitude[V] = ...;
float premio[V] = ...;
float Tmax = ...;
float Autonomia = ...;

// consumo energético por unidade de distância
float consumo = Tmax/Autonomia;

// distância euclidiana
float distancia[i in V][j in V] = 111000 *
    sqrt(
        pow(latitude[i]-latitude[j],2)
        +
        pow(longitude[i]-longitude[j],2)
    );

// custo energético
float custo[i in V][j in V] =
    distancia[i][j] * consumo;

//*******************************
// Variáveis
//*******************************

dvar boolean x[V][V];

dvar int+ u[V];

// *******************************
// Função Objetivo
// *******************************

maximize

sum(i in 1..N-2)
sum(j in 1..N-1)
premio[i] * x[i][j];

// *******************************
// Restrições
// *******************************

subject to {

// sem laços
forall(i in V)
    x[i][i] == 0;

// origem (nó 0)
sum(j in 1..N-1)
    x[0][j] == 1;

// destino (nó N-1)
sum(i in 0..N-2)
    x[i][N-1] == 1;

// origem sem entrada
forall(i in V)
    x[i][0] == 0;

// destino sem saída
forall(j in V)
    x[N-1][j] == 0;

// conservação de fluxo
forall(k in 1..N-2)

    sum(i in V : i!=k)
        x[i][k]

    ==

    sum(j in V : j!=k)
        x[k][j];

// visita no máximo uma vez
forall(k in 1..N-2)

    sum(i in V : i!=k)
        x[i][k]

    <= 1;

// limite de bateria
sum(i in V)
sum(j in V)
custo[i][j] * x[i][j]

<= Tmax;

// MTZ

forall(i in 1..N-1)
    2 <= u[i];

forall(i in 1..N-1)
    u[i] <= N;

forall(i in 1..N-2)
forall(j in 1..N-1)

    if(i!=j)

        u[i] - u[j] + 1

        <=

        (N-1)*(1-x[i][j]);

}

// *******************************
// Pós-processamento: Exibir a sequência de nós
// *******************************
execute DISPLAY_PATH {
    writeln("\n=== SEQUENCIA DE ANIMAIS RESGATADOS ===");
    
    var atual = 0;
    write(atual);
    
    var visitados = 0;
    var total_resgatados = 0;   // Contador de vítimas resgatadas
    
    while (atual != N-1 && visitados < N) {
        var proximoEncontrado = false;
        
        for (var j in V) {
            if (x[atual][j] > 0.5) {
                write(" -> " + j);
                
                // Conta apenas nós intermediários (vítimas)
                if (j != 0 && j != N-1)
                    total_resgatados += 1;
                
                atual = j;
                proximoEncontrado = true;
                break;
            }
        }
        
        if (!proximoEncontrado) {
            writeln(" -> (Caminho interrompido/inválido)");
            break;
        }
        
        visitados++;
    }
    
    writeln("\n==================================");
    writeln("Urgência Total Atendida: ", cplex.getObjValue());
    writeln("Total de resgatados: ", total_resgatados);
    writeln("==================================");
}

execute PARAMS {
    cplex.tilim = 60;
}