## Koneksi Database : Setting

Merubah sistem setting agar mengambil data dari database untuk menyimpan settingan.

Alur kerja

- Saat aplikasi pertama kali di buka: Ambil data state setting dari database. Jika fail, set default melalui hardcode.
- User masuk ke tampilan 'Setting.fxml'
- User merubah setting, dan menyimpan setting baru melalui `btnSaveSetting`, sistem mem-validasi kemudian menyimpan ke dalam database.
- Redirect ke halaman utama.

Note:

- User dapat men-cancel operasi perubahan setting melalui button 'btnCancel'. Jika sudah terdapat perubahan, tampilkan dialog konfirmasi.

---

Sistem membuat koneksi database SQLite dengan alur sebagai berikut.

- Pastikan file db tersedia.
- Jika tidak ada, maka buat dengan melakukan eksekusi SQL pada file `database.sql`.

Pastikan format kode di buat secara baik dan rapi!
