# Android – Zura Touch

Duas apps Android (Kotlin + Jetpack Compose) para quiosques de vending com experiência **Roleta** ou **Cartas**.

## Apps

| Módulo | ID | Versão | Função |
|--------|-----|--------|--------|
| `app-customer` | `com.zuratouch.customer` | 1.0.0 | Ecrã do cliente (kiosk) |
| `app-operator` | `com.zuratouch.operator` | 1.0.0 | Gestão, stock, modo de experiência |

Ambas partilham `sharedUserId` + base Room `zura_touch.db`.

## Estrutura

```
android/
├── app-customer/       ExperienceHost → Roleta | Cartas
├── app-operator/       Tabs operador + selector de experiência
├── feature/
│   ├── sale-wheel/     UI + VM da roleta
│   └── sale-cards/     UI + VM das cartas
└── core/
    ├── domain/         PrizeEngine, CardBoardGenerator, CardsAnalytics
    ├── data/           Room v8, StockRepository
    ├── hardware/       Bootstrap, heartbeat
    ├── payment/        PaymentTerminal
    ├── vending/        VMC / UART
    └── feedback/       Som e haptics
```

## Requisitos

- Android Studio Ladybug+
- JDK 17
- Android SDK API 35

Abrir **`android/`** no Android Studio (não a raiz do monorepo).

## Build & testes

```bash
cd android
./gradlew testDebugUnitTest :app-customer:assembleDebug :app-operator:assembleDebug
```

Debug usa hardware simulado. Release usa UART/VMC real.

## CI

GitHub Actions constrói ambas as apps e corre testes unitários — ver [`../../.github/workflows/android-ci.yml`](../../.github/workflows/android-ci.yml).

## Instalação em campo

Ver [`../docs/deployment.md`](../docs/deployment.md) — assinatura partilhada, instalação dos 2 APKs, launcher, UART.

## Operador

App **Operador** (separada): PIN → Prémios / Stock / Máquina / **Experiência** / Registos.

A app Cliente **não** tem ecrã de operador.

## Experiências (cliente)

| Modo | Fluxo |
|------|--------|
| **Roleta** | Pay → spin (cosmético) → dispense planeado |
| **Cartas** | Pay → tabuleiro 24 → escolha → dispense da carta |

Troca de modo só quando idle (sem pagamento/jogo/entrega ativos).

## Hardware

[`../hardware/`](../hardware/) — protocolo UART/VMC.

## Protótipo visual

[`../prototype/`](../prototype/) — referência UX da roleta.
