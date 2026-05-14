# Statistics Tracker

Fix my tracker, especially for label `labelTodayStats` for update the data after a session minute. Only fetch data from database that are same with the current local time user. The point is, fix the data so it can sync withour overhead our process

---

Sistem dapat menyimpan statistik penggunaak aplikasi pomodoro timer ini. Adapund data yang akan diperlukan untuk melakukan tracking adalah sebagai berikut:

- How many session user do for each day?
- How many session that have been failed?

## Constraint

We're use SQLite for the database engine. Modified our SQL, so it can handle our requirement above. Note, the schema must be in good form, especially when there are new feature, make sure the schema doesnt break!

### Code Flow

- User start pomodoro session
- If session if finish than store the progress to database.
- If failed, record it to our database

So user can see how many session if success and failed. Please make this support for analisys by date.

---
