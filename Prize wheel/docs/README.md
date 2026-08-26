# Documentação

| Documento | Descrição |
|-----------|-----------|
| [`deployment.md`](deployment.md) | Instalação em campo — 2 APKs, shared DB, launcher, CI |
| [`../hardware/README.md`](../hardware/README.md) | UART, VMC, layout da máquina |
| [`../android/README.md`](../android/README.md) | Build, módulos, arquitetura |

## Arquitetura (resumo)

- **2 apps:** Cliente (`com.zuratouch.customer`) + Operador (`com.zuratouch.operator`)
- **Experiência configurável:** `WHEEL` | `CARDS` em Room (`AppConfig.experienceMode`, v8)
- **Cartas:** 24 prémios reais, 1 slot premium, escolha determina outcome
- **Analytics local:** taxa de pick do slot premium vs 1/24 — tab Registos no Operador

## Tags sugeridas

| Tag | Estado |
|-----|--------|
| `v0.1-stable` | Monólito original |
| `v1.0.0-customer-wheel-only` | Pós Phase 4 |
| `v1.0.0-cards-mvp` | Roleta + Cartas MVP (Phase 6–7) |

## SRS v0.1

Objetivo: app Android para vending com Spin-to-Win / Mystery Box.

Tecnologias: Kotlin, Compose, Room, UART/VMC.

Fora de scope v0.1: cloud, loyalty, analytics remota, multi-idioma.
