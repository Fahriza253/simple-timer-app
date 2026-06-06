# Project Overview : Simple Timer App

A lightweight, cross-platform desktop timer pomodoro application built with JavaFX. This is a personal learning project for exploring Java desktop application development and Maven build workflows.

- Database : SQLite
- Using MVC with DAO / Repository Architecture
- User raw JDBC

Untuk konteks lebih lanjut, analisis file yang telah di berikan. File tersebut berisi progress coding saat ini. Baik yang telah terdapat pada 'source project' atau yang dikirim manual!

## Styling With Css

Karakteristik

- Dark theme, use #2d3436
- Primary color is sky blue
- Good micro interaction with good state transition
- Soft color
- Good ui and ux

Here are the placeholder,

```
.root {
  /* 1. BASE STRUCTURE */
  -color-dark-bg: #2f3640;
  -color-dark-surface: #353b48;
  -color-dark-border: #444d5c;

  /* 2. TYPOGRAPHY */
  -color-dark-text-primary: #f5f6fa;
  -color-dark-text-secondary: #a4b0be;

  /* 3. PRIMARY ACTION (BRAND / MAIN BUTTON) */
  -color-primary: #4a90e2;
  -color-primary-hover: #5ba0ec;
  -color-primary-active: #3a7bc8;

  /* 4. SECONDARY ACTION (CANCEL / OUTLINE BUTTONS) */
  -color-secondary-btn: #444d5c;
  -color-secondary-hover: #505a6a;
  -color-secondary-active: #38404d;

  /* 5. DISABLED STATE */
  -color-disabled-bg: #3c4450;
  -color-disabled-text: #747d8c;

  /* 6. SEMANTIC FEEDBACK */
  -color-success: #4cd137;
  -color-error: #e84118;
  -color-warning: #fbc531;

  /* Set default font family for the entire app */
  -fx-font-family: "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  -fx-background-color: -color-dark-bg;
}

/* --- LAYOUT & CONTAINERS --- */

.surface-card {
  /* Cocok untuk membungkus elemen timer atau form task */
  -fx-background-color: -color-dark-surface;
  -fx-background-radius: 12px;
  -fx-padding: 24px;
  /* Memberikan bayangan lembut agar card terlihat 'mengangkat' (elevated) */
  -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.25), 10, 0, 0, 4);
}

/* --- TYPOGRAPHY --- */

.label {
  -fx-text-fill: -color-dark-text-primary;
  -fx-font-size: 14px;
}

.text-secondary {
  -fx-text-fill: -color-dark-text-secondary;
  -fx-font-size: 12px;
}

.timer-display {
  /* Class khusus untuk angka timer yang besar */
  -fx-font-size: 72px;
  -fx-font-weight: bold;
  -fx-text-fill: -color-primary;
  -fx-alignment: center;
}

/* --- BUTTONS --- */

/* Default / Secondary Button */
.button {
  -fx-text-fill: -color-dark-text-primary;
  -fx-font-size: 14px;
  -fx-font-weight: bold;
  -fx-alignment: center;
  -fx-background-color: -color-secondary-btn;
  -fx-background-radius: 8px;
  -fx-border-radius: 8px;
  -fx-padding: 10px 20px;
  -fx-cursor: hand; /* Memberikan feedback kursor saat di-hover */
}

.button:hover {
  -fx-background-color: -color-secondary-hover;
}

/* Menggunakan :pressed bukan :active untuk JavaFX */
.button:pressed {
  -fx-background-color: -color-secondary-active;
  -fx-translate-y: 1px; /* Efek tombol tertekan secara fisik */
}

.button:disabled {
  -fx-background-color: -color-disabled-bg;
  -fx-text-fill: -color-disabled-text;
  -fx-cursor: default;
}

/* Primary Button (Untuk tombol "Start Timer" atau "Save Task") */
.btn-primary {
  -fx-background-color: -color-primary;
  -fx-text-fill: white;
}

.btn-primary:hover {
  -fx-background-color: -color-primary-hover;
}

.btn-primary:pressed {
  -fx-background-color: -color-primary-active;
}

/* --- INPUT FIELDS --- */

.text-field {
  -fx-background-color: -color-dark-bg;
  -fx-text-fill: -color-dark-text-primary;
  -fx-border-color: -color-dark-border;
  -fx-border-radius: 6px;
  -fx-background-radius: 6px;
  -fx-padding: 10px 12px;
  -fx-prompt-text-fill: -color-dark-text-secondary;
}

.text-field:focused {
  /* Highlight border dengan warna primary saat user mengetik */
  -fx-border-color: -color-primary;
  -fx-background-color: -color-dark-surface;
}

/* --- TABLE VIEW --- */

.table-view {
  -fx-background-color: -color-dark-bg;
  -fx-border-color: -color-dark-border;
  -fx-border-radius: 8px;
  -fx-background-radius: 8px;
  -fx-padding: 0;
}

/* Menghilangkan focus ring bawaan JavaFX (garis biru di luar tabel) */
.table-view:focused {
  -fx-background-color: -color-dark-bg;
  -fx-background-insets: 0;
}

/* --- TABLE HEADER --- */

/* Area background untuk seluruh baris header */
.table-view .column-header-background {
  -fx-background-color: -color-dark-surface;
  -fx-background-radius: 8px 8px 0 0;
  -fx-border-color: transparent transparent -color-dark-border transparent;
  -fx-border-width: 0 0 1px 0;
}

/* Kolom header individual */
.table-view .column-header,
.table-view .filler {
  -fx-background-color: transparent;
  -fx-size: 40px; /* Tinggi header */
  -fx-border-color: transparent -color-dark-border transparent transparent;
  -fx-border-width: 0 1px 0 0;
}

/* Menghilangkan garis vertikal di kolom paling akhir agar rapi */
.table-view .column-header:last-visible {
  -fx-border-color: transparent;
}

/* Teks di dalam header */
.table-view .column-header .label {
  -fx-text-fill: -color-dark-text-secondary;
  -fx-font-weight: bold;
  -fx-alignment: center-left;
  -fx-padding: 0 12px;
}

/* --- TABLE ROWS & CELLS --- */

/* Baris tabel (Row) */
.table-row-cell {
  -fx-background-color: -color-dark-bg;
  -fx-border-color: transparent transparent -color-dark-border transparent;
  -fx-border-width: 0 0 1px 0; /* Garis pemisah antar baris */
  -fx-cell-size: 40px; /* Tinggi baris data */
}

/* Efek saat baris di-hover */
.table-row-cell:hover {
  -fx-background-color: -color-secondary-hover;
}

/* Efek saat baris diklik/dipilih */
.table-row-cell:selected {
  -fx-background-color: -color-primary;
}

/* Sel individual di dalam tabel */
.table-cell {
  -fx-text-fill: -color-dark-text-primary;
  -fx-padding: 0 12px;
  -fx-alignment: center-left;
  -fx-border-color: transparent; /* Menghilangkan border default antar sel */
}

/* Mengubah warna teks menjadi putih saat baris dipilih agar kontras dengan warna primary */
.table-row-cell:selected .table-cell {
  -fx-text-fill: white;
}

/* --- EMPTY TABLE PLACEHOLDER --- */

/* Teks yang muncul saat tabel tidak memiliki data ("No content in table") */
.table-view .placeholder .label {
  -fx-text-fill: -color-dark-text-secondary;
  -fx-font-size: 14px;
}
```
