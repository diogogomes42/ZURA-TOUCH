package com.zuratouch.feature.directpurchase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.zuratouch.feature.directpurchase.components.DirectPurchaseCatalogLayout
import com.zuratouch.feature.directpurchase.components.DirectPurchaseProductCard
import com.zuratouch.feature.directpurchase.theme.ZuraColors
import com.zuratouch.feature.directpurchase.ui.trackUserActivity
import com.zuratouch.prizewheel.domain.DirectPurchaseOffer

@Composable
fun DirectPurchaseScreen(
    viewModel: DirectPurchaseViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    onUserInteraction: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    val dismissEnabled = !state.isBusy

    Dialog(
        onDismissRequest = {
            if (dismissEnabled) {
                if (state.step == DirectPurchaseStep.CATALOG) {
                    onDismiss()
                } else {
                    viewModel.backToCatalog()
                }
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .trackUserActivity(onActivity = onUserInteraction)
                .background(
                    Brush.radialGradient(
                        colors = listOf(ZuraColors.BackgroundMid, ZuraColors.BackgroundDark),
                        radius = 1200f,
                    ),
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 620.dp)
                    .align(Alignment.Center)
                    .padding(
                        horizontal = 20.dp,
                        vertical = if (state.step == DirectPurchaseStep.CATALOG) 16.dp else 28.dp,
                    ),
            ) {
                DirectPurchaseTopBar(
                    step = state.step,
                    onClose = {
                        if (state.step == DirectPurchaseStep.CATALOG) onDismiss() else viewModel.backToCatalog()
                    },
                    onBack = viewModel::backToCatalog,
                    closeEnabled = dismissEnabled,
                )
                Spacer(modifier = Modifier.height(if (state.step == DirectPurchaseStep.CATALOG) 6.dp else 12.dp))
                when (state.step) {
                    DirectPurchaseStep.CATALOG -> CatalogStep(
                        state = state,
                        onSelectOffer = viewModel::selectOffer,
                    )
                    DirectPurchaseStep.CONFIRM_PHYSICAL -> ConfirmPhysicalStep(
                        offer = state.selectedOffer,
                        onConfirm = viewModel::confirmPhysicalAndPay,
                    )
                    DirectPurchaseStep.EMAIL_INPUT -> EmailInputStep(
                        offer = state.selectedOffer,
                        email = state.customerEmail,
                        errorMessage = state.errorMessage,
                        onEmailChange = viewModel::updateEmail,
                        onContinue = viewModel::confirmEmailAndPay,
                    )
                    DirectPurchaseStep.PAYMENT_PENDING -> PaymentPendingStep()
                    DirectPurchaseStep.SUCCESS -> SuccessStep(
                        message = state.successMessage.orEmpty(),
                        onDone = {
                            viewModel.dismissSuccess()
                            onDismiss()
                        },
                    )
                    DirectPurchaseStep.ERROR -> ErrorStep(
                        message = state.errorMessage.orEmpty(),
                        onRetry = viewModel::retryFromError,
                        onBack = viewModel::backToCatalog,
                    )
                }
            }
        }
    }
}

@Composable
private fun DirectPurchaseTopBar(
    step: DirectPurchaseStep,
    onClose: () -> Unit,
    onBack: () -> Unit,
    closeEnabled: Boolean,
) {
    val title = when (step) {
        DirectPurchaseStep.CONFIRM_PHYSICAL -> stringResource(R.string.direct_purchase_confirm_title)
        DirectPurchaseStep.EMAIL_INPUT -> stringResource(R.string.direct_purchase_email_title)
        DirectPurchaseStep.SUCCESS -> stringResource(R.string.direct_purchase_success_title)
        else -> stringResource(R.string.direct_purchase_title)
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (step != DirectPurchaseStep.CATALOG &&
                step != DirectPurchaseStep.PAYMENT_PENDING &&
                step != DirectPurchaseStep.SUCCESS &&
                step != DirectPurchaseStep.ERROR
            ) {
                TextButton(
                    onClick = onBack,
                    enabled = closeEnabled,
                    modifier = Modifier.align(Alignment.CenterStart),
                ) {
                    Text(
                        text = stringResource(R.string.direct_purchase_back),
                        color = ZuraColors.TextMuted,
                        fontSize = 12.sp,
                    )
                }
            }
            TextButton(
                onClick = onClose,
                enabled = closeEnabled,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Text(
                    text = stringResource(R.string.direct_purchase_close),
                    color = ZuraColors.TextMuted,
                    fontSize = 12.sp,
                )
            }
        }
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = if (step == DirectPurchaseStep.CATALOG) 18.sp else 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        if (step == DirectPurchaseStep.CATALOG) {
            Text(
                text = stringResource(R.string.direct_purchase_subtitle),
                color = ZuraColors.TextMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun CatalogStep(
    state: DirectPurchaseUiState,
    onSelectOffer: (DirectPurchaseOffer) -> Unit,
) {
    DirectPurchaseCatalogLayout(
        machineOffers = state.catalog.machineOffers,
        digitalOffers = state.catalog.digitalOffers,
        onSelectOffer = onSelectOffer,
        machineSectionTitle = stringResource(R.string.direct_purchase_section_machine),
        digitalSectionTitle = stringResource(R.string.direct_purchase_section_digital),
        machineEmptyMessage = stringResource(R.string.direct_purchase_empty_machine),
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun ConfirmPhysicalStep(
    offer: DirectPurchaseOffer?,
    onConfirm: () -> Unit,
) {
    if (offer == null) return
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DirectPurchaseProductCard(
            offer = offer,
            onBuyClick = {},
            modifier = Modifier.fillMaxWidth(),
            showBuyButton = false,
        )
        PrimaryActionButton(
            label = stringResource(R.string.direct_purchase_continue),
            onClick = onConfirm,
        )
    }
}

@Composable
private fun EmailInputStep(
    offer: DirectPurchaseOffer?,
    email: String,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onContinue: () -> Unit,
) {
    if (offer == null) return
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        DirectPurchaseProductCard(
            offer = offer,
            onBuyClick = {},
            modifier = Modifier.fillMaxWidth(),
            showBuyButton = false,
        )
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.direct_purchase_email_hint)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { onContinue() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = ZuraColors.Accent,
                unfocusedBorderColor = Color.White.copy(alpha = 0.35f),
                focusedLabelColor = ZuraColors.Accent,
                unfocusedLabelColor = ZuraColors.TextMuted,
                cursorColor = ZuraColors.Accent,
            ),
            shape = RoundedCornerShape(12.dp),
        )
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.White.copy(alpha = 0.06f),
        ) {
            Text(
                text = stringResource(R.string.direct_purchase_email_notice),
                color = ZuraColors.TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(12.dp),
            )
        }
        if (!errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                color = ZuraColors.Error,
                fontSize = 12.sp,
            )
        }
        PrimaryActionButton(
            label = stringResource(R.string.direct_purchase_continue),
            onClick = onContinue,
        )
    }
}

@Composable
private fun PaymentPendingStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(color = ZuraColors.Accent, strokeWidth = 3.dp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.direct_purchase_payment_pending),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SuccessStep(
    message: String,
    onDone: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.direct_purchase_success_title),
            color = ZuraColors.Accent,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            color = Color.White,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryActionButton(
            label = stringResource(R.string.direct_purchase_done),
            onClick = onDone,
        )
    }
}

@Composable
private fun ErrorStep(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            color = ZuraColors.Error,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))
        PrimaryActionButton(label = "Tentar novamente", onClick = onRetry)
        TextButton(onClick = onBack) {
            Text(
                text = stringResource(R.string.direct_purchase_back),
                color = ZuraColors.TextMuted,
            )
        }
    }
}

@Composable
private fun PrimaryActionButton(
    label: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = ZuraColors.Accent,
            contentColor = ZuraColors.BackgroundDark,
        ),
        shape = RoundedCornerShape(13.dp),
    ) {
        Text(text = label, fontWeight = FontWeight.Black)
    }
}
