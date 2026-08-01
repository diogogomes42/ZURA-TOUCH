# Zura Touch – Prize Wheel

Monorepo do projeto **Spin-to-Win** para máquinas de vending Zura Touch.

## Estrutura

```
PrizeWheel/
├── android/      → Aplicação Android (Kotlin + Compose)
├── prototype/    → Protótipo HTML/CSS/JS (referência visual)
├── docs/         → Documentação e especificações
├── hardware/     → Protocolos UART/VMC, MDB, esquemas
├── assets/       → Logos, ícones e imagens de marca
└── README.md
```

## Início rápido

| O que queres fazer | Onde ir |
|--------------------|---------|
| Desenvolver / compilar a app | [`android/`](android/) |
| Ver o protótipo visual da roda | [`prototype/index.html`](prototype/index.html) |
| Consultar a especificação | [`docs/`](docs/) |
| Integrar com a máquina | [`hardware/`](hardware/) |
| Atualizar branding | [`assets/`](assets/) |

## Android Studio

Abre a pasta **`android/`** (não a raiz do monorepo):

```
File → Open → PrizeWheel/android
```

## Versão

SRS v0.1 — Mystery Box com roda de prémios, pagamento VMC cashless e gestão de stock.
