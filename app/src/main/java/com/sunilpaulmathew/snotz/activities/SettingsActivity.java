package com.sunilpaulmathew.snotz.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.sunilpaulmathew.snotz.BuildConfig;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.SettingsAdapter;
import com.sunilpaulmathew.snotz.utils.Billing;
import com.sunilpaulmathew.snotz.utils.Common;
import com.sunilpaulmathew.snotz.utils.Security;
import com.sunilpaulmathew.snotz.utils.SettingsItems;
import com.sunilpaulmathew.snotz.utils.Utils;
import com.sunilpaulmathew.snotz.utils.sNotzUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.Executor;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on October 17, 2020
 */
public class SettingsActivity extends AppCompatActivity {

    private AppCompatImageButton mBack;
    private BiometricPrompt mBiometricPrompt;
    private final ArrayList <SettingsItems> mData = new ArrayList<>();
    private LinearLayout mProgressLayout;
    private String mJSONString = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mBack = findViewById(R.id.back_button);
        mProgressLayout = findViewById(R.id.progress_layout);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        SettingsAdapter mRecycleViewAdapter = new SettingsAdapter(mData);
        mRecyclerView.setAdapter(mRecycleViewAdapter);

        mData.add(new SettingsItems(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")", "Copyright: © 2021-2022, sunilpaulmathew", sNotzUtils.getDrawable(R.drawable.ic_info, this), null));
        if (Utils.isFingerprintAvailable(this)) {
            mData.add(new SettingsItems(getString(R.string.biometric_lock), getString(R.string.biometric_lock_summary), sNotzUtils.getDrawable(R.drawable.ic_fingerprint, this), null));
        } else {
            mData.add(new SettingsItems(getString(R.string.pin_protection), getString(R.string.pin_protection_message), sNotzUtils.getDrawable(R.drawable.ic_lock, this), null));
        }
        mData.add(new SettingsItems(getString(R.string.show_hidden_notes), getString(R.string.show_hidden_notes_summary), sNotzUtils.getDrawable(R.drawable.ic_eye, this), null));
        mData.add(new SettingsItems(getString(R.string.note_color_background), getString(R.string.color_select_dialog, getString(R.string.note_color_background)), sNotzUtils.getDrawable(R.drawable.ic_color, this), null));
        mData.add(new SettingsItems(getString(R.string.note_color_text), getString(R.string.color_select_dialog, getString(R.string.note_color_text)), sNotzUtils.getDrawable(R.drawable.ic_text, this), null));
        mData.add(new SettingsItems(getString(R.string.backup_notes), getString(R.string.backup_notes_summary), sNotzUtils.getDrawable(R.drawable.ic_backup, this), null));
        mData.add(new SettingsItems(getString(R.string.restore_notes), getString(R.string.restore_notes_summary), sNotzUtils.getDrawable(R.drawable.ic_restore, this), null));
        mData.add(new SettingsItems(getString(R.string.clear_notes), getString(R.string.clear_notes_summary), sNotzUtils.getDrawable(R.drawable.ic_clear, this), null));
        mData.add(new SettingsItems(getString(R.string.donations), getString(R.string.donations_summary), sNotzUtils.getDrawable(R.drawable.ic_donate, this), null));
        mData.add(new SettingsItems(getString(R.string.invite_friends), getString(R.string.invite_friends_Summary), sNotzUtils.getDrawable(R.drawable.ic_share, this), null));
        mData.add(new SettingsItems(getString(R.string.welcome_note), getString(R.string.welcome_note_summary), sNotzUtils.getDrawable(R.drawable.ic_home, this), null));
        mData.add(new SettingsItems(getString(R.string.translations), getString(R.string.translations_summary), sNotzUtils.getDrawable(R.drawable.ic_translate, this), "https://poeditor.com/join/project?hash=LOg2GmFfbV"));
        mData.add(new SettingsItems(getString(R.string.rate_us), getString(R.string.rate_us_Summary), sNotzUtils.getDrawable(R.drawable.ic_rate, this), "https://play.google.com/store/apps/details?id=com.sunilpaulmathew.snotz"));
        mData.add(new SettingsItems(getString(R.string.support), getString(R.string.support_summary), sNotzUtils.getDrawable(R.drawable.ic_support, this), "https://t.me/smartpack_kmanager"));
        mData.add(new SettingsItems(getString(R.string.faq), getString(R.string.faq_summary), sNotzUtils.getDrawable(R.drawable.ic_faq, this), "https://ko-fi.com/post/sNotz-FAQ-H2H42H6A8"));

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> {
            if (mData.get(position).getUrl() != null) {
                Utils.launchURL(mData.get(position).getUrl(), this);
            } else if (position == 0) {
                Intent settings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                settings.setData(uri);
                startActivity(settings);
                finish();
            } else if (position == 1) {
                if (Utils.isFingerprintAvailable(this)) {
                    mBiometricPrompt.authenticate(Utils.showBiometricPrompt(this));
                } else {
                    if (Security.isPINEnabled(this)) {
                        Security.removePIN(this);
                        mRecycleViewAdapter.notifyItemChanged(position);
                        Utils.showSnackbar(mRecyclerView, getString(R.string.pin_protection_status, getString(R.string.deactivated)));
                    } else {
                        Security.setPIN(false, getString(R.string.pin_enter), mRecycleViewAdapter, this);
                    }
                }
            } else if (position == 2) {
                if (Utils.getBoolean("use_biometric", false, this) && Utils.isFingerprintAvailable(this)) {
                    Common.isHiddenNote(true);
                    mBiometricPrompt.authenticate(Utils.showBiometricPrompt(this));
                } else {
                    if (Security.isPINEnabled(this)) {
                        Security.manageHiddenNotes(mRecycleViewAdapter, this);
                    } else {
                        Utils.saveBoolean("hidden_note", !Utils.getBoolean("hidden_note", false, this), this);
                        Common.isHiddenNote(false);
                        Utils.reloadUI(mProgressLayout, this).execute();
                    }
                }
            } else if (position == 3) {
                ColorPickerDialogBuilder
                        .with(this)
                        .setTitle(R.string.choose_color)
                        .initialColor(Utils.getInt("accent_color", sNotzUtils.getColor(R.color.color_teal, this), this))
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(selectedColor -> {
                        })
                        .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                            Utils.saveInt("accent_color", selectedColor, this);
                            Utils.showSnackbar(mRecyclerView, getString(R.string.choose_color_message, getString(R.string.note_color_background)));
                            Utils.reloadUI(mProgressLayout,this).execute();
                            mRecycleViewAdapter.notifyItemChanged(position);
                            Common.isReloading(true);
                        })
                        .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        }).build().show();
            } else if (position == 4) {
                ColorPickerDialogBuilder
                        .with(this)
                        .setTitle(R.string.choose_color)
                        .initialColor(Utils.getInt("text_color", sNotzUtils.getColor(R.color.color_white, this), this))
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(selectedColor -> {
                        })
                        .setPositiveButton(R.string.ok, (dialog, selectedColor, allColors) -> {
                            Utils.saveInt("text_color", selectedColor, this);
                            Utils.showSnackbar(mRecyclerView, getString(R.string.choose_color_message, getString(R.string.note_color_text)));
                            Utils.reloadUI(mProgressLayout,this).execute();
                            mRecycleViewAdapter.notifyItemChanged(position);
                            Common.isReloading(true);
                        })
                        .setNegativeButton(R.string.cancel, (dialog, which) -> {
                        }).build().show();
            } else if (position == 5) {
                if (Utils.exist(getFilesDir().getPath() + "/snotz")) {
                    new MaterialAlertDialogBuilder(this).setItems(getResources().getStringArray(
                            R.array.backup_options), (dialogInterface, i) -> {
                        switch (i) {
                            case 0:
                                saveDialog(".backup", Utils.read(getFilesDir().getPath() + "/snotz"));
                                break;
                            case 1:
                                saveDialog(".txt", sNotzUtils.sNotzToText(this));
                                break;
                        }
                    }).setOnDismissListener(dialogInterface -> {
                    }).show();
                } else {
                    Utils.showSnackbar(mRecyclerView, getString(R.string.note_list_empty));
                }
            } else if (position == 6) {
                Intent restore = new Intent(Intent.ACTION_GET_CONTENT);
                restore.setType("*/*");
                restore.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(restore, 0);
            } else if (position == 7) {
                if (Utils.exist(getFilesDir().getPath() + "/snotz")) {
                    new MaterialAlertDialogBuilder(this)
                            .setMessage(getString(R.string.clear_notes_message))
                            .setNegativeButton(R.string.cancel, (dialog, which) -> {
                            })
                            .setPositiveButton(R.string.delete, (dialog, which) -> {
                                Utils.delete(getFilesDir().getPath() + "/snotz");
                                Utils.reloadUI(mProgressLayout,this).execute();
                                onBackPressed();
                            })
                            .show();
                } else {
                    Utils.showSnackbar(mRecyclerView, getString(R.string.note_list_empty));
                }
            } else if (position == 8) {
                Billing.showDonationMenu(this);
            } else if (position == 9) {
                Intent share_app = new Intent();
                share_app.setAction(Intent.ACTION_SEND);
                share_app.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                share_app.putExtra(Intent.EXTRA_TEXT, getString(R.string.shared_by_message, BuildConfig.VERSION_NAME));
                share_app.setType("text/plain");
                Intent shareIntent = Intent.createChooser(share_app, getString(R.string.share_with));
                startActivity(shareIntent);
            } else if (position == 10) {
                Intent welcome = new Intent(this, WelcomeActivity.class);
                startActivity(welcome);
                finish();
            }
        });

        mBack.setOnClickListener(v -> onBackPressed());

        Executor executor = ContextCompat.getMainExecutor(this);
        mBiometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Utils.showSnackbar(mBack, getString(R.string.authentication_error, errString));
                mRecycleViewAdapter.notifyItemRangeChanged(1,2);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if (Common.isHiddenNote()) {
                    Utils.saveBoolean("hidden_note", !Utils.getBoolean("hidden_note", false, SettingsActivity.this), SettingsActivity.this);
                    Common.isHiddenNote(false);
                    Utils.reloadUI(mProgressLayout,SettingsActivity.this).execute();
                } else {
                    Utils.useBiometric(mBack, SettingsActivity.this);
                }
                mRecycleViewAdapter.notifyItemRangeChanged(1,2);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Utils.showSnackbar(mBack, getString(R.string.authentication_failed));
                mRecycleViewAdapter.notifyItemRangeChanged(1,2);
            }
        });

        Utils.showBiometricPrompt(this);
    }

    private void saveDialog(String type, String sNotz) {
        if (Build.VERSION.SDK_INT < 30 && Utils.isPermissionDenied(this)) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return;
        }
        Utils.dialogEditText(null, null,
                (dialogInterface, i) -> {
                }, text -> {
                    if (text.isEmpty()) {
                        Utils.showSnackbar(mBack, getString(R.string.text_empty));
                        return;
                    }
                    if (!text.endsWith(type)) {
                        text += type;
                    }
                    if (text.contains(" ")) {
                        text = text.replace(" ", "_");
                    }
                    if (Build.VERSION.SDK_INT >= 30) {
                        try {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.MediaColumns.DISPLAY_NAME, text);
                            values.put(MediaStore.MediaColumns.MIME_TYPE, "*/*");
                            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                            Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                            OutputStream outputStream = getContentResolver().openOutputStream(uri);
                            outputStream.write(sNotz.getBytes());
                            outputStream.close();
                        } catch (IOException ignored) {
                        }
                    } else {
                        Utils.create(sNotz, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + text);
                    }
                    Utils.showSnackbar(mBack, getString(R.string.backup_notes_message, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + text));
                }, -1, this).setOnDismissListener(dialogInterface -> {
        }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            assert uri != null;
            File mSelectedFile = null;
            if (Utils.isDocumentsUI(uri)) {
                @SuppressLint("Recycle")
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    mSelectedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
                }
            } else {
                mSelectedFile = new File(uri.getPath());
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                for (int result = bis.read(); result != -1; result = bis.read()) {
                    buf.write((byte) result);
                }
                mJSONString = buf.toString("UTF-8");
            } catch (IOException ignored) {}

            if (mJSONString == null || !sNotzUtils.validBackup(mJSONString)) {
                Utils.showSnackbar(mBack, getString(R.string.restore_error));
                return;
            }
            new MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.restore_notes_question, mSelectedFile != null ?
                            mSelectedFile.getName() : "backup"))
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                    })
                    .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                        sNotzUtils.restoreNotes(mJSONString, this);
                        Utils.reloadUI(mProgressLayout,this).execute();
                        finish();
                    }).show();
        }
    }

}