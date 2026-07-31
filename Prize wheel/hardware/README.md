# Hardware

Documentação de integração com a máquina de vending.

## Conteúdo

| Ficheiro | Descrição |
|----------|-----------|
| [`vmc-protocol.md`](vmc-protocol.md) | Protocolo VMC Reyeah (implementado na app) |
| `uart/` | *(futuro)* PDFs do fabricante |
| `mdb/` | *(futuro)* Protocolo MDB |
| `schematics/` | *(futuro)* Esquemas elétricos |

## Ligação física

- **Interface:** UART serial
- **Porta padrão:** `/dev/ttyS0` (configurável no ecrã de operador)
- **Parâmetros:** 9600 bps, 8 data bits, 1 stop bit, sem paridade, sem flow control

## Mapeamento de corredores

Cada produto na base de dados tem um `vmc_lane` (1–255) que corresponde ao corredor físico da máquina. Configura no ecrã de operador ou directamente na tabela `product_slots`.

### Layout 8 camadas × 3 espiras

| Parâmetro | Valor |
|-----------|-------|
| Camadas | 8 (camada 1 = topo) |
| Espiras por camada | 3 (esquerda → direita) |
| Corredor VMC | `(camada - 1) × 3 + espiral` → L1–L24 |
| Capacidade espiral | S = 5 · M = 10 · L = 15 unidades |

No operador → separador **Máquina**: grelha visual + botão «Carregar layout de teste 8×3».

## Implementação Android

Código em [`../android/app/src/main/java/com/zuratouch/prizewheel/vending/`](../android/app/src/main/java/com/zuratouch/prizewheel/vending/).

## Testes

No ecrã de operador → **Diagnóstico**:

- Reconectar UART
- Ler DEVICE_ID
- Testar dispense por corredor (L1–L4)
