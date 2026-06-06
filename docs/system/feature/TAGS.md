# Feature : Tags

Fitur 'tags' bertujuan untuk memberikan label setiap sesi pomodoro yang telah di selesaikan oleh pengguna. Data sesi yang selesai akan di simpan dengan entitas `tag` yang akan digunakan untuk data statistik yang setiap sesi yang telah dilakukan bagi User.

## Use Case

Berikut ini uraian mengenai bagaimana 'user' akan menggunakan fitur `tags` :

- Pada page `Home.fxml` sistem menyediakan dua component baru. Pertama 'labelSelectedTags', yang berfungsi untuk menampilkan tags yang sedang digunakan untuk sesi pomodoro saat ini. Kedua 'btnSelectTags' yang berfungsi untuk membuka dialog `TagsMenu` untuk pemilihan tags.
- Pada `TagsMenu` akan di sediakan list tags yang tersedia, user tinggal memilih satu tags yang akan digunakan. Setelah itu user akan menyimpan pilihannya.
- Sistem akan otomatis kembali ke halaman `Home.fxml` dengan tags yang telah di pilih.

User juga dapat menambahkan tag mereka sendiri dengan cara sebagai berikut :

- User masuk ke menu setting (`Setting,fxnk`), akan tersedia button dengan label 'Edit Tags' yang akan mengganti main 'scene' menjadi page khusus untuk pengeditan tags. `Setting.fxml` akan otomatis tertutup dan
- `TagsTable.fxml` akan menjadi page untuk pengeditan tags. Pada page ini user dapat melakukan CRUD 'tags'

Aturan :

- User dapat untuk tidak memilih tags apapun.
- Sesi yang selesai akan tetap di simpan, baik dengan tag atau tidak
- Secara default, tags yang terpilih ada 'none'

## Database

Berikut ini rancangan entitas `tag`.

`tag_table` :

- id [PRIMARY]
- name [UNIQUE, SHORT TEXT]
- created_at
- updated_at
- deleted_at
- total_session

Keterangan :

- Mendukung soft delete
- `total_session` adalah jumlah sesi yang selesai.

## How it Work

### Adding tags

- User go to page `tags.fxml`
- User input in TextField with id `inputTagName`
- System validate input above with constraint :
  - No duplicate record in database
  - Only contain alpha-numeric
- If validated, than converted into capitalize each word. Than show success dialog. Else show error.
- System create new record in `tag` table

#### Class

- Create new class for handling input validation called `InputValidation`. This class will be use for any other validation needed in future time.
- Create a needed model
- Create a repository
- Create a needed service

### Selecting Tags

- User go to page `tags.fxml`
- Sistem menampilkan table yang berisi tags yang tersedia
- User memilih `tag` melalui column actions dengan tombol `select` yang akan memilih tag tersebut
- Sistem menyimpan tag yang dipilih untuk digunakan pada class atau operasi lain.
- User hanya dapat memilih satu `tag` dalam satu waktu
- Sistem akan menyediakan efek visual khusus pada table untuk menandai `tag` mana yang sedang di pilih
