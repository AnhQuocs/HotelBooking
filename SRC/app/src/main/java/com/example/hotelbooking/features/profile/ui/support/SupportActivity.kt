package com.example.hotelbooking.features.profile.ui.support

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppButton
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.features.profile.util.sendSupportEmail
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue

class SupportActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SupportScreen(onBackClick = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var includeDeviceInfo by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            AppTopBar(
                text = stringResource(R.string.support_title),
                onBackClick = onBackClick
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimen.PaddingM)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.support_hint),
                style = AfacadTypography.titleMedium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(AppSpacing.M))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.subject_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(AppShape.ShapeS)
            )

            Spacer(modifier = Modifier.height(AppSpacing.M))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text(stringResource(R.string.content_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                minLines = 5,
                shape = RoundedCornerShape(AppShape.ShapeS)
            )

            Spacer(modifier = Modifier.height(AppSpacing.S))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { includeDeviceInfo = !includeDeviceInfo }
            ) {
                Checkbox(
                    checked = includeDeviceInfo,
                    onCheckedChange = { includeDeviceInfo = it },
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
                )
                Text(
                    text = stringResource(R.string.include_device_info),
                    style = AfacadTypography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppSpacing.M),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(Dimen.SizeSM),
                    tint = Color.Gray
                )

                Spacer(modifier = Modifier.width(AppSpacing.XS))

                Text(
                    text = stringResource(R.string.support_response_hint),
                    style = AfacadTypography.bodySmall,
                    color = Color.Gray
                )
            }

            val errorEmptyFields = stringResource(id = R.string.error_empty_fields)

            AppButton(
                onClick = {
                    if (title.isBlank() || content.isBlank()) {
                        Toast.makeText(context, errorEmptyFields, Toast.LENGTH_SHORT).show()
                    } else {
                        sendSupportEmail(context, title, content, includeDeviceInfo)

                        Toast.makeText(context, context.getString(R.string.redirecting_to_email), Toast.LENGTH_SHORT).show()

                        onBackClick()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.send_request)
            )
        }
    }
}