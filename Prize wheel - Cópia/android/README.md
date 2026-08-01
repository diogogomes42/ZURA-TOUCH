# Android – Zura Touch Prize Wheel

Aplicação nativa Android (Kotlin + Jetpack Compose) para o experience Spin-to-Win.

## Requisitos

- Android Studio (Ladybug ou superior)
- JDK 17
- Android SDK API 35

## Build

Abre **esta pasta** (`android/`) no Android Studio e sincroniza o Gradle.

```bash
cd android
gradle assembleDebug test
```

Debug usa hardware simulado (`USE_FAKE_HARDWARE=true`). Release comunica via UART com o controlador VMC.

## Funcionalidades (v0.1)

- Roda de prémios 3D com animação e categorias dinâmicas
- Pagamento VMC cashless + dispense com confirmação
- Stock persistente (Room) e histórico de vendas
- Ecrã de operador (long-press no logo, PIN `1234`)
- Som, vibração, categorias ponderadas, fallback de dispense

## Arquitetura

```
app/src/main/java/com/zuratouch/prizewheel/
├── data/       Room + StockRepository
├── domain/     PrizeWheelEngine, models
├── payment/    PaymentTerminal
├── vending/    VMC UART + protocolo
├── feedback/   Som e haptics
└── ui/         Compose screens + ViewModels
```

## Operador

Long-press em **ZURA TOUCH** → PIN `1234` → Stock / Config / Histórico / Diagnóstico.

## Hardware

Ver [`../hardware/`](../hardware/) para protocolo UART e mapeamento de corredores.

## Protótipo visual

Referência UX em [`../prototype/`](../prototype/).
