# Protocolo VMC (Reyeah)

Resumo do protocolo implementado em `android/.../vending/VmcProtocol.kt`.

## UART

| Parâmetro | Valor |
|-----------|-------|
| Baud rate | 9600 |
| Data bits | 8 |
| Stop bits | 1 |
| Parity | None |
| Flow control | None |

## Formato de frame

```
FF 00 [header] [command] [payload length] [payload...] [checksum]
```

- **App → VMC:** header `0x55`
- **VMC → App:** header `0xAA`
- **Checksum:** byte baixo de `header + command + length + payload`

## Comandos principais

| Comando | Código | Uso |
|---------|--------|-----|
| DEVICE_ID | `0x31` | Identificar controlador |
| PAYMENT | `0x11` | Iniciar pagamento (cashless, moedas, notas) |
| DISPENSE | `0x41` | Entregar produto num corredor |
| QUERY_STATUS | `0xE1` | Poll de estado (pagamento / entrega) |
| CLEAR_FAULT | `0xA2` | Limpar avaria |
| CANCEL_CASHLESS | `0xB2` | Cancelar pagamento cashless |
| REFUND_COINS | `0xB1` | Devolver moedas (fallback após cashless) |

## Pagamento cashless

Payload: `[amount 4 bytes LE][method 1 byte][lane 1 byte]`

- Amount em **cêntimos de euro** (uint32 little-endian)
- Method: `2` = cashless

## Dispense

Payload: `[lane 1 byte][quantity 1 byte]`

## Query status — respostas

| Payload | Significado |
|---------|-------------|
| `0x01` (1 byte) | Entrega concluída |
| Outro (1 byte) | Entrega falhou |
| 4 bytes LE | Pagamento concluído (valor em cêntimos) |

## Fluxo na app

1. `startPayment` → poll `queryStatus` até `PaymentCompleted`
2. Seleccionar categoria + slot
3. `dispense(lane)` → poll até `DeliveryCompleted`
4. Revelar prémio e decrementar stock

> Para documentação completa do fabricante, adiciona o PDF original em `hardware/uart/`.
