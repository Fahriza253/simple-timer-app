## Analisis Arsitektur Saat Ini

Secara umum, struktur project sudah cukup baik untuk skala aplikasi desktop JavaFX kecil-menengah. Pemisahan package juga sudah cukup jelas:

- `controller` → UI logic
- `service` → business logic
- `model` → entity/config model
- `util` → helper & infrastructure
- `config` → default configuration mode

Namun untuk implementasi `Setting`, saat ini masih memiliki beberapa masalah arsitektur yang cukup penting.

---

# Masalah Pada Implementasi Saat Ini

## 1. `AppConfig` Masih Berbasis Singleton In-Memory

Saat ini:

```java
private static AppConfig instance;
```

Semua setting hanya hidup di memory.

Akibatnya:

- Setting hilang saat aplikasi restart
- Tidak ada persistence
- Tidak scalable
- Sulit di-test
- Controller langsung mengubah state global

Padahal requirement sekarang:

> setting harus disimpan ke database SQLite

---

---

## 2. `SettingController` Melanggar Separation of Concern

Saat ini controller:

- Mengatur UI
- Mengubah config
- Menjadi validator
- Menjadi persistence handler

Contoh:

```java
spinnerDefaultPomodoro.valueProperty().addListener((obs, oldVal, newVal) ->
    config.setPomodoroMinute(newVal)
);
```

Masalah:

- UI langsung memodifikasi state aplikasi
- Tidak ada proses save explicit
- Tidak ada cancel state
- Sulit rollback perubahan

Ini melanggar:

- SRP
- Clean MVC
- Maintainability

---

## 3. Tidak Ada Repository / DAO Untuk Config

Saat ini belum ada:

```text
AppConfigDAO
AppConfigRepository
```

Akibatnya:

- Query database tersebar
- Sulit maintain
- Sulit extend
- Controller akan makin gemuk

---

## 4. Cancel Tidak Bisa Rollback

Karena perubahan langsung diterapkan ke singleton:

```java
config.setPomodoroMinute(newVal);
```

Maka tombol cancel sebenarnya tidak berguna.

---

# Solusi Arsitektur Yang Direkomendasikan

Gunakan flow berikut:

```text
UI (Controller)
    ↓
Service
    ↓
Repository / DAO
    ↓
SQLite
```

---

# Arsitektur Baru

## Layer Baru

Tambahkan:

```text
repository/
└── AppConfigRepository.java

service/
└── AppConfigService.java
```

---

# Flow Baru

## Saat App Start

```text
App.java
    ↓
AppConfigService.load()
    ↓
DB
    ↓
Jika gagal → gunakan default hardcode
```

---

## Saat User Membuka Setting

Controller hanya:

- menampilkan data
- membaca input
- memanggil service save()

Controller tidak boleh tahu SQL.

---

## Saat User Klik Save

```text
Controller
    ↓
Validation
    ↓
AppConfigService.save()
    ↓
Repository
    ↓
SQLite
```

---

# Kenapa Solusi Ini Baik?

## 1. Separation of Concern Sangat Jelas

| Layer      | Responsibility |
| ---------- | -------------- |
| Controller | UI interaction |
| Service    | business rule  |
| Repository | database       |
| Model      | data structure |

---

## 2. Mudah Di-maintain

Jika nanti:

- ganti SQLite → MySQL
- tambah sync cloud
- tambah export/import setting

Controller tidak perlu berubah.

---

## 3. Cancel Menjadi Valid

Karena perubahan belum disimpan sebelum tombol save ditekan.

---

## 4. Lebih Aman

Validation bisa dipusatkan di service.

---

## 5. Scalable

Jika nanti setting bertambah:

- dark mode
- notification
- sound volume
- startup behavior

arsitektur tetap bersih.

---

# Kekurangan Pendekatan Ini

## 1. Sedikit Lebih Banyak Class

Tradeoff normal clean architecture.

Tetapi sangat worth it.

---

## 2. Ada Sedikit Boilerplate

DAO/repository memang menambah code.

Namun benefit maintainability jauh lebih besar.

---

# Implementasi Yang Direkomendasikan

---

# 1. Update Database Schema

Tambahkan ke `database.sql`

```sql
CREATE TABLE IF NOT EXISTS app_config (
    id INTEGER PRIMARY KEY CHECK (id = 1),

    pomodoro_minute INTEGER NOT NULL DEFAULT 25,
    short_break_minute INTEGER NOT NULL DEFAULT 5,
    long_break_minute INTEGER NOT NULL DEFAULT 15,

    auto_start_pomodoro INTEGER NOT NULL DEFAULT 0,
    auto_start_break INTEGER NOT NULL DEFAULT 0,

    long_break_interval INTEGER NOT NULL DEFAULT 4
);

INSERT OR IGNORE INTO app_config (id) VALUES (1);
```

---

# 2. Refactor Model `AppConfig`

## `model/AppConfig.java`

```java
package com.dpzstudio.timer.model;

public class AppConfig {

    private boolean autoStartPomodoro;
    private boolean autoStartBreak;

    private int longBreakInterval;

    private int pomodoroMinute;
    private int shortBreakMinute;
    private int longBreakMinute;

    public AppConfig() {

    }

    public static AppConfig createDefault() {
        AppConfig config = new AppConfig();

        config.setPomodoroMinute(25);
        config.setShortBreakMinute(5);
        config.setLongBreakMinute(15);

        config.setAutoStartPomodoro(false);
        config.setAutoStartBreak(false);

        config.setLongBreakInterval(4);

        return config;
    }

    public boolean isAutoStartPomodoro() {
        return autoStartPomodoro;
    }

    public void setAutoStartPomodoro(boolean autoStartPomodoro) {
        this.autoStartPomodoro = autoStartPomodoro;
    }

    public boolean isAutoStartBreak() {
        return autoStartBreak;
    }

    public void setAutoStartBreak(boolean autoStartBreak) {
        this.autoStartBreak = autoStartBreak;
    }

    public int getLongBreakInterval() {
        return longBreakInterval;
    }

    public void setLongBreakInterval(int longBreakInterval) {
        this.longBreakInterval = longBreakInterval;
    }

    public int getPomodoroMinute() {
        return pomodoroMinute;
    }

    public void setPomodoroMinute(int pomodoroMinute) {
        this.pomodoroMinute = pomodoroMinute;
    }

    public int getShortBreakMinute() {
        return shortBreakMinute;
    }

    public void setShortBreakMinute(int shortBreakMinute) {
        this.shortBreakMinute = shortBreakMinute;
    }

    public int getLongBreakMinute() {
        return longBreakMinute;
    }

    public void setLongBreakMinute(int longBreakMinute) {
        this.longBreakMinute = longBreakMinute;
    }
}
```

---

# 3. Buat Repository

## `repository/AppConfigRepository.java`

```java
package com.dpzstudio.timer.repository;

import com.dpzstudio.timer.model.AppConfig;
import com.dpzstudio.timer.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AppConfigRepository {

    public AppConfig find() throws SQLException {
        String sql = """
            SELECT *
            FROM app_config
            WHERE id = 1
        """;

        try (
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {

            if (!rs.next()) {
                return null;
            }

            AppConfig config = new AppConfig();

            config.setPomodoroMinute(
                rs.getInt("pomodoro_minute")
            );

            config.setShortBreakMinute(
                rs.getInt("short_break_minute")
            );

            config.setLongBreakMinute(
                rs.getInt("long_break_minute")
            );

            config.setAutoStartPomodoro(
                rs.getBoolean("auto_start_pomodoro")
            );

            config.setAutoStartBreak(
                rs.getBoolean("auto_start_break")
            );

            config.setLongBreakInterval(
                rs.getInt("long_break_interval")
            );

            return config;
        }
    }

    public void update(AppConfig config) throws SQLException {
        String sql = """
            UPDATE app_config
            SET
                pomodoro_minute = ?,
                short_break_minute = ?,
                long_break_minute = ?,
                auto_start_pomodoro = ?,
                auto_start_break = ?,
                long_break_interval = ?
            WHERE id = 1
        """;

        try (
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, config.getPomodoroMinute());
            stmt.setInt(2, config.getShortBreakMinute());
            stmt.setInt(3, config.getLongBreakMinute());

            stmt.setBoolean(4, config.isAutoStartPomodoro());
            stmt.setBoolean(5, config.isAutoStartBreak());

            stmt.setInt(6, config.getLongBreakInterval());

            stmt.executeUpdate();
        }
    }
}
```

---

# 4. Buat Service

## `service/AppConfigService.java`

```java
package com.dpzstudio.timer.service;

import com.dpzstudio.timer.model.AppConfig;
import com.dpzstudio.timer.repository.AppConfigRepository;

public class AppConfigService {

    private static AppConfig cachedConfig;

    private final AppConfigRepository repository =
        new AppConfigRepository();

    public AppConfig loadConfig() {

        if (cachedConfig != null) {
            return cachedConfig;
        }

        try {
            AppConfig config = repository.find();

            if (config == null) {
                config = AppConfig.createDefault();
            }

            cachedConfig = config;

        } catch (Exception e) {
            e.printStackTrace();

            cachedConfig = AppConfig.createDefault();
        }

        return cachedConfig;
    }

    public void saveConfig(AppConfig config) {

        validate(config);

        try {
            repository.update(config);

            cachedConfig = config;

        } catch (Exception e) {
            throw new RuntimeException(
                "Failed save app config",
                e
            );
        }
    }

    private void validate(AppConfig config) {

        if (config.getPomodoroMinute() < 1) {
            throw new IllegalArgumentException(
                "Pomodoro minute invalid"
            );
        }

        if (config.getShortBreakMinute() < 1) {
            throw new IllegalArgumentException(
                "Short break minute invalid"
            );
        }

        if (config.getLongBreakMinute() < 1) {
            throw new IllegalArgumentException(
                "Long break minute invalid"
            );
        }

        if (config.getLongBreakInterval() < 1) {
            throw new IllegalArgumentException(
                "Long break interval invalid"
            );
        }
    }
}
```

---

# 5. Refactor `SettingController`

## Yang Penting:

Controller:

- tidak langsung save realtime
- hanya save saat tombol ditekan
- cancel bisa rollback

---

## `SettingController.java`

```java
package com.dpzstudio.timer.controller;

import com.dpzstudio.timer.model.AppConfig;
import com.dpzstudio.timer.service.AppConfigService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class SettingController implements Initializable {

    @FXML private CheckBox checkboxAutoStartPomo;
    @FXML private CheckBox checkboxAutoStartBreak;

    @FXML private Spinner<Integer> spinnerLongBreakInterval;

    @FXML private Spinner<Integer> spinnerDefaultPomodoro;
    @FXML private Spinner<Integer> spinnerDefaultShortBreak;
    @FXML private Spinner<Integer> spinnerDefaultLongBreak;

    @FXML private Button btnSaveSetting;
    @FXML private Button btnCancel;

    private final AppConfigService configService =
        new AppConfigService();

    private AppConfig loadedConfig;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        loadedConfig = configService.loadConfig();

        initializeSpinner();

        bindData();

        btnSaveSetting.setOnAction(e -> saveSetting());

        btnCancel.setOnAction(e -> cancelSetting());
    }

    private void initializeSpinner() {

        spinnerLongBreakInterval.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1,
                10,
                loadedConfig.getLongBreakInterval()
            )
        );

        spinnerDefaultPomodoro.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1,
                99,
                loadedConfig.getPomodoroMinute()
            )
        );

        spinnerDefaultShortBreak.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1,
                99,
                loadedConfig.getShortBreakMinute()
            )
        );

        spinnerDefaultLongBreak.setValueFactory(
            new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1,
                99,
                loadedConfig.getLongBreakMinute()
            )
        );
    }

    private void bindData() {

        checkboxAutoStartPomo.setSelected(
            loadedConfig.isAutoStartPomodoro()
        );

        checkboxAutoStartBreak.setSelected(
            loadedConfig.isAutoStartBreak()
        );
    }

    private void saveSetting() {

        try {

            AppConfig newConfig = new AppConfig();

            newConfig.setPomodoroMinute(
                spinnerDefaultPomodoro.getValue()
            );

            newConfig.setShortBreakMinute(
                spinnerDefaultShortBreak.getValue()
            );

            newConfig.setLongBreakMinute(
                spinnerDefaultLongBreak.getValue()
            );

            newConfig.setLongBreakInterval(
                spinnerLongBreakInterval.getValue()
            );

            newConfig.setAutoStartPomodoro(
                checkboxAutoStartPomo.isSelected()
            );

            newConfig.setAutoStartBreak(
                checkboxAutoStartBreak.isSelected()
            );

            configService.saveConfig(newConfig);

            closeWindow();

        } catch (Exception e) {

            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setHeaderText("Save Failed");

            alert.setContentText(e.getMessage());

            alert.showAndWait();
        }
    }

    private void cancelSetting() {

        if (!hasChanges()) {
            closeWindow();
            return;
        }

        Alert alert = new Alert(
            Alert.AlertType.CONFIRMATION
        );

        alert.setHeaderText(
            "Discard changes?"
        );

        alert.setContentText(
            "Unsaved changes will be lost."
        );

        Optional<ButtonType> result =
            alert.showAndWait();

        if (
            result.isPresent()
            && result.get() == ButtonType.OK
        ) {
            closeWindow();
        }
    }

    private boolean hasChanges() {

        return
            !Objects.equals(
                spinnerDefaultPomodoro.getValue(),
                loadedConfig.getPomodoroMinute()
            )
            ||
            !Objects.equals(
                spinnerDefaultShortBreak.getValue(),
                loadedConfig.getShortBreakMinute()
            )
            ||
            !Objects.equals(
                spinnerDefaultLongBreak.getValue(),
                loadedConfig.getLongBreakMinute()
            )
            ||
            !Objects.equals(
                spinnerLongBreakInterval.getValue(),
                loadedConfig.getLongBreakInterval()
            )
            ||
            checkboxAutoStartPomo.isSelected()
                != loadedConfig.isAutoStartPomodoro()
            ||
            checkboxAutoStartBreak.isSelected()
                != loadedConfig.isAutoStartBreak();
    }

    private void closeWindow() {
        Stage stage =
            (Stage) btnCancel.getScene().getWindow();

        stage.close();
    }
}
```

---

# 6. Update `PomodoroService`

Hilangkan singleton lama:

```java
private final AppConfig config = AppConfig.getInstance();
```

Ganti:

```java
private final AppConfigService configService =
    new AppConfigService();
```

Kemudian gunakan:

```java
AppConfig config = configService.loadConfig();
```

di method yang membutuhkan config.

---

# 7. Refactor `Pomodoro`, `ShortBreak`, `LongBreak`

Saat ini object mode membaca singleton global.

Lebih baik inject config atau baca via service.

Namun untuk project kecil, pendekatan sekarang masih acceptable.

---

# Catatan Penting Tambahan

## Bug Pada `ResourceReader`

Saat ini:

```java
builder.append(line).
append(line)
```

Ini append dua kali.

Harusnya:

```java
builder
    .append(line)
    .append(System.lineSeparator());
```

---

# Masalah Duplicated Package

Saat ini ada:

```text
config/
model/
```

yang memiliki class sama:

```text
Pomodoro
LongBreak
ShortBreak
PomodoroMode
```

Ini redundant dan membingungkan.

Sebaiknya hapus package:

```text
config/
```

dan gunakan hanya:

```text
model/
```

---

# Kesimpulan

Refactor ini memberikan:

- persistence setting yang proper
- clean architecture
- cancel/save workflow yang valid
- controller lebih ringan
- business logic terpusat
- lebih scalable
- lebih mudah maintain
- lebih siap untuk fitur lanjutan

Dan yang paling penting:

> UI tidak lagi menjadi pusat business logic dan persistence.
