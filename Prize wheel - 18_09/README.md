# Zura Touch – Prize Wheel

Monorepo do projeto **Spin-to-Win / Cartas** para máquinas de vending Zura Touch.

## Estrutura

```
Prize wheel/
├── android/      → 2 apps (Cliente + Operador) + core + features
├── prototype/    → Protótipo HTML/CSS/JS (referência visual)
├── docs/         → Deployment, especificações
├── hardware/     → Protocolos UART/VMC
├── assets/       → Logos e branding
└── README.md
```

## Início rápido

| O que queres fazer | Onde ir |
|--------------------|---------|
| Desenvolver / compilar | [`android/`](android/) |
| Instalar no quiosque (2 APKs) | [`docs/deployment.md`](docs/deployment.md) |
| Ver protótipo da roda | [`prototype/index.html`](prototype/index.html) |
| Integrar hardware | [`hardware/`](hardware/) |

## Android Studio

Abre **`Prize wheel/android/`**:

```
File → Open → …/ZURA TOUCH/Prize wheel/android
```

## Versão

**v1.0.0** — Cliente dual experience (Roleta + Cartas), Operador separado, CI dual-APK.

