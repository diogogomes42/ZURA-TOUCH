# Instalação em campo — Zura Touch (2 APKs)

Guia para quiosques Android com **app Cliente** + **app Operador**, base de dados partilhada e modo de experiência configurável (Roleta ou Cartas).

## APKs

| App | `applicationId` | Função |
|-----|-------------------|--------|
| **Cliente** | `com.zuratouch.customer` | Ecrã do cliente (Roleta ou Cartas) |
| **Operador** | `com.zuratouch.operator` | Stock, preço, PIN, modo de experiência, diagnóstico |

Ambas usam `android:sharedUserId="com.zuratouch.shared"` e a mesma base Room (`zura_touch.db`).

## Pré-requisitos

- Android 8.0+ (API 26)
- **Mesma chave de assinatura** para os dois APKs (obrigatório para `sharedUserId`)
- Dispositivo dedicado ao quiosque (recomendado)

## Build (debug / testes)

```bash
cd "Prize wheel/android"
./gradlew testDebugUnitTest :app-customer:assembleDebug :app-operator:assembleDebug
```

APKs gerados:

- `app-customer/build/outputs/apk/debug/app-customer-debug.apk`
- `app-operator/build/outputs/apk/debug/app-operator-debug.apk`

## Build release

1. Configure keystore em `android/keystore.properties` (não versionar) ou variáveis CI.
2. Assine **ambos** os módulos com a **mesma** keystore.
3. Instale release no dispositivo de produção.

> Debug usa hardware simulado (`USE_FAKE_HARDWARE=true`). Release comunica via UART/VMC real.

## Instalação no dispositivo

```bash
adb install -r app-operator-release.apk
adb install -r app-customer-release.apk
```

Ordem: operador primeiro ou cliente primeiro — desde que a assinatura seja a mesma.

### Primeira utilização

1. Abrir **Operador** → definir PIN (mín. 6 dígitos).
2. Tab **Máquina** → preço, porta série, som.
3. Tab **Stock** / **Prémios** → repor filas e categorias.
4. Tab **Experiência** → escolher **Roleta** ou **Cartas**.
5. Abrir **Cliente** (ecrã principal do quiosque).

A app Cliente observa `experienceMode` em tempo real. A troca Roleta ↔ Cartas só ocorre quando **não há venda ativa** (idle).

## Modo quiosque (launcher)

- Definir **Cliente** como app de arranque / launcher (Device Owner ou MDM).
- Manter **Operador** acessível só ao pessoal (atalho oculto, segunda app no drawer, ou perfil de manutenção).

## Base de dados partilhada

- Ficheiro: `zura_touch.db` (Room v8)
- Caminho canónico: diretório de dados da app **Cliente** (`com.zuratouch.customer`)
- A app Operador abre o mesmo ficheiro via `sharedUserId` + mesma assinatura
- Room usa `enableMultiInstanceInvalidation()` para a Cliente ver alterações feitas pelo Operador

**Se as apps não partilham dados:**

1. Desinstalar **Cliente** e **Operador**
2. Reinstalar **ambas** com a **mesma keystore** (debug ou release)
3. Confirmar `android:sharedUserId="com.zuratouch.shared"` nos dois manifests
4. Configurar stock/modo no Operador **antes** ou **depois** — a Cliente actualiza em tempo real quando idle

> Instalar só uma app, ou assinaturas diferentes, faz cada app usar uma BD local separada (comportamento antigo).

## UART / VMC

- Caminho série configurável no Operador (ex.: `/dev/ttyS0`)
- Ver tab **Registos** → testes de ligação e motor
- Protocolo: [`../hardware/vmc-protocol.md`](../hardware/vmc-protocol.md)

## Analytics Cartas (local)

Cada venda Cartas regista em `sale_logs.message`:

```
experience=CARDS;boardSeed=…;selectedCardIndex=…;wasTargetSlot=true|false
```

No Operador, tab **Registos** → painel **Cartas** mostra:

- Rondas concluídas
- Taxa observada de escolha do slot premium vs expectativa aleatória (1/24 ≈ 4,2%)

Sem cloud — dados ficam no dispositivo.

## Tags Git sugeridas

| Tag | Significado |
|-----|-------------|
| `v0.1-stable` | Monólito original (referência) |
| `v1.0.0-customer-wheel-only` | Cliente só roleta, operador separado |
| `v1.0.0-cards-mvp` | Cliente roleta + cartas, operador completo |

```bash
git tag -a v1.0.0-cards-mvp -m "Customer dual experience + operator split"
git push origin v1.0.0-cards-mvp
```

## CI

Workflow GitHub Actions: [`.github/workflows/android-ci.yml`](../../.github/workflows/android-ci.yml) — testes unitários + APKs debug de ambas as apps.
