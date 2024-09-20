package com.gvs.avisacitas.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gvs.avisacitas.R;
import com.gvs.avisacitas.login.ui.login.LoginActivity;
import com.gvs.avisacitas.main.MainActivity;
import com.gvs.avisacitas.model.accounts.Account;
import com.gvs.avisacitas.model.sqlite.AccountRepository;
import com.gvs.avisacitas.model.sqlite.AvisacitasSQLiteOpenHelper;
import com.gvs.avisacitas.model.sqlite.GeneralDataRepository;
import com.gvs.avisacitas.utils.error.LogHelper;

import org.json.JSONArray;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

	private ProgressBar progressBar;
	private TextView progressText;
	private TextView loadingFileText;
	private Handler handler = new Handler();
	private int progressStatus = 0;

	// Lista simulada de archivos que se están cargando
	private final String[] filesToLoad = {
			"android_config_0123", "user_data_cache_1024", "system_logcat_2345", "device_info_3125", "backup_Cloud_4120", "android_ui_0321", "kernel_log_5012", "apk_download_1934", "system_resource_0567", "temp_file_cache_8763", "android_os_update_3419", "dalvik_cache_5893", "battery_stats_9182", "wifi_scan_results_4412",
			"camera_config_2140", "app_permissions_1284", "error_report_0239", "secure_boot_1395", "device_health_6271", "android_notification_9382", "logcat_report_3024", "settings_backup_4143", "gps_coordinates_0837", "network_policy_8293", "developer_mode_2718", "user_preferences_4948", "system_ui_tuner_0321",
			"cpu_usage_stats_0982", "fingerprint_scanner_log_9014", "trusted_device_data_3274", "secure_element_log_5634", "volume_control_log_8729", "android_activity_log_2938", "media_volume_settings_4103", "app_cache_clear_7318", "file_observer_report_1302", "android_app_drawer_4259", "input_methods_log_9173",
			"app_lock_settings_5148", "overlay_manager_log_2748", "digital_wellbeing_report_4931", "device_admin_settings_6312", "sleep_mode_log_8950", "android_oem_unlock_0732", "gpu_usage_stats_5140", "screen_rotation_log_9284", "dark_theme_settings_6120", "data_backup_service_3278", "screenshot_cache_5109",
			"system_lock_log_5237", "display_orientation_log_7893", "trusted_places_log_9140", "android_rollback_report_6024", "bootloader_unlock_log_1023", "android_security_patch_6394", "dynamic_wifi_scan_3745", "battery_stats_cache_8490", "audio_input_settings_9531", "android_quick_settings_4310",
			"backup_manager_service_6145", "memory_usage_log_2038", "fastboot_settings_log_7523", "notification_dots_log_8213", "system_animation_log_6219", "android_usb_host_log_3092", "dynamic_wallpaper_log_5391", "android_deep_sleep_log_4381", "vpn_data_stats_6042", "device_maintenance_log_1983",
			"digital_balance_log_4219", "provisioning_report_7429", "app_background_usage_9321", "usb_function_settings_4285", "thermal_throttling_log_5934", "root_user_logs_8391", "wifi_qos_log_9240", "mobile_network_debug_3801", "android_apps_list_6124", "dark_mode_schedule_7382", "file_system_log_5039",
			"adb_pairing_log_8427", "power_menu_settings_3279", "file_restore_service_9320", "media_buffer_cache_5312", "ambient_light_sensor_log_9438", "android_performance_mode_2743", "system_security_settings_7420", "usb_power_supply_log_3128", "screen_pinning_log_1748", "system_heap_log_5912",
			"battery_temp_monitor_2043", "gesture_detection_log_7385", "android_experimental_features_1349", "display_cutout_log_9037", "keyboard_shortcut_log_1028", "split_screen_report_3270", "android_overlay_settings_8910", "usb_audio_settings_3452", "system_server_log_8741", "device_rotation_report_2638",

	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		progressBar = findViewById(R.id.progress_bar);
		progressText = findViewById(R.id.progress_text);
		loadingFileText = findViewById(R.id.loading_file_text);

		//GeneralDataRepository generalDataRepository = new GeneralDataRepository(this);
		AccountRepository accountRepository = new AccountRepository(this);

		// Simular la carga de archivos
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < filesToLoad.length; i++) {
					final String currentFile = filesToLoad[i]; // Obtener el archivo actual

					// Simular el tiempo de carga de cada archivo
					try {
						Thread.sleep(50); // Simular carga de 1 segundo por archivo
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					// Calcular el progreso
					progressStatus = (i + 1) * 100 / filesToLoad.length;

					// Actualizar la barra de progreso y el texto en la UI
					handler.post(new Runnable() {
						@Override
						public void run() {
							// Actualizar el nombre del archivo que se está cargando
							loadingFileText.setText(String.format("%s%s", getString(R.string.loading), currentFile));
							// Actualizar el progreso
							progressBar.setProgress(progressStatus);
							progressText.setText(getString(R.string.loading) + progressStatus + "%");
						}
					});
				}


				//generalDataRepository.updatePanicBtn(true);

				try {
					Account hasAccount = accountRepository.getActiveAccount();

					if (hasAccount != null) {

						//CalendarHelper.insertOrUpdateAllCalendarEvents(getApplicationContext());

						// Una vez que todos los archivos han sido cargados, iniciar la actividad principal
						Intent intent = new Intent(SplashActivity.this, MainActivity.class);
						startActivity(intent);
						finish(); // Finalizar SplashActivity
					}

					if (hasAccount == null) {

						// Insertar una cuenta falsa
						Account fakeAccount = new Account();
						fakeAccount.setPk_mcid("fake_mcid_123");
						fakeAccount.setName("Fake User");
						fakeAccount.setEmail("fakeuser@example.com");
						fakeAccount.setPhone("638397366");
						fakeAccount.setWaToken("fake_token");
						fakeAccount.setConnect("true");
						fakeAccount.setEpochUTCAdded(System.currentTimeMillis() / 1000L);
						fakeAccount.setActive("true");
						fakeAccount.setCompanyName("Fake Company");
						fakeAccount.setCustomPostMsg("Custom Fake PostMsg");

						// Convertir las cuentas a JSON
						JSONObject fakeAccountJson = fakeAccount.toJsonObject();

//						// Crear un JSONArray para almacenar múltiples cuentas
//						JSONArray accountsArray = new JSONArray();
//						accountsArray.put(fakeAccountJson);

						// Insertar las cuentas en la base de datos
						accountRepository.dbInsertAccountBlocking(fakeAccountJson);

						//Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
						Intent intent = new Intent(SplashActivity.this, MainActivity.class);
						startActivity(intent);
						finish(); // Finalizar SplashActivity

					}

				} catch (Exception ex) {
					LogHelper.addLogError(ex);
				}
			}
		}).start();
	}
}